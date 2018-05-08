/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.domain.Level;
import otmharjoitustyo.domain.Player;

import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SelectionScene {
    
    public static void buildAndSet(Main main) {
        
        VBox vboxLevels = new VBox();
//        vboxLevels.setPrefSize(300, 180);
//        vboxLevels.setSpacing(10);
//        vboxLevels.setPadding(new Insets(30, 30, 30, 30));
        

        ArrayList<Level> levels = null;
        
        try{
            levels = main.getLevelDao().listAll();
        } catch(Exception e){
             // Exception redirection to a dedicated exception scene, 
             // in order to avoid a possible unending exception redirection loop.
             ExceptionScene.buildAndSet(main, "A problem occured while listing the levels of the game.");
             return;
        }
        
        for(Level level : levels){
            
            Image thumbnail = SwingFXUtils.toFXImage(level.getThumbnail(), null);
            Image thumbnailHover = SwingFXUtils.toFXImage(level.getThumbnailHover(), null);
            
            ImageView thumbnailView = new ImageView(thumbnail);
            
            thumbnailView.setOnMouseEntered((event) -> {
                thumbnailView.setImage(thumbnailHover);
            });
            
            thumbnailView.setOnMouseExited((event) -> {
                thumbnailView.setImage(thumbnail);
            });
            
            thumbnailView.setOnMouseClicked((event) -> {
                NameEntryScene.buildAndSet(main, level.getName(), null);
            });
            
            vboxLevels.getChildren().add(thumbnailView);
        }
        
        
        ImageView starView = new ImageView(new Image("file:Graphics/star.png"));
        Label leaderboardTitle = new Label("TOP 5 Players with most wins");
        leaderboardTitle.setStyle("-fx-font: 12px Verdana; -fx-font-weight: bold;");
        
        HBox starAndTitle = new HBox();
        starAndTitle.setStyle("-fx-alignment: center;");
        starAndTitle.setSpacing(7);
        starAndTitle.getChildren().addAll(starView, leaderboardTitle);
        
        
        
        VBox vboxWinners = new VBox();
        vboxWinners.setStyle("-fx-background-color: #e6ff99;");
        vboxWinners.setPrefSize(300, 180);
        vboxWinners.setSpacing(10);
        vboxWinners.setPadding(new Insets(30, 30, 30, 30));
        vboxWinners.getChildren().add(starAndTitle);
        
        
        ArrayList<Player> players = null;
        
        try{
            players = main.getPlayerDao().findWinners();
        } catch(Exception e){
             // Exception redirection to a dedicated exception scene, 
             // in order to avoid a possible unending exception redirection loop.
             ExceptionScene.buildAndSet(main, "A problem occured while listing the TOP 5 players.");
             return;
        }
        
        for(Player player : players){
            vboxWinners.getChildren().add(new Label("  " + player.getWins() + "  " + player.getName()));
        }
        
        
        HBox hbox = new HBox();
        hbox.getChildren().add(vboxLevels);
        hbox.getChildren().add(vboxWinners);
        
        
        ImageView logoView = new ImageView(new Image("file:Graphics/logo.png"));
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #74ecf2; -fx-alignment: center;");
        mainBox.getChildren().add(logoView);
        mainBox.getChildren().add(hbox);
        
        Scene selectionScene = new Scene(mainBox);
        main.getStage().setScene(selectionScene);
    }
    
}
