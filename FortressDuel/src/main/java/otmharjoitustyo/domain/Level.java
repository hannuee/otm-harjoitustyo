/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

import java.awt.image.BufferedImage;

public class Level {
    
    private String name;
    private BufferedImage gameField;
    private BufferedImage background;
    
    private int leftCannonX;
    private int leftCannonY;
    private int rightCannonX;
    private int rightCannonY;
    
    private int leftFortressMaxX;
    private int leftFortressMinX;
    private int leftFortressMaxY;
    private int leftFortressMinY;
    
    private int rightFortressMaxX;
    private int rightFortressMinX;
    private int rightFortressMaxY;
    private int rightFortressMinY;
    
    // For the Vacuum chamber level:
    private boolean vacuumPossible;
    private int ammunitionMaxY;
    private int ammunitionMinY;
    
    public Level(String name, BufferedImage gameField, BufferedImage background, 
                 int leftCannonX, int leftCannonY, int rightCannonX, int rightCannonY, 
                 int leftFortressMaxX, int leftFortressMinX, int leftFortressMaxY, int leftFortressMinY, 
                 int rightFortressMaxX, int rightFortressMinX, int rightFortressMaxY, int rightFortressMinY, 
                 boolean vacuumPossible, int ammunitionMaxY, int ammunitionMinY) {
        this.name = name;
        this.gameField = gameField;
        this.background = background;
        
        this.leftCannonX = leftCannonX;
        this.leftCannonY = leftCannonY;
        this.rightCannonX = rightCannonX;
        this.rightCannonY = rightCannonY;

        this.leftFortressMaxX = leftFortressMaxX;
        this.leftFortressMinX = leftFortressMinX;
        this.leftFortressMaxY = leftFortressMaxY;
        this.leftFortressMinY = leftFortressMinY;

        this.rightFortressMaxX = rightFortressMaxX;
        this.rightFortressMinX = rightFortressMinX;
        this.rightFortressMaxY = rightFortressMaxY;
        this.rightFortressMinY = rightFortressMinY;
        
        this.vacuumPossible = vacuumPossible;
        this.ammunitionMaxY = ammunitionMaxY;
        this.ammunitionMinY = ammunitionMinY;
    }
    
    public String getName() {
        return this.name;
    }
    
    public BufferedImage getGameField() {
        return this.gameField;
    }
    
    public BufferedImage getBackground() {
        return this.background;
    }

    
    public int getLeftCannonX() {
        return leftCannonX;
    }

    public int getLeftCannonY() {
        return leftCannonY;
    }

    public int getRightCannonX() {
        return rightCannonX;
    }

    public int getRightCannonY() {
        return rightCannonY;
    }
    

    public int getLeftFortressMaxX() {
        return leftFortressMaxX;
    }

    public int getLeftFortressMinX() {
        return leftFortressMinX;
    }

    public int getLeftFortressMaxY() {
        return leftFortressMaxY;
    }

    public int getLeftFortressMinY() {
        return leftFortressMinY;
    }
    

    public int getRightFortressMaxX() {
        return rightFortressMaxX;
    }

    public int getRightFortressMinX() {
        return rightFortressMinX;
    }

    public int getRightFortressMaxY() {
        return rightFortressMaxY;
    }

    public int getRightFortressMinY() {
        return rightFortressMinY;
    }

    
    public boolean isVacuumPossible() {
        return vacuumPossible;
    }

    public int getAmmunitionMaxY() {
        return ammunitionMaxY;
    }

    public int getAmmunitionMinY() {
        return ammunitionMinY;
    }
    
}
