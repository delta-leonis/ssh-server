package org.ssh.field3d.gameobjects.overlay;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

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
 * @author marklef2
 * @date 5-11-2015
 */
public class CameraControlOverlayGO extends GameObject {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Preset button click events
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Button 45 degree org.ssh.view click event
     * 
     * @author marklef2
     *        
     */
    class OnButton45DegViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Set camera to 45 degree angle, set location to center, set zoom
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateX(45);
            
        }
        
    }
    
    /**
     * 
     * Button rotate down click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonRotateDownClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Smooth out rotation
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera()
                    .SetRotateX(CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().GetRotateX() - 10);
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Rotation button click events
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Button rotate left click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonRotateLeftClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Smooth out rotation
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera()
                    .SetRotateY(CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().GetRotateY() - 10);
        }
    }
    
    /**
     * 
     * Button rotate right click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonRotateRightClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Smooth out rotation
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera()
                    .SetRotateY(CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().GetRotateY() + 10);
        }
        
    }
    
    /**
     * 
     * Button rotate up click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonRotateUpClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Smooth out rotation
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera()
                    .SetRotateX(CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().GetRotateX() + 10);
        }
        
    }
    
    /**
     * 
     * Button side org.ssh.view click event
     * 
     * @author marklef2
     *        
     */
    class OnButtonSideViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Set camera to side org.ssh.view, set location to center, set zoom
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateX(0);
            
        }
        
    }
    
    /**
     * 
     * Button top org.ssh.view click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonTopViewClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Set camera to top org.ssh.view, set location to center, set zoom
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateY(0);
            CameraControlOverlayGO.this.GetGame().GetThirdPersonCamera().SetRotateX(90);
            
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Zoom button click events
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Button zoom in click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonZoomInClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO zoom in
            // GetGame().GetThirdPersonCamera().SetRotateY(0);
            // GetGame().GetThirdPersonCamera().SetRotateX(90);
            
        }
        
    }
    
    /**
     * 
     * Button zoom out click event.
     * 
     * @author marklef2
     *        
     */
    class OnButtonZoomOutClicked implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent event) {
            
            // TODO Zoom out
        }
    }
    
    // TODO: move to config
    private static final int BUTTON_OFFSET = 35;
    private final Group      _controlsGroup;
                             
    private final Button     _buttonTopView;
                             
    private final Button     _buttonSideView;
                             
    private final Button     _button45DegView;
                             
    private final Button     _buttonRotateLeft;
                             
    private final Button     _buttonRotateRight;
                             
    private final Button     _buttonRotateUp;
                             
    private final Button     _buttonRotateDown;
                             
    private final Button     _buttonZoomIn;
                             
    private final Button     _buttonZoomOut;
                             
    public CameraControlOverlayGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Create group for the controls
        this._controlsGroup = new Group();
        
        // Creating buttons
        this._buttonTopView = new Button("Top View");
        this._buttonSideView = new Button("Side View");
        this._button45DegView = new Button("45 Degree View");
        
        this._buttonRotateLeft = new Button("Rotate Left");
        this._buttonRotateRight = new Button("Rotate Right");
        this._buttonRotateUp = new Button("Rotate Up");
        this._buttonRotateDown = new Button("Rotate Down");
        
        this._buttonZoomIn = new Button("+");
        this._buttonZoomOut = new Button("-");
        
        // Setting translation
        this._buttonSideView.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET);
        this._button45DegView.setTranslateY((2 * CameraControlOverlayGO.BUTTON_OFFSET));
        
        this._buttonRotateLeft.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 4);
        this._buttonRotateRight.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 5);
        this._buttonRotateUp.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 6);
        this._buttonRotateDown.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 7);
        
        this._buttonZoomIn.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 9);
        this._buttonZoomOut.setTranslateY(CameraControlOverlayGO.BUTTON_OFFSET * 10);
        
        // Adding buttons to controls group
        this._controlsGroup.getChildren().add(this._button45DegView);
        this._controlsGroup.getChildren().add(this._buttonSideView);
        this._controlsGroup.getChildren().add(this._buttonTopView);
        
        this._controlsGroup.getChildren().add(this._buttonRotateLeft);
        this._controlsGroup.getChildren().add(this._buttonRotateRight);
        this._controlsGroup.getChildren().add(this._buttonRotateUp);
        this._controlsGroup.getChildren().add(this._buttonRotateDown);
        
        this._controlsGroup.getChildren().add(this._buttonZoomIn);
        this._controlsGroup.getChildren().add(this._buttonZoomOut);
    }
    
    @Override
    public void Destroy() {
        
        // TODO remove event handlers
        
        // If 2d group contains the controls group
        if (this.GetGame().Get2DGroup().getChildren().contains(this._controlsGroup)) {
            
            // Remove from 2d group
            this.GetGame().Get2DGroup().getChildren().remove(this._controlsGroup);
        }
    }
    
    @Override
    public void Initialize() {
        
        // Adding buttons to 2d group
        this.GetGame().Get2DGroup().getChildren().add(this._controlsGroup);
        
        // Hook events
        this._button45DegView.setOnMouseClicked(new OnButton45DegViewClicked());
        this._buttonSideView.setOnMouseClicked(new OnButtonSideViewClicked());
        this._buttonTopView.setOnMouseClicked(new OnButtonTopViewClicked());
        
        this._buttonRotateLeft.setOnMouseClicked(new OnButtonRotateLeftClicked());
        this._buttonRotateRight.setOnMouseClicked(new OnButtonRotateRightClicked());
        this._buttonRotateUp.setOnMouseClicked(new OnButtonRotateUpClicked());
        this._buttonRotateDown.setOnMouseClicked(new OnButtonRotateDownClicked());
        
        this._buttonZoomIn.setOnMouseClicked(new OnButtonZoomInClicked());
        this._buttonZoomOut.setOnMouseClicked(new OnButtonZoomOutClicked());
    }
    
    @Override
    public void Update(final long timeDivNano) {
    }
}
