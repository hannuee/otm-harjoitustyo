/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class Game {
    
    BufferedImage gameField;
    
    /*
    States:
    1 = Left player's turn.
    2 = Simulating the effects of an ammunition shot by the left player.
    3 = Right player's turn.
    4 = Simulating the effects of an ammunition shot by the right player.
    */
    int state;
    
    private static final int AMMUNITION_RADIUS = 7;
    private static final int EXPLOSION_RADIUS = 50;
   
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
    
    /**
     * 
     * @param gameField Image of the Game Field.
     */
    public Game(BufferedImage gameField, int leftCannonX, int leftCannonY, int rightCannonX, int rightCannonY){
        this.gameField = gameField;
        
        this.leftCannonX = leftCannonX;
        this.leftCannonY = leftCannonY;
        this.rightCannonX = rightCannonX;
        this.rightCannonY = rightCannonY;
        
        this.state = 1;
        
        this.oldAmmunitionExist = false;
        this.explosion = false;
    }
    
    private void nextState(){
        ++state;
        if(state == 5){
            state = 1;
        }
    }
    
    // From normal to image coordinates
    private int yTransform(int y){
        return this.gameField.getHeight() - y;
    }
    
    
    private int ammunitionX(double seconds){
        double positionDueInitialVx = this.initialVx * seconds;
        
        int positionDueCannonPosition;
        if(state == 2){
            positionDueCannonPosition = this.leftCannonX;
        } else {
            positionDueCannonPosition = this.rightCannonX;
        }
        
        return (int)positionDueInitialVx + positionDueCannonPosition;
    }
    
    private int ammunitionXwithDrag(double t){
        final double a = 0.00019242255;
        final double v = Math.abs(this.initialVx);
        final int p;
        if(state == 2){
            p = this.leftCannonX;
        } else {
            p = this.rightCannonX;
        }
        
        double up1 = a*p;
        
        double up2 = Math.log(a*t + 1/v);
        
        double up3 = Math.log(1/v);
        
        // Differential equation only works with positive v, so when v is actually negative, positive v is used but the equation is flipped.
        if(0 <= this.initialVx){ 
            return (int)((up1 + up2 - up3)/a);
        } else {
            return (int)((up1 - up2 + up3)/a);
        }
    }
    
    private int ammunitionY(double seconds){
        double positionDueGravity = (-9.81/2.0) * Math.pow(seconds, 2);
        double positionDueInitialVy = this.initialVy * seconds;
        
        int positionDueCannonPosition;
        if(state == 2){
            positionDueCannonPosition = this.leftCannonY;
        } else {
            positionDueCannonPosition = this.rightCannonY;
        }
        
        return (int)(positionDueGravity + positionDueInitialVy) + positionDueCannonPosition;
    }
    
    private int ammunitionYwithDrag(double t){
        final double a = 0.00019242255;
        final double g = 9.81;
        
        final double v = this.initialVy;
        final int p;
        if(state == 2){
            p = this.leftCannonY;
        } else {
            p = this.rightCannonY;
        }
       
        
        double up1;
        if(0 <= v){
            up1 = Math.log(Math.cos(
                Math.sqrt(a)*Math.sqrt(g)*t - Math.acos(Math.sqrt(g)/Math.sqrt(a*v*v+g))));
        } else {
            up1 = Math.log(Math.cos(
                Math.sqrt(a)*Math.sqrt(g)*t + Math.acos(Math.sqrt(g)/Math.sqrt(a*v*v+g))));
        }
        
        double up2 = Math.log(Math.sqrt(g)/Math.sqrt(a*v*v+g));
        
        double up3 = a*p;
        
        
        return (int)((up1 - up2 + up3)/a);
    }
    
    private boolean insertCircleWithImpactDetectionOption(int circleX, int circleY, int radius, int color, boolean detectionON){
        int yMax = gameField.getHeight();
        int xMax = gameField.getWidth();
        
        int y = circleY + radius;
        int x = circleX - radius;
        
        int yTarget = y - 2 * radius; // So y-loop must substract!
        int xTarget = x + 2 * radius;
        
        // Loops which go through a rectangle pixel by pixel that will hold the circle to be drawn.
        while(y >= yTarget){
            while(x <= xTarget){
                
                // (x-x0)2 + (y-y0)2 <= radius2
                if((x - circleX)*(x - circleX) + (y - circleY)*(y - circleY) <= radius*radius){
                    
                    // gameField boundary check.        // <= VS <   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if(0 < x && x < xMax && 0 < y && y < yMax){
                        if(detectionON && gameField.getRGB(x, yTransform(y)) != Color.WHITE.getRGB()){  // Not white == impact into fortress or ground. 
                            return true;
                        }
                        gameField.setRGB(x, yTransform(y), color);
                    }
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
        if(state != 2 && state != 4){
            return null;
        }
        
        // Jos ammuksella vanha sijainti niin poistetaan vanhat pixelit.
        removeOldAmmunitionIfExistent();
        
        // Lasketaan uusi ammuksen sijainti.
        int ammunitionX = ammunitionXwithDrag(seconds);
        int ammunitionY = ammunitionYwithDrag(seconds);
        
        // Tarkastetaan rajat:
        // jos vas tai oik yli niin palautetaan tyhjä
        if(ammunitionX < -AMMUNITION_RADIUS || gameField.getWidth() + AMMUNITION_RADIUS < ammunitionX){
            nextState();
            return gameField;
        }
       
        
        boolean impact = false;
        
        // Tarkastetaan osuma linnoihin ja maahan samalla kun piirretään ammuksen uutta paikkaa.
        impact = insertCircleWithImpactDetectionOption(ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED.getRGB(), true);

        // Jos osui maahan tai linnaan.
        if(impact){
            // Poistetaan linnapixelit räjähdysalueelta ja samalla ammus:
            insertCircleWithImpactDetectionOption(ammunitionX, ammunitionY, EXPLOSION_RADIUS, Color.WHITE.getRGB(), false);
            oldAmmunitionExist = false;
            
            nextState();
            
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
    
    public BufferedImage getStaticSnapshot(){
        return this.gameField;
    }
    
    public void setAndFireCannon(int initialVx, int initialVy){
        this.initialVx = initialVx;
        this.initialVy = initialVy;
        nextState();
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
    
    public String checkWinner(){
        int blackPixels = 0;
        int redPixels = 0;
        
        int yMax = gameField.getHeight();
        int xMax = gameField.getWidth();
        
        int y = 0;
        int x = 0;
        while(y < yMax){
            while(x < xMax){
                
                int pixelColor = gameField.getRGB(x, y);
                if(pixelColor == Color.BLACK.getRGB()){
                    ++blackPixels;
                } else if(pixelColor == Color.RED.getRGB()){
                    ++redPixels;
                }
                
                ++x;
            }
            
            x = 0;
            ++y;
        }
        
        if(blackPixels > 0 && redPixels > 0){
            return null;
        } else if(blackPixels == 0 && redPixels == 0){
            return "TIE!";
        } else if(blackPixels == 0){
            return "Right player won!";
        } else {
            return "Left player won!";
        }
    }
    
}
