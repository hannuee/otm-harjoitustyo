/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.database;

import otmharjoitustyo.domain.Level;

import java.util.ArrayList;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelDao {
    
    private final Database database;
    
    public LevelDao(Database database) {
        this.database = database;
    }
   
    public Level findOne(String name) throws SQLException, IOException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Level WHERE name = ?");
        statement.setString(1, name);
        
        ResultSet result = statement.executeQuery();
        
        if (result.next()) {
            
            BufferedImage gameField = ImageIO.read(result.getBinaryStream("gamefield"));
            
            result.close();
            statement.close();
            connection.close();
            
            return new Level(name, gameField);
            
        } else {
            
            result.close();
            statement.close();
            connection.close();
            
            return null;
        }
    }
    
    // return a list with only names.
    public ArrayList<Level> listAll() throws SQLException, IOException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Level");
        
        ResultSet result = statement.executeQuery();
        
        ArrayList<Level> levels = new ArrayList<>();
        
        while (result.next()) {
            String name = result.getString("name");
            
            levels.add(new Level(name, null));
        } 
            
        result.close();
        statement.close();
        connection.close();
        
        return levels;
    }
    
}
