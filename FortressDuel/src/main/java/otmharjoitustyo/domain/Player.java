/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

public class Player {
    
    private String name;
    private int wins;
    private int ties;
    private int losses;
    
    public Player(String name, int wins, int ties, int losses){
        this.name = name;
        this.wins = wins;
        this.ties = ties;
        this.losses = losses;
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
    
    public void addWin(){
        ++this.wins;
    }
    
    public void addTie(){
        ++this.ties;
    }
    
    public void addLoss(){
        ++this.losses;
    }
    
}
