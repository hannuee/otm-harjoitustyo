/**
 * @author Hannu Erälaukko
 */
package otmharjoitustyo.logic;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;

public class ImageOperations {
    
    // From normal to image coordinates
    public static int yTransform(BufferedImage image, int y) {
        return image.getHeight() - y;
    }
    
    public static void imageCircularAreaPixelLoops(int xStart, int yStart, int xTarget, int yTarget, 
                                            int circleX, int circleY, int radius, PixelHandler pixelHandler){
        int x = xStart;
        int y = yStart;
        
        // Loops which go through a rectangle pixel by pixel that will hold the circle.
        while (y <= yTarget) {
            while (x <= xTarget) {
                // Circle expression: (x-x0)2 + (y-y0)2 <= radius2
                if ((x - circleX) * (x - circleX) + (y - circleY) * (y - circleY) <= radius * radius) {
                    pixelHandler.handle(x, y);
                }
                ++x;
            }
            x = xStart; 
            ++y;
        }        
    }
    
    
    public static void imageCircularAreaHandler(BufferedImage image, int circleX, int circleY, int radius, PixelHandler pixelHandler){
        // Definition of a square area that holds the circle area.
        int yStart = circleY - radius;
        int xStart = circleX - radius;
        int yTarget = yStart + 2 * radius;
        int xTarget = xStart + 2 * radius;
        
        // Image boundary checks.
        if (yStart < 0) yStart = 0;
        if (xStart < 0) xStart = 0;
        if (image.getHeight() <= yTarget) yTarget = image.getHeight() - 1;
        if (image.getWidth() <= xTarget) xTarget = image.getWidth() - 1;
        
        
        imageCircularAreaPixelLoops(xStart, yStart, xTarget, yTarget, 
                                    circleX, circleY, radius, pixelHandler);
    }
    
    // image handler,  pixel handler
    public static boolean insertCircle(BufferedImage image, int circleX, int circleY, int radius, 
                                                          Color color, BufferedImage fillImage) {
        int[] nonWhitePixelsOverwritten = {0};
        
        imageCircularAreaHandler(image, circleX, circleY, radius, 
                (x, y)->{
                    if (image.getRGB(x, yTransform(image, y)) != Color.WHITE.getRGB()) {
                        nonWhitePixelsOverwritten[0] = 1;
                    }
                    
                    if (color != null) {  // Fill circle with given color.
                        image.setRGB(x, yTransform(image, y), color.getRGB());
                    } else {            // Fill circle with given image.
                        image.setRGB(x, yTransform(image, y), fillImage.getRGB(x, yTransform(image, y)));
                    }                
                });
        
        return nonWhitePixelsOverwritten[0] == 1;
    }
    
    // advance päällä: Kasvata keltaista alueta punaisella kaverisääntöjen ja satunnaisuuden perusteella.
    // pois päältä:    Vaihda punaiset keltaisiksi.
    public static void explosionAdvancer(BufferedImage image, int explosionSeed, 
                                   int circleX, int circleY, int radius, 
                                   boolean advance, Color color, Color replace, BufferedImage fillImage) {
        Random random = new Random(explosionSeed);
        
        imageCircularAreaHandler(image, circleX, circleY, radius, 
                (x, y)->{
                    if (color != null && replace != null) {
                        if (image.getRGB(x, yTransform(image, y)) == color.getRGB()) {
                            image.setRGB(x, yTransform(image, y), replace.getRGB()); 
                        }
                    } else if (color != null && fillImage != null) {
                        if (image.getRGB(x, yTransform(image, y)) == color.getRGB()) {
                            image.setRGB(x, yTransform(image, y), fillImage.getRGB(x, yTransform(image, y))); 
                        }
                    } else if (advance) {
                        if (image.getRGB(x, yTransform(image, y)) != Color.YELLOW.getRGB()
                            && 3 <= ImageOperations.countYellowBuddies(image, x, y) - random.nextInt(3)) {
                            image.setRGB(x, yTransform(image, y), Color.RED.getRGB()); 
                        }
                    }
                });
    }
    
    public static int jee(BufferedImage image, int x, int y, 
                          boolean upOK, boolean rightOK, boolean downOK, boolean leftOK) {
        int count = 0;
        
        int color;
        if (upOK) {
            color = image.getRGB(x, yTransform(image, y + 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (upOK && rightOK) {
            color = image.getRGB(x + 1, yTransform(image, y + 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (rightOK) {
            color = image.getRGB(x + 1, yTransform(image, y));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (downOK && rightOK) {
            color = image.getRGB(x + 1, yTransform(image, y - 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (downOK) {
            color = image.getRGB(x, yTransform(image, y - 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (downOK && leftOK) {
            color = image.getRGB(x - 1, yTransform(image, y - 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (leftOK) {
            color = image.getRGB(x - 1, yTransform(image, y));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        if (upOK && leftOK) {
            color = image.getRGB(x - 1, yTransform(image, y + 1));
            if (color == Color.YELLOW.getRGB()) {
                ++count;
            }
        }
        
        return count;
    }
    
    public static int countYellowBuddies(BufferedImage image, int x, int y) {
        // Image boundary checks.
        boolean upOK = y + 1 < image.getHeight();
        boolean rightOK = x + 1 < image.getWidth();
        boolean downOK = 0 <= y - 1;
        boolean leftOK = 0 <= x - 1;
        
        return jee(image, x, y, 
                   upOK, rightOK, downOK, leftOK);
    }
    
    public static BufferedImage createNewImageAsCombinationOfFrontImageAndBackground(BufferedImage frontImage, BufferedImage background) {
        BufferedImage combined = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        int yMax = frontImage.getHeight();
        int xMax = frontImage.getWidth();
        
        int y = 0;
        int x = 0;
        while (y < yMax) {
            while (x < xMax) {
                
                if (frontImage.getRGB(x, y) == Color.WHITE.getRGB()) {
                    combined.setRGB(x, y, background.getRGB(x, y));
                } else {
                    combined.setRGB(x, y, frontImage.getRGB(x, y));
                }
                
                ++x;
            }
            
            x = 0;
            ++y;
        }
        
        return combined;
    }
    
    public static int countNonWhitePixelsFromDefinedImageArea(BufferedImage image, int minX, int maxX, int minY, int maxY) {
        int nonWhitePixels = 0;
        
        int yIndex = minY;
        int xIndex = minX;
        while (yIndex < maxY) {
            while (xIndex < maxX) {
                
                if (image.getRGB(xIndex, ImageOperations.yTransform(image, yIndex)) != Color.WHITE.getRGB()) {
                    ++nonWhitePixels;
                }
                
                ++xIndex;
            }
            
            xIndex = minX;
            ++yIndex;
        }
        
        return nonWhitePixels;
    }
    
}
