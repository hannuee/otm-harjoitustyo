/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.database;

import otmharjoitustyo.domain.Level;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelDao {
    
    private final Database database;
    
    public LevelDao(Database database) {
        this.database = database;
    }
    
    public Level resultSetToLevel(ResultSet result) throws SQLException, IOException {
        String name = result.getString("name");
        BufferedImage gameField = ImageIO.read(result.getBinaryStream("gameField"));
        BufferedImage thumbnail = ImageIO.read(result.getBinaryStream("thumbnail"));
        BufferedImage thumbnailHover = ImageIO.read(result.getBinaryStream("thumbnailHover"));
        BufferedImage background = ImageIO.read(result.getBinaryStream("background"));

        int leftCannonX = result.getInt("leftCannonX");
        int leftCannonY = result.getInt("leftCannonY");
        int rightCannonX = result.getInt("rightCannonX"); 
        int rightCannonY = result.getInt("rightCannonY"); 

        int leftFortressMaxX = result.getInt("leftFortressMaxX"); 
        int leftFortressMinX = result.getInt("leftFortressMinX"); 
        int leftFortressMaxY = result.getInt("leftFortressMaxY"); 
        int leftFortressMinY = result.getInt("leftFortressMinY"); 

        return new Level(name, thumbnail, thumbnailHover, gameField, background, 
                 leftCannonX, leftCannonY, rightCannonX, rightCannonY, 
                 leftFortressMaxX, leftFortressMinX, leftFortressMaxY, leftFortressMinY, 
                 result.getInt("rightFortressMaxX"), result.getInt("rightFortressMinX"), result.getInt("rightFortressMaxY"), result.getInt("rightFortressMinY"), 
                 result.getBoolean("vacuumPossible"), result.getInt("ammunitionMaxY"), result.getInt("ammunitionMinY"));
    }
   
    public Level findOne(String name) throws SQLException, IOException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Level WHERE name = ?");
        statement.setString(1, name);
        
        ResultSet result = statement.executeQuery();
        
        if (result.next()) {
            
            Level resultLevel = resultSetToLevel(result);
            
            result.close();
            statement.close();
            connection.close();
            
            return resultLevel;
            
        } else {
            
            result.close();
            statement.close();
            connection.close();
            
            return null;
        }
    }
    
    // returns a list of levels with only names, thumbnails and thumbnailsHovers.
    public ArrayList<Level> listAll() throws SQLException, IOException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT name, thumbnail, thumbnailHover FROM Level");
        
        ResultSet result = statement.executeQuery();
        
        ArrayList<Level> levels = new ArrayList<>();
        
        while (result.next()) {
            String name = result.getString("name");
            BufferedImage thumbnail = ImageIO.read(result.getBinaryStream("thumbnail"));
            BufferedImage thumbnailHover = ImageIO.read(result.getBinaryStream("thumbnailHover"));
            
            levels.add(new Level(name, thumbnail, thumbnailHover));
        } 
            
        result.close();
        statement.close();
        connection.close();
        
        return levels;
    }
    
}
