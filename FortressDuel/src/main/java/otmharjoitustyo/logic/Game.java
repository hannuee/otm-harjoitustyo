/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import otmharjoitustyo.domain.Level;

import java.util.Random;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class Game {
    
    /*
    States:
    1 = Left player's turn.
    2 = Simulating the trajectory of an ammunition shot by the left player.
    3 = Explosion simulation of the left player's ammunition.
    4 = Right player's turn.
    5 = Simulating the trajectory of an ammunition shot by the right player.
    6 = Explosion simulation of the right player's ammunition.
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
    boolean vacuum;
    
    
    private BufferedImage gameFieldWithBackground;
    
    private static final int AMMUNITION_RADIUS = 7;
    private static final int EXPLOSION_RADIUS = 50;
    private static final double EXPLOSION_DURATION = 0.75;
    
    double initialVx;
    double initialVy;
    
    boolean oldAmmunitionExist;
    int oldAmmunitionX;
    int oldAmmunitionY;
    
    int explosionX;
    int explosionY;
    double explosionStartTime;
    int explosionSeed;
    
    int leftFortressPixelsStart;
    int rightFortressPixelsStart;

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
        initializeGameFieldWithBackground();
        
        this.oldAmmunitionExist = false;
        
        this.leftFortressPixelsStart = leftFortressPixels();
        this.rightFortressPixelsStart = rightFortressPixels();
        
        this.vacuum = false;
    }
    
    private void nextState() {
        ++state;
        if (state == 7) {
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
    
    
    public int calculateYwithDragResult(double t, double a, double g, double v, int p) {
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
        
        return calculateYwithDragResult(t, a, g, v, p);
    }
          
    
    // advance päällä: Kasvata keltaista alueta punaisella kaverisääntöjen ja satunnaisuuden perusteella.
    // pois päältä:    Vaihda punaiset keltaisiksi.
    private void explosionAdvancer(BufferedImage image, int xMin, int xMax, int yMin, int yMax, 
                                   int circleX, int circleY, int radius, 
                                   boolean advance, Color color, Color replace, BufferedImage fillImage) {
        
        int yStart = circleY - radius;
        int xStart = circleX - radius;
        int yTarget = yStart + 2 * radius;
        int xTarget = xStart + 2 * radius;
        
        // Image boundary checks.
        if (yStart < 0) {
            yStart = 0;
        }
        if (xStart < 0) {
            xStart = 0;
        }
        if (image.getHeight() <= yTarget) {
            yTarget = image.getHeight() - 1;
        }
        if (image.getWidth() <= xTarget) {
            xTarget = image.getWidth() - 1;
        }
        
        int y = yStart;
        int x = xStart;
        
        Random random = new Random(explosionSeed);
        
        // Loops which go through a rectangle pixel by pixel that will hold the explosion to be drawn.
        while (y <= yTarget) {
            while (x <= xTarget) {
                
                // Circle expression: (x-x0)2 + (y-y0)2 <= radius2
                if ((x - circleX) * (x - circleX) + (y - circleY) * (y - circleY) <= radius * radius) {
                    
                    if (color != null && replace != null) {
                        if (image.getRGB(x, yTransform(y)) == color.getRGB()) {
                            image.setRGB(x, yTransform(y), replace.getRGB()); 
                        }
                    } else if (color != null && fillImage != null) {
                        if (image.getRGB(x, yTransform(y)) == color.getRGB()) {
                            image.setRGB(x, yTransform(y), fillImage.getRGB(x, yTransform(y))); 
                        }
                    } else if (advance) {
                        if (image.getRGB(x, yTransform(y)) != Color.YELLOW.getRGB()
                            && 3 <= ImageOperations.countYellowBuddies(image, x, y, xMin, xMax, yMin, yMax) - random.nextInt(3)) {
                            image.setRGB(x, yTransform(y), Color.RED.getRGB()); 
                        }
                    }
                }
                
                ++x;
            }
            
            x = xStart;
            ++y;
        }
        
    }
    
    private void removeOldAmmunitionIfExistent() {
        if (oldAmmunitionExist) {
            ImageOperations.insertCircle(gameField, oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, Color.WHITE, null);
            ImageOperations.insertCircle(gameFieldWithBackground, oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, null, background);
            oldAmmunitionExist = false;
        }
    }
    
    private void initializeGameFieldWithBackground() {     // REFAKTOROI LUUPPI KOMBO!!!!!!!!!!!!!!!!!!!!???????
        int yMax = gameField.getHeight();
        int xMax = gameField.getWidth();
        
        int y = 0;
        int x = 0;
        while (y < yMax) {
            while (x < xMax) {
                
                if (gameField.getRGB(x, y) == Color.WHITE.getRGB()) {
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
    
    public BufferedImage trajectorySimulation(double seconds) {
        // Jos ammuksella vanha sijainti niin poistetaan vanhat pixelit.
        removeOldAmmunitionIfExistent();

        // Lasketaan uusi ammuksen sijainti.
        int ammunitionX;
        int ammunitionY;
        if (vacuum) {
            ammunitionX = ammunitionX(seconds);
            ammunitionY = ammunitionY(seconds);
        } else {
            ammunitionX = ammunitionXwithDrag(seconds);
            ammunitionY = ammunitionYwithDrag(seconds);
        }

        // Tarkastetaan rajat:
        // jos vasen, oikea tai alaraja yli niin palautetaan tyhjä ja muutetaan pelin tilaa.
        if (ammunitionX < -AMMUNITION_RADIUS || gameField.getWidth() + AMMUNITION_RADIUS < ammunitionX || ammunitionY < -AMMUNITION_RADIUS) {
            nextState();
            nextState();  // Two nextStates because there is no ammunition explosion.
            return gameFieldWithBackground;
        }

        // Trace for Vacuum chamber level.
        if (level.isVacuumPossible()) {
            if (0 < ammunitionX && ammunitionX < background.getWidth() && 0 < ammunitionY && ammunitionY < background.getHeight()) {
                background.setRGB(ammunitionX, yTransform(ammunitionY), Color.BLACK.getRGB());
            }
        }

        // Tarkastetaan osuma linnoihin ja maahan samalla kun piirretään ammuksen uutta paikkaa.
        boolean impact = ImageOperations.insertCircle(gameField, ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED, null);
        ImageOperations.insertCircle(gameFieldWithBackground,  ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED, null);

        boolean gameFieldHardBoundaryImpact = level.isVacuumPossible() && 
                (ammunitionY > level.getAmmunitionMaxY() - AMMUNITION_RADIUS || 
                 ammunitionY < level.getAmmunitionMinY() + AMMUNITION_RADIUS);

        // Jos osui maahan tai linnaan.
        if (impact || gameFieldHardBoundaryImpact) {

            explosionSeed = new Random().nextInt();

            oldAmmunitionExist = false;  // Because red ammunitions are now considered as explosion graphics.

            nextState();

            explosionX = ammunitionX;
            explosionY = ammunitionY;
            explosionStartTime = seconds;

        } else {
            oldAmmunitionExist = true;
            oldAmmunitionX = ammunitionX;
            oldAmmunitionY = ammunitionY;
        }
        
        return gameFieldWithBackground;
    }
    
    public void explosionExpansion() {
        // punaset keltasiks.
        // punasien lisäys
        // MOLEMPIIN!

        for (int i = 0; i < 3; i++) {
            explosionAdvancer(gameField, 0, gameField.getWidth(), 0, gameField.getHeight(), 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              false, Color.RED, Color.YELLOW, null);
            explosionAdvancer(gameFieldWithBackground, 0, gameFieldWithBackground.getWidth(), 0, gameFieldWithBackground.getHeight(), 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              false, Color.RED, Color.YELLOW, null);

            explosionAdvancer(gameField, 0, gameField.getWidth(), 0, gameField.getHeight(), 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              true, null, null, null);
            explosionAdvancer(gameFieldWithBackground, 0, gameFieldWithBackground.getWidth(), 0, gameFieldWithBackground.getHeight(), 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              true, null, null, null);                    
        }        
    }
    
    public void explosionCleanUp() {
        // Lopussa gWb keltaset ja punaset täytetään backgroundilla
        // ja g keltaset ja punaset täytetään täytetään valkosella.

        // Eka punaset keltasiks niin tarvii korvata vaan punaset.
        explosionAdvancer(gameField, 0, gameField.getWidth(), 0, gameField.getHeight(), 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.RED, Color.YELLOW, null);
        explosionAdvancer(gameFieldWithBackground, 0, gameFieldWithBackground.getWidth(), 0, gameFieldWithBackground.getHeight(), 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.RED, Color.YELLOW, null);


        explosionAdvancer(gameField, 0, gameField.getWidth(), 0, gameField.getHeight(), 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.YELLOW, Color.WHITE, null);
        explosionAdvancer(gameFieldWithBackground, 0, gameFieldWithBackground.getWidth(), 0, gameFieldWithBackground.getHeight(), 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.YELLOW, null, background);

        nextState();
    }
    
    public BufferedImage explosionSimulation(double seconds) {
        if (seconds < explosionStartTime + EXPLOSION_DURATION) {
            explosionExpansion();
        } else {
            explosionCleanUp();
        }
        return gameFieldWithBackground;
    }
    
    public BufferedImage getSimulationSnapshot(double seconds) {
        if (state == 2 || state == 5) {
            return trajectorySimulation(seconds);
        } else if (state == 3 || state == 6) {
            return explosionSimulation(seconds);
        }
        return null;  // Signal that trajectory and explosion simulations have ended.
    }
     
    public BufferedImage getStaticSnapshot() {
        return gameFieldWithBackground;
    }
    
    public void setAndFireCannon(int initialVx, int initialVy) {
        this.initialVx = initialVx;
        this.initialVy = initialVy;
        nextState();
    }
    
    public void setVacuum(boolean vacuum) {
        if (level.isVacuumPossible() && (state == 1 || state == 4)) {
            this.vacuum = vacuum;
        }
    }
    
    public boolean getVacuum() {
        return this.vacuum;
    }
    
    public int getState() {
        return state;
    }
    
    private int countNonWhitePixelsFromDefinedImageArea(BufferedImage image, int minX, int maxX, int minY, int maxY) {
        int nonWhitePixels = 0;
        
        int yIndex = minY;
        int xIndex = minX;
        while (yIndex < maxY) {
            while (xIndex < maxX) {
                
                if (image.getRGB(xIndex, yTransform(yIndex)) != Color.WHITE.getRGB()) {
                    ++nonWhitePixels;
                }
                
                ++xIndex;
            }
            
            xIndex = minX;
            ++yIndex;
        }
        
        return nonWhitePixels;
    }
    
    public int leftFortressPixels() {
        return countNonWhitePixelsFromDefinedImageArea(gameField, 
                level.getLeftFortressMinX(), level.getLeftFortressMaxX(), 
                level.getLeftFortressMinY(), level.getLeftFortressMaxY());
    }
    
    public int rightFortressPixels() {
        return countNonWhitePixelsFromDefinedImageArea(gameField, 
                level.getRightFortressMinX(), level.getRightFortressMaxX(), 
                level.getRightFortressMinY(), level.getRightFortressMaxY());
    }
    
    public double leftFortressPercentage() {
        return leftFortressPixels() / (1.0 * this.leftFortressPixelsStart);
    }
    
    public double rightFortressPercentage() {
        return rightFortressPixels() / (1.0 * this.rightFortressPixelsStart);
    }
    
    public String checkWinner() {   // EI ENÄÄN TARPEELLINEN.
        int leftFortressPixels = leftFortressPixels();
        int rightFortressPixels = rightFortressPixels();
        
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
