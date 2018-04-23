/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import otmharjoitustyo.domain.Level;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class Game {
    
    /*
    States:
    1 = Left player's turn.
    2 = Simulating the effects of an ammunition shot by the left player.
    3 = Right player's turn.
    4 = Simulating the effects of an ammunition shot by the right player.
    */
    private int state;
    
    
    
    private Level level;
    // Due to possible performance gains, some of the information from the given Level 
    // is copied to the Game instead of using it by reference. 
    private BufferedImage gameField;
    private BufferedImage background;
    private int leftCannonX;
    private int leftCannonY;
    private int rightCannonX;
    private int rightCannonY;
    private int ammunitionMaxY;
    private int ammunitionMinY;
    
    
    private BufferedImage gameFieldWithBackground;
    
    private static final int AMMUNITION_RADIUS = 7;
    private static final int EXPLOSION_RADIUS = 50;
    
    double initialVx;
    double initialVy;
    
    boolean oldAmmunitionExist;
    int oldAmmunitionX;
    int oldAmmunitionY;
    
    boolean explosion;
    int explosionX;
    int explosionY;
    

    public Game(Level level) {
        this.state = 1;
        
        this.level = level;
        // Due to possible performance gains, some of the information from the given Level 
        // is copied to the Game instead of using it by reference. 
        this.gameField = level.getGameField();
        this.background = level.getBackground();
        this.leftCannonX = level.getLeftCannonX();
        this.leftCannonY = level.getLeftCannonY();
        this.rightCannonX = level.getRightCannonX();
        this.rightCannonY = level.getRightCannonY();
        this.ammunitionMaxY = level.getAmmunitionMaxY();
        this.ammunitionMinY = level.getAmmunitionMinY();
        
        
        this.gameFieldWithBackground = new BufferedImage(gameField.getWidth(), gameField.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        this.oldAmmunitionExist = false;
        this.explosion = false;
    }
    
    private void nextState() {
        ++state;
        if (state == 5) {
            state = 1;
        }
    }
    
    // From normal to image coordinates
    private int yTransform(int y) {
        return this.gameField.getHeight() - y;
    }
    
    
    public int ammunitionX(double seconds) { // public for testing, otherwise private.
        double positionDueInitialVx = this.initialVx * seconds;
        
        int positionDueCannonPosition;
        if (state == 2) {
            positionDueCannonPosition = this.leftCannonX;
        } else {
            positionDueCannonPosition = this.rightCannonX;
        }
        
        return (int) positionDueInitialVx + positionDueCannonPosition;
    }
    
    public int ammunitionXwithDrag(double t) {  // public for testing, otherwise private.
        final double a = 0.00019242255;
        final double v = Math.abs(this.initialVx);
        final int p;
        if (state == 2) {
            p = this.leftCannonX;
        } else {
            p = this.rightCannonX;
        }
        
        double up1 = a * p;
        
        double up2 = Math.log(a * t + 1 / v);
        
        double up3 = Math.log(1 / v);
        
        // The solution used for the differential equation only works with positive v,
        // so when v is negative, positive v is used but the solution outcome is mirrored,
        // and when v is zero, the x-position of the ammunition stays the same as the cannon's x-position.
        if (0 < this.initialVx) { 
            return (int) ((up1 + up2 - up3) / a);
        } else if (this.initialVx == 0) {
            return p;
        } else {
            return (int) ((up1 - up2 + up3) / a);
        }
    }
    
    public int ammunitionY(double seconds) {  // public for testing, otherwise private.
        double positionDueGravity = (-9.81 / 2.0) * Math.pow(seconds, 2);
        double positionDueInitialVy = this.initialVy * seconds;
        
        int positionDueCannonPosition;
        if (state == 2) {
            positionDueCannonPosition = this.leftCannonY;
        } else {
            positionDueCannonPosition = this.rightCannonY;
        }
        
        return (int) (positionDueGravity + positionDueInitialVy) + positionDueCannonPosition;
    }
    
    public int ammunitionYwithDrag(double t) {  // public for testing, otherwise private.
        final double a = 0.00019242255;
        final double g = 9.81;
        
        final double v = this.initialVy;
        final int p;
        if (state == 2) {
            p = this.leftCannonY;
        } else {
            p = this.rightCannonY;
        }
       
        
        double up1;
        if (0 <= v) {
            up1 = Math.log(Math.cos(
                Math.sqrt(a) * Math.sqrt(g) * t - Math.acos(Math.sqrt(g) / Math.sqrt(a * v * v + g))));
        } else {
            up1 = Math.log(Math.cos(
                Math.sqrt(a) * Math.sqrt(g) * t + Math.acos(Math.sqrt(g) / Math.sqrt(a * v * v + g))));
        }
        
        double up2 = Math.log(Math.sqrt(g) / Math.sqrt(a * v * v + g));
        
        double up3 = a * p;
        
        
        return (int) ((up1 - up2 + up3) / a);
    }
    
    private boolean insertCircleWithImpactDetectionOption(int circleX, int circleY, int radius, int color, boolean detectionON) {
        int yMax = gameField.getHeight();
        int xMax = gameField.getWidth();
        
        int y = circleY + radius;
        int x = circleX - radius;
        
        int yTarget = y - 2 * radius; // So y-loop must substract!
        int xTarget = x + 2 * radius;
        
        // Loops which go through a rectangle pixel by pixel that will hold the circle to be drawn.
        while (y >= yTarget) {
            while (x <= xTarget) {
                
                // (x-x0)2 + (y-y0)2 <= radius2
                if ((x - circleX) * (x - circleX) + (y - circleY) * (y - circleY) <= radius * radius) {
                    
                    // gameField boundary check.        // <= VS <   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (0 < x && x < xMax && 0 < y && y < yMax) {
                        if (detectionON && gameField.getRGB(x, yTransform(y)) != Color.WHITE.getRGB()) {  // Not white == impact into fortress or ground. 
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
    
    private void removeOldAmmunitionIfExistent() {
        if (oldAmmunitionExist) {
            insertCircleWithImpactDetectionOption(oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, Color.WHITE.getRGB(), false);
            oldAmmunitionExist = false;
        }
    }
    
    private void updateGameFieldWithBackground(){     // REFAKTOROI LUUPPI KOMBO!!!!!!!!!!!!!!!!!!!!???????
        int yMax = gameField.getHeight();
        int xMax = gameField.getWidth();
        
        int y = 0;
        int x = 0;
        while (y < yMax) {
            while (x < xMax) {
                
                if(gameField.getRGB(x, y) == Color.WHITE.getRGB()){
                    gameFieldWithBackground.setRGB(x, y, background.getRGB(x, y));
                } else {
                    gameFieldWithBackground.setRGB(x, y, gameField.getRGB(x, y));
                }
                
                ++x;
            }
            
            x = 0;
            ++y;
        }
    }
    
    public BufferedImage getSimulationSnapshot(double seconds) {
        if (state != 2 && state != 4) {
            return null;
        }
        
        // Jos ammuksella vanha sijainti niin poistetaan vanhat pixelit.
        removeOldAmmunitionIfExistent();
        
        // Lasketaan uusi ammuksen sijainti.
        int ammunitionX = ammunitionXwithDrag(seconds);
        int ammunitionY = ammunitionYwithDrag(seconds);
        
        // Tarkastetaan rajat:
        // jos vasen, oikea tai alaraja yli niin palautetaan tyhjä ja muutetaan pelin tilaa.
        if (ammunitionX < -AMMUNITION_RADIUS || gameField.getWidth() + AMMUNITION_RADIUS < ammunitionX || ammunitionY < -AMMUNITION_RADIUS) {
            nextState();
            //updateGameFieldWithBackground();
            return gameField; //WithBackground;
        }
       
        
        boolean impact = false;
        
        // Tarkastetaan osuma linnoihin ja maahan samalla kun piirretään ammuksen uutta paikkaa.
        impact = insertCircleWithImpactDetectionOption(ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED.getRGB(), true);

        // Jos osui maahan tai linnaan.
        if (impact) {
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
        
        //updateGameFieldWithBackground();
        return gameField; //WithBackground;
    }
    
    public BufferedImage getStaticSnapshot() {
        //updateGameFieldWithBackground();
        return gameField; //WithBackground;
    }
    
    public void setAndFireCannon(int initialVx, int initialVy) {
        this.initialVx = initialVx;
        this.initialVy = initialVy;
        nextState();
    }
    
    public int[] explosionCoordinates() {
        if (explosion) {
            return new int[]{explosionX, explosionY};
        }
        return null;
    }
    
    public int getState() {
        return state;
    }
    
    public String checkWinner() {     // REFAKTOROI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        int leftFortressPixels = 0;
        int rightFortressPixels = 0;
        
        // Left:
        int yMax = level.getLeftFortressMaxY();
        int xMax = level.getLeftFortressMaxX();
        int y = level.getLeftFortressMinY();
        int x = level.getLeftFortressMinX();
        while (y < yMax) {
            while (x < xMax) {
                
                if (gameField.getRGB(x, yTransform(y)) != Color.WHITE.getRGB()) {
                    ++leftFortressPixels;
                }
                
                ++x;
            }
            
            x = level.getLeftFortressMinX();
            ++y;
        }
        
        // Right:
        yMax = level.getRightFortressMaxY();
        xMax = level.getRightFortressMaxX();
        y = level.getRightFortressMinY();
        x = level.getRightFortressMinX();
        while (y < yMax) {
            while (x < xMax) {
                
                if (gameField.getRGB(x, yTransform(y)) != Color.WHITE.getRGB()) {
                    ++rightFortressPixels;
                }
                
                ++x;
            }
            
            x = level.getRightFortressMinX();
            ++y;
        }
        
        if (leftFortressPixels > 0 && rightFortressPixels > 0) {
            return null;
        } else if (leftFortressPixels == 0 && rightFortressPixels == 0) {
            return "TIE!";
        } else if (leftFortressPixels == 0) {
            return "Right player won!";
        } else {
            return "Left player won!";
        }
    }
    
}
