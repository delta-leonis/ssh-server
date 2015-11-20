package org.ssh.field3d.gameobjects.overlay;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * CameraPresetOverlay class
 * 
 * This class is responsible for the 2d camera preset controls.
 *
 * @author Mark Lefering
 */
public class CameraControlOverlayGO extends OverlayGO {
    
    /**  The FXML file for the layout. */
    private static final String LAYOUT_FXML_FILE     = "cameracontroloverlay.fxml";
                                                     
    /** The rotation sensitivity for the button in degree per second. */
    private static final float  ROTATION_SENSITIVITY = 40.0f;
                                                     
    /** The amount of nano seconds per second. */
    private static final float  NANO_SEC_PER_SEC     = 1000000000.0f;
                                                     
    /** The button rotate left pressing. */
    private boolean             buttonRotateLeftPressing;
    
    /** The button rotate right pressing. */
    private boolean             buttonRotateRightPressing;
    
    /** The button rotate up pressing. */
    private boolean             buttonRotateUpPressing;
    
    /** The button rotate down pressing. */
    private boolean             buttonRotateDownPressing;
                                
    /** The button zoom in pressing. */
    private boolean             buttonZoomInPressing;
    
    /** The button zoom out pressing. */
    private boolean             buttonZoomOutPressing;
                                
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public CameraControlOverlayGO(final Game game) {
        
        // Initialize super class
        super(game, LAYOUT_FXML_FILE);
        
        // Setting default values
        buttonRotateDownPressing = buttonRotateLeftPressing = buttonRotateRightPressing = buttonRotateUpPressing = false;
        buttonZoomInPressing = buttonZoomOutPressing = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        super.onInitialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        super.onDestroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
        double cameraRotationY = this.getGame().getThirdPersonCamera().getRotateY();
        double cameraRotationX = this.getGame().getThirdPersonCamera().getRotateX();
        
        double rotationAmount = ROTATION_SENSITIVITY * (timeDivNano / NANO_SEC_PER_SEC);
        
        if (buttonRotateLeftPressing) {
            
            // Rotate camera left
            this.getGame().getThirdPersonCamera().setRotateY(cameraRotationY - rotationAmount);
        }
        else if (this.buttonRotateRightPressing) {
        
            // Rotate camera right
            this.getGame().getThirdPersonCamera().setRotateY(cameraRotationY + rotationAmount);
        }
        else if (this.buttonRotateUpPressing) {
            
            // Rotate camera up
            this.getGame().getThirdPersonCamera().setRotateX(cameraRotationX + rotationAmount);
        }
        else if (this.buttonRotateDownPressing) {
            
            // Rotate camera down
            this.getGame().getThirdPersonCamera().setRotateX(cameraRotationX - rotationAmount);
        }
        else if (this.buttonZoomInPressing) {
            
            // TODO: zoom camera in
        }
        else if (this.buttonZoomOutPressing) {
            
            // TODO: zoom camera out
        }
        
        super.onUpdate(timeDivNano);
    }
    
    /**
     * On button top view action.
     *
     * @param actionEvent the action event
     */
    @FXML
    private void onButtonTopViewAction(ActionEvent actionEvent) {
        
        // TODO: Set zoom
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(90);
    }
    
    /**
     * On button side view action.
     *
     * @param actionEvent the action event
     */
    @FXML
    private void onButtonSideViewAction(ActionEvent actionEvent) {
    
        // TODO: Set zoom
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(0);
    }
    
    /**
     * On button45 deg view action.
     *
     * @param actionEvent the action event
     */
    @FXML
    private void onButton45DegViewAction(ActionEvent actionEvent) {
    
        // TODO: Set zoom
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(45);
    }
    
    /**
     * On button rotate left pressed.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateLeftPressed(MouseEvent mouseEvent) {
        
        // Setting rotating left state
        buttonRotateLeftPressing = true;
    }
    
    /**
     * On button rotate left released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateLeftReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating left state
        buttonRotateLeftPressing = false;
    }
    
    /**
     * On button rotate right pressed.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateRightPressed(MouseEvent mouseEvent) {
        
        // Setting rotating right state
        buttonRotateRightPressing = true;
    }
    
    /**
     * On button rotate right released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateRightReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating left state
        buttonRotateRightPressing = false;
    }
    
    /**
     * On button rotate up pressed.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateUpPressed(MouseEvent mouseEvent) {
        
        // Setting rotating up state
        buttonRotateUpPressing = true;
    }
    
    /**
     * On button rotate up released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateUpReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating up state
        buttonRotateUpPressing = false;
    }
    
    /**
     * On button rotate down pressed.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateDownPressed(MouseEvent mouseEvent) {
        
        // Setting rotating down state
        buttonRotateDownPressing = true;
    }
    
    /**
     * On button rotate down released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonRotateDownReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating down state
        buttonRotateDownPressing = false;
    }
    
    /**
     * On button zoom in pressed.
     *
     * @param mouseEvent the mouse event
     */
    // Zoom buttons
    @FXML
    private void onButtonZoomInPressed(MouseEvent mouseEvent) {
        
        // Setting zooming in state
        buttonZoomInPressing = true;
    }
    
    /**
     * On button zoom in released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonZoomInReleased(MouseEvent mouseEvent) {
        
        // Setting not zooming in state
        buttonZoomInPressing = false;
    }
    
    /**
     * On button zoom out pressed.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonZoomOutPressed(MouseEvent mouseEvent) {
        
        // Setting zooming out state
        buttonZoomOutPressing = true;
    }
    
    /**
     * On button zoom out released.
     *
     * @param mouseEvent the mouse event
     */
    @FXML
    private void onButtonZoomOutReleased(MouseEvent mouseEvent) {
        
        // Setting not zooming out state
        buttonZoomOutPressing = false;
    }
}
