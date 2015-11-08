/**************************************************************************************************
 *
 * ThirdPersonCamera This class is for our 3rd person camera.
 *
 **************************************************************************************************
 *
 * TODO: change size according to zoom of camera TODO: javadoc TODO: comment TODO: cleanup
 *
 **************************************************************************************************
 * @see GameObject
 *      
 * @author marklef2
 * @date 15-10-2015
 */
package org.ssh.field3d.core.gameobjects.camera;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.math.Xform;

import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;

public class ThirdPersonCamera extends GameObject {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Public statics
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
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
                                                               
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Vector3f                _pivot;
    private final PerspectiveCamera _perspectiveCamera;
    private final Xform             _xForm1, _xForm2, _xForm3;
                                    
    private final double            _nearClip, _farClip;
    private double                  _zoomMax, _zoomMin;
    private double                  _locXMin, _locXMax, _locZMin, _locZMax;
                                    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public ThirdPersonCamera(final Game game) {
        
        // Initialize super class
        super(game);
        
        this._pivot = new Vector3f(0.0f, 200.0f, 0.0f);
        
        // Create new perspective camera
        this._perspectiveCamera = new PerspectiveCamera(true);
        
        // Create 3 transforms
        this._xForm1 = new Xform();
        this._xForm2 = new Xform();
        this._xForm3 = new Xform();
        
        // Setting near clip
        this._nearClip = ThirdPersonCamera.DEFAULT_NEAR_PANE;
        // Setting far clip
        this._farClip = ThirdPersonCamera.DEFAULT_FAR_PANE;
        
        // Setting default minimal & maximal values
        this._zoomMax = ThirdPersonCamera.CAMERA_ZOOM_MAX;
        this._zoomMin = ThirdPersonCamera.CAMERA_ZOOM_MIN;
        this._locXMax = ThirdPersonCamera.CAMERA_LOC_X_MAX;
        this._locXMin = ThirdPersonCamera.CAMERA_LOC_X_MIN;
        this._locZMax = ThirdPersonCamera.CAMERA_LOC_Z_MAX;
        this._locZMin = ThirdPersonCamera.CAMERA_LOC_Z_MIN;
        
        // Setup camera
        this._perspectiveCamera.setNearClip(this._nearClip);
        this._perspectiveCamera.setFarClip(this._farClip);
        this._perspectiveCamera.setDepthTest(DepthTest.ENABLE);
        
        // Creating Xforms
        this._xForm1.getChildren().add(this._xForm2);
        this._xForm2.getChildren().add(this._xForm3);
        this._xForm3.getChildren().add(this._perspectiveCamera);
        
        // Setting initial rotations
        this._xForm1.rx.setAngle(ThirdPersonCamera.INITIAL_CAMERA_ROT_X);
        this._xForm1.ry.setAngle(ThirdPersonCamera.INITIAL_CAMERA_ROT_Y);
        
        // Flip y-axis so positive y is upwards
        this._xForm3.setRotateZ(180.0);
    }
    
    @Override
    public void Destroy() {
    }
    
    public Vector3f GetCameraLoc() {
        
        return this.GetPivot().add(this.GetPivotOffsetLoc());
    }
    
    public Xform GetCameraXform() {
        return this._xForm1;
    }
    
    public double GetMaxLocX() {
        return this._locXMax;
    }
    
    public double GetMaxLocZ() {
        return this._locZMax;
    }
    
    public double GetMaxZoom() {
        return this._zoomMax;
    }
    
    public double GetMinLocX() {
        return this._locXMin;
    }
    
    public double GetMinLocZ() {
        return this._locZMin;
    }
    
    public double GetMinZoom() {
        return this._zoomMin;
    }
    
    public PerspectiveCamera GetPerspectiveCamera() {
        return this._perspectiveCamera;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public Vector3f GetPivot() {
        return this._pivot;
    }
    
    public Vector3f GetPivotOffsetLoc() {
        
        Point3D tmpPoint = new Point3D(this._xForm2.getTranslateX(),
                this._xForm2.getTranslateY(),
                this._xForm2.getTranslateZ());
        tmpPoint = this._xForm1.rx.transform(tmpPoint);
        tmpPoint = this._xForm1.ry.transform(tmpPoint);
        tmpPoint = this._xForm1.rz.transform(tmpPoint);
        
        return new Vector3f((float) tmpPoint.getX(), (float) tmpPoint.getY(), (float) tmpPoint.getZ());
    }
    
    public double GetRotateX() {
        return this._xForm1.rx.getAngle();
    }
    
    public double GetRotateY() {
        return this._xForm1.ry.getAngle();
    }
    
    public double GetRotateZ() {
        return this._xForm1.rz.getAngle();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Overridden methods from GameObject
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void Initialize() {
        
        // Add our camera Xform to the camera group
        this.GetGame().getCameraGroup().getChildren().add(this._xForm1);
    }
    
    public void SetMaxLocX(final double locXMax) {
        this._locXMax = locXMax;
    }
    
    public void SetMaxLocZ(final double locZMax) {
        this._locZMax = locZMax;
    }
    
    public void SetMaxZoom(final double zoomMax) {
        this._zoomMax = zoomMax;
    }
    
    public void SetMinLocX(final double locXMin) {
        this._locXMin = locXMin;
    }
    
    public void SetMinLocZ(final double locZMin) {
        this._locZMin = locZMin;
    }
    
    public void SetMinZoom(final double zoomMin) {
        this._zoomMin = zoomMin;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void SetPivot(final Vector3f pivot) {
        this._pivot = pivot;
    }
    
    public void SetRotateX(final double angleX) {
        this._xForm1.rx.setAngle(angleX);
    }
    
    public void SetRotateY(final double angleY) {
        this._xForm1.ry.setAngle(angleY);
    }
    
    public void SetRotateZ(final double angleZ) {
        this._xForm1.rz.setAngle(angleZ);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        ///////////////////////////////////////////////
        // Rotation around pivot
        ///////////////////////////////////////////////
        if (this.GetGame().getMouseInputHandler().IsMidButtonDown()) {
            
            // Rotate around y-axis
            this._xForm1.ry
                    .setAngle(this._xForm1.ry.getAngle() + (this.GetGame().getMouseInputHandler().GetMouseDeltaX()
                            * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
            // Rotate around x-axis
            this._xForm1.rx
                    .setAngle(this._xForm1.rx.getAngle() + (this.GetGame().getMouseInputHandler().GetMouseDeltaY()
                            * ThirdPersonCamera.MOUSE_LOOK_SENSITIVITY));
        }
        
        // Limit x-axis rotation
        if (this._xForm1.rx.getAngle() > 90.0) {
            this._xForm1.rx.setAngle(90.0);
        }
        else if (this._xForm1.rx.getAngle() < 0.0) {
            this._xForm1.rx.setAngle(0.0);
        }
        
        ///////////////////////////////////////////////
        // Zoom
        ///////////////////////////////////////////////
        double zoomCalc = (this.GetGame().getMouseInputHandler().GetScrollWheelYValue()
                * ThirdPersonCamera.MOUSE_ZOOM_SENSITIVITY) + ThirdPersonCamera.INITIAL_CAMERA_DISTANCE;
                
        // Limit zoom
        if (zoomCalc < this._zoomMin) {
            zoomCalc = this._zoomMin;
        }
        else if (zoomCalc > this._zoomMax) {
            zoomCalc = this._zoomMax;
        }
        
        // Perform "zoom" (translate camera closer or away from pivot point)
        this._xForm2.setTranslate(0.0, 0.0, zoomCalc);
        
        // Translate to pivot location
        this._xForm1.setTranslate(this._pivot.x, this._pivot.y, this._pivot.z);
        
        final double movementScale = 1
                - ((this.GetGame().getMouseInputHandler().GetScrollWheelYValue() + 990.0) / 2000.0);
                
        ///////////////////////////////////////////////
        // Move pivot
        ///////////////////////////////////////////////
        if (this.GetGame().getMouseInputHandler().IsLeftButtonDown()) {
            
            // Calculate mouse values
            final double mouseXCalc = this.GetGame().getMouseInputHandler().GetMouseDeltaX()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;
            final double mouseYCalc = this.GetGame().getMouseInputHandler().GetMouseDeltaY()
                    * ThirdPersonCamera.MOUSE_MOVEMENT_SENSITIVITY;
                    
            // Rotate mouse translation according to camera
            Point3D mouseMovement = new Point3D(mouseXCalc, 0.0, mouseYCalc);
            mouseMovement = this._xForm1.ry.transform(mouseMovement);
            
            // Update pivot
            this._pivot.x += mouseMovement.getX() * movementScale;
            // Limit zoom
            this._pivot.z += mouseMovement.getZ() * movementScale;
        }
        
        // Limit movement
        if (this._pivot.x < this._locXMin) {
            this._pivot.x = (float) this._locXMin;
        }
        else if (this._pivot.x > this._locXMax) {
            this._pivot.x = (float) this._locXMax;
        }
        
        if (this._pivot.z < this._locZMin) {
            this._pivot.z = (float) this._locZMin;
        }
        else if (this._pivot.z > this._locZMax) {
            this._pivot.z = (float) this._locZMax;
        }
    }
}
