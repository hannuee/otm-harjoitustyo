/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.GameService;

import java.awt.image.BufferedImage;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameScene {
    
    public GameScene(Main main, GameService gameService){
        Canvas canvas = new Canvas(gameService.getGameFieldWidth(), gameService.getGameFieldHeight());
        GraphicsContext pen = canvas.getGraphicsContext2D();
        pen.setStroke(Color.BLACK);
        
        GameBar gameBar = new GameBar(main, gameService);
        gameBar.indicateTurn();
        
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

                BufferedImage gameImage = gameService.getSimulationSnapshot(2.0*simulationTime);
                if(gameImage != null){
                    Image image = SwingFXUtils.toFXImage(gameImage, null);
                    pen.drawImage(image, 0, 0);
                } else {
                    this.stop();
                    startTime = -1;
                    
                    gameBar.updateProgressBars();
                    
                    try {
                        if (gameService.isGameOver()) {
                            new SelectionScene(main, gameService);
                            return;
                        }                        
                    } catch (Exception e) {
                        new ExceptionScene(main, gameService, "A problem occured while saving the results of the game.");
                        return;                        
                    }
  
                    gameBar.indicateTurn();                  
                }                    
                
            }
        };
        gameBar.setAnimationTimer(simulation);
        
        
        VBox vbox = new VBox();
        vbox.getChildren().add(canvas);
        vbox.getChildren().add(gameBar.getGameBarElement());

        // Drawing of the cannon aiming vectors.         
        canvas.setOnMouseMoved((event) -> {
            if (gameService.getGameState() == 1) {
                Image image = SwingFXUtils.toFXImage(gameService.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                // 1 x y  2 x y 
                pen.strokeLine(gameService.getLeftCannonX(), gameService.getGameFieldHeight() - gameService.getLeftCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform JA tykin sijainti levelistä!!!! 
            
                gameBar.updateLeftAimingInfo(event);
            } else if (gameService.getGameState() == 4) {
                Image image = SwingFXUtils.toFXImage(gameService.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                // 1 x y  2 x y
                pen.strokeLine(gameService.getRightCannonX(), gameService.getGameFieldHeight() - gameService.getRightCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform.
                
                gameBar.updateRightAimingInfo(event);
            }  
        });
        
        // Firing of the cannons.
        canvas.setOnMouseClicked((event) -> {
            if (gameService.fireCannonIfPossible((int)event.getX(), (int)event.getY())) {
                gameBar.indicateNoTurn();
                simulation.start();
            }
        });
        
        
        Scene gameScene = new Scene(vbox);
        main.setNewSceneOnStage(gameScene);
    }
    
}
