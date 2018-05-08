/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.database.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    
    LevelDao levelDao;
    PlayerDao playerDao;
    Stage stage;
    
    @Override
    public void init() {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        this.levelDao = new LevelDao(database);
        this.playerDao = new PlayerDao(database);
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
        
        SelectionScene.buildAndSet(this);
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(Main.class);
    }
    
    public LevelDao getLevelDao(){
        return this.levelDao;
    }
    
    public PlayerDao getPlayerDao(){
        return this.playerDao;
    }
    
    public Stage getStage(){
        return this.stage;
    }
    
}
