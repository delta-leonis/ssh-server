package nl.saxion.robosim.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
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
import nl.saxion.robosim.model.Model;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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
    private Timeline renderLoop;
    private Image pause, play, forward, forward2, forward4, forward8, backward, backward2, backward4, backward8;
    private AIListener ail;
    private Map<String, ArrayList<Long>> specialFrames = new TreeMap<>();
    boolean dragging = false;

    @FXML
    private ComboBox refereeCommands;

    @FXML
    private Canvas canvas;

    @FXML
    private Pane sliderPane;

    @FXML
    private Slider slider;

    @FXML
    private ImageView start_button, forward_button, backward_button;

    @FXML
    private javafx.scene.control.Label FPS, RDR, CMD, RobotId, RobotX, RobotY, RobotOrientation;

    @FXML
    private ListView<String> frameList;

    /**
     * This method initializes the canvas and connects handler's to listview's and buttons's
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sets height of canvas to full screen minus the width of the side bar
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        canvas.setHeight(screenSize.getHeight() - 165);
        canvas.setWidth(screenSize.getWidth() - 300);
        slider.setMinWidth(screenSize.getWidth() - 340);
        renderer = new Renderer(canvas);
        model = Model.getInstance();
        model.setUIController(this);
        model.setCanvas(canvas);
        model.setRenderer(renderer);

        /* Define a single frame, with the duration of second/60 */
        KeyFrame keyFrame = new KeyFrame(new Duration(1000 / 60), event -> {
            long startNano = System.nanoTime();
            model.nextFrame();
            renderer.render();
            long ms = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startNano, TimeUnit.NANOSECONDS);
            FPS.setText("" + (ms > 0 ? Math.min(60, 1000 / ms) : 60));
            RDR.setText("" + (ms > 0 ? Math.round(ms) : "<1") + "ms");
        });

        // The render loop play the frames
        renderLoop = new Timeline(keyFrame);
        renderLoop.setCycleCount(Animation.INDEFINITE);

        // Fill the combobox with referee commands
        loadImages();
        setInputListeners();

        try {
            ail = new AIListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop's the simulation
     */
    @FXML
    public void stopClick() {
        aiStop();
        start_button.setImage(play);
        renderLoop.stop();
        model.setCurrentFrame(0);
        model.nextFrame();
        renderer.render();

        FPS.setText("Stopped");
        RDR.setText("-");

        // Set acceleration back to normal state
        model.setAcceleration(1);
        acceleration();
    }

    /**
     * Starts the simulation
     */
    @FXML
    public void startClick() {
        if (renderLoop.getStatus() == Animation.Status.RUNNING) {
            renderLoop.stop();
            FPS.setText("Paused");
            RDR.setText("-");
            start_button.setImage(play);
            aiStop();
        } else {
            if (!model.getFrames().isEmpty()) {
                renderLoop.play();
                start_button.setImage(pause);
                aiStart();
            } else {
                Notifications.create()
                        .title("Error")
                        .text("No log found!")
                        .showWarning();
            }
        }
    }

    /**
     * Stop's the threads that are sending/recieving
     */
    public void aiStop() {
        if (ail != null) {
            ail.terminate();
        }
    }

    /**
     * Start's the threads that are sending/recieving
     */
    public void aiStart() {

        try {
            ail = new AIListener();
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
            model.setReversedAcceleration(8);
            backward_button.setImage(backward8);
        } else if (model.getAcceleration() == 0.25) {
            model.setReversedAcceleration(4);
            backward_button.setImage(backward4);
        } else if (model.getAcceleration() == 0.5) {
            model.setReversedAcceleration(2);
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
     *
     * @param number
     */
    private void changeFrameNumber(int number) {
        model.setCurrentFrame(number);
        renderer.render();
    }

    /**
     * Set frame number based on what framenumber is selected in list
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
        new FileDialog(renderer).startDialog((Stage) canvas.getScene().getWindow());
    }

    /**
     * Opens settings menu
     */
    public void showSettings() {
        new SettingsDialog().startDialog((Stage) canvas.getScene().getWindow());
    }

    /**
     * Reset robots position to bench
     */
    public void resetRobots() {

    }

    public void updateRobotUI(int selectedId, float xPos, float yPos, float orientation) {
        RobotId.setText("" + selectedId);
        RobotY.setText(String.format("%.2f", yPos));
        RobotX.setText(String.format("%.2f", xPos));
        RobotOrientation.setText(String.format("%.2f", orientation));
    }

    public void updateUI(String command, double position, int frameNumber) {
        CMD.setText(command);
        slider.adjustValue(position);
        frameList.getFocusModel().focus(frameNumber);
        frameList.scrollTo(frameNumber);
    }

    private void loadImages() {
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

    private void setInputListeners() {
        // Select the robot at the clicked point
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> model.selectRobotByPosition(event.getX(),
                event.getY(), renderLoop.getStatus() != Animation.Status.RUNNING));

        // Drag robot
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            model.setRobotPosition(event.getX(), event.getY(), renderLoop.getStatus() != Animation.Status.RUNNING);
            dragging = true;
        });

        // Release robot, place the robot in the bench if the mouse is released inside the bench
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            model.setInBench(event.getX(), event.getY(), renderLoop.getStatus() != Animation.Status.RUNNING);
            dragging = false;
        });

        canvas.setOnScroll(event1 -> {
            if (dragging)
                model.rotateSelectedRobot(event1.getDeltaY(), renderLoop.getStatus() != Animation.Status.RUNNING);
        });

        // Changes the frame based on selection on the slider
        slider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> model.setCurrentFrame((int) (model.size() *
                (slider.getValue() / 100))));

        // Changes the frame based on selection in listview
        frameList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeFrameNumber(Integer.parseInt(newValue));
            model.nextFrame();
            renderer.render();
        });

        ArrayList<Rectangle> sliderPoints = new ArrayList<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        refereeCommands.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int frameCount = model.getAllFrameNumbers().size();
            if (sliderPoints.size() > 0) {
                for (Rectangle r : sliderPoints)
                    r.setVisible(false);
            }
            if (newValue != null) {
                for (long x : specialFrames.get(newValue)) {
                    double positie = ((double) x / (double) frameCount) * 100;
                    positie = ((screenSize.getWidth() - 360) / 100) * positie;

                    Rectangle sliderPoint = new Rectangle(4, 8);
                    sliderPoints.add(sliderPoint);
                    sliderPoint.setFill(Color.ORANGE);
                    sliderPoint.setLayoutX(positie);
                    //   sliderPoint.setLayoutY(5);
                    sliderPane.getChildren().add(sliderPoint);
                }
            }
        });
    }

    public void setCommands(TreeSet<String> commandSet, Map<Long, String> allCommands) {
        commandSet.stream().filter(s -> !refereeCommands.getItems().contains(s)).forEach(s -> refereeCommands.getItems().add(s));
        frameList.setItems(model.getAllFrameNumbers());
        specialFrames.clear();
        for (Map.Entry<Long, String> searchReferee : allCommands.entrySet()) {
            Long frame = searchReferee.getKey();
            String refereeCommando = searchReferee.getValue();
            ArrayList<Long> temp = specialFrames.get(refereeCommando) != null ? specialFrames.get(refereeCommando) : new ArrayList<>();
            temp.add(frame);

            if (specialFrames.get(refereeCommando) == null) {
                specialFrames.put(refereeCommando, temp);
            } else {
                if (!specialFrames.get(refereeCommando).contains(frame)) {
                    specialFrames.get(refereeCommando).add(frame);
                }
            }
        }
    }

    public void unselect() {
        RobotId.setText("");
        RobotY.setText("");
        RobotX.setText("");
        RobotOrientation.setText("");
    }

    public void resetAI() {
        model.moveAiToBench();
    }
}
