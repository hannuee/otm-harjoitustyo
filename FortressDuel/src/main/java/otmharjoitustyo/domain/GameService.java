/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

import otmharjoitustyo.database.LevelDao;
import otmharjoitustyo.database.PlayerDao;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;


public class GameService {
    
    private LevelDao levelDao;
    private PlayerDao playerDao;
    
    private Game game;
    private Level level;
    private Player leftPlayer;
    private Player rightPlayer;
    
    private String leftPlayerName;
    private String rightPlayerName;
    
    private int[] leftPrevious;
    private int[] rightPrevious;
    
    public GameService(LevelDao levelDao, PlayerDao playerDao) {
        this.levelDao = levelDao;
        this.playerDao = playerDao;
    }

    
    // Selection scene:
    
    public ArrayList<Level> listAllLevels() throws SQLException, IOException {
        return levelDao.listAll();
    }
    
    public ArrayList<Player> listTOP5winners() throws SQLException {
        return playerDao.findWinners();
    }
    
    
    // Name entry scene:
    
    public void initializeGame(String levelName) throws SQLException, IOException {
        level = this.levelDao.findOne(levelName);
        
        game = new Game(level);
        
        leftPrevious = new int[]{0,0,0};  // The last member indicates if the cannon has been fired previously.
        rightPrevious = new int[]{0,0,0};
    }
    
    public boolean takeNamesAndCheckIfTheyAreTheSame(String leftPlayerName, String rightPlayerName) {
        this.leftPlayerName = leftPlayerName;
        this.rightPlayerName = rightPlayerName;
        return leftPlayerName.equals(rightPlayerName);
    }
    
    public void initializePlayers() throws SQLException {
        leftPlayer = playerDao.findOne(leftPlayerName);
        rightPlayer = playerDao.findOne(rightPlayerName);
        
        if(leftPlayer == null){
            leftPlayer = new Player(leftPlayerName, 0, 0, 0);
            playerDao.add(leftPlayer);
        }
        if(rightPlayer == null){
            rightPlayer = new Player(rightPlayerName, 0, 0, 0);
            playerDao.add(rightPlayer);
        }        
    }
    
    
    // Game scene AND GameBar:

    public String getLeftPlayerName() {
        return leftPlayerName;
    }

    public String getRightPlayerName() {
        return rightPlayerName;
    }
    
    public double getLeftFortressPercentage() {
        return game.leftFortressPercentage();
    }
    
    public double getRightFortressPercentage() {
        return game.rightFortressPercentage();
    }
    
    public int getGameFieldWidth() {
        return level.getGameField().getWidth();   
    } 
    
    public int getGameFieldHeight() {
        return level.getGameField().getHeight();   
    }
    
    public boolean isVacuumPossibleInThisLevel() {
        return level.isVacuumPossible();
    }
    
    public int getGameState() {    
        return game.getState();
    }
    
    public BufferedImage getSimulationSnapshot(double simulationTime) {
        return game.getSimulationSnapshot(simulationTime);
    }
    
    public BufferedImage getStaticSnapshot() {
        return game.getStaticSnapshot();
    }
    
    public boolean isGameOver() throws SQLException {
        if (game.getState() == 1) {  // The game can only end when there has been even amount of turns.

            int leftFortressPixels = game.leftFortressPixels();
            int rightFortressPixels = game.rightFortressPixels();

            // Check that at least one of the fortresses is finished, meaning the game is over.
            if (leftFortressPixels == 0 || rightFortressPixels == 0) {
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

                playerDao.update(leftPlayer);
                playerDao.update(rightPlayer);

                return true;
            }
        }
     
        return false;  // The game is not over yet.
    }
    
    public boolean changeVacuumStateIfPossible() {
        if (game.getState() == 1 || game.getState() == 4) {  // Can not be changed while simulation is in progress.
            if (game.getVacuum()) {
                game.setVacuum(false);
            } else {
                game.setVacuum(true);
            }
        }
        return game.getVacuum();
    }
    
    public boolean fireCannonWithPreviousSettingsIfPossible() {
        if(game.getState() == 1 && leftPrevious[2] != 0){ 
            game.setAndFireCannon(leftPrevious[0], leftPrevious[1]);
            return true;
        } else if(game.getState() == 4 && rightPrevious[2] != 0){
            game.setAndFireCannon(rightPrevious[0], rightPrevious[1]);
            return true;
        }
        return false;
    }
    
    public boolean fireCannonIfPossible(int mouseX, int mouseY) {
        if (game.getState() == 1) {
            int initialVx = mouseX - level.getLeftCannonX();
            int initialVy = level.getGameField().getHeight() - mouseY - level.getLeftCannonY(); 
            
            game.setAndFireCannon(initialVx, initialVy);

            // For the Vacuum chamber level, lets memorize the aiming information.
            if (level.isVacuumPossible()) {
                leftPrevious[0] = initialVx;
                leftPrevious[1] = initialVy;
                leftPrevious[2] = 1; 
            }

            return true;
        } else if (game.getState() == 4) {
            int initialVx = mouseX - level.getRightCannonX();
            int initialVy = level.getGameField().getHeight() - mouseY - level.getRightCannonY(); 
            
            game.setAndFireCannon(initialVx, initialVy);  

            // For the Vacuum chamber level, lets memorize the aiming information.
            if (level.isVacuumPossible()) {
                rightPrevious[0] = initialVx;
                rightPrevious[1] = initialVy;
                rightPrevious[2] = 1; 
            }

            return true;
        }
        return false;
    }
    
    public int getLeftCannonX() {
        return level.getLeftCannonX();
    }
    
    public int getLeftCannonY() {
        return level.getLeftCannonY();
    }
    
    public int getRightCannonX() {
        return level.getRightCannonX();
    }
    
    public int getRightCannonY() {
        return level.getRightCannonY();
    }
    
}
