package org.ssh.ui.components.overlay;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.reactfx.EventStreams;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.centersection.GameScene;
import org.ssh.ui.components.centersection.gamescene.GameSceneCameraController;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author Jeroen de Jong
 * @author Joost Overeem
 * @date 3-2-2016
 */
public class CameraControlOverlay extends UIComponent<GridPane> {
    /**
     * Controller for the camera
     */
    private GameSceneCameraController controller;
    /**
     * The rotation sensitivity for the button in degree per second.
     */
    private static final float ROTATION_SENSITIVITY = 40.0f;

    /**
     * The zoom sensitivity for the button in mm per second.
     */
    private static final float ZOOM_SENSITIVITY = 400.0f;

    /**
     * The amount of nano seconds per second.
     */
    private static final float NANO_SEC_PER_SEC = 1_000_000_000.0f;
    /**
     * The zoom value for the TopSection view preset
     */
    private static final float ZOOM_TOP_VIEW = -8000.0f;

    /**
     * The zoom value for the side view preset.
     */
    private static final float ZOOM_SIDE_VIEW = -8000.0f;
    /**
     * The zoom value for the 45 degree view preset.
     */
    private static final float ZOOM_45_DEG_VIEW = -7000.0f;

    private HashMap<ButtonAction, Boolean> updateList = new HashMap<>();
    private double rotationAmount, zoomAmount;

    /**
     * Instantiates a new overlay with all controls
     */
    public CameraControlOverlay() {
        super("cameraControlOverlay", "overlay/cameracontroloverlay.fxml");
        Platform.runLater(() ->
                //retrieve the controller
                controller = ((GameScene)((Pane)UI.getHighestParent(this.getComponent())
                        // look for the 3d field
                        .lookup("#fieldBase")).getChildren().get(0))
                        // get his
                        .getCameraController());

        // add all buttons of this scene to the update list
        getComponent().getChildren().stream()
                .filter(child -> child instanceof Button)
                .forEach(child -> updateList.put(ButtonAction.valueOf(child.getId()), false));

        /**
         * Timer which handles the buttons that should be processed while they're pressed
         */
        new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                long timeDiv = last - now;
                last = now;
                //calculate rotation amount and zoom amount based on time difference between now and previous frame
                rotationAmount = ROTATION_SENSITIVITY * (timeDiv / NANO_SEC_PER_SEC);
                 zoomAmount = ZOOM_SENSITIVITY * (timeDiv / NANO_SEC_PER_SEC);

                updateList.entrySet().forEach( pair -> {
                    //if primary button is down
                    if(pair.getValue())
                        handleButtons(pair.getKey());
                });
            }
        }.start();
    }

    /**
     * Handles camera translations and rotation
     * @param action                action to process
     */
    private void handleButtons(ButtonAction action) {
        switch(action){
            case ROTATE_RIGHT:
                    controller.setRotateZ(controller.getRotateZ().getAngle() - rotationAmount);
                break;
            case ROTATE_LEFT:
                    controller.setRotateZ(controller.getRotateZ().getAngle() + rotationAmount);
                break;
            case ROTATE_UP:
                    controller.setRotateX(controller.getRotateX().getAngle() - rotationAmount);
                break;
            case ROTATE_DOWN:
                    controller.setRotateX(controller.getRotateX().getAngle() + rotationAmount);
                break;
            case ZOOM_IN:
                    controller.setZoom(controller.getZoom() - zoomAmount);
                break;
            case ZOOM_OUT:
                    controller.setZoom(controller.getZoom() + zoomAmount);
                break;
        }

    }

    /**
     * enum describing each possible button combination<br />
     * <em>Note: </em> {@link Node#getId()} of a button should equal to a enum constant
     */
    private enum ButtonAction{
        TOP_VIEW, SKEW_VIEW, ROTATE_LEFT, ROTATE_RIGHT, ROTATE_UP, ROTATE_DOWN, ZOOM_IN, ZOOM_OUT, HOP_LEFT, HOP_RIGHT;
    }

    /**
     * onReleased and onPressed event for buttons that should be handled every tick when pressed
     * @param event the {@link MouseEvent} as provided by the source
     */
    @FXML
    private void onButtonPressed(MouseEvent event){
        updateList.put(
                ButtonAction.valueOf(((Node)event.getSource()).getId()), // convert to enum
                event.isPrimaryButtonDown()); // save primary button state as the value
    }

    /**
     * On button TopSection view action event handler.
     *
     * @param actionEvent The {@link ActionEvent}.
     */
    @FXML
    private void onButtonTopViewAction(ActionEvent actionEvent) {
        if (controller == null)
            return;
        controller.setPivot(0, 0, 0);
        controller.setEuler(-Math.PI/2, 0, 0);
        controller.setZoom(ZOOM_TOP_VIEW);
    }

    /**
     * On button side view action event handler.
     *
     * @param actionEvent The {@link ActionEvent}.
     */
    @FXML
    private void onButtonSideViewAction(ActionEvent actionEvent) {
        if (controller == null)
            return;
        controller.setPivot(0, 0, 0);
        controller.setEuler(0, 0, 0);
        controller.setZoom(ZOOM_SIDE_VIEW);
    }

    /**
     * On button45 degree view action event handler.
     *
     * @param actionEvent The {@link ActionEvent}.
     */
    @FXML
    private void onButton45DegViewAction(ActionEvent actionEvent) {
        if (controller == null)
            return;
        controller.setPivot(0, 0, 0);
        controller.setEuler(-Math.PI/4, 0, 0);
        controller.setZoom(ZOOM_45_DEG_VIEW);
    }

    /**
     * Changes the sight a quarter (90 degrees) to the right.
     */
    @FXML
    private void hopCameraPositionQuarterRight() {
        if(controller == null)
            return;
        controller.setRotate(0,
                (controller.getRotateY().getAngle() + 270) % 360,
                0);
    }


    /**
     * Changes the sight a quarter (90 degrees) to the left.
     */
    @FXML
    private void hopCameraPositionQuarterLeft() {
        if(controller == null)
            return;
        controller.setRotate(0,
                (controller.getRotateY().getAngle() + 90) % 360,
                0);
    }
}
