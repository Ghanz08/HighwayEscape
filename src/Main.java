import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create assets directory if it doesn't exist
        File assetsDir = new File("assets");
        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }
        
        // Create default assets if they don't exist
        createDefaultAssetIfMissing("road.png", createDefaultRoadImage());
        createDefaultAssetIfMissing("player_car.png", createDefaultPlayerCarImage());
        createDefaultAssetIfMissing("enemy_car.png", createDefaultEnemyCarImage());
        createDefaultAssetIfMissing("truck_cab.png", createDefaultTruckCabImage());
        createDefaultAssetIfMissing("truck_trailer.png", createDefaultTruckTrailerImage());
        
        // Create and show the game frame on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame();
            gameFrame.setVisible(true);
        });
    }
    
    /**
     * Creates a default asset file if it doesn't exist
     * @param filename The filename to create
     * @param image The image data to write
     */
    private static void createDefaultAssetIfMissing(String filename, BufferedImage image) {
        File file = new File("assets/" + filename);
        if (!file.exists()) {
            try {
                ImageIO.write(image, "PNG", file);
                System.out.println("Created default " + filename + " at: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create " + filename + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates a default road image that matches the provided road image
     * @return BufferedImage containing the road design
     */
    private static BufferedImage createDefaultRoadImage() {
        BufferedImage roadImage = new BufferedImage(500, 700, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = roadImage.createGraphics();
        
        // Fill with road color (dark purple/gray like in the image)
        g.setColor(new java.awt.Color(118, 110, 129));
        g.fillRect(0, 0, 500, 700);
        
        // Draw the road markings (white lines like in the image)
        g.setColor(java.awt.Color.WHITE);
        
        // Draw 4 vertical lines similar to the provided image
        int lineWidth = 10;
        int[] xPositions = {210, 290, 370, 450};
        for (int x : xPositions) {
            g.fillRect(x - (lineWidth/2), 0, lineWidth, 700);
        }
        
        g.dispose();
        return roadImage;
    }
    
    /**
     * Creates a default player car image
     * @return BufferedImage containing the player car design
     */
    private static BufferedImage createDefaultPlayerCarImage() {
        BufferedImage carImage = new BufferedImage(60, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = carImage.createGraphics();
        
        // Draw car body
        g.setColor(Color.RED);
        g.fillRect(10, 10, 40, 80);
        
        // Draw windows
        g.setColor(Color.CYAN);
        g.fillRect(15, 20, 30, 20);
        
        // Draw wheels
        g.setColor(Color.BLACK);
        g.fillRect(5, 15, 10, 20);
        g.fillRect(45, 15, 10, 20);
        g.fillRect(5, 65, 10, 20);
        g.fillRect(45, 65, 10, 20);
        
        g.dispose();
        return carImage;
    }
    
    /**
     * Creates a default enemy car image
     * @return BufferedImage containing the enemy car design
     */
    private static BufferedImage createDefaultEnemyCarImage() {
        BufferedImage carImage = new BufferedImage(60, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = carImage.createGraphics();
        
        // Draw car body
        g.setColor(Color.BLUE);
        g.fillRect(10, 10, 40, 80);
        
        // Draw windows
        g.setColor(Color.CYAN);
        g.fillRect(15, 20, 30, 20);
        
        // Draw wheels
        g.setColor(Color.BLACK);
        g.fillRect(5, 15, 10, 20);
        g.fillRect(45, 15, 10, 20);
        g.fillRect(5, 65, 10, 20);
        g.fillRect(45, 65, 10, 20);
        
        g.dispose();
        return carImage;
    }
    
    /**
     * Creates a default truck cab image
     * @return BufferedImage containing the truck cab design
     */
    private static BufferedImage createDefaultTruckCabImage() {
        BufferedImage cabImage = new BufferedImage(80, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cabImage.createGraphics();
        
        // Draw cab body
        g.setColor(new Color(30, 50, 150));
        g.fillRect(10, 10, 60, 40);
        
        // Draw windshield
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(15, 15, 50, 15);
        
        // Draw wheels
        g.setColor(Color.BLACK);
        g.fillRect(5, 30, 15, 20);
        g.fillRect(60, 30, 15, 20);
        
        g.dispose();
        return cabImage;
    }
    
    /**
     * Creates a default truck trailer image
     * @return BufferedImage containing the truck trailer design
     */
    private static BufferedImage createDefaultTruckTrailerImage() {
        BufferedImage trailerImage = new BufferedImage(80, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = trailerImage.createGraphics();
        
        // Draw trailer body
        g.setColor(new Color(50, 50, 50));
        g.fillRect(10, 0, 60, 110);
        
        // Draw trailer details
        g.setColor(Color.DARK_GRAY);
        g.drawRect(15, 10, 50, 90);
        
        // Draw wheels
        g.setColor(Color.BLACK);
        g.fillRect(5, 80, 15, 20);
        g.fillRect(60, 80, 15, 20);
        
        g.dispose();
        return trailerImage;
    }
}