/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

import java.awt.image.BufferedImage;

public class Level {
    
    private String name;
    private BufferedImage gameField;
    
    public Level(String name, BufferedImage gameField){
        this.name = name;
        this.gameField = gameField;
    }
    
    public String getName(){
        return this.name;
    }
    
    public BufferedImage getGameField(){
        return this.gameField;
    }
    
}
