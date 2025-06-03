import java.awt.*;
import java.awt.image.BufferedImage;

public class ObstacleCar {
    // Obstacle types
    public static final int TYPE_CAR = 0;
    public static final int TYPE_TRUCK = 1;
    
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private BufferedImage sprite;
    private boolean isActive;
    private int type;
    
    // For truck type (trailer part)
    private BufferedImage trailerSprite;
    private int trailerY; // Separate Y position for the trailer
    
    /**
     * Constructor for ObstacleCar
     * @param x Initial x position
     * @param y Initial y position
     * @param speed Movement speed
     * @param type Type of obstacle (car or truck)
     */
    public ObstacleCar(int x, int y, int speed, int type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
        this.isActive = true;
        
        if (type == TYPE_CAR) {
            this.sprite = AssetLoader.loadImage(AssetLoader.ENEMY_CAR);
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
        } else if (type == TYPE_TRUCK) {
            // Load truck cab sprite (front part)
            this.sprite = AssetLoader.loadImage(AssetLoader.TRUCK_CAB);
            // Load truck trailer sprite (back part)
            this.trailerSprite = AssetLoader.loadImage(AssetLoader.TRUCK_TRAILER);
            
            this.width = Math.max(sprite.getWidth(), trailerSprite.getWidth());
            
            // Total height is combined height with some overlap
            int overlap = 40; // Adjust this value for how much the trailer overlaps with the cab
            this.height = sprite.getHeight() + trailerSprite.getHeight() - overlap;
            
            // Position cab at the front (y)
            // Position trailer BEHIND the cab (y - trailerHeight + overlap)
            this.trailerY = y + sprite.getHeight() - overlap; // Trailer is BEHIND the cab
        }
    }
    
    /**
     * Updates the obstacle car position
     * @param gameHeight Height of the game panel
     */
    public void update(int gameHeight) {
        // Update positions
        y += speed;
        
        if (type == TYPE_TRUCK) {
            trailerY += speed;
        }
        
        // Deactivate if car/truck has gone off screen
        if (type == TYPE_CAR) {
            if (y > gameHeight) {
                isActive = false;
            }
        } else if (type == TYPE_TRUCK) {
            // For truck, deactivate when trailer (bottom part) goes off screen
            if (trailerY > gameHeight) {
                isActive = false;
            }
        }
    }
    
    /**
     * Renders the obstacle car
     * @param g2d Graphics2D object for rendering
     */
    public void render(Graphics2D g2d) {
        if (!isActive) return;
        
        if (type == TYPE_CAR) {
            g2d.drawImage(sprite, x, y, null);
        } else if (type == TYPE_TRUCK) {
            // Draw the cab (it's in front/below the trailer)
            g2d.drawImage(sprite, x, y, null);
            // Draw the trailer (it's behind/above the cab)
            g2d.drawImage(trailerSprite, x, trailerY, null);
        }
    }
    
    /**
     * Returns the bounding rectangle for collision detection
     * @return Rectangle representing car's bounds
     */
    public Rectangle getBounds() {
        if (type == TYPE_CAR) {
            return new Rectangle(x, y, width, height);
        } else if (type == TYPE_TRUCK) {
            // For truck, create a bounding box that includes cab and trailer with overlap
            return new Rectangle(
                x, 
                trailerY, // Start from the trailer's top
                width,
                height // Use the adjusted height with overlap
            );
        }
        return new Rectangle(x, y, width, height);
    }
    
    // Getters and setters
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getType() {
        return type;
    }
}
