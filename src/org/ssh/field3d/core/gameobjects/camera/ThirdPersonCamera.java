package org.ssh.field3d.core.gameobjects.camera;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Xform;
import org.ssh.managers.manager.Models;
import org.ssh.models.CameraSettings;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;

/**
 * ThirdPersonCamera class. This class is used for the third person camera for the 3d world.
 *
 * @author Mark Lefering
 * @see GameObject
 */
public class ThirdPersonCamera extends GameObject {
    
    /** The Constant DEFAULT_NEAR_PANE. */
    public static final double      DEFAULT_NEAR_PANE          = 10;
                                                               
    /** The Constant DEFAULT_FAR_PANE. */
    public static final double      DEFAULT_FAR_PANE           = 30000.0;
                                                               
    /** The Constant INITIAL_CAMERA_DISTANCE. */
    public static final double      INITIAL_CAMERA_DISTANCE    = -10000.0;
                                                               
    /** The Constant MOUSE_LOOK_SENSITIVITY. */
    public static final double      MOUSE_LOOK_SENSITIVITY     = 0.35;
                                                               
    /** The Constant MOUSE_MOVEMENT_SENSITIVITY. */
    public static final double      MOUSE_MOVEMENT_SENSITIVITY = 10.0;
                                                               
    /** The Constant MOUSE_ZOOM_SENSITIVITY. */
    public static final double      MOUSE_ZOOM_SENSITIVITY     = 10.0;
                                                               
    /** The pivot which the camera "follows". */
    private Point3D                 pivot;
                                    
    /** The camera settings model */
    private CameraSettings          cameraSettingsModel;
                                    
    /** The perspective camera. */
    private final PerspectiveCamera perspectiveCamera;
                                    
    /** The transformations. */
    private final Xform             xForm1, xForm2, xForm3;
                                    
    /** The clipping for the camera. */
    private final double            nearClip, farClip;
                                    
    /** The zoom boundaries. */
    private double                  zoomMax, zoomMin;
                                    
    /** The location boundaries. */
    private double                  locXMin, locXMax, locZMin, locZMax;
                                    
    /**
     * Constructor. This instantiates a new ThirdPersonCamera object.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public ThirdPersonCamera(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating camera settings model
        this.cameraSettingsModel = (CameraSettings) Models.create(CameraSettings.class);
        
        // Setting pivot
        this.pivot = new Point3D(this.cameraSettingsModel.getLocation().getX(),
                this.cameraSettingsModel.getLocation().getY(),
                this.cameraSettingsModel.getLocation().getZ());
                
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
        this.zoomMax = this.getGame().getMouseInputHandler().getScrollWheelMaxValue();
        this.zoomMin = this.getGame().getMouseInputHandler().getScrollWheelMinValue();
        this.locXMax = 0.0;
        this.locXMin = 0.0;
        this.locZMax = 0.0;
        this.locZMin = 0.0;
        
        // Setup camera
        this.perspectiveCamera.setNearClip(this.nearClip);
        this.perspectiveCamera.setFarClip(this.farClip);
        this.perspectiveCamera.setDepthTest(DepthTest.ENABLE);
        
        // Creating Xforms
        this.xForm1.getChildren().add(this.xForm2);
        this.xForm2.getChildren().add(this.xForm3);
        this.xForm3.getChildren().add(this.perspectiveCamera);
        
        // Load angles from camera settings model
        this.xForm1.rotationX.setAngle(this.cameraSettingsModel.getPitch());
        this.xForm1.rotationY.setAngle(this.cameraSettingsModel.getYaw());
        
        // Load zoom from camera settings model
        this.setZoom(this.cameraSettingsModel.getZoom());
        
        // Flip y-axis so positive y is upwards
        this.xForm3.setRotateZ(180.0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Add our camera Xform to the camera group
        Platform.runLater(() -> this.getGame().getCameraGroup().getChildren().add(this.xForm1));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {

        // Check if middle button is down
        if (this.getGame().getMouseInputHandler().isMidButtonDown()) {

            // Rotate around y-axis
            this.xForm1.rotationY
                    .setAngle(this.xForm1.rotationY.getAngle() + (this.getGame().getMouseInputHandler().getMouseDeltaX()
                            * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
            // Rotate around x-axis
            this.xForm1.rotationX
                    .setAngle(this.xForm1.rotationX.getAngle() + (this.getGame().getMouseInputHandler().getMouseDeltaY()
                            * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
        }

        // Limit x-axis rotation
        if (this.xForm1.rotationX.getAngle() > 90.0) {

            // Set rotation to 90.0 degrees
            this.xForm1.rotationX.setAngle(90.0);
        } else if (this.xForm1.rotationX.getAngle() < 0.0) {

            // Set rotation to 0 degrees
            this.xForm1.rotationX.setAngle(0.0);
        }

        // Calculate zoom value
        double zoomCalc = (this.getGame().getMouseInputHandler().getScrollWheelYValue()
                * ThirdPersonCamera.MOUSE_ZOOM_SENSITIVITY) + ThirdPersonCamera.INITIAL_CAMERA_DISTANCE;

        // Limit zoom
        if (zoomCalc < this.zoomMin) {
            zoomCalc = this.zoomMin;
        } else if (zoomCalc > this.zoomMax) {
            zoomCalc = this.zoomMax;
        }

        // Perform "zoom" (translate camera closer or away from pivot point)
        this.xForm2.setTranslate(0.0, 0.0, zoomCalc);

        // Translate to pivot location
        this.xForm1.setTranslate(this.pivot.getX(), this.pivot.getY(), this.pivot.getZ());

        final double movementScale = 1.0
                - ((this.getGame().getMouseInputHandler().getScrollWheelYValue() + 990.0) / 2000.0);

        // Check if left mouse button is down
        if (this.getGame().getMouseInputHandler().isLeftButtonDown()) {

            // Calculate mouse values
            final double mouseXCalc = this.getGame().getMouseInputHandler().getMouseDeltaX()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;
            final double mouseYCalc = this.getGame().getMouseInputHandler().getMouseDeltaY()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;

            // Create point of mouse x and y coordinates
            Point3D translation = new Point3D(mouseXCalc, 0.0, mouseYCalc);
            // Rotate point
            translation = this.xForm1.rotationY.transform(translation);
            // Scale point
            translation = new Point3D(movementScale * translation.getX(),
                    translation.getY(),
                    movementScale * translation.getZ());

            // Update pivot
            this.pivot = this.pivot.add(translation);
        }

        // Limit movement x-axis
        if (this.pivot.getX() < this.locXMin) {
            this.pivot = new Point3D(this.locXMin, this.pivot.getY(), this.pivot.getZ());
        } else if (this.pivot.getX() > this.locXMax) {
            this.pivot = new Point3D(this.locXMax, this.pivot.getY(), this.pivot.getZ());
        }

        // Limit movement z-axis
        if (this.pivot.getZ() < this.locZMin) {
            this.pivot = new Point3D(this.pivot.getX(), this.pivot.getY(), this.locZMin);
        } else if (this.pivot.getZ() > this.locZMax) {
            this.pivot = new Point3D(this.pivot.getX(), this.pivot.getY(), this.locZMax);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if we need to remove the camera
        if (this.getGame().getCameraGroup().getChildren().contains(this.xForm1)) {
            
            // Remove our camera Xform from the camera group
            Platform.runLater(() -> this.getGame().getCameraGroup().getChildren().remove(this.xForm1));
        }
        
        // Update camera settings model
        this.cameraSettingsModel.update(
                "position", this.pivot,
                "zoom", this.getZoom(),
                "yaw", this.getRotateY(),
                "pitch", this.getRotateX());
                
        // Save camera settings model
        this.cameraSettingsModel.save();
    }
    
    /**
     * Gets the camera location.
     *
     * @return The camera {@link Point3D location}.
     */

    public Point3D getCameraLoc() {
        return this.getPivot().add(this.getPivotOffsetLoc());
    }
    
    /**
     * Gets the camera transformation.
     *
     * @return The camera {@link Xform transformation}.
     */

    public Xform getCameraXform() {
        return this.xForm1;
    }
    
    /**
     * Gets the maximal location at the x-axis.
     *
     * @return The maximal location at the x-axis.
     */

    public double getMaxLocX() {
        return this.locXMax;
    }
    
    /**
     * Gets the maximal location at the z-axis.
     *
     * @return The maximal location at the z-axis.
     */

    public double getMaxLocZ() {
        return this.locZMax;
    }
    
    /**
     * Gets the maximal zoom value.
     *
     * @return The minimal zoom value.
     */

    public double getMaxZoom() {
        return this.zoomMax;
    }
    
    /**
     * Gets the minimal location on the x-axis.
     *
     * @return The minimal location on the x-axis.
     */

    public double getMinLocX() {
        return this.locXMin;
    }
    
    /**
     * Gets the minimal location at the z-axis.
     *
     * @return The minimal location at the z-axis.
     */

    public double getMinLocZ() {
        return this.locZMin;
    }
    
    /**
     * Gets the minimal zoom value.
     *
     * @return The minimal zoom value.
     */

    public double getMinZoom() {
        return this.zoomMin;
    }
    
    /**
     * Gets the perspective camera.
     *
     * @return The {@link PerspectiveCamera camera}.
     */
    public PerspectiveCamera getPerspectiveCamera() {
        return this.perspectiveCamera;
    }
    
    /**
     * Gets the pivot.
     *
     * @return The {@link Point3D pivot}.
     */
    public Point3D getPivot() {
        return this.pivot;
    }
    
    /**
     * Gets the zoom value.
     *
     * @return The zoom value.
     */
    public double getZoom() {
        return this.getGame().getMouseInputHandler().getScrollWheelYValue();
    }
    
    /**
     * Gets the pivot offset location.
     *
     * @return The pivot offset {@link Point3D location}.
     */
    public Point3D getPivotOffsetLoc() {
        
        // Getting translation of camera (from the pivot)
        Point3D tmpPoint = new Point3D(this.xForm2.getTranslateX(),
                this.xForm2.getTranslateY(),
                this.xForm2.getTranslateZ());
        // Rotate the translation according to the rotation of the camera
        tmpPoint = this.xForm1.rotationX.transform(tmpPoint);
        tmpPoint = this.xForm1.rotationY.transform(tmpPoint);
        tmpPoint = this.xForm1.rotationZ.transform(tmpPoint);
        
        // Create new vector out of the rotated values
        return tmpPoint;
    }
    
    /**
     * Gets the rotation around the x-axis.
     *
     * @return The rotation around the x-axis.
     */
    public double getRotateX() {
        return this.xForm1.rotationX.getAngle();
    }
    
    /**
     * Gets the rotation around the y-axis.
     *
     * @return The rotation around the y-axis.
     */
    public double getRotateY() {
        return this.xForm1.rotationY.getAngle();
    }
    
    /**
     * Gets the rotation around the z-axis.
     *
     * @return The rotation around the z-axis.
     */

    public double getRotateZ() {
        return this.xForm1.rotationZ.getAngle();
    }
    
    /**
     * Sets the maximal location on the x-axis.
     *
     * @param locXMax
     *            The new maximal location on the x-axis.
     */
    public void setMaxLocX(final double locXMax) {
        
        // Setting maximal location on the x-axis
        this.locXMax = locXMax;
        
        // Check if we need to update the location on the x-axis
        if (this.pivot.getX() > locXMax) {
            
            // Setting location on the x-axis
            this.pivot = new Point3D(locXMax, this.pivot.getY(), this.pivot.getZ());
        }
    }
    
    /**
     * Sets the maximal location on the z-axis.
     *
     * @param locZMax
     *            The new maximal location on the z-axis.
     */
    public void setMaxLocZ(final double locZMax) {
        
        // Setting maximal location on the z-axis
        this.locZMax = locZMax;
        
        // Check if we need to update the location on the z-axis.
        if (this.pivot.getZ() > locZMax) {
            
            // Setting location on z-axis.
            this.pivot = new Point3D(this.pivot.getX(), this.pivot.getY(), locZMax);
        }
    }
    
    /**
     * Sets the maximal zoom value.
     *
     * @param zoomMax
     *            The new maximal zoom value.
     */

    public void setMaxZoom(final double zoomMax) {
        
        // Setting maximal zoom
        this.zoomMax = zoomMax;
        
        // Check if we need to update zoom
        if (this.getZoom() > zoomMax) {
            
            // Setting max zoom
            this.setZoom(zoomMax);
        }
    }
    
    /**
     * Sets the minimal location on the x-axis.
     *
     * @param locXMin
     *            The new minimal location on the x-axis.
     */
    public void setMinLocX(final double locXMin) {
        
        // Setting minimal location on the x-axis
        this.locXMin = locXMin;
        
        // Check if we need to update the location on the x-axis
        if (this.pivot.getX() < locXMin) {
            
            // Setting minimal location on the x-axis
            this.pivot = new Point3D(locXMin, this.pivot.getY(), this.pivot.getZ());
        }
    }
    
    /**
     * Sets the minimal location on the z-axis.
     *
     * @param locZMin
     *            The new minimal location on the z-axis.
     */
    public void setMinLocZ(final double locZMin) {
        
        // Setting minimal location on the z-axis
        this.locZMin = locZMin;
        
        // Check if we need to update the location on the z-axis
        if (this.pivot.getZ() < locZMin) {
            
            // Setting minimal location on the z-axis
            this.pivot = new Point3D(this.pivot.getX(), this.pivot.getY(), locZMin);
        }
    }
    
    /**
     * Sets the minimal zoom value.
     *
     * @param zoomMin
     *            The new minimal zoom value.
     */

    public void setMinZoom(final double zoomMin) {
        
        // Setting minimal zoom value
        this.zoomMin = zoomMin;
        
        // Check if we need to update the zoom value
        if (this.getZoom() < zoomMin) {
            
            // Setting minimal zoom
            this.setZoom(zoomMin);
        }
    }
    
    /**
     * Sets the pivot.
     *
     * @param pivot
     *            The new {@link Point3D pivot}.
     */
    public void setPivot(final Point3D pivot) {
        this.pivot = pivot;
    }
    
    /**
     * Sets the rotation around the x-axis.
     *
     * @param angleX
     *            The new rotation around the x-axis.
     */
    public void setRotateX(final double angleX) {
        this.xForm1.rotationX.setAngle(angleX);
    }
    
    /**
     * Sets the rotation around the y-axis.
     *
     * @param angleY
     *            The new rotation around the y-axis.
     */
    public void setRotateY(final double angleY) {
        this.xForm1.rotationY.setAngle(angleY);
    }
    
    /**
     * Sets the rotation around the z-axis.
     *
     * @param angleZ
     *            The new rotation around the z-axis.
     */

    public void setRotateZ(final double angleZ) {
        this.xForm1.rotationZ.setAngle(angleZ);
    }
    
    /**
     * Sets the zoom value of the camera.
     *
     * @param zoom
     *            The new zoom value (-1000 - 1000).
     */
    public void setZoom(final double zoom) {
        
        // Setting zoom (change the mouse wheel value)
        this.getGame().getMouseInputHandler().setMouseWheelYValue((long) zoom);
    }
}
