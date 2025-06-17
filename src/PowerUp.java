import java.awt.*;

public class PowerUp {
    public enum PowerUpType {
        INVINCIBILITY(Color.YELLOW, 5000, "Invincible"),
        SCORE_MULTIPLIER(Color.ORANGE, 7000, "2x Score"),
        EXTRA_LIFE(Color.GREEN, 0, "Extra Life");
        
        private final Color color;
        private final int duration;
        private final String name;
        
        PowerUpType(Color color, int duration, String name) {
            this.color = color;
            this.duration = duration;
            this.name = name;
        }
        
        public Color getColor() { return color; }
        public int getDuration() { return duration; }
        public String getName() { return name; }
    }
    
    private int x, y;
    private final int width = 40, height = 40;
    private final PowerUpType type;
    private final long spawnTime;
    
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }
    
    public void update() {
        y += 3;
        
        double time = (System.currentTimeMillis() - spawnTime) / 1000.0;
        y += (int)(Math.sin(time * 3) * 5);
    }
    
    public void draw(Graphics2D g2d) {
        // Glowing effect
        g2d.setColor(new Color(type.getColor().getRed(), type.getColor().getGreen(), 
                              type.getColor().getBlue(), 50));
        g2d.fillOval(x - 10, y - 10, width + 20, height + 20);
        
        // Power-up circle
        g2d.setColor(type.getColor());
        g2d.fillOval(x, y, width, height);
        
        // Symbol
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String symbol = getSymbol();
        int textX = x + (width - fm.stringWidth(symbol)) / 2;
        int textY = y + (height + fm.getAscent()) / 2;
        g2d.drawString(symbol, textX, textY);
    }
    
    private String getSymbol() {
        switch (type) {
            case INVINCIBILITY: return "★";
            case SCORE_MULTIPLIER: return "2x";
            case EXTRA_LIFE: return "+1";
            default: return "?";
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean isOffScreen() {
        return y > 800;
    }
    
    // Getters
    public PowerUpType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
