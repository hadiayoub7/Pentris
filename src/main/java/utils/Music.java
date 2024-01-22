package utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class Music {
    private static final String BASE_PATH = "/sounds/";
    private MediaPlayer mediaPlayer;

    public Music() {
        
    }

    public void gameOver() {
        stop();
        updateMusic("GameOver.mp3");
        play();
        mediaPlayer.setOnEndOfMedia(this::welcome);
    }

    public void tetris() {
        stop();
        updateMusic("Original.mp3");
        play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }
    
    public void welcome() {
        stop();
        updateMusic("Welcome.mp3");
        play();
    }
    
    public void pause() {
        welcome();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }
    
    public void lineCleared() {
        stop();
        updateMusic("LineCleared.mp3");
        play();
        mediaPlayer.setOnEndOfMedia(this::tetris);
    }
    
    public void newScore() {
        stop();
        updateMusic("NewScore.mp3");
        play();
        mediaPlayer.setOnEndOfMedia(this::tetris);
    }

    private void update(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
    }

    private void updateMusic(String fileName) {
        String fullPath = Objects.requireNonNull(getClass().getResource(BASE_PATH + fileName)).toExternalForm();
        update(fullPath);
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
}
