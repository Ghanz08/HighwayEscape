import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class StartMenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 700;
    private static final Color BACKGROUND_COLOR = new Color(12, 66, 90); // Dark blue like in the image
    
    private BufferedImage carImage;
    private Point carPosition;
    private java.util.List<SmokeParticle> smokeParticles;
    private java.util.Random random;
    
    private Font titleFont;
    private Font buttonFont;
    
    private StartMenuListener listener;
    private Timer animationTimer;
    
    public StartMenuPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        
        carImage = AssetLoader.loadImage(AssetLoader.PLAYER_CAR);
        carPosition = new Point(120, 80);
        
        // Initialize smoke effect
        smokeParticles = new java.util.ArrayList<>();
        random = new java.util.Random();
        
        // Initialize fonts
        titleFont = new Font("Arial", Font.ITALIC, 44);
        buttonFont = new Font("Arial", Font.BOLD, 18);
        
        // Add key listener for Enter key
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && listener != null) {
                    listener.onStartGame();
                }
            }
        });
        
        // Start animation timer
        animationTimer = new Timer(50, e -> {
            updateSmoke();
            repaint();
        });
        animationTimer.start();
    }
    
    private void updateSmoke() {
        // Add new smoke particles occasionally
        if (random.nextInt(3) == 0) {
            smokeParticles.add(new SmokeParticle(
                    carPosition.x + 100, // Position near the car's rear
                    carPosition.y + 120,
                    random.nextInt(20) - 10, // Random X velocity
                    -random.nextInt(5) - 1,  // Upward Y velocity
                    random.nextInt(20) + 10  // Random size
            ));
        }
        
        // Update existing particles
        for (int i = smokeParticles.size() - 1; i >= 0; i--) {
            SmokeParticle particle = smokeParticles.get(i);
            particle.update();
            
            // Remove old particles
            if (particle.alpha <= 0) {
                smokeParticles.remove(i);
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fill background with the dark blue color
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        // Draw smoke particles
        for (SmokeParticle particle : smokeParticles) {
            particle.draw(g2d);
        }
        
        // Draw car (rotated slightly like in the image)
        if (carImage != null) {
            g2d.rotate(Math.toRadians(-15), carPosition.x + carImage.getWidth()/2, 
                    carPosition.y + carImage.getHeight()/2);
            g2d.drawImage(carImage, carPosition.x, carPosition.y, null);
            g2d.rotate(Math.toRadians(15), carPosition.x + carImage.getWidth()/2, 
                    carPosition.y + carImage.getHeight()/2);
        }
        
        // Draw "Highway Escape" title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        g2d.drawString("Highway Escape", 140, 120);
        
        // Draw "Press Enter" button with rounded rectangle background
        g2d.setColor(new Color(128, 128, 128, 180)); // Semi-transparent grey
        int buttonWidth = 150;
        int buttonHeight = 40;
        int buttonX = (PANEL_WIDTH - buttonWidth) / 2;
        int buttonY = 170;
        g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
        
        // Draw button text
        g2d.setColor(Color.WHITE);
        g2d.setFont(buttonFont);
        FontMetrics metrics = g2d.getFontMetrics(buttonFont);
        String buttonText = "Press Enter";
        int textX = buttonX + (buttonWidth - metrics.stringWidth(buttonText)) / 2;
        int textY = buttonY + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(buttonText, textX, textY);
    }
    
    public void setStartMenuListener(StartMenuListener listener) {
        this.listener = listener;
    }
    
    // Helper class for smoke particle animation
    private static class SmokeParticle {
        float x, y;
        float xVel, yVel;
        float size;
        float alpha;
        
        public SmokeParticle(float x, float y, float xVel, float yVel, float size) {
            this.x = x;
            this.y = y;
            this.xVel = xVel;
            this.yVel = yVel;
            this.size = size;
            this.alpha = 0.8f;
        }
        
        public void update() {
            x += xVel;
            y += yVel;
            size += 0.5f;
            alpha -= 0.02f;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
    
    // Interface for menu callbacks
    public interface StartMenuListener {
        void onStartGame();
    }
}
