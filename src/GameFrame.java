import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StartMenuPanel startMenuPanel;
    private GamePanel gamePanel;
    
    private static final String START_MENU_PANEL = "START_MENU_PANEL";
    private static final String GAME_PANEL = "GAME_PANEL";
    
    /**
     * Constructor for GameFrame
     */
    public GameFrame() {
        setTitle("Highway Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Preload assets
        AssetLoader.preloadAssets();
        
        // Create card layout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create and add start menu panel
        startMenuPanel = new StartMenuPanel();
        startMenuPanel.setStartMenuListener(() -> startGame());
        mainPanel.add(startMenuPanel, START_MENU_PANEL);
        
        // Create and add game panel
        gamePanel = new GamePanel();
        gamePanel.setGameOverListener(() -> showStartMenu());
        mainPanel.add(gamePanel, GAME_PANEL);
        
        // Show start menu initially
        cardLayout.show(mainPanel, START_MENU_PANEL);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Pack the frame to fit the preferred size of its components
        pack();
        
        // Center the frame on the screen
        setLocationRelativeTo(null);
    }
    
    /**
     * Start the game
     */
    private void startGame() {
        gamePanel.initGame();
        cardLayout.show(mainPanel, GAME_PANEL);
        gamePanel.requestFocusInWindow(); // Important for keyboard input
    }
    
    /**
     * Show the start menu
     */
    private void showStartMenu() {
        cardLayout.show(mainPanel, START_MENU_PANEL);
        startMenuPanel.requestFocusInWindow();
    }
}
