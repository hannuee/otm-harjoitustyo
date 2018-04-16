/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.database;

import otmharjoitustyo.domain.Player;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDao {
    
    private final Database database;
    
    public PlayerDao(Database database) {
        this.database = database;
    }
    
    public Player findOne(String name) throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Player WHERE name = ?");
        statement.setString(1, name);
        
        ResultSet result = statement.executeQuery();
        
        if (result.next()) {
            int wins = result.getInt("wins");
            int ties = result.getInt("ties");
            int losses = result.getInt("losses");
            
            result.close();
            statement.close();
            connection.close();
            
            return new Player(name, wins, ties, losses);
            
        } else {
            
            result.close();
            statement.close();
            connection.close();
            
            return null;
        }
    }
    
    public ArrayList<Player> findAll() throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Player");
        
        ResultSet result = statement.executeQuery();
        
        ArrayList<Player> players = new ArrayList<>();
        
        while (result.next()) {
            String name = result.getString("name");
            int wins = result.getInt("wins");
            int ties = result.getInt("ties");
            int losses = result.getInt("losses");
            
            players.add(new Player(name, wins, ties, losses));
        } 
            
        result.close();
        statement.close();
        connection.close();
        
        return players;
    }
    
    public void add(Player player) throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Player(name, wins, ties, losses) values (?, ?, ?, ?)");
        statement.setString(1, player.getName());
        statement.setInt(2, player.getWins());
        statement.setInt(3, player.getTies());
        statement.setInt(4, player.getLosses());
        
        statement.executeUpdate();
        
        statement.close();
        connection.close();
    }
    
    public void update(Player player) throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE Player SET wins = ?, ties = ?, losses = ? WHERE name = ?");
        statement.setInt(1, player.getWins());
        statement.setInt(2, player.getTies());
        statement.setInt(3, player.getLosses());
        statement.setString(4, player.getName());
        
        statement.executeUpdate();
        
        statement.close();
        connection.close();
    }
    
}

