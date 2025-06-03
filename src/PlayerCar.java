import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerCar {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private BufferedImage sprite;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private int lastMoveDirection = 0; // 0 = none, -1 = left, 1 = right
    
    /**
     * Constructor for PlayerCar
     * @param x Initial x position
     * @param y Initial y position
     * @param speed Movement speed
     */
    public PlayerCar(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.sprite = AssetLoader.loadImage(AssetLoader.PLAYER_CAR);
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.isMovingLeft = false;
        this.isMovingRight = false;
    }
    
    /**
     * Updates the player car position based on movement flags
     * @param gameWidth Width of the game panel
     */
    public void update(int gameWidth) {
        lastMoveDirection = 0;
        
        if (isMovingLeft) {
            x -= speed;
            lastMoveDirection = -1;
        }
        if (isMovingRight) {
            x += speed;
            lastMoveDirection = 1;
        }
        
        // Keep the car within the game bounds
        if (x < 0) {
            x = 0;
        }
        if (x + width > gameWidth) {
            x = gameWidth - width;
        }
    }
    
    /**
     * Renders the player car and optional skid marks
     * @param g2d Graphics2D object for rendering
     */
    public void render(Graphics2D g2d) {
        // Draw skid marks if car is moving sideways
        if (lastMoveDirection != 0) {
            g2d.setColor(Color.BLACK);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            
            // Left tire skid mark
            if (lastMoveDirection < 0) {
                g2d.fillRect(x + 10, y + height - 5, 5, 20);
            }
            // Right tire skid mark
            else if (lastMoveDirection > 0) {
                g2d.fillRect(x + width - 15, y + height - 5, 5, 20);
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw the car sprite
        g2d.drawImage(sprite, x, y, null);
    }
    
    /**
     * Returns the bounding rectangle for collision detection
     * @return Rectangle representing car's bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    // Getters and setters
    public void setMovingLeft(boolean isMovingLeft) {
        this.isMovingLeft = isMovingLeft;
    }
    
    public void setMovingRight(boolean isMovingRight) {
        this.isMovingRight = isMovingRight;
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
}
