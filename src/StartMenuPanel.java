import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class StartMenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 700;
    private static final Color BACKGROUND_COLOR = new Color(12, 66, 90);
    
    private BufferedImage carImage;
    private Point carPosition;
    private List<SmokeParticle> smokeParticles;
    private Random random;
    private Font titleFont, buttonFont;
    private StartMenuListener listener;
    private Timer animationTimer;
    private HighScoreManager highScoreManager;
    
    public StartMenuPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        
        carImage = AssetLoader.loadImage(AssetLoader.PLAYER_CAR);
        carPosition = new Point(120, 80);
        smokeParticles = new ArrayList<>();
        random = new Random();
        titleFont = new Font("Arial", Font.ITALIC, 44);
        buttonFont = new Font("Arial", Font.BOLD, 18);
        highScoreManager = new HighScoreManager();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && listener != null) {
                    SoundManager.getInstance().playSound("button");
                    listener.onStartGame();
                } else if (e.getKeyCode() == KeyEvent.VK_H) {
                    SoundManager.getInstance().playSound("button");
                    showHighScores();
                }
            }
        });
        
        animationTimer = new Timer(50, e -> {
            updateSmoke();
            repaint();
        });
        animationTimer.start();
    }
    
    private void showHighScores() {
        StringBuilder sb = new StringBuilder("HIGH SCORES\n\n");
        List<HighScoreManager.HighScoreEntry> scores = highScoreManager.getHighScores();
        
        for (int i = 0; i < scores.size(); i++) {
            HighScoreManager.HighScoreEntry entry = scores.get(i);
            sb.append(String.format("%d. %s\n", i + 1, entry.toString()));
        }
        
        if (scores.isEmpty()) {
            sb.append("No high scores yet!\nPlay the game to set your first record!");
        }
        
        javax.swing.JOptionPane.showMessageDialog(this, sb.toString(), "High Scores", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateSmoke() {
        if (random.nextInt(3) == 0) {
            smokeParticles.add(new SmokeParticle(
                    carPosition.x + 100,
                    carPosition.y + 120,
                    random.nextInt(20) - 10,
                    -random.nextInt(5) - 1,
                    random.nextInt(20) + 10
            ));
        }
        
        for (int i = smokeParticles.size() - 1; i >= 0; i--) {
            SmokeParticle particle = smokeParticles.get(i);
            particle.update();
            
            if (particle.alpha <= 0) {
                smokeParticles.remove(i);
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw smoke particles
        for (SmokeParticle particle : smokeParticles) {
            particle.draw(g2d);
        }
        
        // Draw car
        if (carImage != null) {
            g2d.rotate(Math.toRadians(-15), carPosition.x + carImage.getWidth()/2, 
                    carPosition.y + carImage.getHeight()/2);
            g2d.drawImage(carImage, carPosition.x, carPosition.y, null);
            g2d.rotate(Math.toRadians(15), carPosition.x + carImage.getWidth()/2, 
                    carPosition.y + carImage.getHeight()/2);
        }
        
        // Title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        g2d.drawString("Highway Escape", 140, 120);
        
        // Start Game Button
        g2d.setColor(new Color(128, 128, 128, 180));
        int buttonWidth = 150, buttonHeight = 40;
        int buttonX = (PANEL_WIDTH - buttonWidth) / 2;
        int buttonY = 170;
        g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(buttonFont);
        FontMetrics metrics = g2d.getFontMetrics(buttonFont);
        String buttonText = "Press Enter";
        int textX = buttonX + (buttonWidth - metrics.stringWidth(buttonText)) / 2;
        int textY = buttonY + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(buttonText, textX, textY);
        
        // High Scores Button
        g2d.setColor(new Color(100, 100, 100, 160));
        int highScoreButtonY = buttonY + 60;
        g2d.fillRoundRect(buttonX, highScoreButtonY, buttonWidth, buttonHeight, 20, 20);
        
        g2d.setColor(Color.WHITE);
        String highScoreText = "High Scores (H)";
        int highScoreTextX = buttonX + (buttonWidth - metrics.stringWidth(highScoreText)) / 2;
        int highScoreTextY = highScoreButtonY + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(highScoreText, highScoreTextX, highScoreTextY);
        
        // Instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(new Color(255, 255, 255, 200));
        String instruction1 = "Enter - Start Game";
        String instruction2 = "H - View High Scores";
        
        FontMetrics smallMetrics = g2d.getFontMetrics();
        int inst1X = (PANEL_WIDTH - smallMetrics.stringWidth(instruction1)) / 2;
        int inst2X = (PANEL_WIDTH - smallMetrics.stringWidth(instruction2)) / 2;
        
        g2d.drawString(instruction1, inst1X, PANEL_HEIGHT - 80);
        g2d.drawString(instruction2, inst2X, PANEL_HEIGHT - 60);
    }
    
    public void setStartMenuListener(StartMenuListener listener) {
        this.listener = listener;
    }
    
    private static class SmokeParticle {
        float x, y, xVel, yVel, size, alpha;
        
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
    
    public interface StartMenuListener {
        void onStartGame();
    }
}
