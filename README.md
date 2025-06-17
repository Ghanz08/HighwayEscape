# Highway Escape

A 2D top-down racing game created with Java Swing where you control a car and try to avoid obstacles while collecting power-ups and achieving high scores.

## Game Features

- Top-down view racing game with smooth animations
- Player-controlled car that moves in all directions (with boundaries)
- Dynamic scrolling road background that responds to player movement
- Multiple obstacle types (cars, trucks, motorcycles)
- **Power-up system**: Invincibility, score multiplier, extra lives
- **Progressive difficulty**: Faster level progression and more challenging gameplay
- **Limited lives system**: Maximum of 5 lives
- **Particle effects**: Explosions, power-up collections, engine smoke
- Collision detection with visual feedback
- **Pause menu**: Pause/resume gameplay anytime
- Animated start screen with dynamic effects
- Enhanced game over screen with detailed statistics

## Controls

- **Arrow Keys / WASD**: Move car in all directions
  - **Up/W**: Move forward (increases background scroll speed)
  - **Down/S**: Move backward (decreases background scroll speed)
  - **Left/A**: Move left
  - **Right/D**: Move right
- **Enter**: Start game (from start menu)
- **Space**: Restart game (after game over)
- **P**: Pause/Unpause game
- **ESC**: Return to main menu / Open pause menu
- **M**: Toggle music on/off

## Power-Up System

- **★ Invincibility**: Temporary invulnerability to all obstacles (5 seconds)
- **2x Score Multiplier**: Double points for limited time (7 seconds)
- **+1 Extra Life**: Gain one life (maximum 5 lives)

## Difficulty System

- **Level progression**: Increases every 500 points (faster than before)
- **Spawn rate**: More aggressive increase - obstacles spawn 250ms faster each level
- **Speed increase**: Obstacle speed increases every 3 levels
- **Lives limit**: Maximum of 5 lives to increase challenge

## Movement System

The player car can now move in all four directions with intelligent boundaries:

- **Vertical Movement**: Limited to the lower portion of the screen (40% to 90% of screen height)
- **Horizontal Movement**: Full width of the road
- **Dynamic Background**: Moving forward speeds up the road scroll, moving backward slows it down
- **Visual Effects**: Engine exhaust when accelerating forward

## Project Structure

- `Main.java`: Entry point
- `GameFrame.java`: Main window management
- `StartMenuPanel.java`: Start menu screen with animations
- `GamePanel.java`: Main game logic and rendering
- `PlayerCar.java`: Enhanced player car with 4-directional movement
- `ObstacleCar.java`: Obstacle management (cars, trucks, motorcycles)
- `PowerUp.java`: Simplified power-up system
- `ParticleSystem.java`: Visual effects and particles
- `SoundManager.java`: Audio system management
- `HighScoreManager.java`: Score persistence and leaderboard
- `AssetLoader.java`: Enhanced asset loading and management

<!-- ## Screenshots

[Add screenshots here] -->

## Requirements

- Java 8 or higher
- IntelliJ IDEA or any Java IDE
- **Audio support**: Java Sound API (included in standard Java)
- **Storage**: File system access for high scores and settings

## Installation & Setup

1. Clone the repository
2. Ensure you have Java 8+ installed
3. Open the project in your Java IDE
4. Make sure the `assets/` folder contains all required images and sounds
5. Run the `Main.java` file

## How to Run

1. Clone the repository
2. Open the project in your Java IDE
3. Run the `Main.java` file

## License

[Your license information]
