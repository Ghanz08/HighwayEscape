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
    private static final int OBSTACLE_SPAWN_INTERVAL = 2000;
    private static final double TRUCK_SPAWN_PROBABILITY = 0.3;
    
    // Game state
    private boolean isGameRunning;
    private boolean isGameOver;
    private boolean isPaused;
    private long gameStartTime;
    private long score;
    private int lives = 3;
    private int maxLives = 5;
    private int currentDifficulty = 1;
    
    // Game objects
    private PlayerCar playerCar;
    private List<ObstacleCar> obstacles;
    private List<PowerUp> powerUps;
    private Timer gameTimer;
    private Timer obstacleSpawnTimer;
    private Timer powerUpSpawnTimer;
    private Random random;
    
    // Enhanced systems
    private ParticleSystem particleSystem;
    private HighScoreManager highScoreManager;
    private SoundManager soundManager;
    
    // Power-up effects
    private boolean hasInvincibility = false;
    private boolean hasScoreMultiplier = false;
    private long invincibilityEndTime, scoreMultiplierEndTime;
    
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
        
        // Initialize systems
        obstacles = new ArrayList<>();
        powerUps = new ArrayList<>();
        random = new Random();
        particleSystem = new ParticleSystem();
        highScoreManager = new HighScoreManager();
        soundManager = SoundManager.getInstance();
        
        // Initialize game state
        isGameRunning = false;
        isGameOver = false;
        isPaused = false;
        score = 0;
        lives = 3;
        
        // Initialize background
        backgroundImage = AssetLoader.loadImage(AssetLoader.ROAD_BACKGROUND);
        backgroundY1 = 0;
        backgroundY2 = -PANEL_HEIGHT;
        backgroundSpeed = 5;
        
        // Set up timers
        gameTimer = new Timer(1000 / 60, this);
        obstacleSpawnTimer = new Timer(OBSTACLE_SPAWN_INTERVAL, e -> spawnObstacle());
        powerUpSpawnTimer = new Timer(8000, e -> spawnPowerUp());
        
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
        
        // Clear game objects
        obstacles.clear();
        powerUps.clear();
        particleSystem.clear();
        
        // Reset game state
        isGameOver = false;
        isPaused = false;
        score = 0;
        lives = 3;
        currentDifficulty = 1;
        hasInvincibility = false;
        hasScoreMultiplier = false;
        
        // Reset background
        backgroundY1 = 0;
        backgroundY2 = -PANEL_HEIGHT;
        
        // Start timers
        if (!gameTimer.isRunning()) gameTimer.start();
        if (!obstacleSpawnTimer.isRunning()) obstacleSpawnTimer.start();
        if (!powerUpSpawnTimer.isRunning()) powerUpSpawnTimer.start();
        
        gameStartTime = System.currentTimeMillis();
        isGameRunning = true;
        
        // Play game background music instead of engine sound
        soundManager.playMusic("game", true);
    }
    
    private void handleKeyPress(int keyCode) {
        if (isGameOver) {
            if (keyCode == KeyEvent.VK_SPACE) {
                initGame();
                return;
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                showHighScoreDialog();
                if (gameOverListener != null) {
                    gameOverListener.onReturnToMainMenu();
                }
                return;
            }
        }
        
        if (keyCode == KeyEvent.VK_P && isGameRunning) {
            togglePause();
            return;
        }
        
        if (isPaused) return;
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                playerCar.setMovingLeft(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                playerCar.setMovingRight(true);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                playerCar.setMovingUp(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                playerCar.setMovingDown(true);
                break;
            case KeyEvent.VK_M:
                soundManager.setMusicEnabled(!soundManager.isMusicEnabled());
                break;
        }
    }
    
    private void handleKeyRelease(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                playerCar.setMovingLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                playerCar.setMovingRight(false);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                playerCar.setMovingUp(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                playerCar.setMovingDown(false);
                break;
        }
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            obstacleSpawnTimer.stop();
            powerUpSpawnTimer.stop();
            // Pause background music
            soundManager.stopAllMusic();
        } else {
            obstacleSpawnTimer.start();
            powerUpSpawnTimer.start();
            // Resume background music
            soundManager.playMusic("game", true);
        }
    }
    
    /**
     * Spawn a new obstacle at random position
     */
    private void spawnObstacle() {
        if (!isGameRunning || isGameOver) return;
        
        // Decide if we spawn a car or truck
        int obstacleType = (random.nextDouble() < TRUCK_SPAWN_PROBABILITY) ? 
            ObstacleCar.TYPE_TRUCK : ObstacleCar.TYPE_CAR;
        
        int obstacleWidth, obstacleHeight, yPos;
        
        if (obstacleType == ObstacleCar.TYPE_CAR) {
            BufferedImage carSprite = AssetLoader.loadImage(AssetLoader.ENEMY_CAR);
            obstacleWidth = carSprite.getWidth();
            obstacleHeight = carSprite.getHeight();
            yPos = -carSprite.getHeight();
        } else {
            BufferedImage cabSprite = AssetLoader.loadImage(AssetLoader.TRUCK_CAB);
            BufferedImage trailerSprite = AssetLoader.loadImage(AssetLoader.TRUCK_TRAILER);
            obstacleWidth = Math.max(cabSprite.getWidth(), trailerSprite.getWidth());
            
            int overlap = 20;
            obstacleHeight = cabSprite.getHeight() + trailerSprite.getHeight() - overlap;
            yPos = -obstacleHeight;
        }
        
        int roadWidth = PANEL_WIDTH - 100; 
        int startX = 50;
        int maxAttempts = 10;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = startX + random.nextInt(roadWidth - obstacleWidth);
            Rectangle newObstacleBounds = new Rectangle(x, yPos, obstacleWidth, obstacleHeight);
            
            boolean canSpawn = true;
            for (ObstacleCar existingObstacle : obstacles) {
                if (existingObstacle.getY() < 150) {
                    Rectangle existingBounds = existingObstacle.getBounds();
                    Rectangle bufferedBounds = new Rectangle(
                        existingBounds.x - 10, 
                        existingBounds.y - 30, 
                        existingBounds.width + 20, 
                        existingBounds.height + 60
                    );
                    
                    if (newObstacleBounds.intersects(bufferedBounds)) {
                        canSpawn = false;
                        break;
                    }
                }
            }
            
            if (canSpawn) {
                obstacles.add(new ObstacleCar(x, yPos, OBSTACLE_SPEED, obstacleType));
                return;
            }
        }
    }
    
    /**
     * Spawn a new power-up at random position
     */
    private void spawnPowerUp() {
        if (!isGameRunning || isGameOver || isPaused) return;
        
        PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
        PowerUp.PowerUpType randomType = types[random.nextInt(types.length)];
        
        int powerUpWidth = 40, powerUpHeight = 40;
        int roadWidth = PANEL_WIDTH - 140;
        int startX = 50;
        int maxAttempts = 15;
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = startX + random.nextInt(roadWidth);
            int y = -50;
            
            Rectangle newPowerUpBounds = new Rectangle(x - 20, y - 20, powerUpWidth + 40, powerUpHeight + 40);
            
            boolean canSpawn = true;
            
            // Check collision with obstacles
            for (ObstacleCar obstacle : obstacles) {
                if (obstacle.getY() < 200) {
                    Rectangle obstacleBounds = obstacle.getBounds();
                    Rectangle bufferedObstacleBounds = new Rectangle(
                        obstacleBounds.x - 30, obstacleBounds.y - 30,
                        obstacleBounds.width + 60, obstacleBounds.height + 60
                    );
                    
                    if (newPowerUpBounds.intersects(bufferedObstacleBounds)) {
                        canSpawn = false;
                        break;
                    }
                }
            }
            
            // Check collision with other power-ups
            if (canSpawn) {
                for (PowerUp existingPowerUp : powerUps) {
                    if (existingPowerUp.getY() < 200) {
                        Rectangle existingBounds = existingPowerUp.getBounds();
                        Rectangle bufferedBounds = new Rectangle(
                            existingBounds.x - 25, existingBounds.y - 25,
                            existingBounds.width + 50, existingBounds.height + 50
                        );
                        
                        if (newPowerUpBounds.intersects(bufferedBounds)) {
                            canSpawn = false;
                            break;
                        }
                    }
                }
            }
            
            // Check collision with player
            if (canSpawn) {
                Rectangle playerBounds = playerCar.getBounds();
                Rectangle playerArea = new Rectangle(
                    playerBounds.x - 50, playerBounds.y - 100,
                    playerBounds.width + 100, playerBounds.height + 150
                );
                
                if (newPowerUpBounds.intersects(playerArea)) {
                    canSpawn = false;
                }
            }
            
            if (canSpawn) {
                powerUps.add(new PowerUp(x, y, randomType));
                return;
            }
        }
    }
    
    /**
     * Update game logic
     */
    private void updateGame() {
        if (!isGameRunning || isGameOver || isPaused) return;
        
        // Update score
        long baseScore = (System.currentTimeMillis() - gameStartTime) / 100;
        score = hasScoreMultiplier ? baseScore * 2 : baseScore;
        
        updateDifficulty();
        updatePowerUpEffects();
        
        playerCar.update(PANEL_WIDTH, PANEL_HEIGHT);
        
        particleSystem.createEngineSmoke(
            playerCar.getX() + playerCar.getWidth() / 2,
            playerCar.getY() + playerCar.getHeight()
        );
        
        updateObstacles();
        updatePowerUps();
        particleSystem.update();
        updateBackground();
    }
    
    private void updateDifficulty() {
        int newDifficulty = (int) (score / 500) + 1;
        if (newDifficulty > currentDifficulty) {
            currentDifficulty = newDifficulty;
            int newInterval = Math.max(800, OBSTACLE_SPAWN_INTERVAL - (currentDifficulty * 250));
            obstacleSpawnTimer.setDelay(newInterval);
        }
    }
    
    private void updatePowerUpEffects() {
        long currentTime = System.currentTimeMillis();
        
        if (hasInvincibility && currentTime > invincibilityEndTime) hasInvincibility = false;
        if (hasScoreMultiplier && currentTime > scoreMultiplierEndTime) hasScoreMultiplier = false;
    }
    
    private void updateObstacles() {
        Iterator<ObstacleCar> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            ObstacleCar obstacle = iterator.next();
            obstacle.update(PANEL_HEIGHT);
            
            if (!obstacle.isActive()) {
                iterator.remove();
                continue;
            }
            
            boolean collision = false;
            Rectangle playerBounds = playerCar.getBounds();
            
            if (obstacle.getType() == ObstacleCar.TYPE_TRUCK) {
                Rectangle[] truckParts = obstacle.getDetailedBounds();
                for (Rectangle part : truckParts) {
                    if (playerBounds.intersects(part)) {
                        collision = true;
                        break;
                    }
                }
            } else {
                collision = playerBounds.intersects(obstacle.getBounds());
            }
            
            if (collision) {
                if (hasInvincibility) {
                    iterator.remove();
                    particleSystem.createExplosion(
                        obstacle.getX() + obstacle.getWidth() / 2,
                        obstacle.getY() + obstacle.getHeight() / 2,
                        Color.YELLOW, 20
                    );
                    soundManager.playSound("crash");
                } else {
                    lives--;
                    iterator.remove();
                    particleSystem.createExplosion(
                        playerCar.getX() + playerCar.getWidth() / 2,
                        playerCar.getY() + playerCar.getHeight() / 2,
                        Color.RED, 30
                    );
                    soundManager.playSound("crash");
                    
                    if (lives <= 0) {
                        gameOver();
                        return;
                    }
                }
            }
        }
    }
    
    private void updatePowerUps() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update();
            
            if (powerUp.isOffScreen()) {
                iterator.remove();
                continue;
            }
            
            if (playerCar.getBounds().intersects(powerUp.getBounds())) {
                applyPowerUp(powerUp);
                iterator.remove();
                particleSystem.createPowerUpEffect(
                    powerUp.getX() + 20,
                    powerUp.getY() + 20,
                    powerUp.getType().getColor()
                );
                soundManager.playSound("powerup");
            }
        }
    }
    
    private void applyPowerUp(PowerUp powerUp) {
        long currentTime = System.currentTimeMillis();
        
        switch (powerUp.getType()) {
            case INVINCIBILITY:
                hasInvincibility = true;
                invincibilityEndTime = currentTime + powerUp.getType().getDuration();
                break;
            case SCORE_MULTIPLIER:
                hasScoreMultiplier = true;
                scoreMultiplierEndTime = currentTime + powerUp.getType().getDuration();
                break;
            case EXTRA_LIFE:
                if (lives < maxLives) {
                    lives++;
                }
                break;
        }
    }
    
    /**
     * Update the scrolling background
     */
    private void updateBackground() {
        // Adjust background speed based on player movement
        int dynamicBackgroundSpeed = backgroundSpeed;
        
        if (playerCar.isMovingForward()) {
            dynamicBackgroundSpeed += 3;
        } else if (playerCar.isMovingBackward()) {
            dynamicBackgroundSpeed -= 2;
            if (dynamicBackgroundSpeed < 1) dynamicBackgroundSpeed = 1;
        }
        
        backgroundY1 += dynamicBackgroundSpeed;
        backgroundY2 += dynamicBackgroundSpeed;
        
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
        powerUpSpawnTimer.stop();
        
        // Stop game music when game is over
        soundManager.stopAllMusic();
        
        // Check for high score
        if (highScoreManager.isHighScore((int) score)) {
            showHighScoreEntryDialog();
        }
    }
    
    private void showHighScoreEntryDialog() {
        String name = JOptionPane.showInputDialog(this, 
            "New High Score! Enter your name:", 
            "High Score", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (name != null) {
            highScoreManager.addHighScore(name, (int) score, currentDifficulty, "Level " + currentDifficulty);
        }
    }
    
    private void showHighScoreDialog() {
        StringBuilder sb = new StringBuilder("HIGH SCORES\n\n");
        List<HighScoreManager.HighScoreEntry> scores = highScoreManager.getHighScores();
        
        for (int i = 0; i < scores.size(); i++) {
            HighScoreManager.HighScoreEntry entry = scores.get(i);
            sb.append(String.format("%d. %s\n", i + 1, entry.toString()));
        }
        
        if (scores.isEmpty()) {
            sb.append("No high scores yet!");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, backgroundY1, PANEL_WIDTH, PANEL_HEIGHT, null);
            g2d.drawImage(backgroundImage, 0, backgroundY2, PANEL_WIDTH, PANEL_HEIGHT, null);
        }
        
        // Draw particles
        particleSystem.draw(g2d);
        
        // Draw obstacles
        for (ObstacleCar obstacle : obstacles) {
            obstacle.render(g2d);
        }
        
        // Draw power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g2d);
        }
        
        // Draw player with effects
        if (playerCar != null) {
            // Invincibility effect (flashing golden aura)
            if (hasInvincibility) {
                long time = System.currentTimeMillis();
                int alpha = (int)(Math.sin(time / 100.0) * 50 + 100);
                g2d.setColor(new Color(255, 215, 0, alpha));
                g2d.fillOval(playerCar.getX() - 15, playerCar.getY() - 15, 
                           playerCar.getWidth() + 30, playerCar.getHeight() + 30);
            }
            
            playerCar.render(g2d);
        }
        
        // Draw UI
        drawUI(g2d);
        
        // Draw pause screen
        if (isPaused) {
            drawPauseScreen(g2d);
        }
        
        // Draw game over screen
        if (isGameOver) {
            drawGameOverScreen(g2d);
        }
    }
    
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Lives: " + lives + "/" + maxLives, 20, 55);
        g2d.drawString("Level: " + currentDifficulty, 20, 80);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawString("WASD/Arrows: Move", PANEL_WIDTH - 120, PANEL_HEIGHT - 60);
        g2d.drawString("P: Pause", PANEL_WIDTH - 120, PANEL_HEIGHT - 45);
        g2d.drawString("M: Music On/Off", PANEL_WIDTH - 120, PANEL_HEIGHT - 30);
        
        // Draw active power-ups
        int powerUpY = 105;
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        if (hasInvincibility) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("INVINCIBLE", 20, powerUpY);
            powerUpY += 25;
        }
        if (hasScoreMultiplier) {
            g2d.setColor(Color.ORANGE);
            g2d.drawString("2X SCORE", 20, powerUpY);
        }
    }
    
    private void drawPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics metrics = g2d.getFontMetrics();
        String pauseText = "PAUSED";
        int x = (PANEL_WIDTH - metrics.stringWidth(pauseText)) / 2;
        g2d.drawString(pauseText, x, PANEL_HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        metrics = g2d.getFontMetrics();
        String resumeText = "Press P to resume";
        x = (PANEL_WIDTH - metrics.stringWidth(resumeText)) / 2;
        g2d.drawString(resumeText, x, PANEL_HEIGHT / 2 + 50);
    }
    
    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = g2d.getFontMetrics();
        String gameOverText = "GAME OVER";
        int x = (PANEL_WIDTH - metrics.stringWidth(gameOverText)) / 2;
        g2d.drawString(gameOverText, x, PANEL_HEIGHT / 2 - 80);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        metrics = g2d.getFontMetrics();
        String scoreText = "Final Score: " + score;
        x = (PANEL_WIDTH - metrics.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, PANEL_HEIGHT / 2 - 30);
        
        String levelText = "Level Reached: " + currentDifficulty;
        x = (PANEL_WIDTH - metrics.stringWidth(levelText)) / 2;
        g2d.drawString(levelText, x, PANEL_HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        metrics = g2d.getFontMetrics();
        String restartText = "Press SPACE to restart";
        x = (PANEL_WIDTH - metrics.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, PANEL_HEIGHT / 2 + 50);
        
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
