/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import otmharjoitustyo.database.Database;
import otmharjoitustyo.database.LevelDao;
import otmharjoitustyo.domain.Level;

public class GameTest {
    
    Level level;
    
    public GameTest() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        level = levelDao.findOne("Meadow");
    }
    
    // Initialization of the game:
    
    @Test
    public void stateIsCorrectInTheBeginning() {
        Game game = new Game(level);
        assertEquals(1, game.getState());
    }
    
    @Test
    public void staticSnapshotWorksInTheBeginning() {
        Game game = new Game(level);
        BufferedImage snapshot = game.getStaticSnapshot();
        assertNotNull(snapshot);
    }
    
    @Test
    public void leftFortressPercentageIsCorrectInTheBeginning() throws SQLException, IOException {
        Game game = new Game(level);
        assertTrue(0.99999999 < game.leftFortressPercentage());
    }
    
    @Test
    public void rightFortressPercentageIsCorrectInTheBeginning() throws SQLException, IOException {
        Game game = new Game(level);
        assertTrue(0.99999999 < game.rightFortressPercentage());
    }
    
    // Firing of the cannon and ammunition trajectory simulation:
    // (Gamefield is considered to be located on a normal cartesian coordinate plane.
    //  The bottom left corner of the gamefield is in the origin of the coordinate plane.)
    
    @Test
    public void firingOfTheCannonChangesGameState() {
        Game game = new Game(level);
        game.setAndFireCannon(50, 50);
        assertEquals(2, game.getState());
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredStraightUpWithAirDrag() {
        // Left cannon located in (x,y) = (210,244).
        Game game = new Game(level);
        // Ammunition's initial Vx = 0 and Vy = 50.
        game.setAndFireCannon(0, 50);
        
        int x = game.ammunitionXwithDrag(2.0);
        int y = game.ammunitionYwithDrag(2.0);
        
        assertTrue("X-coordinate fails.", x == 210);
        assertTrue("Y-coordinate fails.", 244 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredUpperRightWithAirDrag() {
        // Left cannon located in (x,y) = (210,244).
        Game game = new Game(level);
        // Ammunition's initial Vx = 50 and Vy = 50.
        game.setAndFireCannon(50, 50);
        
        int x = game.ammunitionXwithDrag(2.0);
        int y = game.ammunitionYwithDrag(2.0);
        
        assertTrue("X-coordinate fails.", 210 < x);
        assertTrue("Y-coordinate fails.", 244 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredStraightUpInVacuum() {
        // Left cannon located in (x,y) = (210,244).
        Game game = new Game(level);
        // Ammunition's initial Vx = 0 and Vy = 50.
        game.setAndFireCannon(0, 50);
        
        int x = game.ammunitionX(2.0);
        int y = game.ammunitionY(2.0);
        
        assertTrue("X-coordinate fails.", x == 210);
        assertTrue("Y-coordinate fails.", 244 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredUpperRightInVacuum() {
        // Left cannon located in (x,y) = (210,244).
        Game game = new Game(level);
        // Ammunition's initial Vx = 50 and Vy = 50.
        game.setAndFireCannon(50, 50);
        
        int x = game.ammunitionX(2.0);
        int y = game.ammunitionY(2.0);
        
        assertTrue("X-coordinate fails.", 210 < x);
        assertTrue("Y-coordinate fails.", 244 < y);
    }
    
    
    // Game cycle:
    
    @Test
    public void simulationSnapshotIsGivenWhenAmmunitionIsWithinTheGameField() {
        // Left cannon located in (x,y) = (210,244).
        Game game = new Game(level);
        // Ammunition's initial Vx = 50 and Vy = 50.
        game.setAndFireCannon(50, 50);
        
        BufferedImage simulationSnapshot = game.getSimulationSnapshot(2.0);
        assertNotNull(simulationSnapshot);
    }
    
}
