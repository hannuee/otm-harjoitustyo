/**
 * @author Hannu Erälaukko
 */

import otmharjoitustyo.logic.Game;

import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void gameHasCorrectStateInTheBeginning() {
        Game game = new Game(new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB), 100, 200, 700, 200);
        assertEquals(1, game.getState());
    }
    
}
