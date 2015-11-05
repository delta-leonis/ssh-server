package org.ssh.field3d;


import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.gameobjects.CarGO;
import org.ssh.field3d.gameobjects.FieldGO;
import org.ssh.field3d.gameobjects.RobotGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;

import javafx.scene.AmbientLight;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;



/**
 * 
 * FieldGame class
 * 	This class is the main part of the 3d field, from here everything in the 3d world is created & managed.
 * 
 * @author Mark Lefering - 330430
 *
 */
public class FieldGame extends Game {
	
	
	// TODO: move to config file, or get from org.ssh.models
	public static final double FIELD_TILE_WIDTH = 500.0;
	public static final double FIELD_TILE_DEPTH = 500.0;
	public static final double FIELD_WIDTH = 9000.0;
	public static final double FIELD_DEPTH = 6000.0;
	public static final double FIELD_REAL_WIDTH = FIELD_WIDTH + (2.0 * FIELD_TILE_WIDTH);
	public static final double FIELD_REAL_DEPTH = FIELD_DEPTH + (2.0 * FIELD_TILE_DEPTH);
	
	public static final double FIELD_LINE_HEIGHT = 5.0;
	public static final double FIELD_LINE_WIDTH = 10.0f;
	public static final double FIELD_HEIGHT = 1.0;
	public static final double FIELD_GOAL_WIDTH = 1000.0;
	public static final double FIELD_GOAL_DEPTH = 200.0;
	public static final double FIELD_GOAL_HEIGHT = 190.0;
	public static final double FIELD_GOAL_LINE_WIDTH = 5.0;	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private AmbientLight _ambientLight;
	private PointLight _pointLight1, _pointLight2, _pointLight3, _pointLight4;
	
	private FieldGO _field;
	private CameraControlOverlayGO _cameraControlOverlay;
	private CarGO _easterCar;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Constructor
	 * 
	 * @param root The root of the SubScene.
	 * @param width The width of the SubScene.
	 * @param height The height of the SubScene.
	 * @param antiAliasing Anti-aliasing mode, SceneAntialiasing.DISABLED.
	 */
	public FieldGame(Parent root, double width, double height, SceneAntialiasing antiAliasing) {
		
		// Initialize super class
		super(root, width, height, true, antiAliasing);
		
		// Creating ambient light
		_ambientLight = new AmbientLight(Color.DARKGRAY);
		
		// Creating point lights
		_pointLight1 = new PointLight(Color.WHITE);
		_pointLight2 = new PointLight(Color.WHITE);
		_pointLight3 = new PointLight(Color.WHITE);
		_pointLight4 = new PointLight(Color.WHITE);
		
		// Creating field GameObject	
		_field = new FieldGO(this, FIELD_REAL_WIDTH, FIELD_REAL_DEPTH);
		// Creating camera control overlay GameObject
		_cameraControlOverlay = new CameraControlOverlayGO(this);	
		// Creating easter egg car GameObject
		_easterCar = new CarGO(this);

		// Setup lights
		_pointLight1.setTranslateX(-(FIELD_WIDTH / 4.0));
		_pointLight1.setTranslateY(2000);
		_pointLight1.setTranslateZ(-(FIELD_DEPTH / 4.0));
		
		_pointLight2.setTranslateX(-(FIELD_WIDTH / 4.0));
		_pointLight2.setTranslateY(2000);
		_pointLight2.setTranslateZ(FIELD_DEPTH / 4.0);
		
		_pointLight3.setTranslateX(FIELD_WIDTH / 4.0);
		_pointLight3.setTranslateY(2000);
		_pointLight3.setTranslateZ(-(FIELD_DEPTH / 4.0));
		
		_pointLight4.setTranslateX(FIELD_WIDTH / 4.0);
		_pointLight4.setTranslateY(2000);
		_pointLight4.setTranslateZ(FIELD_DEPTH / 4.0);		
		
		// Set minimal mouse wheel value
		GetMouseInputHandler().SetMinMouseWheelValue(-1000);
		// Set maximal mouse wheel value
		GetMouseInputHandler().SetMaxMouseWheelValue(1000);
		
		// Set black fill color
		setFill(Color.BLACK);
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Abstract methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize() {
		
		// Add lights to the world
		GetWorldGroup().getChildren().add(_ambientLight);
		GetWorldGroup().getChildren().add(_pointLight1);
		GetWorldGroup().getChildren().add(_pointLight2);
		GetWorldGroup().getChildren().add(_pointLight3);
		GetWorldGroup().getChildren().add(_pointLight4);
		
		// Adding game objects
		AddGameObject(_field);
		AddGameObject(_cameraControlOverlay);		
		AddGameObject(_easterCar);		
		
		// Create some robots
		createRobots();
	}
	@Override
	public void Update(long timeDivNano) { }
	@Override
	public void Destroy() { }
	
	
	/**
	 *  
	 * AddRobot method
	 * 	This method adds a RobotGO to the game.
	 * 
	 * @param robot The RobotGO to add to the game.
	 */
	public void AddRobot(RobotGO robot) {
		
		// Add robot to game
		AddGameObject(robot);
	}
	
	
	/**
	 * 
	 * RemoveRobot method
	 * 	This method removes a RobotGO from the game.
	 * 
	 * @param robot The RobotGO to remove from the game.
	 */
	public void RemoveRobot(RobotGO robot) {
		
		// Remove robot from game
		RemoveGameObject(robot);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * createRobots method
	 * 	This method creates some robots.
	 */
	private void createRobots() { 
		
		// Create 22 robots
		for (int i = 0; i < 22; i++) {
			
			// Create new robot
			RobotGO robot = new RobotGO(this);
			// Set location of the robot
			robot.SetLocation(new Vector3f(i * 500, ((float)RobotGO.ROBOT_HEIGHT / 2.0f) + 10.0f,0));
			
			// Add robot to game objects
			AddGameObject(robot);
		}
	}
}
