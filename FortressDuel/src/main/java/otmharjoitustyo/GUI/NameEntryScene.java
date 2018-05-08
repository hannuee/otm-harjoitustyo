/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.domain.Level;
import otmharjoitustyo.domain.Player;
import otmharjoitustyo.logic.Game;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NameEntryScene {
    
    public static void buildAndSet(Main main, String levelName, String errorMessage) {
        Label leftSideLabel = new Label("Nickname of the left player:");
        TextField leftSideNickname = new TextField();
        leftSideNickname.setMaxWidth(220.00);
        
        Label rightSideLabel = new Label("Nickname of the right player:");
        TextField rightSideNickname = new TextField();
        rightSideNickname.setMaxWidth(220.00);
        
        Button startButton = new Button("Start The Game!");
        startButton.setOnAction((event) -> {
            Level level = null;
            try{
                level = main.getLevelDao().findOne(levelName);
            } catch(Exception e){
                // Exception redirection back to the same scene, 
                // because possibly exception causing operations are executed only after a button is pushed.
                NameEntryScene.buildAndSet(main, levelName, "A problem occured while loading the level.");
                return;
            }
            Game game = new Game(level);
            
            
            String leftPlayerNickname = leftSideNickname.getText();
            String rightPlayerNickname = rightSideNickname.getText(); 
            if (leftPlayerNickname.equals(rightPlayerNickname)) {
                NameEntryScene.buildAndSet(main, levelName, "The nicknames can not be the same!");
                return;
            }
            
            Player leftPlayer = null;
            Player rightPlayer = null;
            try{
                leftPlayer = main.getPlayerDao().findOne(leftPlayerNickname);
                rightPlayer = main.getPlayerDao().findOne(rightPlayerNickname);
                
                if(leftPlayer == null){
                    leftPlayer = new Player(leftPlayerNickname, 0, 0, 0);
                    main.getPlayerDao().add(leftPlayer);
                }
                if(rightPlayer == null){
                    rightPlayer = new Player(rightPlayerNickname, 0, 0, 0);
                    main.getPlayerDao().add(rightPlayer);
                }
                
            } catch(Exception e){
                // Exception redirection back to the same scene, 
                // because possibly exception causing operations are executed only after a button is pushed.
                NameEntryScene.buildAndSet(main, levelName, "A problem occured while processing the players' names");
                return;
            }
            
            GameScene.buildAndSet(main, game, level, leftPlayer, rightPlayer);
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
        vbox.getChildren().addAll(leftSideLabel, leftSideNickname, rightSideLabel, rightSideNickname, startButton);
        if (errorMessage != null) {
            vbox.getChildren().add(errorLabel);
        }
        
        Scene nameEntryScene = new Scene(vbox);
        main.getStage().setScene(nameEntryScene);
    }
    
}
