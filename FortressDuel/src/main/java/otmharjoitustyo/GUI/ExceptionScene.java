/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.domain.GameService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ExceptionScene {
    
    public ExceptionScene(Main main, GameService gameService, String message){
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);  
        vbox.setPrefHeight(400);
        vbox.setSpacing(40);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        
        Label messageLabel = new Label(message);
        messageLabel.setTextFill(Color.RED);
        
        Button redirectButton = new Button("Exit to main menu");
        redirectButton.setOnAction((event) -> {
            new SelectionScene(main, gameService);
        });
        
        vbox.getChildren().addAll(messageLabel, redirectButton);
        
        Scene exceptionScene = new Scene(vbox);
        main.setNewSceneOnStage(exceptionScene);
    }
    
}
