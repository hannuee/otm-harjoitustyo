/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class ImageOperations {
    
    // From normal to image coordinates
    public static int yTransform(int imageHeight, int y) {
        return imageHeight - y;
    }
    
    public static boolean insertCircle(BufferedImage image, int circleX, int circleY, int radius, 
                                                          Color color, BufferedImage fillImage) {
        boolean nonWhitePixelsOverwritten = false;
        
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
        
        // Loops which go through a rectangle pixel by pixel that will hold the circle to be drawn.
        while (y <= yTarget) {
            while (x <= xTarget) {
                
                // Circle expression: (x-x0)2 + (y-y0)2 <= radius2
                if ((x - circleX) * (x - circleX) + (y - circleY) * (y - circleY) <= radius * radius) {
                    
                    if (image.getRGB(x, yTransform(image.getHeight(), y)) != Color.WHITE.getRGB()) {
                        nonWhitePixelsOverwritten = true;
                    }
                    
                    if (color != null) {  // Fill circle with given color.
                        image.setRGB(x, yTransform(image.getHeight(), y), color.getRGB());
                    } else {            // Fill circle with given image.
                        image.setRGB(x, yTransform(image.getHeight(), y), fillImage.getRGB(x, yTransform(image.getHeight(), y)));
                    }
                }
                
                ++x;
            }
            
            x = xStart;
            ++y;
        }
        
        return nonWhitePixelsOverwritten;
    }
    
}
