/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

import otmharjoitustyo.domain.ImageOperations;
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

public class ImageOperationsTest {
    
    Level level;
    
    public ImageOperationsTest() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        level = levelDao.findOne("Meadow");
    }
    
    @Test
    public void yellowBuddieCounterCountsZero() {
        assertEquals(0, ImageOperations.countYellowBuddies(level.getGameField(), 10, 600));
    }
    
}
