/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.Game;
import otmharjoitustyo.domain.*;
import otmharjoitustyo.database.*;

import javafx.application.Application;
import javafx.animation.AnimationTimer;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.BorderPane;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.paint.Color;

import java.sql.SQLException;

// Game selection scene
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

// Name entry scene
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Main extends Application {
    
    LevelDao levelDao;
    PlayerDao playerDao;
    
    @Override
    public void init() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        levelDao = new LevelDao(database);
        playerDao = new PlayerDao(database);
    }

    private void buildAndSetGameScene(Stage stage, Game game, Level level, Player leftPlayer, Player rightPlayer){
        Canvas canvas = new Canvas(level.getGameField().getWidth(), level.getGameField().getHeight());
        GraphicsContext pen = canvas.getGraphicsContext2D();
        BorderPane layout = new BorderPane();
        layout.setCenter(canvas);
        
        // Simulaation näyttäminen.
        AnimationTimer simulation = new AnimationTimer() {
            
            long startTime = -1;
            
            // FPS
            int counter = 0;
            int current = 0;
            
            @Override
            public void handle(long nowTime){
                if(startTime == -1){
                    startTime = System.nanoTime();
                }
                
                double simulationTime = (nowTime - startTime)/1000000000.0;

                // FPS
                int simuTime = (int)simulationTime;
                if(current == simuTime){
                    ++counter;
                } else {
                    //System.out.println("Frames during " + current + "s: " + counter);
                    current = simuTime;
                    counter = 1;
                }

                BufferedImage gameImage = game.getSimulationSnapshot(1.5*simulationTime);
                if(gameImage != null){
                    Image image = SwingFXUtils.toFXImage(gameImage, null);
                    pen.drawImage(image, 0, 0);
                } else {
                    this.stop();
                    startTime = -1;
                    
                    // Game over check.
                    if(game.getState() == 1){
                        String result = game.checkWinner();
                        if(result != null){
                            //System.out.println(result); // FOR TESTING!!!!!!!!!!!!!!!!!!!!!!!!!!
                            
                            if(result.equals("TIE!")){
                                leftPlayer.addTie();
                                rightPlayer.addTie();
                            } else if(result.equals("Right player won!")){
                                leftPlayer.addLoss();
                                rightPlayer.addWin();
                            } else {  // Left won
                                leftPlayer.addWin();
                                rightPlayer.addLoss();
                            }
                            
                            try{
                                playerDao.update(leftPlayer);
                                playerDao.update(rightPlayer);
                            } catch(Exception e){
                                //System.out.println("ERROR1");// ERROR?!?!?!?!?!?????????????????????
                            }
                            
                            try{
                                buildAndSetSelectionScene(stage);
                            } catch(Exception e){
                                //System.out.println("ERROR2");// ERROR?!?!?!?!?!?????????????????????
                            }
                            
                        } 
                    }
                }                    
                
            }
        };
        
        // Drawing of the cannon aiming vectors.         
        canvas.setOnMouseMoved((event) -> {
            if(game.getState() == 1){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(level.getLeftCannonX(), 700 - level.getLeftCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform JA tykin sijainti levelistä!!!! 
            } else if(game.getState() == 3){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(level.getRightCannonX(), 700 - level.getRightCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform.
            }  
        });
        
        // Firing of the cannons.
        canvas.setOnMouseClicked((event) -> {
            if(game.getState() == 1){
                game.setAndFireCannon((int)event.getX() - level.getLeftCannonX(), 700 - (int)event.getY() - level.getLeftCannonY());  // Tähänkin jotkut hienot transformit.
                simulation.start();
            } else if(game.getState() == 3){
                game.setAndFireCannon((int)event.getX() - level.getRightCannonX(), 700 - (int)event.getY() - level.getRightCannonY());  // Tähänkin jotkut hienot transformit.
                simulation.start();
            }
        });
        
        Scene gameScene = new Scene(layout);
        stage.setScene(gameScene);
    }
    
    private void buildAndSetNameEntryScene(Stage stage, String levelName) {
        Label leftSideLabel = new Label("Nickname of the player on the left side:");
        TextField leftSideNickname = new TextField();
        leftSideNickname.setMaxWidth(220.00);
        
        Label rightSideLabel = new Label("Nickname of the player on the right side:");
        TextField rightSideNickname = new TextField();
        rightSideNickname.setMaxWidth(220.00);
        
        Button startButton = new Button("Start The Game!");
        startButton.setOnAction((event) -> {
            Level level = null;
            Game game = null;
            try{
                level = levelDao.findOne(levelName);
                game = new Game(level);
            } catch(Exception e){
                //System.out.println("Error while loading the level!");  // MUUTA GRAAFISEKS?!?!?!?!?!
            }
            
            
            String leftPlayerNickname = leftSideNickname.getText();   // TARKASTA ETTÄ EI OLE SAMA NIMI!!!!!!!!!
            String rightPlayerNickname = rightSideNickname.getText(); 
            Player leftPlayer = null;
            Player rightPlayer = null;
            try{
                leftPlayer = playerDao.findOne(leftPlayerNickname);
                rightPlayer = playerDao.findOne(rightPlayerNickname);
                
                if(leftPlayer == null){
                    leftPlayer = new Player(leftPlayerNickname, 0, 0, 0);
                    playerDao.add(leftPlayer);
                }
                if(rightPlayer == null){
                    rightPlayer = new Player(rightPlayerNickname, 0, 0, 0);
                    playerDao.add(rightPlayer);
                }
                
            } catch(Exception e){   
            }
            
            buildAndSetGameScene(stage, game, level, leftPlayer, rightPlayer);
        });
        
        VBox vbox = new VBox();
        vbox.setPrefSize(300, 180);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.getChildren().add(leftSideLabel);
        vbox.getChildren().add(leftSideNickname);
        vbox.getChildren().add(rightSideLabel);
        vbox.getChildren().add(rightSideNickname);
        vbox.getChildren().add(startButton);
        
        Scene nameEntryScene = new Scene(vbox);
        stage.setScene(nameEntryScene);
    }
    
    private void buildAndSetSelectionScene(Stage stage) throws SQLException, IOException{
        VBox vbox = new VBox();
        vbox.setPrefSize(300, 180);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        
        ArrayList<String> levelNames = levelDao.listAll();
        for(String levelName : levelNames){
            
            Button levelButton = new Button(levelName);
            levelButton.setOnAction((event) -> {
                buildAndSetNameEntryScene(stage, levelName);
            });
            vbox.getChildren().add(levelButton);
        }
        
        ArrayList<Player> players = playerDao.findAll();  // FUNTSI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        for(Player player : players){  // TESTAAMISEEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //System.out.println(player.getName() + " " + player.getWins() + " " + player.getTies() + " " + player.getLosses());
        }
        
        Scene selectionScene = new Scene(vbox);
        stage.setScene(selectionScene);
    }
    
    @Override
    public void start(Stage stage) throws SQLException, IOException{
        buildAndSetSelectionScene(stage);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(Main.class);
    }
    
}
