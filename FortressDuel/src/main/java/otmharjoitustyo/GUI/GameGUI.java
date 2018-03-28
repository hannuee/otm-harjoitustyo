/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.Game;

import javafx.application.Application;
import javafx.animation.AnimationTimer;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.BorderPane;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

public class GameGUI extends Application {
    
    BufferedImage gameField;
    Game game;
    
    @Override
    public void init() {
        gameField = null;
        try {
            gameField = ImageIO.read(new File("kenttapohja.png"));
        } catch (IOException e) {
        }
        
        game = new Game(gameField, 118, 228, 238, 733, 238);
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
        new AnimationTimer() {
            
            long startTime = System.nanoTime();
            
            // FPS
            int counter = 0;
            int current = 0;
            
            @Override
            public void handle(long nowTime) {
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
                    game.setAndFireCannon(-50, 50);  // TESTAAMISEEN.
                    startTime = System.nanoTime();
                }
            }
        }.start();
        
        Scene scene = new Scene(layout);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(GameGUI.class);
    }
    
}
