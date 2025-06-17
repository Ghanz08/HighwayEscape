import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private static final int MAX_HIGH_SCORES = 10;
    private List<HighScoreEntry> highScores;
    
    public static class HighScoreEntry implements Comparable<HighScoreEntry>, Serializable {
        private static final long serialVersionUID = 1L;
        public String playerName;
        public int score;
        public String date;
        public int difficulty;
        public String difficultyName;
        
        public HighScoreEntry(String playerName, int score, int difficulty, String difficultyName) {
            this.playerName = playerName;
            this.score = score;
            this.difficulty = difficulty;
            this.difficultyName = difficultyName;
            this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        
        @Override
        public int compareTo(HighScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d (%s) - %s", playerName, score, difficultyName, date);
        }
    }
    
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public boolean isHighScore(int score) {
        return highScores.size() < MAX_HIGH_SCORES || 
               score > highScores.get(highScores.size() - 1).score;
    }
    
    public int getHighScoreRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score > highScores.get(i).score) {
                return i + 1;
            }
        }
        return highScores.size() < MAX_HIGH_SCORES ? highScores.size() + 1 : -1;
    }
    
    public void addHighScore(String playerName, int score, int difficulty, String difficultyName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonymous";
        }
        
        HighScoreEntry newEntry = new HighScoreEntry(playerName.trim(), score, difficulty, difficultyName);
        highScores.add(newEntry);
        Collections.sort(highScores);
        
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }
        
        saveHighScores();
    }
    
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    public HighScoreEntry getHighestScore() {
        return highScores.isEmpty() ? null : highScores.get(0);
    }
    
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (List<HighScoreEntry>) ois.readObject();
            Collections.sort(highScores);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }
    
    public void clearHighScores() {
        highScores.clear();
        saveHighScores();
    }
}
