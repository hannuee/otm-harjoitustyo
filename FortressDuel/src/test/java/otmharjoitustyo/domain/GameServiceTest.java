/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

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
import otmharjoitustyo.domain.GameService;

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
        gameService.initializeGame("Vacuum Chamber");
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
    public void indenticalNamesAreNoticedToBeIdenticalAfterInitialization() throws SQLException, IOException {
        // Test #3 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        gameServiceLocal.initializeGame("Mountains");
        assertTrue(gameServiceLocal.takeNamesAndCheckIfTheyAreTheSame("Bob", "Bob"));
    }
    
    @Test
    public void nonIndenticalNamesAreNoticedToBeNonIdenticalAfterInitialization() throws SQLException, IOException {
        // Test #4 of 4 where local GameService is used instead of setUp GameService
        // in order to achieve realistic conditions.
        GameService gameServiceLocal = new GameService(levelDao, playerDao);
        gameServiceLocal.initializeGame("Mountains");
        assertFalse(gameServiceLocal.takeNamesAndCheckIfTheyAreTheSame("Emma", "Annie"));
    }
    
    
    // Game scene AND GameBar:  (getSimulationSnapshot(double seconds) method is tested indirectly through other test methods.)

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
        assertTrue(gameService.isVacuumPossibleInThisLevel());
    }
    
    // CUSTOM JOSSA TESTATAAN FALSE^^^^!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    @Test
    public void gameStateIndicatesThatItsTurnOfTheLeftPlayer() {
        assertEquals(1, gameService.getGameState());
    }
    
    @Test
    public void cannonCanNotBeFiredWithPreviousSettingsYet() {
        assertFalse(gameService.fireCannonWithPreviousSettingsIfPossible());
    }
    
    @Test
    public void leftPlayerCanFireCannonWithPreviousSettingsAfterOneOwnShot() {
        assertTrue(gameService.fireCannonIfPossible(600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonIfPossible(-600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonWithPreviousSettingsIfPossible());
    }
    
    @Test
    public void rightPlayerCanFireCannonWithPreviousSettingsAfterOneOwnShot() {
        assertTrue(gameService.fireCannonIfPossible(600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonIfPossible(-600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonIfPossible(-600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonWithPreviousSettingsIfPossible());
    }
    
    @Test
    public void cannonCanBeFired() {
        assertTrue(gameService.fireCannonIfPossible(600, 350));
    }
    
    @Test
    public void cannonCanNotBeFiredBecauseSimulationHasNotEnded() {
        assertTrue(gameService.fireCannonIfPossible(600, 350));
        assertFalse(gameService.fireCannonIfPossible(600, 350));
    }
    
    @Test
    public void cannonCanBeFiredAfterSimulationHasEnded() {
        assertTrue(gameService.fireCannonIfPossible(600, 350));
        assertNotNull(gameService.getSimulationSnapshot(20.0));
        assertTrue(gameService.fireCannonIfPossible(-600, 350));
    }
    
    @Test
    public void staticSnapshotWorks() {
        assertNotNull(gameService.getStaticSnapshot());
    }
    
    @Test
    public void theGameIsNotOverInTheBeginning() throws SQLException {
        assertFalse(gameService.isGameOver());
    }
    
    @Test
    public void vacuumStateCanBeChangedONandOFF() {
        assertTrue(gameService.changeVacuumStateIfPossible());
        assertFalse(gameService.changeVacuumStateIfPossible());
    }

    @Test
    public void leftCannonXisCorrect() {
        assertEquals(194, gameService.getLeftCannonX());
    }
    
    @Test
    public void leftCannonYisCorrect() {
        assertEquals(153, gameService.getLeftCannonY());
    }
    
    @Test
    public void rightCannonXisCorrect() {
        assertEquals(1009, gameService.getRightCannonX());
    }
    
    @Test
    public void rightCannonYisCorrect() {
        assertEquals(153, gameService.getRightCannonY());
    }
    
}
