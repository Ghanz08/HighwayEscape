import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    // Game constants
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 700;
    private static final int PLAYER_SPEED = 5;
    private static final int OBSTACLE_SPEED = 3;
    private static final int OBSTACLE_SPAWN_INTERVAL = 2000; // milliseconds
    private static final double TRUCK_SPAWN_PROBABILITY = 0.3; // 30% chance to spawn a truck
    
    // Game state
    private boolean isGameRunning;
    private boolean isGameOver;
    private long gameStartTime;
    private long score;
    
    // Game objects
    private PlayerCar playerCar;
    private List<ObstacleCar> obstacles;
    private Timer gameTimer;
    private Timer obstacleSpawnTimer;
    private Random random;
    
    // Background scrolling
    private BufferedImage backgroundImage;
    private int backgroundY1;
    private int backgroundY2;
    private int backgroundSpeed;
    
    private GameOverListener gameOverListener;
    
    /**
     * Constructor for GamePanel
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Initialize variables
        obstacles = new ArrayList<>();
        random = new Random();
        isGameRunning = false;
        isGameOver = false;
        score = 0;
        
        // Initialize background
        backgroundImage = AssetLoader.loadImage(AssetLoader.ROAD_BACKGROUND);
        backgroundY1 = 0;
        backgroundY2 = -PANEL_HEIGHT;
        backgroundSpeed = 5;
        
        // Set up the game timer (60 FPS)
        gameTimer = new Timer(1000 / 60, this);
        
        // Set up obstacle spawn timer
        obstacleSpawnTimer = new Timer(OBSTACLE_SPAWN_INTERVAL, e -> spawnObstacle());
        
        // Set up key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e.getKeyCode());
            }
        });
        
        // Initialize the game
        initGame();
    }
    
    /**
     * Initialize the game state
     */
    public void initGame() {
        // Load assets
        AssetLoader.preloadAssets();
        
        // Create player car in the middle bottom of the screen
        int playerX = (PANEL_WIDTH - AssetLoader.loadImage(AssetLoader.PLAYER_CAR).getWidth()) / 2;
        int playerY = PANEL_HEIGHT - AssetLoader.loadImage(AssetLoader.PLAYER_CAR).getHeight() - 50;
        playerCar = new PlayerCar(playerX, playerY, PLAYER_SPEED);
        
        // Clear any existing obstacles
        obstacles.clear();
        
        // Reset game state
        isGameOver = false;
        score = 0;
        
        // Reset background positions
        backgroundY1 = 0;
        backgroundY2 = -PANEL_HEIGHT;
        
        // Start timers if not already started
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        if (!obstacleSpawnTimer.isRunning()) {
            obstacleSpawnTimer.start();
        }
        
        gameStartTime = System.currentTimeMillis();
        isGameRunning = true;
    }
    
    /**
     * Handle key press events
     * @param keyCode The key code of the pressed key
     */
    private void handleKeyPress(int keyCode) {
        if (isGameOver) {
            if (keyCode == KeyEvent.VK_SPACE) {
                initGame();
                return;
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                if (gameOverListener != null) {
                    gameOverListener.onReturnToMainMenu();
                }
                return;
            }
        }
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                playerCar.setMovingLeft(true);
                break;
            case KeyEvent.VK_RIGHT:
                playerCar.setMovingRight(true);
                break;
        }
    }
    
    /**
     * Handle key release events
     * @param keyCode The key code of the released key
     */
    private void handleKeyRelease(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                playerCar.setMovingLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                playerCar.setMovingRight(false);
                break;
        }
    }
    
    /**
     * Spawn a new obstacle at random position
     */
    private void spawnObstacle() {
        if (!isGameRunning || isGameOver) {
            return;
        }
        
        // Decide if we spawn a car or truck
        int obstacleType = (random.nextDouble() < TRUCK_SPAWN_PROBABILITY) ? 
            ObstacleCar.TYPE_TRUCK : ObstacleCar.TYPE_CAR;
        
        int obstacleWidth;
        int yPos;
        
        if (obstacleType == ObstacleCar.TYPE_CAR) {
            BufferedImage carSprite = AssetLoader.loadImage(AssetLoader.ENEMY_CAR);
            obstacleWidth = carSprite.getWidth();
            yPos = -carSprite.getHeight();
        } else {
            // For truck, we need both cab and trailer sprites
            BufferedImage cabSprite = AssetLoader.loadImage(AssetLoader.TRUCK_CAB);
            BufferedImage trailerSprite = AssetLoader.loadImage(AssetLoader.TRUCK_TRAILER);
            obstacleWidth = Math.max(cabSprite.getWidth(), trailerSprite.getWidth());
            
            // Position the cab at the top of the screen with trailer completely off-screen
            yPos = -cabSprite.getHeight();
        }
        
        // Calculate random x position
        int roadWidth = PANEL_WIDTH - 100; 
        int startX = 50; 
        int x = startX + random.nextInt(roadWidth - obstacleWidth);
        
        // Create the obstacle
        ObstacleCar obstacle = new ObstacleCar(x, yPos, OBSTACLE_SPEED, obstacleType);
        obstacles.add(obstacle);
    }
    
    /**
     * Update game logic
     */
    private void updateGame() {
        if (!isGameRunning || isGameOver) {
            return;
        }
        
        // Update score (time-based)
        score = (System.currentTimeMillis() - gameStartTime) / 100;
        
        // Update player
        playerCar.update(PANEL_WIDTH);
        
        // Update obstacles
        Iterator<ObstacleCar> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            ObstacleCar obstacle = iterator.next();
            obstacle.update(PANEL_HEIGHT);
            
            // Remove inactive obstacles
            if (!obstacle.isActive()) {
                iterator.remove();
                continue;
            }
            
            // Check for collision with player
            if (playerCar.getBounds().intersects(obstacle.getBounds())) {
                gameOver();
                return;
            }
        }
        
        // Update scrolling background
        updateBackground();
    }
    
    /**
     * Update the scrolling background
     */
    private void updateBackground() {
        backgroundY1 += backgroundSpeed;
        backgroundY2 += backgroundSpeed;
        
        // Reset background positions when they go off screen
        if (backgroundY1 >= PANEL_HEIGHT) {
            backgroundY1 = backgroundY2 - PANEL_HEIGHT;
        }
        if (backgroundY2 >= PANEL_HEIGHT) {
            backgroundY2 = backgroundY1 - PANEL_HEIGHT;
        }
    }
    
    /**
     * Handle game over
     */
    private void gameOver() {
        isGameOver = true;
        obstacleSpawnTimer.stop();
        // Keep game timer running for game over screen updates
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        if (backgroundImage != null) {
            // Draw the background image twice to create scrolling effect
            g2d.drawImage(backgroundImage, 0, backgroundY1, PANEL_WIDTH, PANEL_HEIGHT, null);
            g2d.drawImage(backgroundImage, 0, backgroundY2, PANEL_WIDTH, PANEL_HEIGHT, null);
        } else {
            // If background image is still null, try to load it again
            backgroundImage = AssetLoader.loadImage(AssetLoader.ROAD_BACKGROUND);
        }
        
        // Draw obstacles
        for (ObstacleCar obstacle : obstacles) {
            obstacle.render(g2d);
        }
        
        // Draw player
        if (playerCar != null) {
            playerCar.render(g2d);
        }
        
        // Draw score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        
        // Draw game over screen
        if (isGameOver) {
            drawGameOverScreen(g2d);
        }
    }
    
    /**
     * Draw the game over screen
     * @param g2d Graphics2D object for rendering
     */
    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = g2d.getFontMetrics();
        String gameOverText = "GAME OVER";
        int x = (PANEL_WIDTH - metrics.stringWidth(gameOverText)) / 2;
        g2d.drawString(gameOverText, x, PANEL_HEIGHT / 2 - 50);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        metrics = g2d.getFontMetrics();
        String scoreText = "Final Score: " + score;
        x = (PANEL_WIDTH - metrics.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, PANEL_HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        metrics = g2d.getFontMetrics();
        String restartText = "Press SPACE to restart";
        x = (PANEL_WIDTH - metrics.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, PANEL_HEIGHT / 2 + 50);
        
        // Add main menu button text
        String menuText = "Press ESC for main menu";
        x = (PANEL_WIDTH - metrics.stringWidth(menuText)) / 2;
        g2d.drawString(menuText, x, PANEL_HEIGHT / 2 + 80);
    }
    
    /**
     * Sets the game over listener
     * @param listener The listener to set
     */
    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }
    
    /**
     * Interface for game over callbacks
     */
    public interface GameOverListener {
        void onReturnToMainMenu();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }
}
