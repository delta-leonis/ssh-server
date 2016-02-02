

package org.ssh.ui.components.bottomsection;

import javafx.event.ActionEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.ssh.logs.LogReader;
import org.ssh.logs.LogWriter;
import org.ssh.ui.UIComponent;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Hakkers
 * @author Joost Overeem
 *
 */
public class Timeslider extends UIComponent {

    @FXML
    private GridPane    timesliderRoot;
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
    private Label       blueScoreLabel;
    @FXML
    private Label       yellowScoreLabel;
    @FXML
    private Button      playButton, selectFileButton, reverseButton, recordButton, leftButton, rightButton;
    @FXML
    private GridPane    buttonPane;
    @FXML
    private CheckBox    goalCheckBox, timeoutCheckBox;
    @FXML
    private Insets      sliderpadding;

    private boolean     started      = false;
    private Timeline    timeline;

    private double      speedPerTick = 1.0;
    private boolean     reverse = false;

    private LogReader logReader;
    private LogWriter logWriter;
    private FileChooser fileChooser;
    private boolean recording = false;

    private static final int STEP_SIZE = 100;

    /**
     * Instantiates a new TimeSlider based on "bottomsection/timeslider.fxml"
     */
    public Timeslider() {
        super("timeslider", "bottomsection/timeslider.fxml");

        // Create a FileChooser for future use
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("File Chooser");
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Log Files", "*.log"));
        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        // Make sure the slider listens to the progressbar
        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> this.progressBar
                .setProgress(newValue.doubleValue() / this.slider.getMax()));
        // Initialize the label that displays the time
        this.timeLabel.setText(
                this.getIntToTime((int) this.slider.getValue()) + "/" + this.getIntToTime((int) this.slider.getMax()));
        // Make sure the timeLabel gets updated whenever the slider gets updated
        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> this.timeLabel.setText(
                this.getIntToTime((int) this.slider.getValue()) + "/" + this.getIntToTime((int) this.slider.getMax())));
        this.slider.valueProperty().addListener(observable -> updateScoreLabel());

        this.setupTimer();
    }

    /**
     * Adds a marker at the specified x value
     *
     * @param x
     *            The x position for the marker
     * @param cssMarker
     *            The css for the marker.
     */
    private void addMarker(final int x, final String cssMarker) {
        double pos = x > this.logReader.getDuration() ? this.slider.getWidth()
                : ((double)x / (double)this.logReader.getDuration()) * this.slider.getWidth();
        pos = (int)(pos - sliderpadding.getRight() - sliderpadding.getLeft());
        final Rectangle sliderPoint = new Rectangle(0,0,1,1);
        sliderPoint.getStyleClass().add(cssMarker);
        sliderPoint.setLayoutX(pos - sliderPoint.getWidth()/2);
        this.sliderPointsPane.getChildren().add(sliderPoint);
    }

    /**
     * Updates the labels that track the current scores, based on the value the slider has.
     */
    private void updateScoreLabel(){
        if(logReader != null) {
            blueScoreLabel.setText("" + logReader.getBlueScoreAtTimeMillis((long) slider.getValue()));
            yellowScoreLabel.setText("" + logReader.getYellowScoreAtTimeMillis((long) slider.getValue()));
        }
    }

    /**
     * Changes the given int in seconds into a nicer format like this mm:ss returns the hh:mm:ss
     * format if the match takes longer than an hour.
     */
    private static String getIntToTime(final int millis) {
        String timeString = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        if(TimeUnit.MILLISECONDS.toHours(millis) > 0)
            timeString = TimeUnit.MILLISECONDS.toHours(millis) + timeString;

        return timeString;
    }

    /**
     * Function that uses a {@link LogReader} to setup the slideBar
     *
     * @param path
     *            The path that leads to the log that needs to be played
     */
    public void loadGameLog(String path) {
        Platform.runLater(() -> {
            logReader = new LogReader(path);
            logReader.load();
            this.slider.setMax(logReader.getDuration());
            updateScoreLabel();
        });
    }

    @FXML
    private void openDialog(ActionEvent event){
        loadGameLog(fileChooser.showOpenDialog(this.getStage().getScene().getWindow()).getPath());
    }

    @FXML
    private void fastForward(ActionEvent event){
        this.speedPerTick = this.speedPerTick / 2;
    }

    @FXML
    private void slowDown(ActionEvent event){
        this.speedPerTick = this.speedPerTick * 2;
    }

    /**
     * Reverses the direction of the slider.
     */
    @FXML
    private void reverse(ActionEvent event){
        reverse = !reverse;
    }

    /**
     * Sets up the timer that allows the slider to run on its own.
     */
    private void setupTimer() {
        // Define a single frame, with the duration of second/60
        final KeyFrame keyFrame = new KeyFrame(new Duration(Timeslider.STEP_SIZE), event -> this.updateKeyFrame());

        // The render loop play the frames
        this.timeline = new Timeline(keyFrame);

        this.timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Handler for the {@link Button start button}
     */
    @FXML
    private void start(ActionEvent event) {
        // Switch between start and pause based on the current state
        if (this.started) {
            this.started = false;
            this.playButton.getStyleClass().set(this.playButton.getStyleClass().size() - 1, "button-play");
            this.timeline.pause();
        } else {
            this.started = true;
            this.playButton.getStyleClass().set(this.playButton.getStyleClass().size() - 1, "button-pause");
            this.timeline.play();
        }
    }

    /**
     * Adds speedPerTick to the slider value
     */
    private void updateKeyFrame() {
        double speed = STEP_SIZE * this.speedPerTick;
        final double newValue = !reverse ? this.slider.getValue() + speed : this.slider.getValue() - speed;
        this.slider.setValue(newValue > this.slider.getMax() ? this.slider.getMax() : newValue);
        if(logReader != null) {
            logReader.sendDetectionMessage((long) slider.getValue());
            logReader.sendRefereeMessage((long) slider.getValue());
        }
    }

    /**
     * Handler that adds the markers for the goal times and timeouts in the {@link LogReader}
     */
    @FXML
    private void updateSlider() {
        this.sliderPointsPane.getChildren().clear();
        if (this.goalCheckBox.isSelected()) {
            this.logReader.getYellowGoalTimes().forEach(i -> this.addMarker(i.intValue(), "yellow-goal-marker"));
            this.logReader.getBlueGoalTimes().forEach(i -> this.addMarker(i.intValue(), "blue-goal-marker"));
        }
        if (this.timeoutCheckBox.isSelected())
            this.logReader.getTimeouts().forEach(i -> this.addMarker(i.intValue(), "timeout-marker"));
    }

    @FXML
    private void record(){
        if(!recording) {
            logWriter = new LogWriter();
            logWriter.start();
            recording = true;
        }
        else{
            logWriter.close();
            recording = false;
        }
    }
}

