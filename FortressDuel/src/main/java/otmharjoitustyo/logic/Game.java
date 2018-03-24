/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

public class Game {
    
    int[][] situation;  // private? vai public jotta ei tuu turhia kopiointeja?
    boolean simulationInProgress; // private!
    boolean leftTurn;
   
    int leftCannonX;  // private final?
    int leftCannonY;
    int rightCannonX;
    int rightCannonY;
    
    double initialVx;
    double initialVy;
    
    // Tarkastetaan törmäykset maahan rajoista?
    
    // Törmäykset linnaan erikseen?
    
    
    private int ammunitionY(double seconds){
        double positionDueGravity = (-9.81/2.0) * Math.pow(seconds, 2);
        double positionDueInitialVy = this.initialVy * seconds;
        
        int positionDueCannonPosition;
        if(leftTurn){
            positionDueCannonPosition = this.leftCannonY;
        } else {
            positionDueCannonPosition = this.rightCannonY;
        }
        
        return (int)(positionDueGravity + positionDueInitialVy) + positionDueCannonPosition;
    }
    
    private int ammunitionX(double seconds){
        double positionDueInitialVx = this.initialVx * seconds;
        
        int positionDueCannonPosition;
        if(leftTurn){
            positionDueCannonPosition = this.leftCannonX;
        } else {
            positionDueCannonPosition = this.rightCannonX;
        }
        
        return (int)positionDueInitialVx + positionDueCannonPosition;
    }
    
}
