package field3d.core.game;

import java.util.ArrayList;

import field3d.core.gameobjects.GameObject;
import field3d.core.gameobjects.camera.Camera;
import field3d.core.gameobjects.camera.ThirdPersonCamera;
import field3d.core.gameobjects.input.KeyInputHandler;
import field3d.core.gameobjects.input.MouseInputHandler;
import field3d.core.models.ModelManager;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.util.Duration;


public abstract class Game extends SubScene {
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private Timeline _timeline;
	private ModelManager _modelManager;
	private AnimationTimerHandler _animationTimerHandler;
	private ArrayList<GameObject> _gameObjects;
	
	private KeyInputHandler _keyInputHandler;
	private MouseInputHandler _mouseInputHandler;
	private SubScene _3dScene;
	
	private Camera _camera;
	private ThirdPersonCamera _thirdPersonCamera;

	private Group _worldGroup;
	private Group _cameraGroup;
	
	private Group _2dGroup;
	private Group _3dGroup;
	
	private long _curTime, _prevTime;
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public Game(Parent root, double width, double height) {
		
		// Initialize super class
		super(root, width, height);
		
		// Initialize
		initialize(width, height, true, SceneAntialiasing.DISABLED);
	}
	
	public Game(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
		
		// Initialize super class
		super(root, width, height, false, antiAliasing);
		
		// Initialize
		initialize(width, height, depthBuffer, antiAliasing);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Abstract method
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public abstract void Initialize();
	public abstract void Update(long timeDivNano);
	public abstract void Destroy();	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Internal methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void InternalInitialize() {
		
		// Initialize game
		this.Initialize();
		
		// Setting time
		_curTime = _prevTime = System.nanoTime();
		
		// Play time line
        _timeline.play();
     
        // Start animation timer
        _animationTimerHandler.start();
        
        // Adding game object
        AddGameObject(_keyInputHandler);
		AddGameObject(_mouseInputHandler);
		
		AddGameObject(_thirdPersonCamera);
	}
	
	public void InternalUpdate() {

		long timeDivNano = 0;
		
		// Update previous time
		_prevTime = _curTime;
		// Update current time
		_curTime = System.nanoTime();
		
		// Calculate time difference
		timeDivNano = _curTime - _prevTime;
		
		// Update our game
		this.Update(timeDivNano);
		// Update game objects
		this.updateGameObjects(timeDivNano);
	}
	
	public void InternalDestroy() {
		
		// TODO: Destroy key input handler
		// TODO: Destroy mouse input handler
		
		// Destroy game objects
		destroyGameObjects();
		// Destroy our self
		Destroy();
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Add / Remove game object methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void AddGameObject(GameObject gameObject) {
		
		// Check if we need to add our game object
		if (_gameObjects != null && !_gameObjects.contains(gameObject)) {
			
			// Initialize our game object
			gameObject.Initialize();
			// Add to game objects
			_gameObjects.add(gameObject);
		}
	}
	
	public void RemoveGameObject(GameObject gameObject) {
		
		// Check if we need to remove our game object
		if (_gameObjects != null && _gameObjects.contains(gameObject)) {
			
			// Notify the game object we are removing it from the game
			gameObject.Destroy();
			// Remove from game objects
			_gameObjects.remove(gameObject);
		}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	//public Scene GetMainScene() { return _mainScene; }
	
	public Group GetWorldGroup() { return _worldGroup; }
	public Group GetCameraGroup() { return _cameraGroup; }
	
	public Group Get2DGroup() { return _2dGroup; }
	public Group Get3DGroup() { return _3dGroup; }
	
	public KeyInputHandler GetKeyInputHandler() { return _keyInputHandler; }
	public MouseInputHandler GetMouseInputHandler() { return _mouseInputHandler; }
	public Camera GetCamera() { return _camera; }
	public ThirdPersonCamera GetThirdPersonCamera() { return _thirdPersonCamera; }
	public ModelManager GetModelManager() { return _modelManager; }
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void SetKeyInputHandler(KeyInputHandler keyInputHandler) { _keyInputHandler = keyInputHandler; }
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private void initialize(double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
		
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			
			@Override
            public void handle(ActionEvent t) { }
		};
		
		Duration duration = Duration.millis((1.0 / 60.0) * 1000.0);
		KeyFrame keyFrame = new KeyFrame(duration, onFinished);
		
		// Creating groups
		_cameraGroup = new Group();
		_worldGroup = new Group();
		_2dGroup = new Group();
		_3dGroup = new Group();
		
		// Creating new array list for the game objects
		_gameObjects = new ArrayList<GameObject>();
		
		// Creating new model manger
		_modelManager = new ModelManager();
		
		// Creating new keyboard input handler
		_keyInputHandler = new KeyInputHandler(this);
		_mouseInputHandler = new MouseInputHandler(this);
		
		
		_camera = new Camera(this);
		_thirdPersonCamera = new ThirdPersonCamera(this);
		
		_animationTimerHandler = new AnimationTimerHandler();
		_timeline = new Timeline();
		
		// Create 3D Scene
		_3dScene = new SubScene(_3dGroup, width, height, depthBuffer, antiAliasing);
		
		
		// Get 2d group (root of this SubScene);
		_2dGroup = (Group)this.getRoot();
		
		// Add 3d scene to scene
		_2dGroup.getChildren().add(_3dScene);
		
		// Setting default values
		_curTime = _prevTime = 0;
		
		
		// Adding camera group to the root group
		_3dGroup.getChildren().add(_cameraGroup);
		// Adding world group to the root group
		_3dGroup.getChildren().add(_worldGroup);
		
		// Add key frame to our time line
		_timeline.getKeyFrames().add(keyFrame);
		
		
		// Setting the camera of the 3D SubScene
		_3dScene.setCamera(_thirdPersonCamera.GetPerspectiveCamera());
	}
	
	private void updateGameObjects(long timeDivNano) {
		
		// Check if we need to update
		if (_gameObjects != null) {
			
			// Loop through game objects
			for (GameObject gameObject : _gameObjects) {
				
				// Check if we can update the current game object
				if (gameObject != null)
					// Update the game object
					gameObject.Update(timeDivNano);
			}
		} else { 
			
			System.out.println("ERROR: " + getClass().getName() + ":updateGameObjects(): _gameObject == null");
		}
	}
	
	
	private void destroyGameObjects() {
		
		// Check if we need to update
		if (_gameObjects != null) {
			
			// Loop through game objects
			for (GameObject gameObject : _gameObjects) {
				
				// Check if we can destroy the current game object
				if (gameObject != null) {
				
					// Destroy the current game object
					gameObject.Destroy();
					// Remove from array list
					RemoveGameObject(gameObject);
				}
			}
		} else {
			
			System.out.println("ERROR: " + getClass().getName() + ":destroyGameObjects(): _gameObject == null");
		}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Inner classes
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	class AnimationTimerHandler extends AnimationTimer {

		@Override
		public void handle(long timeDivNano) {
			
			// Update game
			InternalUpdate();
		}
		
		
	}
}
