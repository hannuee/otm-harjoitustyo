/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

public class Player {
    
    private int id;
    private String name;
    private int wins;
    private int ties;
    private int losses;
    
    public Player(int id, String name, int wins, int ties, int losses){
        this.id = id;
        this.name = name;
        this.wins = wins;
        this.ties = ties;
        this.losses = losses;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getWins(){
        return this.wins;
    }
    
    public int getTies(){
        return this.ties;
    }
    
    public int getLosses(){
        return this.losses;
    }
    
}
