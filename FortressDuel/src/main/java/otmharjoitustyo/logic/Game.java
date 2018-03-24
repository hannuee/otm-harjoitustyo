/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Game {
    
    BufferedImage gameField;
    private final int groundLevel;
    
    /*
    States:
    0 = Game over.
    1 = Left player's turn.
    2 = Simulating the effects of an ammunition shot by the left player.
    3 = Right player's turn.
    4 = Simulating the effects of an ammunition shot by the right player.
    */
    int state;
    
    private static final int AMMUNITION_RADIUS = 7;
    private static final int EXPLOSION_RADIUS = 7;
   
    int leftCannonX;
    int leftCannonY;
    int rightCannonX;
    int rightCannonY;
    
    double initialVx;
    double initialVy;
    
    boolean oldAmmunitionExist;
    int oldAmmunitionX;
    int oldAmmunitionY;
    
    // Tarkastetaan törmäykset maahan rajoista?
    
    // Törmäykset linnaan erikseen?
    
    /**
     * 
     * @param gameField Image of the Game Field.
     * @param groundLevel The height of ground level in pixels from the bottom of the image.
     */
    public Game(BufferedImage gameField, int groundLevel, int leftCannonX, int leftCannonY, int rightCannonX, int rightCannonY){
        this.gameField = gameField;
        this.groundLevel = groundLevel;
        
        this.leftCannonX = leftCannonX;
        this.leftCannonY = leftCannonY;
        this.rightCannonX = rightCannonX;
        this.rightCannonY = rightCannonY;
        
        this.state = 1;
        
        this.oldAmmunitionExist = false;
    }
    
    private int yFromNormalToImage(int y){
        return this.gameField.getHeight() - y;
    }
    
    private int ammunitionX(double seconds){
        double positionDueInitialVx = this.initialVx * seconds;
        
        int positionDueCannonPosition;
        if(state == 1){
            positionDueCannonPosition = this.leftCannonX;
        } else {
            positionDueCannonPosition = this.rightCannonX;
        }
        
        return (int)positionDueInitialVx + positionDueCannonPosition;
    }
    
    private int ammunitionY(double seconds){
        double positionDueGravity = (-9.81/2.0) * Math.pow(seconds, 2);
        double positionDueInitialVy = this.initialVy * seconds;
        
        int positionDueCannonPosition;
        if(state == 1){
            positionDueCannonPosition = this.leftCannonY;
        } else {
            positionDueCannonPosition = this.rightCannonY;
        }
        
        return (int)(positionDueGravity + positionDueInitialVy) + positionDueCannonPosition;
    }
    
    private void removeOldAmmunition(){
        // x2 + y2 <= 7
        
    }
    
    public int getSimulationSnapshot(double seconds){
        
        // Jos ammuksella vanha sijainti niin poistetaan vanhat pixelit.
        if(oldAmmunitionExist){
            removeOldAmmunition();
            oldAmmunitionExist = false;
        }
        
        
        // Lasketaan uusi ammuksen sijainti.
        
        // Tarkastetaan rajat:
        // jos vas tai oik yli niin null
        
        // tarkasta osumat maahan ja linnaan kun uusia pixeleitä merkataan.
        //      Jos pixeli osuu linnaan tai maahan niin poista linnapixelit explode-alueelta (ja explode keskus muistiin.)
        
        // Palautetaan tilanne.
        
        
        return 0;
    }
    
    public int[] explosionCoordinates(){
        return null;
    }
    
    public boolean isGameOver(){
        if(this.state == 0){
            return true;
        }
        return false;
    }
    
    
}
