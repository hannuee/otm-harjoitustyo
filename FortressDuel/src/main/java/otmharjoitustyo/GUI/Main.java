/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.GameService;
import otmharjoitustyo.database.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.Scene;

public class Main extends Application {
    
    private GameService gameService;
    private Stage stage;
    
    @Override
    public void init() {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        PlayerDao playerDao = new PlayerDao(database);
        this.gameService = new GameService(levelDao, playerDao);
    }
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        
        stage.getIcons().addAll(
                new Image("file:Graphics/icon16.png"), 
                new Image("file:Graphics/icon24.png"), 
                new Image("file:Graphics/icon32.png"), 
                new Image("file:Graphics/icon48.png"), 
                new Image("file:Graphics/icon64.png"), 
                new Image("file:Graphics/icon256.png"));
        stage.setTitle("Fortress Duel");
        
        new SelectionScene(this, this.gameService);
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(Main.class);
    }
    
    public void setNewSceneOnStage(Scene newScene){
        this.stage.setScene(newScene);
    }
    
}
