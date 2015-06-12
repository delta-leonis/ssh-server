package nl.saxion.robosim.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.saxion.robosim.application.FileDialog;
import nl.saxion.robosim.application.SettingsDialog;
import nl.saxion.robosim.application.exitDialog;
import nl.saxion.robosim.communications.AIListener;
import nl.saxion.robosim.communications.MultiCastServer;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import nl.saxion.robosim.model.protobuf.SslReferee;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * The UIController class controls the interface. The input is controlled here.
 *
 * @author Kris Minkjan
 * @author Ken Sleebos
 * @author Joost van Dijk
 */
public class UIController implements Initializable {
    private Renderer renderer;
    private Model model;
    private Timeline timeline;
    private Image pause, play, forward, forward2, forward4, forward8, backward, backward2, backward4, backward8;
    private MultiCastServer mcst;
    private AIListener ail;

    @FXML
    private Canvas canvas;

    @FXML
    private Pane sliderPane;

    @FXML
    private Slider slider;

    @FXML
    private ImageView start_button, forward_button, backward_button;

    @FXML
    private javafx.scene.control.Label FPS, RDR, CMD, RobotId, RobotX, RobotY, RobotOrientation, RobotConfidence;

    @FXML
    private ListView<String> frameList;

    /**
     * This method initializes the canvas and connects handler's to listview's and buttons's
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            ail = new AIListener();
            mcst = new MultiCastServer();
        } catch (IOException e) {
            e.printStackTrace();
        }


        model = Model.getInstance();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Sets height of canvas to full screen minus the width of the side bar
        canvas.setHeight(screenSize.getHeight() - 165);
        canvas.setWidth(screenSize.getWidth() - 300);
        slider.setMinWidth(screenSize.getWidth() - 340);

        // Model.getSSLField().updateSizeSet(canvas);
        model.setSSLField(canvas);
        renderer = new Renderer(canvas);
        renderer.drawField();

        // Select the robot at the clicked point
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            model.selectRobotByPosition(event.getX(), event.getY());
            if (timeline.getStatus() != Animation.Status.RUNNING) {
                renderer.render();
            }

            SSL_DetectionRobot r = model.getSelectedRobot();
            if (r != null) {
                RobotId.setText("ID:" + r.getRobotId());
                RobotY.setText("Y:" + r.getY());
                RobotX.setText("X:" + r.getX());
                RobotOrientation.setText("Orientation:" + r.getX());
                RobotConfidence.setText("Confidence:" + r.getConfidence());
            }
        });

        // Drag robot
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            System.out.println("dragging");
            model.setRobotPosition(event.getX(), event.getY());
            if (timeline.getStatus() != Animation.Status.RUNNING) {
                renderer.render();
            }
        });

        // Changes the frame based on selection in listview
        frameList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeFrameNumber(Integer.parseInt(newValue));
            model.nextFrame();
            renderer.render();
        });

        // Add all framenumbers to listview
        frameList.setItems(model.getAllFrameNumbers());

        // Changes the frame based on selection on the slider
        slider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double newFrameNumber = (model.getAllFrameNumbers().size() / 100) * slider.getValue();
            model.setCurrentFrame((int) newFrameNumber);
        });



        int[] specialFrames = {20000, 30000, 50000};
        // Add yellow bar on slider for special frames
        int frameCount = model.getAllFrameNumbers().size();
        for (int x : specialFrames) {
            double positie = ((double) x / (double) frameCount) * 100;
            positie = (screenSize.getWidth() - 340) / 100 * positie;

            Rectangle sliderPoint = new Rectangle(5, 10);
            sliderPoint.setFill(Color.ORANGE);
            sliderPoint.setLayoutX(positie);
            sliderPoint.setLayoutY(5);
            sliderPane.getChildren().add(sliderPoint);

        }

        
        // Define a single frame, with the duration of second/60
        KeyFrame keyFrame = new KeyFrame(new Duration(1000 / 60), event -> {
            long startNano = System.nanoTime();

            model.nextFrame();
            renderer.render();
            slider.adjustValue(model.getSimulationPosition());

            long frameTime = System.nanoTime() - startNano;
            long ms = TimeUnit.MILLISECONDS.convert(frameTime, TimeUnit.NANOSECONDS);
            FPS.setText("FPS: " + (ms > 0 ? Math.min(60, 1000 / ms) : 60));
            ms = TimeUnit.MILLISECONDS.convert(frameTime, TimeUnit.NANOSECONDS);
            RDR.setText("RDR: " + (ms > 0 ? ms : "<0") + "ms");
            SslReferee.SSL_Referee ref = model.getLastReferee();
            if(ref != null) {
                CMD.setText(ref.getCommand().toString());
            }

            if (model.getSelectedRobot() != null) {
                SSL_DetectionRobot r = model.getSelectedRobot();
                RobotId.setText("ID:" + r.getRobotId());
                RobotY.setText("Y:" + r.getY());
                RobotX.setText("X:" + r.getX());
                RobotOrientation.setText("Orientation:" + r.getX());
                RobotConfidence.setText("Confidence:" + r.getConfidence());
            }

            int frameNumber = model.getFrameNumber();
            frameList.getFocusModel().focus(frameNumber);
            frameList.scrollTo(frameNumber);
        });

        // The render loop play the frames
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);

        // Images for the play and stop actions
        pause = new Image("/resources/pause.png");
        play = new Image("/resources/play.png");

        // Images for the forward actions
        forward = new Image("/resources/forward.png");
        forward2 = new Image("/resources/forward-x2.png");
        forward4 = new Image("/resources/forward-x4.png");
        forward8 = new Image("/resources/forward-x8.png");

        // Images for the backward actions
        backward = new Image("/resources/backward.png");
        backward2 = new Image("/resources/backward-x2.png");
        backward4 = new Image("/resources/backward-x4.png");
        backward8 = new Image("/resources/backward-x8.png");
    }

    /**
     * Stop's the simulation
     */
    @FXML
    public void stopClick() {
        aiStop();
        start_button.setImage(play);
        timeline.stop();
        model.setCurrentFrame(0);
        model.nextFrame();
        renderer.render();

        FPS.setText("Stopped");
        RDR.setText("-");
    }

    /**
     * Starts the simulation
     */
    @FXML
    public void startClick() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
            FPS.setText("Paused");
            RDR.setText("-");
            start_button.setImage(play);
            aiStop();
        } else {
            timeline.play();
            start_button.setImage(pause);
            aiStart();
        }
    }

    /**
     * Stop's the threads that are sending/recieving
     */
    public void aiStop() {
        ail.interrupt();
    }
    /**
     * Start's the threads that are sending/recieving
     */
    public void aiStart() {

        try {
            ail = new AIListener();
            mcst = new MultiCastServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ail.start();
    }

    /**
     * Sets the image of acceleration button
     */
    @FXML
    public void acceleration() {
        if (model.getAcceleration() == 1) {
            backward_button.setImage(backward);
            forward_button.setImage(forward);
        } else if (model.getAcceleration() == 0.125) {
            backward_button.setImage(backward8);
        } else if (model.getAcceleration() == 0.25) {
            backward_button.setImage(backward4);
        } else if (model.getAcceleration() == 0.5) {
            backward_button.setImage(backward2);
        } else if (model.getAcceleration() == 2) {
            forward_button.setImage(forward2);
        } else if (model.getAcceleration() == 4) {
            forward_button.setImage(forward4);
        } else if (model.getAcceleration() == 8) {
            forward_button.setImage(forward8);
        } else {
            model.setAcceleration(1);
            backward_button.setImage(backward);
            forward_button.setImage(forward);
        }
    }

    /**
     * Fast forwards the simulation when clicked on forward button
     */
    @FXML
    public void forwardClick() {
        model.setAcceleration(model.getAcceleration() + model.getAcceleration());

        // Accelerate to given values
        acceleration();
    }

    /**
     * Slows down the simulation when clicked on backward buton
     */
    @FXML
    public void backwardClick() {
        model.setAcceleration(model.getAcceleration() / 2);

        // Accelerate to given values
        acceleration();
    }

    /**
     * Sets frame in the model to a specific frame number
     * @param number
     */
    private void changeFrameNumber(int number) {
        model.setCurrentFrame(number);
        renderer.render();
    }

    /**
     * Set frame number based on what framenumber is selected in list
     * @param event
     */
    public void keyUpDown(KeyEvent event) {
        if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
            String frameNumber = frameList.getSelectionModel().getSelectedItem();
            changeFrameNumber(Integer.parseInt(frameNumber));
        }
    }

    /**
     * Opens exit dialog
     */
    public void exitApplication() {
        try {
            exitDialog exitDialog = new exitDialog(canvas.getParent(), this);
            exitDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens exit dialog on escape button
     * @param event
     */
    public void exitOnEscape(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            exitApplication();
        }
    }

    /**
     * Opens file chooser
     */
    public void showFileChooser() {
        new FileDialog().startDialog((Stage) canvas.getScene().getWindow());
    }

    /**
     * Opens settings menu
     */
    public void showSettings() {
        new SettingsDialog().startDialog((Stage) canvas.getScene().getWindow());
    }
}
