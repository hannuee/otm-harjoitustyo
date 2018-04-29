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

// Game scene
import javafx.scene.control.ProgressBar;

// Name entry scene
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

// Game selection scene
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

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
        
        HBox hbox = new HBox();
        Label leftLabel = new Label("Left player");
        ProgressBar leftBar = new ProgressBar(game.leftFortressPercentage());
        Label rightLabel = new Label("Right player");
        ProgressBar rightBar = new ProgressBar(game.rightFortressPercentage());
        leftLabel.setTextFill(Color.RED);
        rightLabel.setTextFill(Color.BLACK);
        hbox.getChildren().addAll(leftLabel, leftBar, rightLabel, rightBar);
        
        VBox vbox = new VBox();
        vbox.getChildren().add(canvas);
        vbox.getChildren().add(hbox); 
        
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
                    System.out.println("Frames during " + current + "s: " + counter);
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
                    
                    leftBar.setProgress(game.leftFortressPercentage());
                    rightBar.setProgress(game.rightFortressPercentage());
                    
                    // Game over check.
                    if(game.getState() == 1){
                        
                        int leftFortressPixels = game.leftFortressPixels();
                        int rightFortressPixels = game.rightFortressPixels();

                        if (!(leftFortressPixels > 0 && rightFortressPixels > 0)) {  // Is Game still in progress?
                            if (leftFortressPixels == 0 && rightFortressPixels == 0) {  // TIE!
                                leftPlayer.addTie();
                                rightPlayer.addTie();
                            } else if (leftFortressPixels == 0) {  // Right player won!
                                leftPlayer.addLoss();
                                rightPlayer.addWin();
                            } else {  // Left player won!
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
                    
                    // Coloring the player's name red who's turn it is now that this simulation is over.
                    if(game.getState() == 1){
                        leftLabel.setTextFill(Color.RED);
                    } else if(game.getState() == 4){
                        rightLabel.setTextFill(Color.RED);
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
            } else if(game.getState() == 4){
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
                leftLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                simulation.start();
            } else if(game.getState() == 4){
                game.setAndFireCannon((int)event.getX() - level.getRightCannonX(), 700 - (int)event.getY() - level.getRightCannonY());  // Tähänkin jotkut hienot transformit.
                rightLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                simulation.start();
            }
        });
        
        Scene gameScene = new Scene(vbox);
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
        VBox vboxLevels = new VBox();
        vboxLevels.setPrefSize(300, 180);
        vboxLevels.setSpacing(10);
        vboxLevels.setPadding(new Insets(30, 30, 30, 30));
        
        ArrayList<String> levelNames = levelDao.listAll();
        for(String levelName : levelNames){
            
            Button levelButton = new Button(levelName);
            levelButton.setOnAction((event) -> {
                buildAndSetNameEntryScene(stage, levelName);
            });
            vboxLevels.getChildren().add(levelButton);
        }
        
        
        VBox vboxWinners = new VBox();
        vboxWinners.setPrefSize(300, 180);
        vboxWinners.setSpacing(10);
        vboxWinners.setPadding(new Insets(30, 30, 30, 30));
        vboxWinners.getChildren().add(new Label("Players with most wins"));
        
        ArrayList<Player> players = playerDao.findWinners();  
        for(Player player : players){
            vboxWinners.getChildren().add(new Label(player.getName() + "  " + player.getWins()));
        }
        
        
        HBox hbox = new HBox();
        hbox.getChildren().add(vboxLevels);
        hbox.getChildren().add(vboxWinners);
        
        Scene selectionScene = new Scene(hbox);
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
