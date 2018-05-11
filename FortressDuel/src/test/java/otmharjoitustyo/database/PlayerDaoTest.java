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
import otmharjoitustyo.domain.Player;

public class PlayerDaoTest {
    
    @Test
    public void databaseReturnsCorrectPlayer() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        PlayerDao playerDao = new PlayerDao(database);
        
        Player player = playerDao.findOne("Jack");
        assertEquals("Jack", player.getName());
    }
    
    @Test
    public void databaseReturnsWinners() throws SQLException, IOException {
        Database database = new Database("jdbc:sqlite:Gamedata.db");
        PlayerDao playerDao = new PlayerDao(database);
        
        ArrayList<Player> players = playerDao.findWinners();
        assertEquals(5, players.size());
    }
    
}
