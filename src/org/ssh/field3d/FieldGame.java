package org.ssh.field3d;

import java.util.ArrayList;
import java.util.List;

import org.ssh.field3d.core.game.Game;
//import org.ssh.field3d.gameobjects.CarGO;
import org.ssh.field3d.gameobjects.FieldGO;
import org.ssh.field3d.gameobjects.RobotGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;
import org.ssh.field3d.gameobjects.overlay.contextmenus.ContextOverlayGO;
import org.ssh.managers.Models;
import org.ssh.models.Robot;

import javafx.scene.AmbientLight;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;

// TODO: auto generated java doc
/**
 *
 * FieldGame class This class is the main part of the 3d field, from here everything in the 3d world
 * is created & managed.
 *
 * @author Mark Lefering - 330430
 *         
 */
public class FieldGame extends Game {
    
    // TODO: move to config file, or get from the vision model
    /** The Constant FIELD_TILE_WIDTH. */
    public static final double           FIELD_TILE_WIDTH      = 500.0;
                                                               
    /** The Constant FIELD_TILE_DEPTH. */
    public static final double           FIELD_TILE_DEPTH      = 500.0;
                                                               
    /** The Constant FIELD_WIDTH. */
    public static final double           FIELD_WIDTH           = 9000.0;
                                                               
    /** The Constant FIELD_DEPTH. */
    public static final double           FIELD_DEPTH           = 6000.0;
                                                               
    /** The Constant FIELD_REAL_WIDTH. */
    public static final double           FIELD_REAL_WIDTH      = FieldGame.FIELD_WIDTH
            + (2.0 * FieldGame.FIELD_TILE_WIDTH);
            
    /** The Constant FIELD_REAL_DEPTH. */
    public static final double           FIELD_REAL_DEPTH      = FieldGame.FIELD_DEPTH
            + (2.0 * FieldGame.FIELD_TILE_DEPTH);
            
    /** The Constant FIELD_LINE_HEIGHT. */
    public static final double           FIELD_LINE_HEIGHT     = 5.0;
                                                               
    /** The Constant FIELD_LINE_WIDTH. */
    public static final double           FIELD_LINE_WIDTH      = 10.0f;
                                                               
    /** The Constant FIELD_HEIGHT. */
    public static final double           FIELD_HEIGHT          = 1.0;
                                                               
    /** The Constant FIELD_GOAL_WIDTH. */
    public static final double           FIELD_GOAL_WIDTH      = 1000.0;
                                                               
    /** The Constant FIELD_GOAL_DEPTH. */
    public static final double           FIELD_GOAL_DEPTH      = 200.0;
                                                               
    /** The Constant FIELD_GOAL_HEIGHT. */
    public static final double           FIELD_GOAL_HEIGHT     = 190.0;
                                                               
    /** The Constant FIELD_GOAL_LINE_WIDTH. */
    public static final double           FIELD_GOAL_LINE_WIDTH = 5.0;
                                                               
    /** The ambient light. */
    private final AmbientLight           ambientLight;
                                         
    /** The west point lights */
    private final PointLight             pointLightWestSouth, pointLightWestNorth;
    
	/** The east point lights */
    private final PointLight             pointLightEastSouth, pointLightEastNorth;
                                         
    /** The field game object */
    private final FieldGO                fieldGO;
                                         
    /** The camera control overlay game object. */
    private final CameraControlOverlayGO cameraControlOverlayGO;
        
    /** The context overlay game object. */
    private final ContextOverlayGO       contextOverlayGO;
    
    private List<Robot>                  robots;
                                          
    /** The easter car game object */
    //private final CarGO                  easterCarGO;
                                         
    /**
     * Constructor.
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
        
        // Create robots
        Robot robot = (Robot) Models.create(Robot.class, 0, Color.BLUE);
        Robot robot2 = (Robot) Models.create(Robot.class, 1, Color.BLUE);
        Robot robot3 = (Robot) Models.create(Robot.class, 2, Color.BLUE);
        Robot robot4 = (Robot) Models.create(Robot.class, 3, Color.BLUE);
        
        robot.update("isSelected", true);
        robot2.update("isSelected", false);
        robot3.update("isSelected", false);
        robot4.update("isSelected", false);
        
        // Creating ambient light
        this.ambientLight = new AmbientLight(Color.DARKGRAY);
        
        // Creating point lights
        this.pointLightWestSouth = new PointLight(Color.WHITE);
        this.pointLightWestNorth = new PointLight(Color.WHITE);
        this.pointLightEastSouth = new PointLight(Color.WHITE);
        this.pointLightEastNorth = new PointLight(Color.WHITE);
        
        // Create some robots
        this.createRobots();
        
        // Creating field GameObject
        this.fieldGO = new FieldGO(this, FieldGame.FIELD_REAL_WIDTH, FieldGame.FIELD_REAL_DEPTH);
        // Creating camera control overlay GameObject
        this.cameraControlOverlayGO = new CameraControlOverlayGO(this);
        // Creating context menu overlay GameObject
        this.contextOverlayGO = new ContextOverlayGO(this);
        // Creating easter egg car GameObject
        //this.easterCarGO = new CarGO(this);
        
        // Setup lights
        this.pointLightWestSouth.setTranslateX(-(FieldGame.FIELD_WIDTH / 4.0));
        this.pointLightWestSouth.setTranslateY(2000);
        this.pointLightWestSouth.setTranslateZ(-(FieldGame.FIELD_DEPTH / 4.0));
        
        this.pointLightWestNorth.setTranslateX(-(FieldGame.FIELD_WIDTH / 4.0));
        this.pointLightWestNorth.setTranslateY(2000);
        this.pointLightWestNorth.setTranslateZ(FieldGame.FIELD_DEPTH / 4.0);
        
        this.pointLightEastSouth.setTranslateX(FieldGame.FIELD_WIDTH / 4.0);
        this.pointLightEastSouth.setTranslateY(2000);
        this.pointLightEastSouth.setTranslateZ(-(FieldGame.FIELD_DEPTH / 4.0));
        
        this.pointLightEastNorth.setTranslateX(FieldGame.FIELD_WIDTH / 4.0);
        this.pointLightEastNorth.setTranslateY(2000);
        this.pointLightEastNorth.setTranslateZ(FieldGame.FIELD_DEPTH / 4.0);
        
        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);
        
        // Set black fill color
        this.setFill(Color.BLACK);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        
        // Add lights to the world
        this.getWorldGroup().getChildren().add(this.ambientLight);
        this.getWorldGroup().getChildren().add(this.pointLightWestSouth);
        this.getWorldGroup().getChildren().add(this.pointLightWestNorth);
        this.getWorldGroup().getChildren().add(this.pointLightEastSouth);
        this.getWorldGroup().getChildren().add(this.pointLightEastNorth);
        
        // Adding game objects
        this.addGameObject(this.fieldGO);
        this.addGameObject(this.cameraControlOverlayGO);
        this.addGameObject(this.contextOverlayGO);
        //this.addGameObject(this.easterCarGO);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long timeDivNano) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
    
    /**
     * 
     * AddRobot method This method adds a RobotGO to the game.
     * 
     * @param robot
     *            The RobotGO to add to the game.
     */
    public void addRobot(final RobotGO robot) {
        
        // Add robot to game
        this.addGameObject(robot);
    }
    
    /**
     * 
     * RemoveRobot method This method removes a RobotGO from the game.
     * 
     * @param robot
     *            The RobotGO to remove from the game.
     */
    public void removeRobot(final RobotGO robot) {
        
        // Remove robot from game
        this.removeGameObject(robot);
    }
    
    public List<Robot> getRobots() {
        return this.robots;
    }
    
    /**
     * createRobots method This method creates some robots.
     */
    @SuppressWarnings ("unchecked")
    private void createRobots() {
        
        this.robots = (ArrayList<Robot>) Models.getAll("robot");
       
        // Loop through robot models
        for (Robot robot : this.robots) {
            
            // Creating new robot
            RobotGO tmpRobot = new RobotGO(this, robot);
            
            // Add to game objects
            addGameObject(tmpRobot);
        }
    }
}
