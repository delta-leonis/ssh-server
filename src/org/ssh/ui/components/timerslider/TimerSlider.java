package org.ssh.ui.components.timerslider;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * A {@link Slidebar} that also makes use of a {@link Timeline}, {@link Button controlbuttons} and
 * some {@link CheckBox checkboxes} that can be checked to show the goaltimes and timeouts. The
 * amount of {@link CheckBox checkboxes} can be extended in the future of course. Also makes use of
 * FXML.
 *
 * @author Thomas Hakkers
 *        
 */
public class TimerSlider implements Initializable {
    
    private GameLog     gamelog;
    private final int   sliderWidth  = 300;
    private boolean     started      = false;
    private Timeline    timeline;
                        
    private double      speedPerTick = 1.0 / 60.0;
                                     
    @FXML
    private Pane        sliderPointsPane;
                        
    @FXML
    private StackPane   root;
                        
    @FXML
    private ProgressBar progressBar;
                        
    @FXML
    private Slider      slider;
                        
    @FXML
    private Label       timeLabel;
                        
    @FXML
    private Button      playButton, selectFileButton, pauseButton, recordButton, leftButton, rightButton;
                        
    @FXML
    private GridPane    buttonPane;
                        
    @FXML
    private CheckBox    goalCheckBox, timeoutCheckBox;
                        
    /**
     * Adds a marker at the specified x value
     * 
     * @param x
     *            The x position for the marker
     * @param cssMarker
     *            The css for the marker.
     * @see slider.css
     */
    private void addMarker(final int x, final String cssMarker) {
        final double pos = x > this.slider.getMax() ? this.slider.getMax()
                : (x / this.slider.getMax()) * this.sliderWidth;
        final Rectangle sliderPoint = new Rectangle(pos - 2, this.progressBar.getHeight(), 4, 14);
        sliderPoint.setLayoutX(pos - sliderPoint.getWidth());
        sliderPoint.getStyleClass().add(cssMarker);
        this.sliderPointsPane.getChildren().add(sliderPoint);
    }
    
    /**
     * Changes the given int in seconds into a nicer format like this mm:ss returns the hh:mm:ss
     * format if the match takes longer than an hour.
     */
    public String getIntToTime(final int length) {
        final int hours = length / 3600;
        final int minutes = (length - (hours * 3600)) / 60;
        final int seconds = length % 60;
        
        final String hoursString = (hours / 10) < 1 ? "0" + hours : "" + hours;
        final String minutesString = (minutes / 10) < 1 ? "0" + minutes : "" + minutes;
        final String secondsString = (seconds / 10) < 1 ? "0" + seconds : "" + seconds;
        
        if (hours == 0) return minutesString + ":" + secondsString;
        
        return hoursString + ":" + minutesString + ":" + secondsString;
    }
    
    /**
     * Makes a dummy {@link GameLog}
     */
    private void init() {
        this.gamelog = new GameLog("Wow", 100);
    }
    
    /**
     * Adds functionality to the components that need it
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.init();
        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> this.progressBar
                .setProgress(newValue.doubleValue() / this.slider.getMax()));
                
        this.timeLabel.setText(
                this.getIntToTime((int) this.slider.getValue()) + "/" + this.getIntToTime((int) this.slider.getMax()));
                
        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> this.timeLabel.setText(
                this.getIntToTime((int) this.slider.getValue()) + "/" + this.getIntToTime((int) this.slider.getMax())));
                
        this.goalCheckBox.setOnAction(e -> this.updateSlider());
        this.timeoutCheckBox.setOnAction(e -> this.updateSlider());
        
        this.setupTimer();
        this.setupButtonPane();
    }
    
    /**
     * Function that uses a {@link GameLog} to setup the slideBar
     * 
     * @param gamelog
     *            The Gamelog used
     */
    public void loadGameLog(final GameLog gamelog) {
        this.gamelog = gamelog;
    }
    
    /**
     * Sets up the {@link Button control buttons}
     */
    private void setupButtonPane() {
        this.playButton.getStyleClass().add("button-play");
        this.playButton.setOnAction(e -> this.start());
        this.selectFileButton.getStyleClass().add("button-select-file");
        // pauseButton.getStyleClass().add("button-pause");
        // pauseButton.setOnAction(e -> timeline.pause());
        this.recordButton.getStyleClass().add("button-record");
        this.leftButton.getStyleClass().add("button-left");
        this.leftButton.setOnAction(e -> this.speedPerTick /= 2);
        this.rightButton.getStyleClass().add("button-right");
        this.rightButton.setOnAction(e -> this.speedPerTick *= 2);
    }
    
    private void setupTimer() {
        // Define a single frame, with the duration of second/60
        final KeyFrame keyFrame = new KeyFrame(new Duration(1000 / 60), event -> this.updateKeyFrame());
        
        // The render loop play the frames
        this.timeline = new Timeline(keyFrame);

        this.timeline.setCycleCount(Animation.INDEFINITE);
    }
    
    /**
     * Handler for the {@link Button start button}
     */
    private void start() {
        if (this.started) {
            this.started = false;
            this.playButton.getStyleClass().set(this.playButton.getStyleClass().size() - 1, "button-play");
            this.timeline.pause();
        }
        else {
            this.started = true;
            this.playButton.getStyleClass().set(this.playButton.getStyleClass().size() - 1, "button-pause");
            this.timeline.play();
        }
        
    }
    
    /**
     * Adds speedPerTick to the slider value
     */
    private void updateKeyFrame() {
        final double newValue = this.slider.getValue() + this.speedPerTick;
        this.slider.setValue(newValue > this.slider.getMax() ? this.slider.getMax() : newValue);
    }
    
    /**
     * Handler that adds the markers for the goal times and timeouts in the {@link GameLog}
     */
    private void updateSlider() {
        this.sliderPointsPane.getChildren().clear();
        if (this.goalCheckBox.isSelected()) this.gamelog.getGoalTimes().forEach(i -> this.addMarker(i, "goal-marker"));
        if (this.timeoutCheckBox.isSelected())
            this.gamelog.getTimeouts().forEach(i -> this.addMarker(i, "timeout-marker"));
    }
}
