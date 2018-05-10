/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.domain.Level;
import otmharjoitustyo.domain.Player;
import otmharjoitustyo.logic.Game;
import otmharjoitustyo.logic.GameService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NameEntryScene {
    
    public NameEntryScene(Main main, GameService gameService, String levelName, String errorMessage) {
        Label leftSideLabel = new Label("Nickname of the left player:");
        TextField leftSideName = new TextField();
        leftSideName.setMaxWidth(220.00);
        
        Label rightSideLabel = new Label("Nickname of the right player:");
        TextField rightSideName = new TextField();
        rightSideName.setMaxWidth(220.00);
        
        Button startButton = new Button("Start The Game!");
        startButton.setOnAction((event) -> {
            try{
                gameService.initializeGame(levelName);
            } catch(Exception e){
                // Exception redirection back to the same scene, 
                // because possibly exception causing operations are executed only after a button is pushed.
                new NameEntryScene(main, gameService, levelName, "A problem occured while loading the level.");
                return;
            }
            
            if(gameService.takeNamesAndCheckIfTheyAreTheSame(leftSideName.getText(), rightSideName.getText())){
                new NameEntryScene(main, gameService, levelName, "The nicknames can not be the same!");
                return;                
            }
            
            try{ 
                gameService.initializePlayers();
            } catch(Exception e){
                // Exception redirection back to the same scene, 
                // because possibly exception causing operations are executed only after a button is pushed.
                new NameEntryScene(main, gameService, levelName, "A problem occured while processing the players' names");
                return;
            }
            
            
            new GameScene(main, gameService);
        });
        
        Label errorLabel = null;
        if (errorMessage != null) {
            errorLabel = new Label(errorMessage);
            errorLabel.setStyle("-fx-font: 10px Verdana; -fx-text-fill: RED;");
        }
        
        VBox vbox = new VBox();
        vbox.setPrefHeight(270);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.getChildren().addAll(leftSideLabel, leftSideName, rightSideLabel, rightSideName, startButton);
        if (errorMessage != null) {
            vbox.getChildren().add(errorLabel);
        }
        
        Scene nameEntryScene = new Scene(vbox);
        main.setNewSceneOnStage(nameEntryScene);
    }
    
}
