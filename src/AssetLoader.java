import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetLoader {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    // Asset paths
    public static final String PLAYER_CAR = "assets/player_car.png";
    public static final String ENEMY_CAR = "assets/enemy_car.png";
    public static final String ROAD_BACKGROUND = "assets/road.png";
    public static final String TRUCK_CAB = "assets/truck_cab.png";
    public static final String TRUCK_TRAILER = "assets/truck_trailer.png";
    
    /**
     * Loads an image from the given path
     */
    public static BufferedImage loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            BufferedImage image = ImageIO.read(new File(path));
            imageCache.put(path, image);
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            
            if (path.equals(ROAD_BACKGROUND)) {
                return createDefaultRoadBackground();
            }
            if (path.equals(TRUCK_CAB)) {
                return createTruckCab();
            }
            if (path.equals(TRUCK_TRAILER)) {
                return createTruckTrailer();
            }
            
            return null;
        }
    }
    
    private static BufferedImage createDefaultRoadBackground() {
        BufferedImage roadImage = new BufferedImage(500, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = roadImage.createGraphics();
        
        g.setColor(new Color(118, 110, 129));
        g.fillRect(0, 0, 500, 700);
        
        g.setColor(Color.WHITE);
        int lineWidth = 10;
        int[] xPositions = {210, 290, 370, 450};
        for (int x : xPositions) {
            g.fillRect(x - (lineWidth/2), 0, lineWidth, 700);
        }
        
        g.dispose();
        imageCache.put(ROAD_BACKGROUND, roadImage);
        return roadImage;
    }
    
    private static BufferedImage createTruckCab() {
        BufferedImage cabImage = new BufferedImage(80, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cabImage.createGraphics();
        
        g.setColor(new Color(30, 50, 150));
        g.fillRect(0, 0, 80, 60);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(10, 10, 60, 20);
        
        g.dispose();
        imageCache.put(TRUCK_CAB, cabImage);
        return cabImage;
    }
    
    private static BufferedImage createTruckTrailer() {
        BufferedImage trailerImage = new BufferedImage(80, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = trailerImage.createGraphics();
        
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 80, 120);
        g.setColor(Color.BLACK);
        g.drawRect(5, 5, 70, 110);
        
        g.dispose();
        imageCache.put(TRUCK_TRAILER, trailerImage);
        return trailerImage;
    }
    
    /**
     * Preloads all game assets into the cache
     */
    public static void preloadAssets() {
        loadImage(PLAYER_CAR);
        loadImage(ENEMY_CAR);
        loadImage(ROAD_BACKGROUND);
        loadImage(TRUCK_CAB);
        loadImage(TRUCK_TRAILER);
    }
}
