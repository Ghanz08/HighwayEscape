import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        // Create assets directories
        createDirectories();
        
        // Create default assets if missing
        createDefaultAssets();
        
        // Initialize sound system
        SoundManager.getInstance();
        
        // Start game
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame();
            gameFrame.setVisible(true);
        });
    }
    
    private static void createDirectories() {
        new File("assets").mkdirs();
        new File("assets/sounds").mkdirs();
        new File("assets/music").mkdirs();
    }
    
    private static void createDefaultAssets() {
        createDefaultAssetIfMissing("road.png", createDefaultRoadImage());
        createDefaultAssetIfMissing("player_car.png", createDefaultPlayerCarImage());
        createDefaultAssetIfMissing("enemy_car.png", createDefaultEnemyCarImage());
        createDefaultAssetIfMissing("truck_cab.png", createDefaultTruckCabImage());
        createDefaultAssetIfMissing("truck_trailer.png", createDefaultTruckTrailerImage());
    }
    
    private static void createDefaultAssetIfMissing(String filename, BufferedImage image) {
        File file = new File("assets/" + filename);
        if (!file.exists()) {
            try {
                ImageIO.write(image, "PNG", file);
                System.out.println("Created default " + filename);
            } catch (IOException e) {
                System.err.println("Failed to create " + filename + ": " + e.getMessage());
            }
        }
    }
    
    private static BufferedImage createDefaultRoadImage() {
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
        return roadImage;
    }
    
    private static BufferedImage createDefaultPlayerCarImage() {
        BufferedImage carImage = new BufferedImage(60, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = carImage.createGraphics();
        
        g.setColor(Color.RED);
        g.fillRect(10, 10, 40, 80);
        g.setColor(Color.CYAN);
        g.fillRect(15, 20, 30, 20);
        g.setColor(Color.BLACK);
        g.fillRect(5, 15, 10, 20);
        g.fillRect(45, 15, 10, 20);
        g.fillRect(5, 65, 10, 20);
        g.fillRect(45, 65, 10, 20);
        
        g.dispose();
        return carImage;
    }
    
    private static BufferedImage createDefaultEnemyCarImage() {
        BufferedImage carImage = new BufferedImage(60, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = carImage.createGraphics();
        
        g.setColor(Color.BLUE);
        g.fillRect(10, 10, 40, 80);
        g.setColor(Color.CYAN);
        g.fillRect(15, 20, 30, 20);
        g.setColor(Color.BLACK);
        g.fillRect(5, 15, 10, 20);
        g.fillRect(45, 15, 10, 20);
        g.fillRect(5, 65, 10, 20);
        g.fillRect(45, 65, 10, 20);
        
        g.dispose();
        return carImage;
    }
    
    private static BufferedImage createDefaultTruckCabImage() {
        BufferedImage cabImage = new BufferedImage(80, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cabImage.createGraphics();
        
        g.setColor(new Color(30, 50, 150));
        g.fillRect(10, 10, 60, 40);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(15, 15, 50, 15);
        g.setColor(Color.BLACK);
        g.fillRect(5, 30, 15, 20);
        g.fillRect(60, 30, 15, 20);
        
        g.dispose();
        return cabImage;
    }
    
    private static BufferedImage createDefaultTruckTrailerImage() {
        BufferedImage trailerImage = new BufferedImage(80, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = trailerImage.createGraphics();
        
        g.setColor(new Color(50, 50, 50));
        g.fillRect(10, 0, 60, 110);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(15, 10, 50, 90);
        g.setColor(Color.BLACK);
        g.fillRect(5, 80, 15, 20);
        g.fillRect(60, 80, 15, 20);
        
        g.dispose();
        return trailerImage;
    }
}