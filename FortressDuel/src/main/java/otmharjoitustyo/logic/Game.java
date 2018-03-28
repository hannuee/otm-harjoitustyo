/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;

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
    private static final int EXPLOSION_RADIUS = 14;
   
    int leftCannonX;
    int leftCannonY;
    int rightCannonX;
    int rightCannonY;
    
    double initialVx;
    double initialVy;
    
    boolean oldAmmunitionExist;
    int oldAmmunitionX;
    int oldAmmunitionY;
    
    boolean explosion;
    int explosionX;
    int explosionY;
    
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
        this.explosion = false;
        
        
        // Testausta varten:
        this.initialVx = 50;
        this.initialVy = 50;
    }
    
    
    // From normal to image coordinates
    private int yTransform(int y){
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
    
    private boolean insertCircleWithImpactDetectionOption(int circleX, int circleY, int radius, int color, boolean detectionON){
        int y = circleY + radius;
        int x = circleX - radius;
        
        int yTarget = y - 2 * radius; // So y-loop must substract!
        int xTarget = x + 2 * radius;
        
        // Loops which go through a rectangle pixel by pixel that will hold the circle to be drawn.
        while(y >= yTarget){
            while(x <= xTarget){
                
                // (x+x0)2 + (y+y0)2 <= 7
                if((x - circleX)*(x - circleX) + (y - circleY)*(y - circleY) <= radius*radius){

                    if(detectionON && gameField.getRGB(x, yTransform(y)) == 0){  // Black == Fortress impact!!!
                        return true;
                    }
                    
                    gameField.setRGB(x, yTransform(y), color);
                }
                
                ++x;
            }
            
            x = circleX - radius;
            --y;
        }
        
        return false;  // No impact detected or impact detection not turned on.
    }
    
    private void removeOldAmmunitionIfExistent(){
        if(oldAmmunitionExist){
            insertCircleWithImpactDetectionOption(oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, Color.WHITE.getRGB(), false);
            oldAmmunitionExist = false;
        }
    }
    
    // GUIn tulisi käyttää ennen jokaista käyttöä statea.
    // GUIn tulisi kysyä jokaisen metodin käytön jälkeen räjähdystä.
    public BufferedImage getSimulationSnapshot(double seconds){
        
        // Jos ammuksella vanha sijainti niin poistetaan vanhat pixelit.
        removeOldAmmunitionIfExistent();
        
        // Lasketaan uusi ammuksen sijainti.
        int ammunitionX = ammunitionX(seconds);
        int ammunitionY = ammunitionY(seconds);
        
        // Tarkastetaan rajat:
        // jos vas tai oik yli niin palautetaan tyhjä
        if(ammunitionX < -AMMUNITION_RADIUS || gameField.getWidth() + AMMUNITION_RADIUS < ammunitionX){
            ++state;
            if(state == 5){
                state = 1;
            }
            
            return gameField;
        }
        
        
        boolean impact = false;
        
        // Tarkasta osuma maahan:
        if(ammunitionY < groundLevel + AMMUNITION_RADIUS){
            impact = true;
        }
        
        if(!impact){
            // Maahan ei osuttu, tarkastetaan osuma linnoihin samalla kun piirretään ammuksen uutta paikkaa.
            impact = insertCircleWithImpactDetectionOption(ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED.getRGB(), true);
        }
        
        
        // Jos osui maahan tai linnaan.
        if(impact){
            // Poistetaan linnapixelit räjähdysalueelta ja samalla ammus:
            insertCircleWithImpactDetectionOption(ammunitionX, ammunitionY, EXPLOSION_RADIUS, Color.WHITE.getRGB(), false);
            
            ++state;
            if(state == 5){
                state = 1;
            }
            
            explosion = true;
            explosionX = ammunitionX;
            explosionY = ammunitionY;
        } else {
            oldAmmunitionExist = true;
            oldAmmunitionX = ammunitionX;
            oldAmmunitionY = ammunitionY;
        }

        
        return gameField;
    }
    
    public int[] explosionCoordinates(){
        if(explosion){
            return new int[]{explosionX, explosionY};
        }
        return null;
    }
    
    public int getState(){
        return state;
    }
    
}
