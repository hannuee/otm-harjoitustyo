/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.Game;

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

public class GameGUI extends Application {
    
    BufferedImage gameField;
    Game game;
    
    @Override
    public void init() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        gameField = levelDao.findOne(1).getGameField();
        game = new Game(gameField, 228, 238, 733, 238);
    }    
    
    @Override
    public void start(Stage stage){
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
            public void handle(long nowTime) {
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
                    startTime = -1;
                    this.stop();
                    
                    // Tänne tarkastus että oliko juuri oikean puolimmaisen vuoro,
                    // jos oli niin tarkastetaan voittiko jompikumpi vai oliko tasapeli.
                    // Tai ehkä sittenkin getSimulationSnapshot heti alkuun.
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
        
        
        Scene scene = new Scene(layout);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(GameGUI.class);
    }
    
}
