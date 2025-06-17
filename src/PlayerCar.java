import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerCar {
    private int x, y, width, height, speed;
    private BufferedImage sprite;
    private boolean isMovingLeft, isMovingRight, isMovingUp, isMovingDown;
    private int lastMoveDirection = 0;
    private int minY = 300, maxY = 650;
    
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
    }
    
    /**
     * Updates the player car position based on movement flags
     * @param gameWidth Width of the game panel
     * @param gameHeight Height of the game panel
     */
    public void update(int gameWidth, int gameHeight) {
        lastMoveDirection = 0;
        
        if (isMovingLeft) {
            x -= speed;
            lastMoveDirection = -1;
        }
        if (isMovingRight) {
            x += speed;
            lastMoveDirection = 1;
        }
        if (isMovingUp) y -= speed;
        if (isMovingDown) y += speed;
        
        // Bounds checking
        if (x < 0) x = 0;
        if (x + width > gameWidth) x = gameWidth - width;
        if (y < minY) y = minY;
        if (y + height > maxY) y = maxY - height;
    }
    
    /**
     * Renders the player car and optional skid marks
     * @param g2d Graphics2D object for rendering
     */
    public void render(Graphics2D g2d) {
        // Draw skid marks
        if (lastMoveDirection != 0) {
            g2d.setColor(Color.BLACK);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            
            if (lastMoveDirection < 0) {
                g2d.fillRect(x + 10, y + height - 5, 5, 20);
            } else {
                g2d.fillRect(x + width - 15, y + height - 5, 5, 20);
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw engine exhaust
        if (isMovingUp) {
            g2d.setColor(new Color(100, 100, 100, 100));
            g2d.fillOval(x + width/2 - 5, y + height, 10, 15);
        }
        
        g2d.drawImage(sprite, x, y, null);
    }
    
    /**
     * Returns the bounding rectangle for collision detection
     * @return Rectangle representing car's bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    // Movement setters
    public void setMovingLeft(boolean isMovingLeft) { this.isMovingLeft = isMovingLeft; }
    public void setMovingRight(boolean isMovingRight) { this.isMovingRight = isMovingRight; }
    public void setMovingUp(boolean isMovingUp) { this.isMovingUp = isMovingUp; }
    public void setMovingDown(boolean isMovingDown) { this.isMovingDown = isMovingDown; }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isMovingForward() { return isMovingUp; }
    public boolean isMovingBackward() { return isMovingDown; }
}
