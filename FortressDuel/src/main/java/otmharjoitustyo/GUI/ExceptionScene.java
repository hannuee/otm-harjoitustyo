/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ExceptionScene {
    
    public static void buildAndSet(Main main, String message){
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);  // min aiemman ikkunan tiedoista????
        vbox.setPrefHeight(400);
        vbox.setSpacing(40);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        
        Label messageLabel = new Label(message);
        messageLabel.setTextFill(Color.RED);
        
        Button redirectButton = new Button("Exit to main menu");
        redirectButton.setOnAction((event) -> {
            SelectionScene.buildAndSet(main);
        });
        
        vbox.getChildren().addAll(messageLabel, redirectButton);
        
        Scene exceptionScene = new Scene(vbox);
        main.getStage().setScene(exceptionScene);
    }
    
}
