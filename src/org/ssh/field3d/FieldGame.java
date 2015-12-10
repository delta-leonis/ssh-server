package org.ssh.field3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.ssh.field3d.core.game.Game;
// import org.ssh.field3d.gameobjects.CarGO;
import org.ssh.field3d.gameobjects.FieldGO;
import org.ssh.field3d.gameobjects.RobotGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.Model;
import org.ssh.models.Robot;

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
    
    /** The ambient light. */
    private final AmbientLight           ambientLight;
                                         
    /** The west point lights. */
    private final PointLight             pointLightWestSouth, pointLightWestNorth;
                                         
    /** The east point lights. */
    private final PointLight             pointLightEastSouth, pointLightEastNorth;
                                         
    /** The field game object. */
    private final FieldGO                fieldGO;
                                         
    /** The camera control overlay game object. */
    private final CameraControlOverlayGO cameraControlOverlayGO;
                                         
    /** The robots. */
    private List<Robot>                  robotsVisionModel;
                                         
    /** The robot game objects. */
    private Queue<RobotGO>               robotGameObjects;
                                         
    /** The field vision model. */
    private Field                        fieldVisionModel;
                                         
    /** The easter car game object */
    // private final CarGO easterCarGO;
    
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
        
        // Creating ambient light
        this.ambientLight = new AmbientLight(Color.DARKGRAY);
        
        // Creating point lights
        this.pointLightWestSouth = new PointLight(Color.WHITE);
        this.pointLightWestNorth = new PointLight(Color.WHITE);
        this.pointLightEastSouth = new PointLight(Color.WHITE);
        this.pointLightEastNorth = new PointLight(Color.WHITE);
        
        // Creating list for the robot game objects
        this.robotGameObjects = new ConcurrentLinkedQueue<RobotGO>();
        
        // Creating field GameObject
        this.fieldGO = new FieldGO(this);
        // Creating camera control overlay GameObject
        this.cameraControlOverlayGO = new CameraControlOverlayGO(this);
        // Creating easter egg car GameObject
        // this.easterCarGO = new CarGO(this);
        
        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);
        
        // Set black fill color
        this.setFill(Color.BLACK);
        
        // Adding game objects
        this.addGameObject(this.fieldGO);
        this.addGameObject(this.cameraControlOverlayGO);
        // this.addGameObject(this.easterCarGO);
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
        
        updateGeometry();
        updateDetection();
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
     * {@inheritDoc}
     */
    @Override
    public void updateGeometry() {
        
        // Trying to get field vision model
        Optional<Model> tmpOptionalField = Models.get("field");
        
        // If a model is present
        if (tmpOptionalField.isPresent()) {
            
            // Set field vision model
            this.fieldVisionModel = (Field) tmpOptionalField.get();
            
            // Setting bounds for the location of the camera
            this.getThirdPersonCamera().setMaxLocX(this.fieldVisionModel.getFieldLength() / 2.0);
            this.getThirdPersonCamera().setMinLocX(-(this.fieldVisionModel.getFieldLength() / 2.0));
            this.getThirdPersonCamera().setMaxLocZ(this.fieldVisionModel.getFieldWidth() / 2.0);
            this.getThirdPersonCamera().setMinLocZ(-(this.fieldVisionModel.getFieldWidth() / 2.0));
            
            // Setup lights
            this.pointLightWestSouth.setTranslateX(-(this.fieldVisionModel.getFieldLength() / 4.0));
            this.pointLightWestSouth.setTranslateY(2000.0);
            this.pointLightWestSouth.setTranslateZ(-(this.fieldVisionModel.getFieldWidth() / 4.0));
            
            this.pointLightWestNorth.setTranslateX(-(this.fieldVisionModel.getFieldLength() / 4.0));
            this.pointLightWestNorth.setTranslateY(2000.0);
            this.pointLightWestNorth.setTranslateZ(this.fieldVisionModel.getFieldWidth() / 4.0);
            
            this.pointLightEastSouth.setTranslateX(this.fieldVisionModel.getFieldLength() / 4.0);
            this.pointLightEastSouth.setTranslateY(2000.0);
            this.pointLightEastSouth.setTranslateZ(-(this.fieldVisionModel.getFieldWidth() / 4.0));
            
            this.pointLightEastNorth.setTranslateX(this.fieldVisionModel.getFieldLength() / 4.0);
            this.pointLightEastNorth.setTranslateY(2000.0);
            this.pointLightEastNorth.setTranslateZ(this.fieldVisionModel.getFieldWidth() / 4.0);
        }
        
        // Clear robots
        this.clearRobots();
        
        // There has been new data, re-create robots
        this.createRobots();
        
        // Call the updateVisionData method from the super class.
        // By doing this, the game objects also get a call that the vision model was updated
        super.updateGeometry();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDetection() {
        
        // Call super updateDetection method
        super.updateDetection();
        
        // Clear robots
        this.clearRobots();
        
        // There has been new data, re-create robots
        this.createRobots();
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
        
        // Add to the list of robot game objects
        this.robotGameObjects.add(robot);
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
        
        // Remove from the list of robot game objects
        this.robotGameObjects.remove(robot);
    }
    
    /**
     * Gets the {@link List} of robots used in the game.
     *
     * @return The {@link List} of robots.
     */
    public List<Robot> getRobots() {
        return this.robotsVisionModel;
    }
    
    /**
     * createRobots method. This method creates the robots on the field.
     */
    private void createRobots() {
        
        // Getting list of robots from the vision model
        this.robotsVisionModel = Models.<Robot>getAll().stream()
                .filter(robot -> robot.getName().equals("robot")).collect(Collectors.toList());
        
        // Loop through robot models
        for (Robot robot : this.robotsVisionModel) {
            
            // Creating new robot
            RobotGO tmpRobot = new RobotGO(this, robot);
            
            // Add to game objects
            addGameObject(tmpRobot);
        }
    }
    
    /**
     * Clear robots method. This method clears the robots on the field.
     */
    private void clearRobots() {
        
        // Loop through robot game objects
        for (RobotGO robotGameObject : this.robotGameObjects) {
            
            // Remove game object
            this.removeRobot(robotGameObject);
        }
    }
}
