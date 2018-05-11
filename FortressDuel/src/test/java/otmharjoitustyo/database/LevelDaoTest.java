/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import otmharjoitustyo.domain.Level;

public class LevelDaoTest {
    
    public LevelDaoTest() {
    }
    
    @Test
    public void databaseReturnsLevel() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        Level level = levelDao.findOne("Meadow");
        
        assertNotNull(level);
    }
    
    @Test
    public void databaseListsAllLevels() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        LevelDao levelDao = new LevelDao(database);
        
        ArrayList<Level> levels = levelDao.listAll();
        
        assertEquals(3, levels.size());
    }
    
}
