package field3d;

import field3d.core.game.Game;
import field3d.core.math.Vector3f;
import field3d.gameobjects.CarGO;
import field3d.gameobjects.FieldGO;
import field3d.gameobjects.PenaltySpotGO;
import field3d.gameobjects.RobotGO;
import field3d.gameobjects.SkyboxGO;
import field3d.gameobjects.overlay.CameraPresetOverlay;
import javafx.scene.AmbientLight;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;




public class FieldGame extends Game {
	
	
	// TODO: move to config file, or get from model
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
	public static final double FIELD_PENALTY_SPOT = 1000.0;
	public static final double FIELD_PENALTY_SPOT_SIZE = 10.0;
	
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private AmbientLight _ambientLight;
	private PointLight _pointLight1, _pointLight2, _pointLight3, _pointLight4;
	
	private FieldGO _field;
	private SkyboxGO _skybox;
	private CameraPresetOverlay _cameraPresetOverlay;
	// TODO: move to field
	private PenaltySpotGO _penaltySpot1;
	private PenaltySpotGO _penaltySpot2;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/*public FieldGame(Parent root, double width, double height, Scene mainScene) {
		
		// Initialize super class
		super(root, width, height, mainScene);
		
		initialize();
	}*/
	
	public FieldGame(Parent root, double width, double height, SceneAntialiasing antiAliasing) {
		
		// Initialize super class
		super(root, width, height, true, antiAliasing);
		
		initialize();
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
		//AddGameObject(_skybox);
		AddGameObject(_penaltySpot1);
		AddGameObject(_penaltySpot2);
		AddGameObject(_cameraPresetOverlay);
		
		CarGO car = new CarGO(this);
		
		AddGameObject(car);
		
		
		// Create some robots
		createRobots();
		
		setFill(Color.BLACK);
	}

	@Override
	public void Update(long timeDivNano) { }
	@Override
	public void Destroy() { }
	
	
	public void AddRobot(RobotGO robot) {
		
		AddGameObject(robot);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private void initialize() {
		
		// Creating ambient light
		_ambientLight = new AmbientLight(Color.DARKGRAY);
		
		// Creating point lights
		_pointLight1 = new PointLight(Color.WHITE);
		_pointLight2 = new PointLight(Color.WHITE);
		_pointLight3 = new PointLight(Color.WHITE);
		_pointLight4 = new PointLight(Color.WHITE);
		
			
		_field = new FieldGO(this, FIELD_REAL_WIDTH, FIELD_REAL_DEPTH);
		_skybox = new SkyboxGO(this);
		_cameraPresetOverlay = new CameraPresetOverlay(this);
		
		// TODO: move to field
		_penaltySpot1 = new PenaltySpotGO(this, new Vector3f((float)((FIELD_WIDTH / 2.0) - FIELD_PENALTY_SPOT), 20, 0), FIELD_PENALTY_SPOT_SIZE);
		_penaltySpot2 = new PenaltySpotGO(this, new Vector3f((float)(-(FIELD_WIDTH / 2.0) + FIELD_PENALTY_SPOT), 20, 0), FIELD_PENALTY_SPOT_SIZE);
	
		
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
		
		GetMouseInputHandler().SetMinMouseWheelValue(-1000);
		GetMouseInputHandler().SetMaxMouseWheelValue(1000);
	}
	
	private void createRobots() { 
		
		for (int i = 0; i < 22; i++) {
			
			RobotGO robot = new RobotGO(this);
			robot.SetLocation(new Vector3f(i * 500, ((float)RobotGO.ROBOT_HEIGHT / 2.0f) + 10.0f,0));
			
			AddGameObject(robot);
		}
	}
}
