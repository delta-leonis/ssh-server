package org.ssh.field3d.core.gameobjects.camera;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.math.Xform;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;

/**************************************************************************************************
 *
 * ThirdPersonCamera class. This class is used for the 3rd person camera.
 *
 * @see GameObject
 *      
 * @author marklef2
 */
public class ThirdPersonCamera extends GameObject {
    
    // TODO: Move to config
    public static final double      DEFAULT_NEAR_PANE          = 0.1;
    public static final double      DEFAULT_FAR_PANE           = 10000000.0;
    public static final double      INITIAL_CAMERA_DISTANCE    = -10000.0;
    public static final double      INITIAL_CAMERA_ROT_X       = 90.0;
    public static final double      INITIAL_CAMERA_ROT_Y       = 0.0;
    public static final double      MOUSE_LOOK_SENSITIVITY     = 0.35;
    public static final double      MOUSE_MOVEMENT_SENSITIVITY = 10.0;
    public static final double      MOUSE_ZOOM_SENSITIVITY     = 10.0;
    public static final double      CAMERA_ZOOM_MIN            = -200000.0;
    public static final double      CAMERA_ZOOM_MAX            = -200.0;
    public static final double      CAMERA_LOC_X_MIN           = -(FieldGame.FIELD_WIDTH / 2.0) - 200.0;
    public static final double      CAMERA_LOC_X_MAX           = (FieldGame.FIELD_WIDTH / 2.0) + 200.0;
    public static final double      CAMERA_LOC_Z_MIN           = -(FieldGame.FIELD_DEPTH / 2.0) - 200.0;
    public static final double      CAMERA_LOC_Z_MAX           = (FieldGame.FIELD_DEPTH / 2.0) + 200.0;
                                                               
    private Vector3f                pivot;
    private final PerspectiveCamera perspectiveCamera;
    private final Xform             xForm1, xForm2, xForm3;
                                    
    private final double            nearClip, farClip;
    private double                  zoomMax, zoomMin;
    private double                  locXMin, locXMax, locZMin, locZMax;
                                    
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public ThirdPersonCamera(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Setting pivot
        this.pivot = new Vector3f(0.0f, 200.0f, 0.0f);
        
        // Create new perspective camera
        this.perspectiveCamera = new PerspectiveCamera(true);
        
        // Create 3 transforms
        this.xForm1 = new Xform();
        this.xForm2 = new Xform();
        this.xForm3 = new Xform();
        
        // Setting near clip
        this.nearClip = ThirdPersonCamera.DEFAULT_NEAR_PANE;
        // Setting far clip
        this.farClip = ThirdPersonCamera.DEFAULT_FAR_PANE;
        
        // Setting default minimal & maximal values
        this.zoomMax = ThirdPersonCamera.CAMERA_ZOOM_MAX;
        this.zoomMin = ThirdPersonCamera.CAMERA_ZOOM_MIN;
        this.locXMax = ThirdPersonCamera.CAMERA_LOC_X_MAX;
        this.locXMin = ThirdPersonCamera.CAMERA_LOC_X_MIN;
        this.locZMax = ThirdPersonCamera.CAMERA_LOC_Z_MAX;
        this.locZMin = ThirdPersonCamera.CAMERA_LOC_Z_MIN;
        
        // Setup camera
        this.perspectiveCamera.setNearClip(this.nearClip);
        this.perspectiveCamera.setFarClip(this.farClip);
        this.perspectiveCamera.setDepthTest(DepthTest.ENABLE);
        
        // Creating Xforms
        this.xForm1.getChildren().add(this.xForm2);
        this.xForm2.getChildren().add(this.xForm3);
        this.xForm3.getChildren().add(this.perspectiveCamera);
        
        // Setting initial rotations
        this.xForm1.rotationX.setAngle(ThirdPersonCamera.INITIAL_CAMERA_ROT_X);
        this.xForm1.rotationY.setAngle(ThirdPersonCamera.INITIAL_CAMERA_ROT_Y);
        
        // Flip y-axis so positive y is upwards
        this.xForm3.setRotateZ(180.0);
    }
    
    /**
     * Initialize method. This method adds the camera to the camera group.
     */
    @Override
    public void initialize() {
        
        // Add our camera Xform to the camera group
        Platform.runLater(() -> this.getGame().getCameraGroup().getChildren().add(this.xForm1));
    }
    
    /**
     * Update method. This method updates the camera. Movement, rotation and zoom is handled here.
     */
    @Override
    public void update(final long timeDivNano) {
        
        // TODO: Remove magic numbers
        
        // Check if middle button is down
        if (this.getGame().getMouseInputHandler().isMidButtonDown()) {
            
            // Rotate around y-axis
            this.xForm1.rotationY.setAngle(this.xForm1.rotationY.getAngle() + (this.getGame().getMouseInputHandler().getMouseDeltaX()
                    * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
            // Rotate around x-axis
            this.xForm1.rotationX.setAngle(this.xForm1.rotationX.getAngle() + (this.getGame().getMouseInputHandler().getMouseDeltaY()
                    * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
        }
        
        // Limit x-axis rotation
        if (this.xForm1.rotationX.getAngle() > 90.0) {
            
            this.xForm1.rotationX.setAngle(90.0);
        }
        else if (this.xForm1.rotationX.getAngle() < 0.0) {
            
            this.xForm1.rotationX.setAngle(0.0);
        }
        
        // Calculate zoom value
        double zoomCalc = (this.getGame().getMouseInputHandler().getScrollWheelYValue()
                * ThirdPersonCamera.MOUSE_ZOOM_SENSITIVITY) + ThirdPersonCamera.INITIAL_CAMERA_DISTANCE;
                
        // Limit zoom
        if (zoomCalc < this.zoomMin) {
            zoomCalc = this.zoomMin;
        }
        else if (zoomCalc > this.zoomMax) {
            zoomCalc = this.zoomMax;
        }
        
        // Perform "zoom" (translate camera closer or away from pivot point)
        this.xForm2.setTranslate(0.0, 0.0, zoomCalc);
        
        // Translate to pivot location
        this.xForm1.setTranslate(this.pivot.x, this.pivot.y, this.pivot.z);
        
        final double movementScale = 1
                - ((this.getGame().getMouseInputHandler().getScrollWheelYValue() + 990.0) / 2000.0);
                
        // Check if left mouse button is down
        if (this.getGame().getMouseInputHandler().isLeftButtonDown()) {
            
            // Calculate mouse values
            final double mouseXCalc = this.getGame().getMouseInputHandler().getMouseDeltaX()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;
            final double mouseYCalc = this.getGame().getMouseInputHandler().getMouseDeltaY()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;
                    
            // Rotate mouse translation according to camera
            Point3D mouseMovement = new Point3D(mouseXCalc, 0.0, mouseYCalc);
            mouseMovement = this.xForm1.rotationY.transform(mouseMovement);
            
            // Update pivot
            this.pivot.x += mouseMovement.getX() * movementScale;
            // Limit zoom
            this.pivot.z += mouseMovement.getZ() * movementScale;
        }
        
        // Limit movement x-axis
        if (this.pivot.x < this.locXMin) {
            this.pivot.x = (float) this.locXMin;
        }
        else if (this.pivot.x > this.locXMax) {
            this.pivot.x = (float) this.locXMax;
        }
        
        // Limit movement z-axis
        if (this.pivot.z < this.locZMin) {
            this.pivot.z = (float) this.locZMin;
        }
        else if (this.pivot.z > this.locZMax) {
            this.pivot.z = (float) this.locZMax;
        }
    }
    
    /**
     * Destroy method. This method removes the camera from the camera group.
     */
    @Override
    public void destroy() {
        
        // Check if we need to remove the camera
        if (this.getGame().getCameraGroup().getChildren().contains(this.xForm1)) {
            
            // Remove our camera Xform from the camera group
            Platform.runLater(() -> this.getGame().getCameraGroup().getChildren().remove(this.xForm1));
        }
    }
    
    public Vector3f getCameraLoc() {
        
        return this.getPivot().add(this.getPivotOffsetLoc());
    }
    
    public Xform getCameraXform() {
        return this.xForm1;
    }
    
    public double getMaxLocX() {
        return this.locXMax;
    }
    
    public double getMaxLocZ() {
        return this.locZMax;
    }
    
    public double getMaxZoom() {
        return this.zoomMax;
    }
    
    public double getMinLocX() {
        return this.locXMin;
    }
    
    public double getMinLocZ() {
        return this.locZMin;
    }
    
    public double getMinZoom() {
        return this.zoomMin;
    }
    
    public PerspectiveCamera getPerspectiveCamera() {
        return this.perspectiveCamera;
    }
    
    public Vector3f getPivot() {
        return this.pivot;
    }
    
    public Vector3f getPivotOffsetLoc() {
        
        // Getting translation of camera (from the pivot)
        Point3D tmpPoint = new Point3D(this.xForm2.getTranslateX(),
                this.xForm2.getTranslateY(),
                this.xForm2.getTranslateZ());
        // Rotate the translation according to the rotation of the camera
        tmpPoint = this.xForm1.rotationX.transform(tmpPoint);
        tmpPoint = this.xForm1.rotationY.transform(tmpPoint);
        tmpPoint = this.xForm1.rotationZ.transform(tmpPoint);
        
        // Create new vector out of the rotated values
        return new Vector3f((float) tmpPoint.getX(), (float) tmpPoint.getY(), (float) tmpPoint.getZ());
    }
    
    public double getRotateX() {
        return this.xForm1.rotationX.getAngle();
    }
    
    public double getRotateY() {
        return this.xForm1.rotationY.getAngle();
    }
    
    public double getRotateZ() {
        return this.xForm1.rotationZ.getAngle();
    }
    
    public void setMaxLocX(final double locXMax) {
        this.locXMax = locXMax;
    }
    
    public void setMaxLocZ(final double locZMax) {
        this.locZMax = locZMax;
    }
    
    public void setMaxZoom(final double zoomMax) {
        this.zoomMax = zoomMax;
    }
    
    public void setMinLocX(final double locXMin) {
        this.locXMin = locXMin;
    }
    
    public void setMinLocZ(final double locZMin) {
        this.locZMin = locZMin;
    }
    
    public void setMinZoom(final double zoomMin) {
        this.zoomMin = zoomMin;
    }
    
    public void setPivot(final Vector3f pivot) {
        this.pivot = pivot;
    }
    
    public void setRotateX(final double angleX) {
        this.xForm1.rotationX.setAngle(angleX);
    }
    
    public void setRotateY(final double angleY) {
        this.xForm1.rotationY.setAngle(angleY);
    }
    
    public void setRotateZ(final double angleZ) {
        this.xForm1.rotationZ.setAngle(angleZ);
    }
    
}
