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
    
    public static int countYellowBuddies(BufferedImage image, int x, int y) {
        int[] clockwiseX = {0, 1, 0, 0,-1,-1, 0, 0};
        int[] clockwiseY = {1, 0,-1,-1, 0, 0, 1, 1};
        
        int count = 0;
        
        int i = 0;
        while(i < clockwiseX.length){
            x += clockwiseX[i];
            y += clockwiseY[i];
            if(0 <= x && x < image.getWidth() && 0 <= y && y < image.getHeight() && image.getRGB(x, yTransform(image, y)) == Color.YELLOW.getRGB()) ++count;
            ++i;
        }
        return count;
    }
    
    public static void imageSquareAreaPixelLooper(int xStart, int yStart, int xTarget, int yTarget, 
                                             PixelHandler pixelHandler) {
        int x = xStart;
        int y = yStart;
        
        // Loops which go through a rectangle pixel by pixel.
        while (y <= yTarget) {
            while (x <= xTarget) {
                pixelHandler.handle(x, y);
                ++x;
            }
            x = xStart; 
            ++y;
        }             
    }
    
    public static BufferedImage newImageAsCombinationOfFrontAndBackground(BufferedImage frontImage, BufferedImage background) {
        BufferedImage combined = new BufferedImage(frontImage.getWidth(), frontImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        imageSquareAreaPixelLooper(0, 0, frontImage.getWidth() - 1, frontImage.getHeight() - 1, 
            (x, y)->{
                if (frontImage.getRGB(x, y) == Color.WHITE.getRGB()) {
                    combined.setRGB(x, y, background.getRGB(x, y));
                } else {
                    combined.setRGB(x, y, frontImage.getRGB(x, y));
                }                    
            });
        
        return combined; 
    }
    
    public static int countNonWhitePixelsFromDefinedImageArea(BufferedImage image, int minX, int maxX, int minY, int maxY) {
        int[] nonWhitePixels = {0};
        
        imageSquareAreaPixelLooper(minX, minY, maxX, maxY,
            (x, y)->{
                if (image.getRGB(x, ImageOperations.yTransform(image, y)) != Color.WHITE.getRGB()) {
                    ++nonWhitePixels[0];
                }                  
            });
        
        return nonWhitePixels[0];
    }
    
}
