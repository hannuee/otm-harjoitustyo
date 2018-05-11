/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import otmharjoitustyo.database.Database;
import otmharjoitustyo.database.LevelDao;
import otmharjoitustyo.database.PlayerDao;
import otmharjoitustyo.domain.Level;
import otmharjoitustyo.domain.Player;
import otmharjoitustyo.logic.GameService;

public class GameServiceTest {
    
    GameService gameService;
    LevelDao levelDao;
    PlayerDao playerDao;
    
    public GameServiceTest() {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        levelDao = new LevelDao(database);
        playerDao = new PlayerDao(database);
    }
    
    @Before
    public void setUp() throws SQLException, IOException {
        gameService = new GameService(levelDao, playerDao);
        gameService.initializeGame("Mountains");
        gameService.takeNamesAndCheckIfTheyAreTheSame("Emma", "Annie");
        gameService.initializePlayers();
    }
    
    
    // Selection scene:
    
    @Test
    public void allLevelsAreListed() throws SQLException, IOException {
        // Test #1 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        ArrayList<Level> allLevels = gameServiceLocal.listAllLevels();
        assertEquals(3, allLevels.size());
    }
    
    @Test
    public void TOP5winnersAreListed() throws SQLException {
        // Test #2 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        ArrayList<Player> TOP5winners = gameServiceLocal.listTOP5winners();
        assertEquals(5, TOP5winners.size());
    }
    
    
    // Name entry scene:  (initializePlayers() method is tested indirectly through setUp method.)
    
    @Test
    public void indenticalNamesAreRejectedAfterInitialization() throws SQLException, IOException {
        // Test #3 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        gameServiceLocal.initializeGame("Mountains");
        assertFalse(gameServiceLocal.takeNamesAndCheckIfTheyAreTheSame("Bob", "Bob"));
    }
    
    @Test
    public void nonIndenticalNamesAreAcceptedAfterInitialization() throws SQLException, IOException {
        // Test #4 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        gameServiceLocal.initializeGame("Mountains");
        assertTrue(gameServiceLocal.takeNamesAndCheckIfTheyAreTheSame("Emma", "Annie"));
    }
    
    
    // Game scene AND GameBar:

    @Test
    public void leftPlayerNameIsCorrect() {
        assertEquals("Emma", gameService.getLeftPlayerName());
    }
    
    @Test
    public void rightPlayerNameIsCorrect() {
        assertEquals("Annie", gameService.getRightPlayerName());
    }
    
    @Test
    public void leftFortressPercentageIsCorrectInTheBeginning() {
        assertTrue(0.9999999 < gameService.getLeftFortressPercentage());
    }
    
    @Test
    public void rightFortressPercentageIsCorrectInTheBeginning() {
        assertTrue(0.9999999 < gameService.getRightFortressPercentage());
    }
    
    @Test
    public void gameFieldWidthIsCorrect() {
        assertEquals(1200, gameService.getGameFieldWidth());
    }
    
    @Test
    public void gameFieldHeightIsCorrect() {
        assertEquals(700, gameService.getGameFieldHeight());
    }
    
    @Test
    public void vacuumInformationIsCorrect() {
        assertFalse(gameService.isVacuumPossibleInThisLevel());
    }
    
    @Test
    public void gameStateIndicatesThatItsTurnOfTheLeftPlayer() {
        assertEquals(1, gameService.getGameState());
    }
    
    @Test
    public void s() {
        
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
    
    @Test
    public void s() {
        
    }
    
    public boolean fireCannonIfPossible(int mouseX, int mouseY) {
        if (game.getState() == 1) {
            int initialVx = mouseX - level.getLeftCannonX();
            int initialVy = level.getGameField().getHeight() - mouseY - level.getLeftCannonY();  // Tähänkin jotkut hienot transformit.
            
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
            int initialVy = level.getGameField().getHeight() - mouseY - level.getRightCannonY();  // Tähänkin jotkut hienot transformit.
            
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
    
    @Test
    public void s() {
        
    }
    
    public BufferedImage getSimulationSnapshot(double simulationTime) {
        return game.getSimulationSnapshot(simulationTime);
    }
    
    @Test
    public void s() {
        
    }
    
    public BufferedImage getStaticSnapshot() {
        return game.getStaticSnapshot();
    }
    
    @Test
    public void s() {
        
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
    
    @Test
    public void s() {
        
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
    
    
    @Test
    public void s() {
        
    }
    
    public int getLeftCannonX() {
        return level.getLeftCannonX();
    }
    
    @Test
    public void s() {
        
    }
    
    public int getLeftCannonY() {
        return level.getLeftCannonY();
    }
    
    @Test
    public void s() {
        
    }
    
    public int getRightCannonX() {
        return level.getRightCannonX();
    }
    
    @Test
    public void s() {
        
    }
    
    public int getRightCannonY() {
        return level.getRightCannonY();
    }
    
    @Test
    public void s() {
        
    }
    
}
