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
 * FieldGame class This class is the main part of the 3d field, from here everything in the 3d world
 * is created & managed.
 *
 * @author Mark Lefering - 330430
 *        
 */
public class FieldGame extends Game {
    
    // TODO: move to config file, or get from org.ssh.models
    public static final double           FIELD_TILE_WIDTH      = 500.0;
    public static final double           FIELD_TILE_DEPTH      = 500.0;
    public static final double           FIELD_WIDTH           = 9000.0;
    public static final double           FIELD_DEPTH           = 6000.0;
    public static final double           FIELD_REAL_WIDTH      = FieldGame.FIELD_WIDTH
            + (2.0 * FieldGame.FIELD_TILE_WIDTH);
    public static final double           FIELD_REAL_DEPTH      = FieldGame.FIELD_DEPTH
            + (2.0 * FieldGame.FIELD_TILE_DEPTH);
            
    public static final double           FIELD_LINE_HEIGHT     = 5.0;
    public static final double           FIELD_LINE_WIDTH      = 10.0f;
    public static final double           FIELD_HEIGHT          = 1.0;
    public static final double           FIELD_GOAL_WIDTH      = 1000.0;
    public static final double           FIELD_GOAL_DEPTH      = 200.0;
    public static final double           FIELD_GOAL_HEIGHT     = 190.0;
    public static final double           FIELD_GOAL_LINE_WIDTH = 5.0;
                                                               
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private final AmbientLight           _ambientLight;
    private final PointLight             _pointLight1, _pointLight2, _pointLight3, _pointLight4;
                                         
    private final FieldGO                _field;
    private final CameraControlOverlayGO _cameraControlOverlay;
    private final CarGO                  _easterCar;
                                         
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Constructor
     * 
     * @param root
     *            The root of the SubScene.
     * @param width
     *            The width of the SubScene.
     * @param height
     *            The height of the SubScene.
     * @param antiAliasing
     *            Anti-aliasing mode, SceneAntialiasing.DISABLED.
     */
    public FieldGame(final Parent root, final double width, final double height, final SceneAntialiasing antiAliasing) {
        
        // Initialize super class
        super(root, width, height, true, antiAliasing);
        
        // Creating ambient light
        this._ambientLight = new AmbientLight(Color.DARKGRAY);
        
        // Creating point lights
        this._pointLight1 = new PointLight(Color.WHITE);
        this._pointLight2 = new PointLight(Color.WHITE);
        this._pointLight3 = new PointLight(Color.WHITE);
        this._pointLight4 = new PointLight(Color.WHITE);
        
        // Creating field GameObject
        this._field = new FieldGO(this, FieldGame.FIELD_REAL_WIDTH, FieldGame.FIELD_REAL_DEPTH);
        // Creating camera control overlay GameObject
        this._cameraControlOverlay = new CameraControlOverlayGO(this);
        // Creating easter egg car GameObject
        this._easterCar = new CarGO(this);
        
        // Setup lights
        this._pointLight1.setTranslateX(-(FieldGame.FIELD_WIDTH / 4.0));
        this._pointLight1.setTranslateY(2000);
        this._pointLight1.setTranslateZ(-(FieldGame.FIELD_DEPTH / 4.0));
        
        this._pointLight2.setTranslateX(-(FieldGame.FIELD_WIDTH / 4.0));
        this._pointLight2.setTranslateY(2000);
        this._pointLight2.setTranslateZ(FieldGame.FIELD_DEPTH / 4.0);
        
        this._pointLight3.setTranslateX(FieldGame.FIELD_WIDTH / 4.0);
        this._pointLight3.setTranslateY(2000);
        this._pointLight3.setTranslateZ(-(FieldGame.FIELD_DEPTH / 4.0));
        
        this._pointLight4.setTranslateX(FieldGame.FIELD_WIDTH / 4.0);
        this._pointLight4.setTranslateY(2000);
        this._pointLight4.setTranslateZ(FieldGame.FIELD_DEPTH / 4.0);
        
        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);
        
        // Set black fill color
        this.setFill(Color.BLACK);
    }
    
    /**
     * 
     * AddRobot method This method adds a RobotGO to the game.
     * 
     * @param robot
     *            The RobotGO to add to the game.
     */
    public void AddRobot(final RobotGO robot) {
        
        // Add robot to game
        this.addGameObject(robot);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * createRobots method This method creates some robots.
     */
    private void createRobots() {
        
        // Create 22 robots
        for (int i = 0; i < 22; i++) {
            
            // Create new robot
            final RobotGO robot = new RobotGO(this);
            // Set location of the robot
            robot.setLocation(new Vector3f(i * 500, ((float) RobotGO.ROBOT_HEIGHT / 2.0f) + 10.0f, 0));
            
            // Add robot to game objects
            this.addGameObject(robot);
        }
    }
    
    @Override
    public void destroy() {
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Abstract methods from GameObject
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize() {
        
        // Add lights to the world
        this.getWorldGroup().getChildren().add(this._ambientLight);
        this.getWorldGroup().getChildren().add(this._pointLight1);
        this.getWorldGroup().getChildren().add(this._pointLight2);
        this.getWorldGroup().getChildren().add(this._pointLight3);
        this.getWorldGroup().getChildren().add(this._pointLight4);
        
        // Adding game objects
        this.addGameObject(this._field);
        this.addGameObject(this._cameraControlOverlay);
        this.addGameObject(this._easterCar);
        
        // Create some robots
        this.createRobots();
    }
    
    /**
     * 
     * RemoveRobot method This method removes a RobotGO from the game.
     * 
     * @param robot
     *            The RobotGO to remove from the game.
     */
    public void RemoveRobot(final RobotGO robot) {
        
        // Remove robot from game
        this.removeGameObject(robot);
    }
    
    @Override
    public void update(final long timeDivNano) {
    }
}
