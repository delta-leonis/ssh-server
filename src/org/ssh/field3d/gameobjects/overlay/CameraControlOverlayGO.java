package org.ssh.field3d.gameobjects.overlay;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * CameraPresetOverlay class
 * 
 * This class is responsible for the 2d camera preset controls.
 *
 * @author Mark Lefering
 */
public class CameraControlOverlayGO extends OverlayGO {
    
    /** The FXML file for the layout. */
    private static final String LAYOUT_FXML_FILE     = "cameracontroloverlay.fxml";
                                                     
    /** The rotation sensitivity for the button in degree per second. */
    private static final float  ROTATION_SENSITIVITY = 40.0f;
                                                     
    /** The zoom sensitivity for the button in mm per second. */
    private static final float  ZOOM_SENSITIVITY     = 400.0f;
                                                     
    /** The amount of nano seconds per second. */
    private static final float  NANO_SEC_PER_SEC     = 1000000000.0f;
                                                     
    /** The zoom value for the top view preset */
    private static final float  ZOOM_TOP_VIEW        = -350.0f;
                                                     
    /** The zoom value for the side view preset. */
    private static final float  ZOOM_SIDE_VIEW       = -350.0f;
                                                     
    /** The zoom value for the 45 degree view preset. */
    private static final float  ZOOM_45_DEG_VIEW     = -300.0f;
                                                     
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
                              
    @FXML
    private GridPane rootPane;
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public CameraControlOverlayGO(final Game game) {
        
        // Initialize super class
        super(game, LAYOUT_FXML_FILE);
        rootPane.maxHeightProperty().bind(game.heightProperty());
        rootPane.minHeightProperty().bind(game.heightProperty());
        rootPane.maxWidthProperty().bind(game.widthProperty());
        rootPane.minWidthProperty().bind(game.widthProperty());
        
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
    public void onUpdateGeometry() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateDetection() {        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
        double cameraRotationY = this.getGame().getThirdPersonCamera().getRotateY();
        double cameraRotationX = this.getGame().getThirdPersonCamera().getRotateX();
        double cameraZoom = this.getGame().getThirdPersonCamera().getZoom();
        
        double rotationAmount = ROTATION_SENSITIVITY * (timeDivNano / NANO_SEC_PER_SEC);
        double zoomAmount = ZOOM_SENSITIVITY * (timeDivNano / NANO_SEC_PER_SEC);
        
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
            
            // Zoom in
            this.getGame().getThirdPersonCamera().setZoom((long) (cameraZoom + zoomAmount));
            
            System.out.println("Camera zoom: " + cameraZoom + ", zoom amount " + zoomAmount);
        }
        else if (this.buttonZoomOutPressing) {
            
            // zoom out
            this.getGame().getThirdPersonCamera().setZoom((long) (cameraZoom - zoomAmount));
            
        }
        
        super.onUpdate(timeDivNano);
    }
    
    /**
     * On button top view action event handler.
     *
     * @param actionEvent
     *            The {@link ActionEvent}.
     */
    @FXML
    private void onButtonTopViewAction(ActionEvent actionEvent) {
        
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting zoom
        this.getGame().getThirdPersonCamera().setZoom(ZOOM_TOP_VIEW);
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(90);
    }
    
    /**
     * On button side view action event handler.
     *
     * @param actionEvent
     *            The {@link ActionEvent}.
     */
    @FXML
    private void onButtonSideViewAction(ActionEvent actionEvent) {
        
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting zoom
        this.getGame().getThirdPersonCamera().setZoom(ZOOM_SIDE_VIEW);
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(0);
    }
    
    /**
     * On button45 degree view action event handler.
     *
     * @param actionEvent
     *            The {@link ActionEvent}.
     */
    @FXML
    private void onButton45DegViewAction(ActionEvent actionEvent) {
        
        // Getting camera pivot location
        Vector3f cameraPivot = this.getGame().getThirdPersonCamera().getPivot();
        
        // Setting pivot location to center
        this.getGame().getThirdPersonCamera().setPivot(new Vector3f(0.0f, cameraPivot.y, 0.0f));
        
        // Setting zoom
        this.getGame().getThirdPersonCamera().setZoom(ZOOM_45_DEG_VIEW);
        
        // Setting rotations
        this.getGame().getThirdPersonCamera().setRotateY(0);
        this.getGame().getThirdPersonCamera().setRotateX(45);
    }
    
    /**
     * On button rotate left pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateLeftPressed(MouseEvent mouseEvent) {
        
        // Setting rotating left state
        buttonRotateLeftPressing = true;
    }
    
    /**
     * On button rotate left released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateLeftReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating left state
        buttonRotateLeftPressing = false;
    }
    
    /**
     * On button rotate right pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateRightPressed(MouseEvent mouseEvent) {
        
        // Setting rotating right state
        buttonRotateRightPressing = true;
    }
    
    /**
     * On button rotate right released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateRightReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating left state
        buttonRotateRightPressing = false;
    }
    
    /**
     * On button rotate up pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateUpPressed(MouseEvent mouseEvent) {
        
        // Setting rotating up state
        buttonRotateUpPressing = true;
    }
    
    /**
     * On button rotate up released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateUpReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating up state
        buttonRotateUpPressing = false;
    }
    
    /**
     * On button rotate down pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateDownPressed(MouseEvent mouseEvent) {
        
        // Setting rotating down state
        buttonRotateDownPressing = true;
    }
    
    /**
     * On button rotate down released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonRotateDownReleased(MouseEvent mouseEvent) {
        
        // Setting not rotating down state
        buttonRotateDownPressing = false;
    }
    
    /**
     * On button zoom in pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    // Zoom buttons
    @FXML
    private void onButtonZoomInPressed(MouseEvent mouseEvent) {
        
        // Setting zooming in state
        buttonZoomInPressing = true;
    }
    
    /**
     * On button zoom in released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonZoomInReleased(MouseEvent mouseEvent) {
        
        // Setting not zooming in state
        buttonZoomInPressing = false;
    }
    
    /**
     * On button zoom out pressed event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonZoomOutPressed(MouseEvent mouseEvent) {
        
        // Setting zooming out state
        buttonZoomOutPressing = true;
    }
    
    /**
     * On button zoom out released event handler.
     *
     * @param mouseEvent
     *            The {@link MouseEvent}.
     */
    @FXML
    private void onButtonZoomOutReleased(MouseEvent mouseEvent) {
        
        // Setting not zooming out state
        buttonZoomOutPressing = false;
    }
}
