package com.musicplayer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.w3c.dom.events.MouseEvent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MusicPlayerController implements Initializable {

    final String playlistPath = "C:\\Playlist";
    private ArrayList<Media> playlistMedia = new ArrayList<>();
    private ArrayList<String> playlistSongNames = new ArrayList<>();
    private int currentSongIndex;
    private int numberOfSongs;
    private double volume = 0.5;
    private double startTime;
    private MediaPlayer mediaPlayer;
    @FXML
    private Label songNameLabel;
    @FXML
    private Button playButton;
    @FXML
    ListView<String> playlistView;
    @FXML
    private Slider songPlayedLengthSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;


    public void setPlaylistMedia(){
        File[] playlistFiles = new File(playlistPath).listFiles();
        for (File playlistFile : playlistFiles) {
            this.playlistMedia.add(new Media(playlistFile.toURI().toString())); // converts files to media
        }
    }
    public void setPlaylistSongNames(){
        File[] playlistFiles = new File(playlistPath).listFiles();
        for (File playlistFile : playlistFiles) {
            this.playlistSongNames.add(playlistFile.getName().split("\\.")[0]); // converts files to media
        }
    }

    @FXML
    public void refreshList(){
        setPlaylistMedia();
        setPlaylistSongNames();
        playlistView.refresh();
    }


    boolean pressedPlayButton = false;
    @FXML
    public void playCurrentSong(){
        if (!pressedPlayButton) {
            if (startTime != 0.0) { // If it's a resume, set the position
                mediaPlayer.seek(Duration.seconds(startTime));
                startTime = 0.0;// Reset startTime
            } else {
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer(playlistMedia.get(currentSongIndex));

                mediaPlayer.setOnReady(() -> {
                    Duration totalDuration = mediaPlayer.getMedia().getDuration();
                    songPlayedLengthSlider.setMax(totalDuration.toSeconds());
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                });

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    songPlayedLengthSlider.setValue(newValue.toSeconds());
                });
            }
            mediaPlayer.play();
            playButton.setText("Stop");
            pressedPlayButton = true;
        } else {
            mediaPlayer.pause();
            startTime = mediaPlayer.getCurrentTime().toSeconds(); // Store the paused time
            playButton.setText("Play");
            pressedPlayButton = false;
        }
        songNameLabel.setText(playlistSongNames.get(currentSongIndex));
    }

    @FXML
    public void nextSong(){
        startTime = 0;
        if(currentSongIndex == numberOfSongs){
            currentSongIndex = 0;
        }
        else{
            currentSongIndex++;
        }
        pressedPlayButton = false;
        playCurrentSong();
    }

    @FXML
    public void previousSong(){
        startTime = 0;
        if(currentSongIndex == 0){
            currentSongIndex = numberOfSongs;
        }
        else{
            currentSongIndex--;
        }
        pressedPlayButton = false;
        playCurrentSong();
    }
    public int getRandomNumber(int n) {
        Random random = new Random();
        return random.nextInt(n);
    }

    @FXML
    public void sliderPressed() {
        startTime = songPlayedLengthSlider.getValue();
        mediaPlayer.seek(Duration.seconds(startTime));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        volumeLabel.setText("Volume: 50%");
        setPlaylistMedia();
        setPlaylistSongNames();
        numberOfSongs = playlistMedia.size() - 1; //array indexing starts from 0
        currentSongIndex = getRandomNumber(playlistMedia.size());
        mediaPlayer = new MediaPlayer(playlistMedia.get(currentSongIndex));
        mediaPlayer.setVolume(volume);
        playlistView.getItems().addAll(playlistSongNames);
        playlistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                currentSongIndex = playlistView.getSelectionModel().getSelectedIndex();
                pressedPlayButton = false;
            }
        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                volume = volumeSlider.getValue();
                volumeLabel.setText("Volume " + String.valueOf((int) volume) + "%");
                mediaPlayer.setVolume(volume/100); //converting to numbers between 0 and 1
            }
        });
    }
}