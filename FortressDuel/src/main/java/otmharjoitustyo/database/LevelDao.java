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
            
            BufferedImage gameField = ImageIO.read(result.getBinaryStream("gameField"));
            BufferedImage background = ImageIO.read(result.getBinaryStream("background"));
            
            int leftCannonX = result.getInt("leftCannonX");
            int leftCannonY = result.getInt("leftCannonY");
            int rightCannonX = result.getInt("rightCannonX"); 
            int rightCannonY = result.getInt("rightCannonY"); 

            int leftFortressMaxX = result.getInt("leftFortressMaxX"); 
            int leftFortressMinX = result.getInt("leftFortressMinX"); 
            int leftFortressMaxY = result.getInt("leftFortressMaxY"); 
            int leftFortressMinY = result.getInt("leftFortressMinY"); 

            int rightFortressMaxX = result.getInt("rightFortressMaxX"); 
            int rightFortressMinX = result.getInt("rightFortressMinX"); 
            int rightFortressMaxY = result.getInt("rightFortressMaxY"); 
            int rightFortressMinY = result.getInt("rightFortressMinY"); 

            boolean vacuumPossible = result.getBoolean("vacuumPossible");
            int ammunitionMaxY = result.getInt("ammunitionMaxY"); 
            int ammunitionMinY = result.getInt("ammunitionMinY");
            
            result.close();
            statement.close();
            connection.close();
            
            return new Level(name, gameField, background, 
                 leftCannonX, leftCannonY, rightCannonX, rightCannonY, 
                 leftFortressMaxX, leftFortressMinX, leftFortressMaxY, leftFortressMinY, 
                 rightFortressMaxX, rightFortressMinX, rightFortressMaxY, rightFortressMinY, 
                 vacuumPossible, ammunitionMaxY, ammunitionMinY);
            
        } else {
            
            result.close();
            statement.close();
            connection.close();
            
            return null;
        }
    }
    
    // returns a list of level names.
    public ArrayList<String> listAll() throws SQLException, IOException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT name FROM Level");
        
        ResultSet result = statement.executeQuery();
        
        ArrayList<String> levelNames = new ArrayList<>();
        
        while (result.next()) {
            String name = result.getString("name");
            
            levelNames.add(name);
        } 
            
        result.close();
        statement.close();
        connection.close();
        
        return levelNames;
    }
    
}
