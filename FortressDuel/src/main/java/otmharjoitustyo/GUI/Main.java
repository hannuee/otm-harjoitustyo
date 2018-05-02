/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.Game;
import otmharjoitustyo.domain.*;
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

// Game scene
import javafx.scene.control.ProgressBar;

// Name entry scene
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

// Game selection scene
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import javafx.scene.image.ImageView;

import java.io.File;
import javax.imageio.ImageIO;
import java.io.FileInputStream;

public class Main extends Application {
    
    LevelDao levelDao;
    PlayerDao playerDao;
    
    @Override
    public void init() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        levelDao = new LevelDao(database);
        playerDao = new PlayerDao(database);
    }

    private void buildAndSetGameScene(Stage stage, Game game, Level level, Player leftPlayer, Player rightPlayer){
        Canvas canvas = new Canvas(level.getGameField().getWidth(), level.getGameField().getHeight());
        GraphicsContext pen = canvas.getGraphicsContext2D();

        
        
        Label leftLabel = new Label(leftPlayer.getName());
        ProgressBar leftBar = new ProgressBar(game.leftFortressPercentage());
        Label angleOfAimLeft = new Label("Cannon angle: ");
        Label amountOfAimLeft = new Label("Muzzle velocity: ");
        
        VBox leftBox = new VBox();
        leftBox.setPrefSize((int)(level.getGameField().getWidth()*0.333), 70);
        leftBox.setStyle("-fx-alignment: center;");
        //leftBox.setSpacing(10);
        leftBox.setPadding(new Insets(30, 30, 30, 30));
        leftBox.getChildren().addAll(leftLabel, leftBar, angleOfAimLeft, amountOfAimLeft);
        
        
        Label rightLabel = new Label(rightPlayer.getName());
        ProgressBar rightBar = new ProgressBar(game.rightFortressPercentage());
        Label angleOfAimRight = new Label("Cannon angle: ");
        Label amountOfAimRight = new Label("Muzzle velocity: ");        
        
        VBox rightBox = new VBox();
        rightBox.setPrefSize((int)(level.getGameField().getWidth()*0.333), 70);
        rightBox.setStyle("-fx-alignment: center;");
        //leftBox.setSpacing(10);
        rightBox.setPadding(new Insets(30, 30, 30, 30));
        rightBox.getChildren().addAll(rightLabel, rightBar, angleOfAimRight, amountOfAimRight);
        
        
        leftLabel.setTextFill(Color.RED);
        rightLabel.setTextFill(Color.BLACK);
        
        
        
        Button vacuumControl = new Button("Suck all the air out!");
        vacuumControl.setTextFill(Color.RED);
        if (level.isVacuumPossible()) {
            vacuumControl.setOnAction((event) -> {
                if (game.getState() == 1 || game.getState() == 4) {
                    if (game.getVacuum()) {
                        game.setVacuum(false);
                        vacuumControl.setText("Suck all the air out!");
                    } else {
                        game.setVacuum(true);
                        vacuumControl.setText("Let air back in!");
                    }
                }
            });
        }
        Button fireWithPrevious = new Button("Fire cannon with previous settings");
        int[] leftPrevious = new int[]{0,0,0};
        int[] rightPrevious = new int[]{0,0,0};
        
        Button exitButton = new Button("Exit game");
        exitButton.setOnAction((event) -> {
            try{
                buildAndSetSelectionScene(stage);
            } catch(Exception e){
                //System.out.println("ERROR2");// ERROR?!?!?!?!?!?????????????????????
            }
        });
        
        VBox centerBox = new VBox();
        centerBox.setPrefSize((int)(level.getGameField().getWidth()*0.333), 70);
        centerBox.setStyle("-fx-alignment: center;");
        centerBox.setSpacing(10);
        centerBox.setPadding(new Insets(30, 30, 30, 30));
        if(level.isVacuumPossible()){
            centerBox.getChildren().addAll(vacuumControl, fireWithPrevious);
        }
        centerBox.getChildren().add(exitButton);
        
        
        HBox hbox = new HBox();
        hbox.getChildren().addAll(leftBox, centerBox, rightBox);

        
        
        VBox vbox = new VBox();
        vbox.getChildren().add(canvas);
        vbox.getChildren().add(hbox); 
        
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

                // FPS
                int simuTime = (int)simulationTime;
                if(current == simuTime){
                    ++counter;
                } else {
                    //System.out.println("Frames during " + current + "s: " + counter);
                    current = simuTime;
                    counter = 1;
                }

                BufferedImage gameImage = game.getSimulationSnapshot(1.5*simulationTime);
                if(gameImage != null){
                    Image image = SwingFXUtils.toFXImage(gameImage, null);
                    pen.drawImage(image, 0, 0);
                } else {
                    this.stop();
                    startTime = -1;
                    
                    leftBar.setProgress(game.leftFortressPercentage());
                    rightBar.setProgress(game.rightFortressPercentage());
                    
                    // Game over check.
                    if(game.getState() == 1){
                        
                        int leftFortressPixels = game.leftFortressPixels();
                        int rightFortressPixels = game.rightFortressPixels();

                        if (!(leftFortressPixels > 0 && rightFortressPixels > 0)) {  // Is Game still in progress?
                            if (leftFortressPixels == 0 && rightFortressPixels == 0) {  // TIE!
                                leftPlayer.addTie();
                                rightPlayer.addTie();
                            } else if (leftFortressPixels == 0) {  // Right player won!
                                leftPlayer.addLoss();
                                rightPlayer.addWin();
                            } else {  // Left player won!
                                leftPlayer.addWin();
                                rightPlayer.addLoss();
                            }
                            
                            try{
                                playerDao.update(leftPlayer);
                                playerDao.update(rightPlayer);
                            } catch(Exception e){
                                //System.out.println("ERROR1");// ERROR?!?!?!?!?!?????????????????????
                            }
                            
                            try{
                                buildAndSetSelectionScene(stage);
                            } catch(Exception e){
                                //System.out.println("ERROR2");// ERROR?!?!?!?!?!?????????????????????
                            }
                        }
                    }
                    
                    // Coloring the player's name red who's turn it is now that this simulation is over.
                    if(game.getState() == 1){
                        leftLabel.setTextFill(Color.RED);
                        vacuumControl.setTextFill(Color.RED);
                    } else if(game.getState() == 4){
                        rightLabel.setTextFill(Color.RED);
                        vacuumControl.setTextFill(Color.RED);
                    }
                    
                }                    
                
            }
        };
        
        // Drawing of the cannon aiming vectors.         
        canvas.setOnMouseMoved((event) -> {
            if(game.getState() == 1){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(level.getLeftCannonX(), 700 - level.getLeftCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform JA tykin sijainti levelistä!!!! 
            
                // Aiming info:
                int xDifference = (int)event.getX() - level.getLeftCannonX();
                int yDifference = 700 - (int)event.getY() - level.getLeftCannonY();
                angleOfAimLeft.setText("Cannon angle: " + (int)((Math.atan2(yDifference, xDifference) / (2*Math.PI)) * 360) + "°");
                amountOfAimLeft.setText("Muzzle velocity: " + (int)Math.hypot(xDifference, yDifference));
            } else if(game.getState() == 4){
                Image image = SwingFXUtils.toFXImage(game.getStaticSnapshot(), null);
                pen.drawImage(image, 0, 0);
                
                pen.setStroke(Color.BLACK);
                // 1 x y  2 x y
                pen.strokeLine(level.getRightCannonX(), 700 - level.getRightCannonY(), event.getX(), event.getY());  // 600 - 238:hin joku yTransform.
                
                // Aiming info:
                int xDifference = (int)event.getX() - level.getRightCannonX();
                int yDifference = 700 - (int)event.getY() - level.getRightCannonY();
                double angle = (Math.atan2(yDifference, xDifference) / (2*Math.PI)) * 360;
                if(0 <= angle){
                    angle = 180 - angle;
                } else {
                    angle = -180 - angle;
                }
                angleOfAimRight.setText("Cannon angle: " + (int)angle + "°");
                amountOfAimRight.setText("Muzzle velocity: " + (int)Math.hypot(xDifference, yDifference));
            }  
        });
        
        // Firing of the cannons.
        canvas.setOnMouseClicked((event) -> {
            if(game.getState() == 1){
                game.setAndFireCannon((int)event.getX() - level.getLeftCannonX(), 700 - (int)event.getY() - level.getLeftCannonY());  // Tähänkin jotkut hienot transformit.
                leftLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                vacuumControl.setTextFill(Color.BLACK);
                
                // For the Vacuum chamber level.
                if (level.isVacuumPossible()) {
                    leftPrevious[0] = (int)event.getX() - level.getLeftCannonX();
                    leftPrevious[1] = 700 - (int)event.getY() - level.getLeftCannonY();
                    leftPrevious[2] = 1; 
                }
                
                simulation.start();
            } else if(game.getState() == 4){
                game.setAndFireCannon((int)event.getX() - level.getRightCannonX(), 700 - (int)event.getY() - level.getRightCannonY());  // Tähänkin jotkut hienot transformit.
                rightLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                vacuumControl.setTextFill(Color.BLACK);
                
                // For the Vacuum chamber level.
                if (level.isVacuumPossible()) {
                    rightPrevious[0] = (int)event.getX() - level.getRightCannonX();
                    rightPrevious[1] = 700 - (int)event.getY() - level.getRightCannonY();
                    rightPrevious[2] = 1; 
                }
                
                simulation.start();
            }
        });
        
        if (level.isVacuumPossible()) {
            fireWithPrevious.setOnAction((event) -> {  // voi GameServicessa varmaan refakata seOnMouseClicked kanssa.
                if(game.getState() == 1 && leftPrevious[2] != 0){
                    game.setAndFireCannon(leftPrevious[0], leftPrevious[1]);
                    leftLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                    vacuumControl.setTextFill(Color.BLACK);
                    simulation.start();
                } else if(game.getState() == 4 && rightPrevious[2] != 0){
                    game.setAndFireCannon(rightPrevious[0], rightPrevious[1]);
                    rightLabel.setTextFill(Color.BLACK);  // Player's turn is over so the name is colored back to black.
                    vacuumControl.setTextFill(Color.BLACK);
                    simulation.start();
                }
            });
        }
        
        Scene gameScene = new Scene(vbox);
        stage.setScene(gameScene);
    }
    
    private void buildAndSetNameEntryScene(Stage stage, String levelName, String errorMessage) {
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
                level = levelDao.findOne(levelName);
            } catch(Exception e){
                //System.out.println("Error while loading the level!");  // MUUTA GRAAFISEKS?!?!?!?!?!
            }
            Game game = new Game(level);
            
            
            String leftPlayerNickname = leftSideNickname.getText();
            String rightPlayerNickname = rightSideNickname.getText(); 
            if (leftPlayerNickname.equals(rightPlayerNickname)) {
                buildAndSetNameEntryScene(stage, levelName, "The nicknames can not be the same!");
                return;
            }
            
            Player leftPlayer = null;
            Player rightPlayer = null;
            try{
                leftPlayer = playerDao.findOne(leftPlayerNickname);
                rightPlayer = playerDao.findOne(rightPlayerNickname);
                
                if(leftPlayer == null){
                    leftPlayer = new Player(leftPlayerNickname, 0, 0, 0);
                    playerDao.add(leftPlayer);
                }
                if(rightPlayer == null){
                    rightPlayer = new Player(rightPlayerNickname, 0, 0, 0);
                    playerDao.add(rightPlayer);
                }
                
            } catch(Exception e){   
            }
            
            buildAndSetGameScene(stage, game, level, leftPlayer, rightPlayer);
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
        stage.setScene(nameEntryScene);
    }
    
    private void buildAndSetSelectionScene(Stage stage) throws SQLException, IOException{
        
        VBox vboxLevels = new VBox();
//        vboxLevels.setPrefSize(300, 180);
//        vboxLevels.setSpacing(10);
//        vboxLevels.setPadding(new Insets(30, 30, 30, 30));
        
        ArrayList<Level> levels = levelDao.listAll();
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
                buildAndSetNameEntryScene(stage, level.getName(), null);
            });
            
            vboxLevels.getChildren().add(thumbnailView);
        }
        
        
        
        File starFile = new File("Graphics/star.png");
        FileInputStream inputstream = new FileInputStream(starFile); 
        Image star = new Image(inputstream);
        ImageView starView = new ImageView(star);
        
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
        
        ArrayList<Player> players = playerDao.findWinners();  
        for(Player player : players){
            vboxWinners.getChildren().add(new Label("  " + player.getWins() + "  " + player.getName()));
        }
        
        
        HBox hbox = new HBox();
        hbox.getChildren().add(vboxLevels);
        hbox.getChildren().add(vboxWinners);
        
        
        
        
        File logoFile = new File("Graphics/logo.png");
        FileInputStream inputstream2 = new FileInputStream(logoFile); 
        Image logo = new Image(inputstream2);
        ImageView logoView = new ImageView(logo);
        
        
        
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: #74ecf2; -fx-alignment: center;");
        mainBox.getChildren().add(logoView);
        mainBox.getChildren().add(hbox);
        
        Scene selectionScene = new Scene(mainBox);
        stage.setScene(selectionScene);
    }
    
    @Override
    public void start(Stage stage) throws SQLException, IOException{
        buildAndSetSelectionScene(stage);
        
//        File logoFile = new File("Graphics/logo.png");
//        FileInputStream inputstream = new FileInputStream(logoFile); 
//        Image logo = new Image(inputstream);

       stage.getIcons().addAll(
               new Image("file:Graphics/icon16.png"), 
               new Image("file:Graphics/icon24.png"), 
               new Image("file:Graphics/icon32.png"), 
               new Image("file:Graphics/icon48.png"), 
               new Image("file:Graphics/icon64.png"), 
               new Image("file:Graphics/icon256.png"));
       stage.setTitle("Fortress Duel");
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(Main.class);
    }
    
}
