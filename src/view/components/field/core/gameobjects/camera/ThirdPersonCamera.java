/**************************************************************************************************
 * 
 *	ThirdPersonCamera
 * 		This class is for our 3rd person camera.
 * 
 **************************************************************************************************
 * 
 * 	TODO: change size according to zoom of camera
 * 	TODO: javadoc
 * 	TODO: comment
 * 	TODO: cleanup
 * 
 **************************************************************************************************
 * @see GameObject
 * 
 * @author marklef2
 * @date 15-10-2015
 */
package view.components.field.core.gameobjects.camera;


import view.components.field.FieldGame;
import view.components.field.core.game.Game;
import view.components.field.core.gameobjects.GameObject;
import view.components.field.core.math.Vector3f;
import view.components.field.core.math.Xform;

import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;


public class ThirdPersonCamera extends GameObject {

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Public statics
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: Move to config
	public static final double DEFAULT_NEAR_PANE = 0.1;
	public static final double DEFAULT_FAR_PANE = 10000000.0;
	public static final double INITIAL_CAMERA_DISTANCE = -10000.0;
	public static final double INITIAL_CAMERA_ROT_X = 90.0;
	public static final double INITIAL_CAMERA_ROT_Y = 0.0;
	public static final double MOUSE_LOOK_SENSITIVITY = 0.35;
	public static final double MOUSE_MOVEMENT_SENSITIVITY = 10.0;
	public static final double MOUSE_ZOOM_SENSITIVITY = 10.0;
	public static final double CAMERA_ZOOM_MIN = -200000.0;
	public static final double CAMERA_ZOOM_MAX = -200.0;
	public static final double CAMERA_LOC_X_MIN = -(FieldGame.FIELD_WIDTH / 2.0) - 200.0;
	public static final double CAMERA_LOC_X_MAX = (FieldGame.FIELD_WIDTH / 2.0) + 200.0;
	public static final double CAMERA_LOC_Z_MIN = -(FieldGame.FIELD_DEPTH / 2.0) - 200.0;
	public static final double CAMERA_LOC_Z_MAX = (FieldGame.FIELD_DEPTH / 2.0) + 200.0;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private Vector3f _pivot;
	private PerspectiveCamera _perspectiveCamera;	
	private Xform _xForm1, _xForm2, _xForm3;
	
	private double _nearClip, _farClip;
	private double _zoomMax, _zoomMin;
	private double _locXMin, _locXMax, _locZMin, _locZMax;
	

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public ThirdPersonCamera(Game game) {
		
		// Initialize super class
		super(game);
		
		_pivot = new Vector3f(0.0f, 200.0f, 0.0f);
		
		// Create new perspective camera
		_perspectiveCamera = new PerspectiveCamera(true);
		
		// Create 3 transforms
		_xForm1 = new Xform();
		_xForm2 = new Xform();
		_xForm3 = new Xform();
		
		
		// Setting near clip
		_nearClip = DEFAULT_NEAR_PANE;
		// Setting far clip
		_farClip = DEFAULT_FAR_PANE;
		
		
		// Setting default minimal & maximal values
		_zoomMax = CAMERA_ZOOM_MAX;
		_zoomMin = CAMERA_ZOOM_MIN;
		_locXMax = CAMERA_LOC_X_MAX;
		_locXMin = CAMERA_LOC_X_MIN;
		_locZMax = CAMERA_LOC_Z_MAX;
		_locZMin = CAMERA_LOC_Z_MIN;
		
		
		// Setup camera
		_perspectiveCamera.setNearClip(_nearClip);
		_perspectiveCamera.setFarClip(_farClip);
		_perspectiveCamera.setDepthTest(DepthTest.ENABLE);
		
		// Creating Xforms
		_xForm1.getChildren().add(_xForm2);
		_xForm2.getChildren().add(_xForm3);
		_xForm3.getChildren().add(_perspectiveCamera);
		
		// Setting initial rotations
		_xForm1.rx.setAngle(INITIAL_CAMERA_ROT_X);
		_xForm1.ry.setAngle(INITIAL_CAMERA_ROT_Y);
		
		// Flip y-axis so positive y is upwards
		_xForm3.setRotateZ(180.0);
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Overridden methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize() {
	
		// Add our camera Xform to the camera group
		GetGame().GetCameraGroup().getChildren().add(_xForm1);
	}

	@Override
	public void Update(long timeDivNano) {
		
		///////////////////////////////////////////////
		//	Rotation around pivot
		///////////////////////////////////////////////
		if (GetGame().GetMouseInputHandler().IsMidButtonDown()) {
			
			// Rotate around y-axis
			_xForm1.ry.setAngle(_xForm1.ry.getAngle() + (GetGame().GetMouseInputHandler().GetMouseDeltaX() * MOUSE_LOOK_SENSITIVITY));
			// Rotate around x-axis
			_xForm1.rx.setAngle(_xForm1.rx.getAngle() + (GetGame().GetMouseInputHandler().GetMouseDeltaY() * MOUSE_LOOK_SENSITIVITY));
		}
		
		// Limit x-axis rotation
		if (_xForm1.rx.getAngle() > 90.0) { _xForm1.rx.setAngle(90.0); }
		else if (_xForm1.rx.getAngle() < 0.0) { _xForm1.rx.setAngle(0.0); }
		
		
		///////////////////////////////////////////////
		//	Zoom
		///////////////////////////////////////////////
		double zoomCalc = GetGame().GetMouseInputHandler().GetScrollWheelYValue() * MOUSE_ZOOM_SENSITIVITY + INITIAL_CAMERA_DISTANCE;
		
		// Limit zoom
		if (zoomCalc < _zoomMin) { zoomCalc = _zoomMin; }
		else if (zoomCalc > _zoomMax) { zoomCalc = _zoomMax; }
		
		// Perform "zoom" (translate camera closer or away from pivot point)
		_xForm2.setTranslate(0.0, 0.0, zoomCalc);
		
		// Translate to pivot location
		_xForm1.setTranslate(_pivot.x, _pivot.y, _pivot.z);
		
		double movementScale = 1 - (GetGame().GetMouseInputHandler().GetScrollWheelYValue() + 990.0) / 2000.0;
		
		
		///////////////////////////////////////////////
		//	Move pivot 
		///////////////////////////////////////////////
		if (GetGame().GetMouseInputHandler().IsLeftButtonDown()) {
			
			// Calculate mouse values
			double mouseXCalc = GetGame().GetMouseInputHandler().GetMouseDeltaX() * MOUSE_MOVEMENT_SENSITIVITY;
			double mouseYCalc = GetGame().GetMouseInputHandler().GetMouseDeltaY() * MOUSE_MOVEMENT_SENSITIVITY;
			
			// Rotate mouse translation according to camera
			Point3D mouseMovement = new Point3D(mouseXCalc, 0.0, mouseYCalc);
			mouseMovement = _xForm1.ry.transform(mouseMovement);
			
			// Update pivot
			_pivot.x += mouseMovement.getX() * movementScale;
			// Limit zoom
			_pivot.z += mouseMovement.getZ() * movementScale;
		}
		
		// Limit movement
		if (_pivot.x < _locXMin) { _pivot.x = (float) _locXMin; }
		else if (_pivot.x > _locXMax) { _pivot.x = (float) _locXMax; }
		
		if (_pivot.z < _locZMin) { _pivot.z = (float) _locZMin; }
		else if (_pivot.z > _locZMax) { _pivot.z = (float) _locZMax; }
		
		
		
	}

	@Override
	public void Destroy() { }

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	public Vector3f GetPivot() { return _pivot; }
	public Vector3f GetPivotOffsetLoc() { 
		
		Point3D tmpPoint = new Point3D(_xForm2.getTranslateX(), _xForm2.getTranslateY(), _xForm2.getTranslateZ());
		tmpPoint = _xForm1.rx.transform(tmpPoint);
		tmpPoint = _xForm1.ry.transform(tmpPoint);
		tmpPoint = _xForm1.rz.transform(tmpPoint);
		
		return new Vector3f((float)tmpPoint.getX(), (float)tmpPoint.getY(), (float)tmpPoint.getZ());
	}
	public Vector3f GetCameraLoc() {
		
		return GetPivot().add(GetPivotOffsetLoc());
	}
	
	
	public Xform GetCameraXform() { return _xForm1; }
	public PerspectiveCamera GetPerspectiveCamera() { return _perspectiveCamera; }
	
	
	public double GetRotateX() { return _xForm1.rx.getAngle(); }
	public double GetRotateY() { return _xForm1.ry.getAngle(); }
	public double GetRotateZ() { return _xForm1.rz.getAngle(); }
	
	public double GetMinZoom() { return _zoomMin; }
	public double GetMaxZoom() { return _zoomMax; }
	public double GetMaxLocX() { return _locXMax; }
	public double GetMinLocX() { return _locXMin; }
	public double GetMaxLocZ() { return _locZMax; }
	public double GetMinLocZ() { return _locZMin; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void SetPivot(Vector3f pivot) { _pivot = pivot; }	
	
	public void SetRotateX(double angleX) { _xForm1.rx.setAngle(angleX); }
	public void SetRotateY(double angleY) { _xForm1.ry.setAngle(angleY); }
	public void SetRotateZ(double angleZ) { _xForm1.rz.setAngle(angleZ); }
	
	public void SetMinZoom(double zoomMin) { _zoomMin = zoomMin; }
	public void SetMaxZoom(double zoomMax) { _zoomMax = zoomMax; }
	public void SetMaxLocX(double locXMax) { _locXMax = locXMax; }
	public void SetMinLocX(double locXMin) { _locXMin = locXMin; }
	public void SetMaxLocZ(double locZMax) { _locZMax = locZMax; }
	public void SetMinLocZ(double locZMin) { _locZMin = locZMin; }
}
