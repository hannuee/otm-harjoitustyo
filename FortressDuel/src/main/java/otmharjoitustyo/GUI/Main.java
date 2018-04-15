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
    
    Database database;
    LevelDao levelDao;
    PlayerDao playerDao;
    
    ArrayList<Player> players;
    
    BufferedImage gameField;
    Game game;
    
    Player leftPlayer;
    Player rightPlayer;
    
    Stage stage;
    
    @Override
    public void init() throws SQLException, IOException {
        database = new Database("jdbc:sqlite:Gamedata.db");
        levelDao = new LevelDao(database);
        playerDao = new PlayerDao(database);
    }

    private void buildAndSetGameScene(){
        final int width = gameField.getWidth();
        final int height = gameField.getHeight();
        
        Canvas canvas = new Canvas(width, height);
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
                    System.out.println("Frames during " + current + "s: " + counter);
                    current = simuTime;
                    counter = 1;
                }

                BufferedImage gameImage = game.getSimulationSnapshot(simulationTime);
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
                            System.out.println(result); // FOR TESTING!!!!!!!!!!!!!!!!!!!!!!!!!!
                            try{
                                buildAndSetSelectionScene();
                            } catch(Exception e){
                                // ERROR?!?!?!?!?!?????????????????????
                            }
                            
                        } 
                    }
                }                    
                
            }
        };
        
        canvas.setOnMouseMoved((event) -> {
            if(game.getState() == 1){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(228, 600 - 238, event.getX(), event.getY());  // 600 - 238:hin joku yTransform.
            } else if(game.getState() == 3){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(733, 600 - 238, event.getX(), event.getY());  // 600 - 238:hin joku yTransform.
            }  
        });
        
        canvas.setOnMouseClicked((event) -> {
            if(game.getState() == 1){
                game.setAndFireCannon((int)event.getX() - 228, 600 - (int)event.getY() - 238);  // Tähänkin jotkut hienot transformit.
                simulation.start();
            } else if(game.getState() == 3){
                game.setAndFireCannon((int)event.getX() - 733, 600 - (int)event.getY() - 238);  // Tähänkin jotkut hienot transformit.
                simulation.start();
            }
        });
        
        Scene gameScene = new Scene(layout);
        stage.setScene(gameScene);
    }
    
    private void buildAndSetNameEntryScene(Level level) {
        Label leftSideLabel = new Label("Nickname of the player on the left side:");
        TextField leftSideNickname = new TextField();
        leftSideNickname.setMaxWidth(220.00);
        
        Label rightSideLabel = new Label("Nickname of the player on the right side:");
        TextField rightSideNickname = new TextField();
        rightSideNickname.setMaxWidth(220.00);
        
        Button startButton = new Button("Start The Game!");
        startButton.setOnAction((event) -> {
            try{
                gameField = levelDao.findOne(level.getId()).getGameField();
                game = new Game(gameField, 228, 238, 733, 238);
            } catch(Exception e){
                System.out.println("Error while loading the level!");  // MUUTA GRAAFISEKS?!?!?!?!?!
            }
            
            String leftPlayerNickname = leftSideNickname.getText();   // TARKASTA ETTÄ EI OLE SAMA NIMI!!!!!!!!!
            String rightPlayerNickname = rightSideNickname.getText(); 
            leftPlayer = null;
            rightPlayer = null;
            for(Player player : players){
                if(player.getName().equals(leftPlayerNickname)){
                    leftPlayer = player;
                } else if(player.getName().equals(rightPlayerNickname)){
                    rightPlayer = player;
                }
            }

            buildAndSetGameScene();
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
    
    private void buildAndSetSelectionScene() throws SQLException, IOException{
        VBox vbox = new VBox();
        vbox.setPrefSize(300, 180);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        
        ArrayList<Level> levels = levelDao.listAll();
        for(Level level : levels){
            
            Button levelButton = new Button(level.getName());
            levelButton.setOnAction((event) -> {
                buildAndSetNameEntryScene(level);
            });
            vbox.getChildren().add(levelButton);
        }
        
        players = playerDao.findAll();  // FUNTSI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        Scene selectionScene = new Scene(vbox);
        stage.setScene(selectionScene);
    }
    
    @Override
    public void start(Stage stage) throws SQLException, IOException{
        this.stage = stage;

        buildAndSetSelectionScene();
      
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(Main.class);
    }
    
}
