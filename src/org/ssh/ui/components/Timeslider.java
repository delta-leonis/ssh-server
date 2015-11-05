package org.ssh.ui.components;

import org.ssh.ui.UIComponent;
import org.ssh.ui.components.timerslider.GameLog;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

public class Timeslider extends UIComponent {

    @FXML
    private GridPane timesliderRoot;
    @FXML
    private Pane sliderPointsPane;
    @FXML
    private StackPane root;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider slider;
    @FXML
    private Label timeLabel;
    @FXML
    private Button playButton, selectFileButton, pauseButton, recordButton, leftButton, rightButton;
    @FXML
    private GridPane buttonPane;
    @FXML
    private CheckBox goalCheckBox, timeoutCheckBox;
    @FXML
    private Insets sliderpadding;

    private GameLog gamelog;
    private boolean started = false;
    private Timeline timeline;

    private double speedPerTick = 1.0 / 60.0;

    public Timeslider() {
        super("timeslider", "timeslider.fxml");
        // Bind size properties
        timesliderRoot.minHeightProperty().bind(this.heightProperty());
        timesliderRoot.maxHeightProperty().bind(this.heightProperty());
        timesliderRoot.minWidthProperty().bind(this.widthProperty());
        timesliderRoot.maxWidthProperty().bind(this.widthProperty());

        // Initializes a "Gamelog". This is a dummy gamelog file and has to be
        // replaced later TODO
        init();
        // Make sure the slider listens to the progressbar
        slider.valueProperty().addListener(
                (observable, oldValue, newValue) -> progressBar.setProgress(newValue.doubleValue() / slider.getMax()));
        // Initialize the label that displays the time
        timeLabel.setText(getIntToTime((int) slider.getValue()) + "/" + getIntToTime((int) slider.getMax()));
        // Make sure the timeLabel gets updated whenever the slider gets updated
        slider.valueProperty().addListener((observable, oldValue, newValue) -> timeLabel
                .setText(getIntToTime((int) slider.getValue()) + "/" + getIntToTime((int) slider.getMax())));
        // Update the slider whenever the ticks in the slider need to be
        // displayed
        goalCheckBox.setOnAction(e -> updateSlider());
        timeoutCheckBox.setOnAction(e -> updateSlider());
        widthProperty().addListener(o -> Platform.runLater(() -> updateSlider()));
        
        setupTimer();
        setupButtonPane();
    }

    /**
     * Sets up the timer that allows the slider to run on its own.
     */
    private void setupTimer() {
        // Define a single frame, with the duration of second/60
        KeyFrame keyFrame = new KeyFrame(new Duration(1000 / 60), event -> updateKeyFrame());

        // The render loop play the frames
        timeline = new Timeline(keyFrame);

        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Adds speedPerTick to the slider value
     */
    private void updateKeyFrame() {
        double newValue = slider.getValue() + speedPerTick;
        slider.setValue(newValue > slider.getMax() ? slider.getMax() : newValue);
    }

    /**
     * Sets up the {@link Button control buttons}
     */
    private void setupButtonPane() {
        playButton.getStyleClass().add("button-play");
        playButton.setOnAction(e -> start());
        selectFileButton.getStyleClass().add("button-select-file");
        // pauseButton.getStyleClass().add("button-pause");
        // pauseButton.setOnAction(e -> timeline.pause());
        recordButton.getStyleClass().add("button-record");
        leftButton.getStyleClass().add("button-left");
        leftButton.setOnAction(e -> speedPerTick /= 2);
        rightButton.getStyleClass().add("button-right");
        rightButton.setOnAction(e -> speedPerTick *= 2);
    }

    /**
     * Handler for the {@link Button start button}
     */
    private void start() {
        // Switch between start and pause based on the current state
        if (started) {
            started = false;
            playButton.getStyleClass().set(playButton.getStyleClass().size() - 1, "button-play");
            timeline.pause();
        } else {
            started = true;
            playButton.getStyleClass().set(playButton.getStyleClass().size() - 1, "button-pause");
            timeline.play();
        }

    }

    /**
     * Makes a dummy {@link GameLog}
     */
    private void init() {
        gamelog = new GameLog("Wow", 100);
    }

    /**
     * Handler that adds the markers for the goal times and timeouts in the
     * {@link GameLog}
     */
    private void updateSlider() {
        sliderPointsPane.getChildren().clear();
        if (goalCheckBox.isSelected())
            gamelog.getGoalTimes().forEach(i -> addMarker(i, "goal-marker"));
        if (timeoutCheckBox.isSelected())
            gamelog.getTimeouts().forEach(i -> addMarker(i, "timeout-marker"));
    }

    /**
     * Adds a marker at the specified x value
     * 
     * @param x
     *            The x position for the marker
     * @param cssMarker
     *            The css for the marker.
     * @see slider.css
     */
    private void addMarker(int x, String cssMarker) {
        double pos = x > slider.getMax() ? slider.getMax() : x / slider.getMax() * slider.getWidth();
        // TODO: Figure out a way to change size in css 
        Rectangle sliderPoint = new Rectangle(pos - 2, progressBar.getHeight(), 4, 14); 
        sliderPoint.getStyleClass().add(cssMarker);
        sliderPoint.setLayoutX(pos - sliderPoint.getWidth());
        sliderPointsPane.getChildren().add(sliderPoint);
    }

    /**
     * Changes the given int in seconds into a nicer format like this mm:ss
     * returns the hh:mm:ss format if the match takes longer than an hour.
     */
    public String getIntToTime(int length) {
        int hours = length / 3600;
        int minutes = (length - hours * 3600) / 60;
        int seconds = length % 60;

        String hoursString = hours / 10 < 1 ? "0" + hours : "" + hours;
        String minutesString = minutes / 10 < 1 ? "0" + minutes : "" + minutes;
        String secondsString = seconds / 10 < 1 ? "0" + seconds : "" + seconds;

        if (hours == 0)
            return minutesString + ":" + secondsString;

        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    /**
     * Function that uses a {@link GameLog} to setup the slideBar
     * 
     * @param gamelog
     *            The Gamelog used
     */
    public void loadGameLog(GameLog gamelog) {
        this.gamelog = gamelog;
    }
}
