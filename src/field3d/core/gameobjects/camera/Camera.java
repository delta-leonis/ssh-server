package field3d.core.gameobjects.camera;

import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;
import field3d.core.math.Vector3f;
import field3d.core.math.Xform;

import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyCode;


public class Camera extends GameObject {
	
	// TODO: move to config file
	public static final double CAMERA_MOVE_SPEED = 2000.0f;
	public static final double CAMERA_ROTATION_SPEED = 90.0f;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	private PerspectiveCamera _perspectiveCamera;	
	private Xform _xForm1, _xForm2, _xForm3;
	private Vector3f _location, _velocity;	
	
	private float _nearPane, _farPane;

	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructor
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 
	 * Constructor
	 * 
	 */
	public Camera(Game game) {
		
		super(game);
		
		// Creating new objects
		_perspectiveCamera = new PerspectiveCamera(true);
		_location = new Vector3f(0.0f, 0.0f, 0.0f);
		_velocity = new Vector3f(0.0f, 0.0f, 0.0f);		
		_xForm1 = new Xform();
		_xForm2 = new Xform();
		_xForm3 = new Xform();
		
		// Setting variables		
		_nearPane = 1.0f;
		_farPane = 10000000.0f;		
		_location.y = 2.0f;
		
		// Adding xforms
		_xForm1.getChildren().add(_xForm2);
		_xForm2.getChildren().add(_xForm3);
		_xForm3.getChildren().add(_perspectiveCamera);
		
		// Flip Y-axis
		_xForm3.setRotateZ(180.0);
		
		// Setting near clip
		_perspectiveCamera.setNearClip(_nearPane);
		// Setting far clip)
		_perspectiveCamera.setFarClip(_farPane);
		// Setting depth test enabled
		_perspectiveCamera.setDepthTest(DepthTest.ENABLE);
	}
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Overridden methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void Initialize() {
		
		// Add to camera group
		GetGame().GetCameraGroup().getChildren().add(_xForm1);
	}
	
	@Override
	public void Update(long timeDivNano) { 
		
		////////////////////////////////////////////////
		//	Forward & Backward
		////////////////////////////////////////////////
		if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.W)) {
			
			_velocity.z = 1.0f;
			
		} else if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.S)) {
			
			_velocity.z = -1.0f;
			
		} else {
			
			_velocity.z = 0.0f;
		}
		
		
		////////////////////////////////////////////////
		//	Left & right
		////////////////////////////////////////////////
		if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.A)) {
			
			_velocity.x = 1.0f;
			
		} else if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.D)) {
			
			_velocity.x = -1.0f;
			
		} else {
			
			_velocity.x = 0.0f;
		}
		
		
		////////////////////////////////////////////////
		//	Up & Down
		////////////////////////////////////////////////
		if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.SPACE)) {
			
			_velocity.y = 1.0f;
			
		} else if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.CONTROL)) {
			
			_velocity.y = -1.0f;
			
		} else {
			
			_velocity.y = 0.0f;
		}
		
		
		////////////////////////////////////////////////
		//	Rotation X-axis
		////////////////////////////////////////////////		
		if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.UP)) {
			
			_xForm1.rx.setAngle(_xForm1.rx.getAngle() + ( CAMERA_ROTATION_SPEED * (timeDivNano / 1000000000.0f) ));
			
		} else if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.DOWN)) {
			
			_xForm1.rx.setAngle(_xForm1.rx.getAngle() - ( CAMERA_ROTATION_SPEED * (timeDivNano / 1000000000.0f) ));
		}
		
		
		////////////////////////////////////////////////
		//	Rotation Y-axis
		////////////////////////////////////////////////	
		if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.LEFT)) {
		
			_xForm1.ry.setAngle(_xForm1.ry.getAngle() + ( CAMERA_ROTATION_SPEED * (timeDivNano / 1000000000.0f) ));
			
		} else if (GetGame().GetKeyInputHandler().isKeyDown(KeyCode.RIGHT)) {
			
			_xForm1.ry.setAngle(_xForm1.ry.getAngle() - ( CAMERA_ROTATION_SPEED * (timeDivNano / 1000000000.0f) ));	
		}
		
		
		////////////////////////////////////////////////
		//	Limit X-axis
		////////////////////////////////////////////////	
		if (_xForm1.rx.getAngle() > 45.0) {
			
			_xForm1.rx.setAngle(45.0);
			
		} else if (_xForm1.rx.getAngle() < -45.0) {
			
			_xForm1.rx.setAngle(-45.0);
		}
		
		
		Point3D tmpVel = new Point3D(_velocity.x, _velocity.y, _velocity.z);
		
		tmpVel = _xForm1.rx.transform(tmpVel);
		tmpVel = _xForm1.ry.transform(tmpVel);
		tmpVel = _xForm1.rz.transform(tmpVel);
		
		
		Vector3f newVel = new Vector3f((float)tmpVel.getX(), (float)tmpVel.getY(), (float)tmpVel.getZ());
		
		// Add velocity to the location
		_location = _location.add(newVel.normalize().scale((float)CAMERA_MOVE_SPEED * ((float)timeDivNano / 1000000000.0f)));
	
		// Translate camera
		_xForm1.setTranslateX(_location.x);
		_xForm1.setTranslateY(_location.y);
		_xForm1.setTranslateZ(_location.z);
	}
	
	@Override
	public void Destroy() { }	
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public Xform GetCameraXfrom() { return _xForm1; }
	public PerspectiveCamera getPerspectiveCamera() { return _perspectiveCamera; }
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void setPerspectiveCamera(PerspectiveCamera perspectiveCamera) { _perspectiveCamera = perspectiveCamera; }
}
