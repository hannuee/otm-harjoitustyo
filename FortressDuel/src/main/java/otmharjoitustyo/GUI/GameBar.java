/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.GUI;

import otmharjoitustyo.logic.GameService;
import otmharjoitustyo.GUI.SelectionScene;
import otmharjoitustyo.GUI.Main;

import javafx.scene.input.MouseEvent;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameBar {
    private Main main;
    private GameService gameService;
    private AnimationTimer simulation;
    
    private static final String AIM_ANGLE_TITLE = "Cannon angle: ";
    private static final String AIM_AMOUNT_TITLE = "Muzzle velocity: ";
    
    private Label leftLabel;
    private ProgressBar leftBar;
    private Label angleOfAimLeft;
    private Label amountOfAimLeft;
    
    private Label rightLabel;
    private ProgressBar rightBar;
    private Label angleOfAimRight;
    private Label amountOfAimRight;  
    
    private Button vacuumControl;
    
    private HBox gameBarBox;
    
    public GameBar(Main main, GameService gameService){
        this.main = main;
        this.gameService = gameService;
        
        VBox leftBox = initializeLeftPlayerInformationBox();
        VBox centerBox = initializeCenterBox();
        VBox rightBox = initializeRightPlayerInformationBox();
        
        gameBarBox = new HBox();
        gameBarBox.getChildren().addAll(leftBox, centerBox, rightBox);
    }
    
    private VBox initializeLeftPlayerInformationBox() {
        leftLabel = new Label(gameService.getLeftPlayerName());
        leftBar = new ProgressBar(gameService.getLeftFortressPercentage());
        angleOfAimLeft = new Label(AIM_ANGLE_TITLE);
        amountOfAimLeft = new Label(AIM_AMOUNT_TITLE);
        
        VBox leftBox = new VBox();
        leftBox.setPrefSize((int)(gameService.getGameFieldWidth()*0.333), 70);
        leftBox.setStyle("-fx-alignment: center;");
        leftBox.setPadding(new Insets(30, 30, 30, 30));
        leftBox.getChildren().addAll(leftLabel, leftBar, angleOfAimLeft, amountOfAimLeft);
        
        return leftBox;
    }
    
    private VBox initializeRightPlayerInformationBox() {
        rightLabel = new Label(gameService.getRightPlayerName());
        rightBar = new ProgressBar(gameService.getRightFortressPercentage());
        angleOfAimRight = new Label(AIM_ANGLE_TITLE);
        amountOfAimRight = new Label(AIM_AMOUNT_TITLE);   
        
        VBox rightBox = new VBox();
        rightBox.setPrefSize((int)(gameService.getGameFieldWidth()*0.333), 70);
        rightBox.setStyle("-fx-alignment: center;");
        rightBox.setPadding(new Insets(30, 30, 30, 30));
        rightBox.getChildren().addAll(rightLabel, rightBar, angleOfAimRight, amountOfAimRight);

        return rightBox;
    }
    
    private void initializeVacuumControlButton() {
        vacuumControl = new Button("Suck all the air out!");
        if (gameService.isVacuumPossibleInThisLevel()) {
            vacuumControl.setOnAction((event) -> {
                if (gameService.changeVacuumStateIfPossible()) {
                    vacuumControl.setText("Let air back in!");
                } else {
                    vacuumControl.setText("Suck all the air out!");
                }
            });
        }        
    }
    
    private Button initializeFireWithPreviousButton() {
        Button fireWithPrevious = new Button("Fire cannon with previous settings");
        if (gameService.isVacuumPossibleInThisLevel()) {
            fireWithPrevious.setOnAction((event) -> {
                if (gameService.fireCannonWithPreviousSettingsIfPossible()) {
                    indicateNoTurn();
                    simulation.start();
                }
            });
        }   
        return fireWithPrevious;
    }
    
    private Button initializeExitButton() {
        Button exitButton = new Button("Exit game");
        exitButton.setOnAction((event) -> {
            simulation.stop();
            new SelectionScene(main, gameService);
        });
        return exitButton;
    }
    
    private VBox initializeCenterBox() {
        // Center buttons
        initializeVacuumControlButton();
        Button fireWithPrevious = initializeFireWithPreviousButton();
        Button exitButton = initializeExitButton();
        
        VBox centerBox = new VBox();
        centerBox.setPrefSize((int)(gameService.getGameFieldWidth()*0.333), 70);
        centerBox.setStyle("-fx-alignment: center;");
        centerBox.setSpacing(10);
        centerBox.setPadding(new Insets(30, 30, 30, 30));
        if(gameService.isVacuumPossibleInThisLevel()){
            centerBox.getChildren().addAll(vacuumControl, fireWithPrevious);
        }
        centerBox.getChildren().add(exitButton);
        
        return centerBox;
    }
    
    public void setAnimationTimer(AnimationTimer simulation){
        this.simulation = simulation;
    }
    
    public HBox getGameBarElement(){
        return gameBarBox;
    }
    
    
    public void indicateNoTurn(){
        leftLabel.setTextFill(Color.BLACK);
        rightLabel.setTextFill(Color.BLACK);
    }
    
    public void indicateTurn(){
        if(gameService.getGameState() == 1){
            leftLabel.setTextFill(Color.RED);
        } else if(gameService.getGameState() == 4){
            rightLabel.setTextFill(Color.RED);
        }
    }
    
    public void updateProgressBars(){
        leftBar.setProgress(gameService.getLeftFortressPercentage());
        rightBar.setProgress(gameService.getRightFortressPercentage());
    }
    
    public void updateLeftAimingInfo(MouseEvent event) {
        int xDifference = (int)event.getX() - gameService.getLeftCannonX();
        int yDifference = gameService.getGameFieldHeight() - (int)event.getY() - gameService.getLeftCannonY();  // TRANSFORM TÄHÄN?????
        angleOfAimLeft.setText(AIM_ANGLE_TITLE + (int)((Math.atan2(yDifference, xDifference) / (2*Math.PI)) * 360) + "°");
        amountOfAimLeft.setText(AIM_AMOUNT_TITLE + (int)Math.hypot(xDifference, yDifference));
    }
    
    public void updateRightAimingInfo(MouseEvent event) {
        int xDifference = (int)event.getX() - gameService.getRightCannonX();
        int yDifference = gameService.getGameFieldHeight() - (int)event.getY() - gameService.getRightCannonY();
        double angle = (Math.atan2(yDifference, xDifference) / (2*Math.PI)) * 360;
        if(0 <= angle){
            angle = 180 - angle;
        } else {
            angle = -180 - angle;
        }
        angleOfAimRight.setText(AIM_ANGLE_TITLE + (int)angle + "°");
        amountOfAimRight.setText(AIM_AMOUNT_TITLE + (int)Math.hypot(xDifference, yDifference));
    }
    
}
