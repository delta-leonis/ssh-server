package org.ssh.field3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.gameobjects.CarGO;
import org.ssh.field3d.gameobjects.DetectionGameObject;
import org.ssh.field3d.gameobjects.GeometryGameObject;
import org.ssh.field3d.gameobjects.detection.BallGameObject;
import org.ssh.field3d.gameobjects.detection.RobotGO;
import org.ssh.field3d.gameobjects.geometry.FieldGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.enums.TeamColor;

import javafx.application.Platform;
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
    
    private static final int ROBOTS_PER_TEAM = 11;
    
    /** The ambient light. */
    private final AmbientLight               ambientLight;
                                             
    /** The west point lights. */
    private final PointLight                 pointLightWestSouth, pointLightWestNorth;
                                             
    /** The east point lights. */
    private final PointLight                 pointLightEastSouth, pointLightEastNorth;
                                             
    /** The field game object. */
    private final FieldGO                    fieldGO;
                                             
    /** The camera control overlay game object. */
    private final CameraControlOverlayGO     cameraControlOverlayGO;
                                             
    /** The detection game objects. */
    private final Queue<DetectionGameObject> detectionGameObjects;
    
    private final List<DetectionGameObject> blueRobots;
    
    private final List<DetectionGameObject> yellowRobots;
                                             
    /** The geometry game objects. */
    private final Queue<GeometryGameObject>  geometryGameObjects;
                                             
    /** The field vision model. */
    private Field                            fieldVisionModel;
                                             
    /** The easter car game object */
    private final CarGO easterCarGO;
    
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
        
        this.detectionGameObjects = new ConcurrentLinkedQueue<DetectionGameObject>();
        this.geometryGameObjects = new ConcurrentLinkedQueue<GeometryGameObject>();
        
        this.blueRobots = new ArrayList<DetectionGameObject>();
        this.yellowRobots = new ArrayList<DetectionGameObject>();
        
        // Creating field GameObject
        this.fieldGO = new FieldGO(this);
        // Creating camera control overlay GameObject
        this.cameraControlOverlayGO = new CameraControlOverlayGO(this);
        // Creating easter egg car GameObject
        this.easterCarGO = new CarGO(this);
        
        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);
        
        // Set black fill color
        this.setFill(Color.BLACK);
        
        this.createRobots();
        
        // Adding game objects
        this.addGeometryGameObject(this.fieldGO);
        this.addGameObject(this.cameraControlOverlayGO);
        this.addGameObject(this.easterCarGO);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        
        // Add lights to the world
        Platform.runLater(() -> {
            
            if (!this.getWorldGroup().getChildren().contains(ambientLight)) {
                this.getWorldGroup().getChildren().add(this.ambientLight);
            }
            
            if (!this.getWorldGroup().getChildren().contains(this.pointLightWestSouth)) {
                this.getWorldGroup().getChildren().add(this.pointLightWestSouth);
            }
            
            if (!this.getWorldGroup().getChildren().contains(this.pointLightWestNorth)) {
                this.getWorldGroup().getChildren().add(this.pointLightWestNorth);
            }
            
            if (!this.getWorldGroup().getChildren().contains(this.pointLightEastSouth)) {
                this.getWorldGroup().getChildren().add(this.pointLightEastSouth);
            }
            
            if (!this.getWorldGroup().getChildren().contains(this.pointLightEastNorth)) {
                this.getWorldGroup().getChildren().add(this.pointLightEastNorth);
            }
        });
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
    public void updateGeometry() {
        
        // Trying to get field vision model
        Optional<Field> tmpOptionalField = Models.get("field");
        
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
        
        for (GeometryGameObject geometryGameObject : this.geometryGameObjects) {
            
            geometryGameObject.onUpdateGeometry();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateDetection() {
                       
        for (DetectionGameObject detectionGameObject : this.detectionGameObjects) {
           
            if (detectionGameObject instanceof RobotGO) {
                
                RobotGO tmpRobot = (RobotGO)detectionGameObject;
                
                if (this.blueRobots.contains(tmpRobot)) {
                    
                    tmpRobot.setRobotVisionModel(this.blueRobots.indexOf(tmpRobot), TeamColor.BLUE);
                    
                } else if (this.yellowRobots.contains(tmpRobot)) {
                    
                    tmpRobot.setRobotVisionModel(this.yellowRobots.indexOf(tmpRobot), TeamColor.YELLOW);
                }
            }
                
            detectionGameObject.onUpdateDetection();
        }
    }
    
    public void addGeometryGameObject(final GeometryGameObject geometryGameObject) {
        
        if (!this.geometryGameObjects.contains(geometryGameObject)) {
        
            this.geometryGameObjects.add(geometryGameObject);
            
            geometryGameObject.onInitialize();
        }
    }
    
    public void addDetectionGameObject(final DetectionGameObject detectionGameObject) {
        
        if (!this.detectionGameObjects.contains(detectionGameObject)) {
            
            this.detectionGameObjects.add(detectionGameObject);
            
            detectionGameObject.onInitialize();
        }
    }
    
    public void removeGeometryGameObject(final GeometryGameObject geometryGameObject) {
        
        if (this.geometryGameObjects.contains(geometryGameObject)) {
        
            this.geometryGameObjects.remove(geometryGameObject);
            
            geometryGameObject.onDestroy();
        }
    }
    
    public void removeDetectionGameObject(final DetectionGameObject detectionGameObject) {
        
        if (this.detectionGameObjects.contains(detectionGameObject)) {
            
            this.detectionGameObjects.remove(detectionGameObject);
            
            detectionGameObject.onDestroy();
        }
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
        this.addDetectionGameObject(robot);
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
        this.removeDetectionGameObject(robot);
    }
    
    /**
     * createRobots method. This method creates the robots on the field.
     */
    private void createRobots() {
        
        for (int i = 0; i < ROBOTS_PER_TEAM; i++) {
            
            RobotGO blueRobot = new RobotGO(this, null);
            RobotGO yellowRobot = new RobotGO(this, null);
            
            this.addDetectionGameObject(blueRobot);
            this.addDetectionGameObject(yellowRobot);
            
            this.blueRobots.add(blueRobot);
            this.yellowRobots.add(yellowRobot);
        }
    }
    
    /**
     * Clear robots method. This method clears the robots on the field.
     */
    private void clearRobots() {
        
        // Loop through robot game objects
        for (DetectionGameObject robotGameObject : this.detectionGameObjects) {
            
            if (robotGameObject instanceof RobotGO) {
                
                // Remove game object
                this.removeRobot((RobotGO)robotGameObject);
            }
        }
    }
    
    public void addBall(final BallGameObject ballGameObject) {
        
        // Check if we need to add ball game object
        if (!this.detectionGameObjects.contains(ballGameObject)) {
                        
            // Add ball to game objects
            this.addDetectionGameObject(ballGameObject);
        }
    }
    
    public void removeBall(final BallGameObject ballGameObject) {
        
        // Check if we need to remove the ball from the game
        if (this.detectionGameObjects.contains(ballGameObject)) {
            
            this.removeDetectionGameObject(ballGameObject);
        }
    }
    
    public void clearBalls() {

        // Loop through ball game objects
        for (DetectionGameObject detectionGameObject : this.detectionGameObjects) {
            
            if (detectionGameObject instanceof BallGameObject) {
                
                // Remove ball
                this.removeBall((BallGameObject)detectionGameObject);
            }
        }
    }
}
