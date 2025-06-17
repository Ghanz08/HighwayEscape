import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> sounds;
    private Map<String, Clip> music;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private float soundVolume = 1.0f;
    private float musicVolume = 0.7f;
    private boolean audioSystemAvailable = true;
    
    private SoundManager() {
        sounds = new HashMap<>();
        music = new HashMap<>();
        checkAudioSystem();
        loadSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void checkAudioSystem() {
        try {
            // Test if audio system is available
            AudioSystem.getMixerInfo();
        } catch (Exception e) {
            System.out.println("Audio system not available, running in silent mode.");
            audioSystemAvailable = false;
        }
    }
    
    private void loadSounds() {
        if (!audioSystemAvailable) {
            return;
        }
        
        try {
            // Load sound effects (WAV format)
            loadSound("crash", "assets/sounds/crash.wav");
            loadSound("powerup", "assets/sounds/powerup.wav");
            loadSound("button", "assets/sounds/button.wav");
            
            // Load background music files (WAV format)
            loadMusic("game", "assets/music/game.wav");
            
            System.out.println("Loaded " + sounds.size() + " sound effects and " + music.size() + " music tracks.");
            
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }
    
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                sounds.put(name, clip);
                System.out.println("Loaded sound: " + name + " from " + path);
            } else {
                System.out.println("Sound file not found: " + path);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Could not load sound: " + path + " - " + e.getMessage());
        }
    }
    
    private void loadMusic(String name, String path) {
        try {
            File musicFile = new File(path);
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                music.put(name, clip);
                System.out.println("Loaded music: " + name + " from " + path);
            } else {
                System.out.println("Music file not found: " + path);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Could not load music: " + path + " - " + e.getMessage());
        }
    }
    
    public void playSound(String name) {
        if (!soundEnabled || !audioSystemAvailable) return;
        
        Clip clip = sounds.get(name);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                setVolume(clip, soundVolume);
                clip.start();
            } catch (Exception e) {
                // Silently ignore audio errors
            }
        }
    }
    
    public void playMusic(String name, boolean loop) {
        if (!musicEnabled || !audioSystemAvailable) return;
        
        stopAllMusic();
        Clip clip = music.get(name);
        if (clip != null) {
            try {
                clip.setFramePosition(0);
                setVolume(clip, musicVolume);
                if (loop) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    clip.start();
                }
            } catch (Exception e) {
                // Silently ignore audio errors
            }
        }
    }
    
    public void stopAllMusic() {
        for (Clip clip : music.values()) {
            if (clip != null && clip.isRunning()) {
                try {
                    clip.stop();
                } catch (Exception e) {
                    // Ignore errors
                }
            }
        }
    }
    
    public void stopSound(String name) {
        Clip clip = sounds.get(name);
        if (clip != null && clip.isRunning()) {
            try {
                clip.stop();
            } catch (Exception e) {
                // Ignore errors
            }
        }
    }
    
    private void setVolume(Clip clip, float volume) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(Math.max(0.001f, volume)) / Math.log(10.0) * 20.0);
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            // Volume control not available or failed
        }
    }
    
    // Status methods
    public boolean isAudioAvailable() {
        return audioSystemAvailable;
    }
    
    public int getLoadedSoundsCount() {
        return sounds.size();
    }
    
    public int getLoadedMusicCount() {
        return music.size();
    }
    
    // Getters and setters
    public void setSoundEnabled(boolean enabled) { 
        this.soundEnabled = enabled; 
    }
    
    public void setMusicEnabled(boolean enabled) { 
        this.musicEnabled = enabled; 
        if (!enabled) stopAllMusic();
    }
    
    public boolean isSoundEnabled() { return soundEnabled; }
    public boolean isMusicEnabled() { return musicEnabled; }
    
    public void setSoundVolume(float volume) { 
        this.soundVolume = Math.max(0, Math.min(1, volume)); 
    }
    
    public void setMusicVolume(float volume) { 
        this.musicVolume = Math.max(0, Math.min(1, volume)); 
    }
}
