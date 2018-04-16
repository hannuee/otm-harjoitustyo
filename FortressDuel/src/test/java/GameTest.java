/**
 * @author Hannu Erälaukko
 */

import otmharjoitustyo.logic.*;
import otmharjoitustyo.domain.*;
import otmharjoitustyo.database.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {
    
    // Database:
    
    @Test
    public void databaseReturnsLevel() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        Level level = levelDao.findOne("basic");
        
        assertNotNull(level);
    }
    
    
    // Initialization of the game:
    
    @Test
    public void stateIsCorrectInTheBeginning() {
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 100, 200, 700, 200);
        assertEquals(1, game.getState());
    }
    
    @Test
    public void staticSnapshotWorksInTheBeginning() {
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 100, 200, 700, 200);
        BufferedImage snapshot = game.getStaticSnapshot();
        assertNotNull(snapshot);
    }
    
    @Test
    public void gameResultIsNullIfAskedAfterInitialization() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        Level level = levelDao.findOne("basic");
        
        Game game = new Game(level.getGameField(), 100, 200, 700, 200);
        assertNull(game.checkWinner());
    }
    
    
    // Firing of the cannon and ammunition trajectory simulation:
    // (Gamefield is considered to be located on a normal cartesian coordinate plane.
    //  The bottom left corner of the gamefield is in the origin of the coordinate plane.)
    
    @Test
    public void firingOfTheCannonChangesGameState() {
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 100, 200, 700, 200);
        game.setAndFireCannon(50, 50);
        assertEquals(2, game.getState());
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredStraightUpWithAirDrag() {
        // Left cannon located in (x,y) = (0,0).
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 0, 0, 700, 200);
        // Ammunition's initial Vx and Vy = 0 and 50.
        game.setAndFireCannon(0, 50);
        
        int x = game.ammunitionXwithDrag(2.0);
        int y = game.ammunitionYwithDrag(2.0);
        
        assertTrue("X-coordinate fails.", x == 0);
        assertTrue("Y-coordinate fails.", 0 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredUpperRightWithAirDrag() {
        // Left cannon located in (x,y) = (0,0).
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 0, 0, 700, 200);
        // Ammunition's initial Vx and Vy = 50 and 50.
        game.setAndFireCannon(50, 50);
        
        int x = game.ammunitionXwithDrag(2.0);
        int y = game.ammunitionYwithDrag(2.0);
        
        assertTrue("X-coordinate fails.", 0 < x);
        assertTrue("Y-coordinate fails.", 0 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredStraightUpInVacuum() {
        // Left cannon located in (x,y) = (0,0).
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 0, 0, 700, 200);
        // Ammunition's initial Vx and Vy = 0 and 50.
        game.setAndFireCannon(0, 50);
        
        int x = game.ammunitionX(2.0);
        int y = game.ammunitionY(2.0);
        
        assertTrue("X-coordinate fails.", x == 0);
        assertTrue("Y-coordinate fails.", 0 < y);
    }
    
    @Test
    public void trajectorySimulationWhenCannonFiredUpperRightInVacuum() {
        // Left cannon located in (x,y) = (0,0).
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 0, 0, 700, 200);
        // Ammunition's initial Vx and Vy = 50 and 50.
        game.setAndFireCannon(50, 50);
        
        int x = game.ammunitionX(2.0);
        int y = game.ammunitionY(2.0);
        
        assertTrue("X-coordinate fails.", 0 < x);
        assertTrue("Y-coordinate fails.", 0 < y);
    }
    
    
    // Game cycle:
    
    @Test
    public void simulationSnapshotIsGivenWhenAmmunitionIsWithinTheGameField() {
        // Left cannon located in (x,y) = (0,0).
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 0, 0, 700, 200);
        // Ammunition's initial Vx and Vy = 50 and 50.
        game.setAndFireCannon(50, 50);
        
        BufferedImage simulationSnapshot = game.getSimulationSnapshot(2.0);
        assertNotNull(simulationSnapshot);
    }
    
}
