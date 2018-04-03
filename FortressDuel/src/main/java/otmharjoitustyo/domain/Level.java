/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.domain;

import java.awt.image.BufferedImage;

public class Level {
    
    private int id;
    private String name;
    private BufferedImage gameField;
    
    public Level(int id, String name, BufferedImage gameField){
        this.id = id;
        this.name = name;
        this.gameField = gameField;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public BufferedImage getGameField(){
        return this.gameField;
    }
    
}
