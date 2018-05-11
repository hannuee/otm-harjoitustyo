/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import otmharjoitustyo.domain.Level;

import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Game {
    
    /**
     * States:
     * 1 = Left player's turn.
     * 2 = Simulating the trajectory of an ammunition shot by the left player.
     * 3 = Explosion simulation of the left player's ammunition.
     * 4 = Right player's turn.
     * 5 = Simulating the trajectory of an ammunition shot by the right player.
     * 6 = Explosion simulation of the right player's ammunition.
     */
    private int state;
    
    /**
     * gameField which is included in the Level, is the core
     * game field where the game logically takes place.
     * Fortress impacts are inspected there.
     */
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
    
    /**
     * gameFieldWithBackground exists only for graphics reasons.
     * Its is updated in parallel with the gameField.
     */
    private BufferedImage gameFieldWithBackground;
    
    private static final int AMMUNITION_RADIUS = 7;
    private static final int EXPLOSION_RADIUS = 50;
    private static final double EXPLOSION_DURATION = 0.75;
    
    double initialVx;
    double initialVy;
    
    int ammunitionX;
    int ammunitionY;
    
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
        
        this.gameFieldWithBackground = ImageOperations.newImageAsCombinationOfFrontAndBackground(gameField, background);
        
        this.oldAmmunitionExist = false;
        
        this.leftFortressPixelsStart = leftFortressPixels();
        this.rightFortressPixelsStart = rightFortressPixels();
        
        this.vacuum = false;
    }
    
    /**
     * Advances the game to the next state.
     */
    private void nextState() {
        ++state;
        if (state == 7) {
            state = 1;
        }
    }
    
    
    /**
     * Calculates the X position of the fired ammunition in vacuum on the given moment of time.
     * (calculateNewAmmunitionPosition(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return The X position of the ammunition.
     */
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
    
    /**
     * Calculates the Y position of the fired ammunition in vacuum on the given moment of time.
     * (calculateNewAmmunitionPosition(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return The X position of the ammunition.
     */
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
    
    /**
     * Calculates the X position of the fired ammunition in normal atmospheric conditions on the given moment of time.
     * (calculateNewAmmunitionPosition(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return The X position of the ammunition.
     */
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
    
    /**
     * (ammunitionYwithDrag(double t)'s help method.)
     */
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
    
    /**
     * Calculates the Y position of the fired ammunition in normal atmospheric conditions on the given moment of time.
     * (calculateNewAmmunitionPosition(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return The X position of the ammunition.
     */
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
    
    
    /**
     * Deletes the ammunition from it's old place if existent,
     * from the logical gameField and from the graphics gameFieldWithBackground.
     * (trajectorySimulation(double seconds)'s help method.)
     */
    public void removeOldAmmunitionIfExistent() {
        if (oldAmmunitionExist) {
            ImageOperations.insertCircle(gameField, oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, Color.WHITE, null);
            ImageOperations.insertCircle(gameFieldWithBackground, oldAmmunitionX, oldAmmunitionY, AMMUNITION_RADIUS, null, background);
            oldAmmunitionExist = false;
        }
    }
    
    /**
     * Calculates where the ammunition should be at the given time.
     * (trajectorySimulation(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     */
    public void calculateNewAmmunitionPosition(double seconds) {
        if (vacuum) {
            ammunitionX = ammunitionX(seconds);
            ammunitionY = ammunitionY(seconds);
        } else {
            ammunitionX = ammunitionXwithDrag(seconds);
            ammunitionY = ammunitionYwithDrag(seconds);
        }        
    }
    
    /**
     * Checks if the ammunition's new position is totally out of bounds.
     * If so, explosion state is skipped.
     * (trajectorySimulation(double seconds)'s help method.)
     * @return true is out of bounds, false otherwise.
     */
    public boolean checkIfAmmunitionTotallyOutOfBounds() {
        if (ammunitionX < -AMMUNITION_RADIUS || gameField.getWidth() + AMMUNITION_RADIUS < ammunitionX || ammunitionY < -AMMUNITION_RADIUS) {
            nextState();
            nextState();  // Two nextStates because we want to skip explosion simulation.
            return true;
        }
        return false;
    }
    
    /**
     * Adds a dot to the background at the center position of the ammunition if in Vacuum Chamber level.
     * (trajectorySimulation(double seconds)'s help method.)
     */
    public void addAmmunitionTrailIfInVacuumChamber() {
        if (level.isVacuumPossible()) {
            if (0 < ammunitionX && ammunitionX < background.getWidth() && 0 < ammunitionY && ammunitionY < background.getHeight()) {
                background.setRGB(ammunitionX, ImageOperations.yTransform(background, ammunitionY), Color.BLACK.getRGB());
            }
        }        
    }
    
    /**
     * Inserts ammunition to it's new place, both in logical gameField and in graphics gameFieldWithBackground.
     * Simultaneously checks if the ammunition hits something else than white in the logical gameField
     * and also if the ammunition hits the upper or lower limits of the Vacuum Chamber level.
     * (trajectorySimulation(double seconds)'s help method.)
     * @return true if the ammunition hit something, false otherwise.
     */
    public boolean insertNewAmmunitionAndCheckImpacts() {
        boolean regularImpact = ImageOperations.insertCircle(gameField, ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED, null);
        ImageOperations.insertCircle(gameFieldWithBackground,  ammunitionX, ammunitionY, AMMUNITION_RADIUS, Color.RED, null);

        boolean gameFieldHardBoundaryImpact = level.isVacuumPossible() && 
                (ammunitionY > level.getAmmunitionMaxY() - AMMUNITION_RADIUS || 
                 ammunitionY < level.getAmmunitionMinY() + AMMUNITION_RADIUS);

        return regularImpact || gameFieldHardBoundaryImpact;
    }
    
    /**
     * Performs the appropriate actions needed in order to enter the explosion simulation
     * when the getSimulaitonSnapshot(double seconds) is called the next time.
     * (trajectorySimulation(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     */
    public void initializeExplosionSimulation(double seconds) {
        explosionSeed = new Random().nextInt();

        oldAmmunitionExist = false;  // Because red ammunition is now considered as explosion graphics.

        nextState();

        explosionX = ammunitionX;
        explosionY = ammunitionY;
        explosionStartTime = seconds;        
    }
    
    /**
     * Performs the appropriate actions needed in order to stay in the trajectory simulation
     * when the getSimulaitonSnapshot(double seconds) is called the next time.
     * (trajectorySimulation(double seconds)'s help method.)
     */
    public void initializeFollowingTrajectorySimulation() {
        oldAmmunitionExist = true;
        oldAmmunitionX = ammunitionX;
        oldAmmunitionY = ammunitionY;        
    }
    
    /**
     * Performs the trajectory simulation of an ammunition shot.
     * (getSimulationSnapshot(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return the graphics gameFieldWithBackground.
     */
    public BufferedImage trajectorySimulation(double seconds) {
        
        removeOldAmmunitionIfExistent();
        calculateNewAmmunitionPosition(seconds);
        if (checkIfAmmunitionTotallyOutOfBounds()) return gameFieldWithBackground;
        addAmmunitionTrailIfInVacuumChamber();

        if (insertNewAmmunitionAndCheckImpacts()) {
            initializeExplosionSimulation(seconds);
        } else {
            initializeFollowingTrajectorySimulation();
        }
        
        return gameFieldWithBackground;
        
    }
    
    /**
     * Performs the explosion expansion phase of the explosion simulation.
     * (explosionSimulation(double seconds)'s help method.)
     */
    public void explosionExpansion() {
        for (int i = 0; i < 3; i++) {
            // First lets turn all the RED pixels into YELLOW pixels. 
            ImageOperations.explosionAdvancer(gameField, explosionSeed, 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              false, Color.RED, Color.YELLOW, null);
            // And the above in parallel to the graphics gameFieldWithBackground.
            ImageOperations.explosionAdvancer(gameFieldWithBackground, explosionSeed, 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              false, Color.RED, Color.YELLOW, null);

            // Then add some new RED pixels to the edge of the YELLOW explosion.
            ImageOperations.explosionAdvancer(gameField, explosionSeed, 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              true, null, null, null);
            // And the above in parallel to the graphics gameFieldWithBackground.
            ImageOperations.explosionAdvancer(gameFieldWithBackground, explosionSeed, 
                              explosionX, explosionY, EXPLOSION_RADIUS, 
                              true, null, null, null);                    
        }        
    }
    
    /**
     * Performs the explosion clean up phase of the explosion simulation.
     * (explosionSimulation(double seconds)'s help method.)
     */
    public void explosionCleanUp() {
        // First lets turn all the RED pixels into YELLOW pixels.
        ImageOperations.explosionAdvancer(gameField, explosionSeed, 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.RED, Color.YELLOW, null);
        // And the above in parallel to the graphics gameFieldWithBackground.
        ImageOperations.explosionAdvancer(gameFieldWithBackground, explosionSeed, 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.RED, Color.YELLOW, null);


        // Then turn all the YELLOW pixels in to WHITE pixels.
        ImageOperations.explosionAdvancer(gameField, explosionSeed, 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.YELLOW, Color.WHITE, null);
        // And the above in parallel to the graphics gameFieldWithBackground.
        // (But YELLOW pixels are replaced with pixels from the background image.)
        ImageOperations.explosionAdvancer(gameFieldWithBackground, explosionSeed, 
                          explosionX, explosionY, EXPLOSION_RADIUS, 
                          false, Color.YELLOW, null, background);

        nextState();
    }
    
    /**
     * Performs the explosion simulation of an ammunition that has hit something.
     * (getSimulationSnapshot(double seconds)'s help method.)
     * @param seconds The moment of time of interest.
     * @return the graphics gameFieldWithBackground.
     */
    public BufferedImage explosionSimulation(double seconds) {
        if (seconds < explosionStartTime + EXPLOSION_DURATION) {
            explosionExpansion();
        } else {
            explosionCleanUp();
        }
        return gameFieldWithBackground;
    }
    
    /**
     * Performs the simulation of an ammunition shot and 
     * the effects it's possible explosion causes.
     * @param seconds The moment of time of interest.
     * @return the graphics gameFieldWithBackground.
     */
    public BufferedImage getSimulationSnapshot(double seconds) {
        if (state == 2 || state == 5) {
            return trajectorySimulation(seconds);
        } else if (state == 3 || state == 6) {
            return explosionSimulation(seconds);
        }
        return null;  // Signal that trajectory and explosion simulations have ended.
    }
     
    /**
     * Returns the graphics gameFieldWithBackground with no ammunition in it.
     * @return the graphics gameFieldWithBackground.
     */
    public BufferedImage getStaticSnapshot() {
        return gameFieldWithBackground;
    }
    
    /**
     * Causes the game to enter the state of ammunition trajectory simulation.
     * @param initialVx The X component of the firing vector, in relation to the appropriate cannon.
     * @param initialVy The Y component of the firing vector, in relation to the appropriate cannon.
     */
    public void setAndFireCannon(int initialVx, int initialVy) {
        this.initialVx = initialVx;
        this.initialVy = initialVy;
        nextState();
    }
    
    /**
     * In Vacuum Chamber level this method can be used to change the functions used to 
     * calculate the position of the ammunition.
     * @param vacuum true means air drag is not considered, false means air drag is considered
     * when calculating the position of an ammunition.
     */
    public void setVacuum(boolean vacuum) {
        if (level.isVacuumPossible() && (state == 1 || state == 4)) {
            this.vacuum = vacuum;
        }
    }
    
    /**
     * Returns the current state of the vacuum in the level.
     * @return true means air drag is not considered, false means air drag is considered
     * when calculating the position of an ammunition..
     */
    public boolean getVacuum() {
        return this.vacuum;
    }
    
    /**
     * Returns the state of the game.
     * @return the state of the game.
     */
    public int getState() {
        return state;
    }
    
    /**
     * Tells how much is remaining of the left fortress.
     * @return the number of non white pixels in the defined area of the left fortress.
     */
    public int leftFortressPixels() {
        return ImageOperations.countNonWhitePixelsFromDefinedImageArea(gameField, 
                level.getLeftFortressMinX(), level.getLeftFortressMaxX(), 
                level.getLeftFortressMinY(), level.getLeftFortressMaxY());
    }
    
    /**
     * Tells how much is remaining of the right fortress.
     * @return the number of non white pixels in the defined area of the right fortress.
     */
    public int rightFortressPixels() {
        return ImageOperations.countNonWhitePixelsFromDefinedImageArea(gameField, 
                level.getRightFortressMinX(), level.getRightFortressMaxX(), 
                level.getRightFortressMinY(), level.getRightFortressMaxY());
    }
    
    /**
     * Tells how much is remaining of the left fortress in percentages in relation to the start.
     * @return the percentage of non white pixels in the defined area of the left fortress in relation to the start.
     */
    public double leftFortressPercentage() {
        return leftFortressPixels() / (1.0 * this.leftFortressPixelsStart);
    }
    
    /**
     * Tells how much is remaining of the right fortress in percentages in relation to the start.
     * @return the percentage of non white pixels in the defined area of the right fortress in relation to the start.
     */
    public double rightFortressPercentage() {
        return rightFortressPixels() / (1.0 * this.rightFortressPixelsStart);
    }
    
}
