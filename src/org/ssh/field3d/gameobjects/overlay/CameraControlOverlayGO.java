package org.ssh.field3d.gameobjects.overlay;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 *
 * CameraPresetOverlay class
 *
 * This class is responsible for the 2d camera preset controls
 *
 * @author Mark Lefering
 */

// TODO: fxml
public class CameraControlOverlayGO extends GameObject {
    
    private static final int BUTTON_OFFSET = 35;
                                           
    private final Group      controlsGroup;
    private final Button     buttonTopView;
    private final Button     buttonSideView;
    private final Button     button45DegView;
    private final Button     buttonRotateLeft;
    private final Button     buttonRotateRight;
    private final Button     buttonRotateUp;
    private final Button     buttonRotateDown;
    private final Button     buttonZoomIn;
    private final Button     buttonZoomOut;
                             
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public CameraControlOverlayGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Create group for the controls
        this.controlsGroup = new Group();
        
        // Creating buttons
        this.buttonTopView = new Button("Top View");
        this.buttonSideView = new Button("Side View");
        this.button45DegView = new Button("45 Degree View");
        
        this.buttonRotateLeft = new Button("Rotate Left");
        this.buttonRotateRight = new Button("Rotate Right");
        this.buttonRotateUp = new Button("Rotate Up");
        this.buttonRotateDown = new Button("Rotate Down");
        
        this.buttonZoomIn = new Button("+");
        this.buttonZoomOut = new Button("-");
        
        // Setting translation
        this.buttonSideView.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET);
        this.button45DegView.setTranslateY((2 * CameraControlOverlayGO.BUTTON_OFFSET));
        
        this.buttonRotateLeft.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 4);
        this.buttonRotateRight.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 5);
        this.buttonRotateUp.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 6);
        this.buttonRotateDown.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 7);
        
        this.buttonZoomIn.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 9);
        this.buttonZoomOut.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 10);
        
        // Adding buttons to controls group
        this.controlsGroup.getChildren().add(this.button45DegView);
        this.controlsGroup.getChildren().add(this.buttonSideView);
        this.controlsGroup.getChildren().add(this.buttonTopView);
        
        this.controlsGroup.getChildren().add(this.buttonRotateLeft);
        this.controlsGroup.getChildren().add(this.buttonRotateRight);
        this.controlsGroup.getChildren().add(this.buttonRotateUp);
        this.controlsGroup.getChildren().add(this.buttonRotateDown);
        
        this.controlsGroup.getChildren().add(this.buttonZoomIn);
        this.controlsGroup.getChildren().add(this.buttonZoomOut);
    }
    
    /**
     * Initialize method. This method adds the control group to the 2d group and hooks events.
     */
    @Override
    public void Initialize() {
        
        // Adding control group to 2d group
        Platform.runLater(() -> this.GetGame().get2DGroup().getChildren().add(this.controlsGroup));
        
        // Hook events
        this.button45DegView.setOnMouseClicked(new OnButton45DegViewClicked());
        this.buttonSideView.setOnMouseClicked(new OnButtonSideViewClicked());
        this.buttonTopView.setOnMouseClicked(new OnButtonTopViewClicked());
        
        this.buttonRotateLeft.setOnMouseClicked(new OnButtonRotateLeftClicked());
        this.buttonRotateRight.setOnMouseClicked(new OnButtonRotateRightClicked());
        this.buttonRotateUp.setOnMouseClicked(new OnButtonRotateUpClicked());
        this.buttonRotateDown.setOnMouseClicked(new OnButtonRotateDownClicked());
        
        this.buttonZoomIn.setOnMouseClicked(new OnButtonZoomInClicked());
        this.buttonZoomOut.setOnMouseClicked(new OnButtonZoomOutClicked());
    }
    
    /**
     * Destroy method. This method removes the control group from the 2d group
     */
    @Override
    public void Destroy() {
        
        // TODO: remove event handlers
        
        // If 2d group contains the controls group
        if (this.GetGame().get2DGroup().getChildren().contains(this.controlsGroup)) {
            
            // Remove from 2d group
            Platform.runLater(() -> this.GetGame().get2DGroup().getChildren().remove(this.controlsGroup));
        }
        
        // Unhook events
        this.button45DegView.setOnMouseClicked(null);
        this.buttonSideView.setOnMouseClicked(null);
        this.buttonTopView.setOnMouseClicked(null);
        
        this.buttonRotateLeft.setOnMouseClicked(null);
        this.buttonRotateRight.setOnMouseClicked(null);
        this.buttonRotateUp.setOnMouseClicked(null);
        this.buttonRotateDown.setOnMouseClicked(null);
        
        this.buttonZoomIn.setOnMouseClicked(null);
        this.buttonZoomOut.setOnMouseClicked(null);
    }
    
    /**
     * 
     * @param timeDivNano
     *            The time difference in nanoseconds.
     */
    @Override
    public void Update(final long timeDivNano) {
    }
    
    /**
     * 
     * Button rotate down click event.
     * 
     * @author Mark Lefering
     */
    class OnButtonRotateDownClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO: Smooth out rotation
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera()
                    .SetRotateX(CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().GetRotateX() - 10);
        }
        
    }
    
    /**
     * 
     * Button rotate left click event.
     * 
     * @author Mark Lefering
     */
    class OnButtonRotateLeftClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO: Smooth out rotation
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera()
                    .SetRotateY(CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().GetRotateY() - 10);
        }
    }
    
    /**
     * 
     * Button rotate right click event.
     * 
     * @author Mark Lefering
     */
    class OnButtonRotateRightClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO Smooth out rotation
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera()
                    .SetRotateY(CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().GetRotateY() + 10);
        }
        
    }
    
    /**
     * 
     * Button rotate up click event.
     * 
     * @author Mark Lefering
     */
    class OnButtonRotateUpClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO: Smooth out rotation
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera()
                    .SetRotateX(CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().GetRotateX() + 10);
        }
        
    }
    
    /**
     * 
     * Button side view click event
     * 
     * @author Mark Lefering
     */
    class OnButtonSideViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO: Set location to center
            // TODO: Set zoom
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateX(0);
            
        }
        
    }
    
    /**
     * 
     * Button 45 degree view click event
     * 
     * @author Mark Lefering
     */
    class OnButton45DegViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // TODO: Set location to center
            // TODO: Set zoom
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateX(45);
        }
    }
    
    /**
     * 
     * Button view click event.
     * 
     * @author Mark Lefering
     *         
     */
    class OnButtonTopViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO: Set location to center
            // TODO: Set zoom
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().getThirdPersonCamera().SetRotateX(90);
        }
        
    }
    
    /**
     * 
     * Button zoom in click event.
     * 
     * @author Mark Lefering
     */
    class OnButtonZoomInClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO: zoom in
        }
    }
    
    /**
     * 
     * Button zoom out click event.
     * 
     * @author Mark Lefering
     *         
     */
    class OnButtonZoomOutClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO: Zoom out
        }
    }
}
