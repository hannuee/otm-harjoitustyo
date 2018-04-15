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
    
    public PlayerDao(Database database){
        this.database = database;
    }
    
    public ArrayList<Player> findAll() throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Player");
        
        ResultSet result = statement.executeQuery();
        
        ArrayList<Player> players = new ArrayList<>();
        
        while(result.next()){
            int id = result.getInt("id");
            String name = result.getString("name");
            int wins = result.getInt("wins");
            int ties = result.getInt("ties");
            int losses = result.getInt("losses");
            
            players.add(new Player(id, name, wins, ties, losses));
        } 
            
        result.close();
        statement.close();
        connection.close();
        
        return players;
    }
    
    public boolean add(Player player) throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Player(name, wins, ties, losses) values (?, ?, ?, ?) RETURNING id");
        statement.setString(1, player.getName());
        statement.setInt(2, player.getWins());
        statement.setInt(3, player.getTies());
        statement.setInt(4, player.getLosses());
        
        ResultSet result = statement.executeQuery();
        
        if(result.next()){
            int id = result.getInt("id");
            player.setId(id);
            
            result.close();
            statement.close();
            connection.close();
            
            return true;

        } else {
            result.close();
            statement.close();
            connection.close();
            
            return false;
        }
    }
    
    public boolean update(Player player) throws SQLException {
        Connection connection = this.database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE Player SET name = ?, wins = ?, ties = ?, losses = ? WHERE id = ?");
        statement.setString(1, player.getName());
        statement.setInt(2, player.getWins());
        statement.setInt(3, player.getTies());
        statement.setInt(4, player.getLosses());
        statement.setInt(5, player.getId());
        
        statement.executeUpdate();
        
        statement.close();
        connection.close();
        
        return true;
    }
    
}

