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
    
    // Asset paths - replace these with your actual file paths
    public static final String PLAYER_CAR = "assets/player_car.png";
    public static final String ENEMY_CAR = "assets/enemy_car.png";
    public static final String ROAD_BACKGROUND = "assets/road.png";
    public static final String TRUCK_CAB = "assets/truck_cab.png";
    public static final String TRUCK_TRAILER = "assets/truck_trailer.png";
    
    // Embedded road background as a fallback if file is not found
    private static BufferedImage embeddedRoadBackground = null;
    
    /**
     * Loads an image from the given path
     * @param path Path to the image file
     * @return BufferedImage object
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
            
            // If it's the road background, use the embedded one
            if (path.equals(ROAD_BACKGROUND)) {
                return getEmbeddedRoadBackground();
            }
            
            // If it's truck assets and they don't exist, create placeholders
            if (path.equals(TRUCK_CAB)) {
                return createTruckCab();
            }
            if (path.equals(TRUCK_TRAILER)) {
                return createTruckTrailer();
            }
            
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Provides the embedded road background as a fallback
     * @return BufferedImage of default road background
     */
    private static BufferedImage getEmbeddedRoadBackground() {
        if (embeddedRoadBackground == null) {
            // Create a simple road background with similar appearance to the provided image
            embeddedRoadBackground = new BufferedImage(500, 700, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = embeddedRoadBackground.createGraphics();
            
            // Fill with road color (dark purple/gray like in the image)
            g.setColor(new Color(118, 110, 129));
            g.fillRect(0, 0, 500, 700);
            
            // Draw the road markings (white lines like in the image)
            g.setColor(Color.WHITE);
            
            // Draw 4 vertical lines similar to the provided image
            int lineWidth = 10;
            int[] xPositions = {210, 290, 370, 450};
            for (int x : xPositions) {
                g.fillRect(x - (lineWidth/2), 0, lineWidth, 700);
            }
            
            g.dispose();
        }
        return embeddedRoadBackground;
    }
    
    /**
     * Sets a custom road background image
     * @param roadImage The image to use as road background
     */
    public static void setRoadBackground(BufferedImage roadImage) {
        if (roadImage != null) {
            imageCache.put(ROAD_BACKGROUND, roadImage);
        }
    }
    
    /**
     * Creates a placeholder truck cab image
     * @return BufferedImage truck cab
     */
    private static BufferedImage createTruckCab() {
        BufferedImage cabImage = new BufferedImage(80, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cabImage.createGraphics();
        
        // Draw a blue cab
        g.setColor(new Color(30, 50, 150));
        g.fillRect(0, 0, 80, 60);
        
        // Add cab details
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(10, 10, 60, 20); // windshield
        
        g.dispose();
        
        imageCache.put(TRUCK_CAB, cabImage);
        return cabImage;
    }
    
    /**
     * Creates a placeholder truck trailer image
     * @return BufferedImage truck trailer
     */
    private static BufferedImage createTruckTrailer() {
        BufferedImage trailerImage = new BufferedImage(80, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = trailerImage.createGraphics();
        
        // Draw a gray trailer
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 80, 120);
        
        // Add trailer details
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
