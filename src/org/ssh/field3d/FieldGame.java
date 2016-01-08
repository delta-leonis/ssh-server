package org.ssh.field3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.scene.*;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.gameobjects.DetectionGameObject;
import org.ssh.field3d.gameobjects.GeometryGameObject;
import org.ssh.field3d.gameobjects.detection.BallGameObject;
import org.ssh.field3d.gameobjects.detection.RobotGO;
import org.ssh.field3d.gameobjects.geometry.FieldGO;
import org.ssh.field3d.gameobjects.overlay.CameraControlOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.models.Ball;
import org.ssh.models.Field;
import org.ssh.models.enums.Allegiance;

import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * FieldGame class This class is the main part of the 3d field, from here everything in the 3d world
 * is created & managed.
 *
 * @author Mark Lefering - 330430
 */
public class FieldGame extends Game {

    /**
     * The amount of robots per team
     */
    private static final int ROBOTS_PER_TEAM = 11;

    /**
     * The ambient light.
     */
    private final AmbientLight ambientLight;

    /**
     * The west point lights.
     */
    private final PointLight pointLightWestSouth, pointLightWestNorth;

    /**
     * The east point lights.
     */
    private final PointLight pointLightEastSouth, pointLightEastNorth;

    /**
     * The field game object.
     */
    private final FieldGO fieldGO;

    /**
     * The camera control overlay game object.
     */
    private final CameraControlOverlayGO cameraControlOverlayGO;

    /**
     * The detection game objects.
     */
    private final Queue<DetectionGameObject> detectionGameObjects;
    /**
     * The geometry game objects.
     */
    private final Queue<GeometryGameObject> geometryGameObjects;

    /**
     * The list of blue robots.
     */
    private final List<DetectionGameObject> allyRobots;
    /**
     * The list of yellow robots.
     */
    private final List<DetectionGameObject> opponentRobots;
    /**
     * The list of balls.
     */
    private final List<DetectionGameObject> balls;

    /**
     * The initialized state.
     */
    private boolean isInitialized;

    /** The easter car game object */
    //private final CarGO easterCarGO;

    /**
     * Constructor.
     *
     * @param root         The root of the {@link javafx.scene.SubScene}.
     * @param width        The width of the {@link javafx.scene.SubScene}.
     * @param height       The height of the {@link javafx.scene.SubScene}.
     * @param antiAliasing Anti-aliasing mode, SceneAntialiasing.DISABLED.
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

        // Creating queue for detection & geometry game objects
        this.detectionGameObjects = new ConcurrentLinkedQueue<>();
        this.geometryGameObjects = new ConcurrentLinkedQueue<>();

        // Creating lists for robots & balls
        this.allyRobots = new ArrayList<>();
        this.opponentRobots = new ArrayList<>();
        this.balls = new ArrayList<>();

        // Creating field GameObject
        this.fieldGO = new FieldGO(this);
        // Creating camera control overlay GameObject
        this.cameraControlOverlayGO = new CameraControlOverlayGO(this);
        // Creating easter egg car GameObject
        //this.easterCarGO = new CarGO(this);

        // Setting not initialized
        this.isInitialized = false;

        // Set minimal mouse wheel value
        this.getMouseInputHandler().setMinMouseWheelValue(-1000);
        // Set maximal mouse wheel value
        this.getMouseInputHandler().setMaxMouseWheelValue(1000);

        // Set black fill color
        this.setFill(Color.BLACK);

        // Create robots
        this.createRobots();

        // Adding game objects
        this.addGeometryGameObject(this.fieldGO);
        this.addGameObject(this.cameraControlOverlayGO);

        // for resizing purposes
        this.setManaged(false);

        // When this SubScene is added
        // it should be notified through a call to #internalInitialize
        Platform.runLater(() ->
                this.internalInitialize());
        ///this.addGameObject(this.easterCarGO);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {

        // Add lights to the world
        Platform.runLater(() -> {

            this.addToWorldIfAbsent(this.ambientLight);
            this.addToWorldIfAbsent(this.pointLightEastNorth);
            this.addToWorldIfAbsent(this.pointLightEastSouth);
            this.addToWorldIfAbsent(this.pointLightWestNorth);
            this.addToWorldIfAbsent(this.pointLightWestSouth);
        });

        // Setting initialized state
        this.isInitialized = true;

        // Update geometry
        this.updateGeometry();
        // Update detection
        this.updateDetection();
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

        // Check if the game already is initialized
        if (this.isInitialized) {

            // If a model is present
            if (tmpOptionalField.isPresent()) {

                // Set field vision model
                Field fieldVisionModel = tmpOptionalField.get();

                // Setting bounds for the location of the camera
                this.getThirdPersonCamera().setMaxLocX(fieldVisionModel.getFieldLength() / 2.0);
                this.getThirdPersonCamera().setMinLocX(-(fieldVisionModel.getFieldLength() / 2.0));
                this.getThirdPersonCamera().setMaxLocZ(fieldVisionModel.getFieldWidth() / 2.0);
                this.getThirdPersonCamera().setMinLocZ(-(fieldVisionModel.getFieldWidth() / 2.0));

                // Setting location of the south west point light
                this.pointLightWestSouth.setTranslateX(-(fieldVisionModel.getFieldLength() / 4.0));
                this.pointLightWestSouth.setTranslateY(2000.0);
                this.pointLightWestSouth.setTranslateZ(-(fieldVisionModel.getFieldWidth() / 4.0));

                // Setting location of the north west point light
                this.pointLightWestNorth.setTranslateX(-(fieldVisionModel.getFieldLength() / 4.0));
                this.pointLightWestNorth.setTranslateY(2000.0);
                this.pointLightWestNorth.setTranslateZ(fieldVisionModel.getFieldWidth() / 4.0);

                // Setting location of the south east point light
                this.pointLightEastSouth.setTranslateX(fieldVisionModel.getFieldLength() / 4.0);
                this.pointLightEastSouth.setTranslateY(2000.0);
                this.pointLightEastSouth.setTranslateZ(-(fieldVisionModel.getFieldWidth() / 4.0));

                // Setting location of the north east point light
                this.pointLightEastNorth.setTranslateX(fieldVisionModel.getFieldLength() / 4.0);
                this.pointLightEastNorth.setTranslateY(2000.0);
                this.pointLightEastNorth.setTranslateZ(fieldVisionModel.getFieldWidth() / 4.0);
            }

            // Loop through every geometry game object and call the onUpdateGeometry() method
            this.geometryGameObjects.forEach(GeometryGameObject::onUpdateGeometry);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateDetection() {

        // Getting all balls
        List<Ball> tmpBalls = Models.<Ball>getAll("ball");

        // Check if the game is already initialized
        if (this.isInitialized) {

            // Check if the size of the arrays match
            if (balls.size() != tmpBalls.size()) {

                // Clear the balls on the field
                this.clearBalls();

                // Loop through balls
                for (Ball tmpBall : tmpBalls) {

                    // Create ball game object
                    BallGameObject ballGameObject = new BallGameObject(this, tmpBall);

                    // Add ball
                    this.addBall(ballGameObject);
                }
            }

            // Loop through detection game objects
            for (DetectionGameObject detectionGameObject : this.detectionGameObjects) {

                // If we've found a RobotGO
                if (detectionGameObject instanceof RobotGO) {

                    // Getting RobotGO
                    RobotGO tmpRobot = (RobotGO) detectionGameObject;

                    // Check if blue robots contains the robot
                    if (this.allyRobots.contains(tmpRobot)) {

                        // Setting vision model of the robot
                        tmpRobot.setRobotVisionModel(this.allyRobots.indexOf(tmpRobot), Allegiance.ALLY);
                    }
                    // Check if yellow robots contains the robot
                    else if (this.opponentRobots.contains(tmpRobot)) {

                        // Setting vision model of the robot
                        tmpRobot.setRobotVisionModel(this.opponentRobots.indexOf(tmpRobot), Allegiance.OPPONENT);
                    }
                }

                // Notify detection game object that there is new data
                detectionGameObject.onUpdateDetection();
            }
        }
    }

    /**
     * Add geometry game object method. This method adds an {@link GeometryGameObject} to the game.
     *
     * @param geometryGameObject The {@link GeometryGameObject} to add to the game.
     */
    public void addGeometryGameObject(final GeometryGameObject geometryGameObject) {

        // Check if the list of geometry game objects does not contain the game object
        if (!this.geometryGameObjects.contains(geometryGameObject)) {

            // Add the geometry game object to the list of geometry game objects
            this.geometryGameObjects.add(geometryGameObject);

            // Call the onInitialize() method of the geometry game object
            geometryGameObject.onInitialize();
        }
    }

    /**
     * Add detection game object method. This method adds an {@link DetectionGameObject} to the game.
     *
     * @param detectionGameObject The {@link DetectionGameObject} to add to the game.
     */
    public void addDetectionGameObject(final DetectionGameObject detectionGameObject) {

        // Check if the list of detection game object does not contain the detection game object
        if (!this.detectionGameObjects.contains(detectionGameObject)) {

            // Add the detection game object to the list of game objects
            this.detectionGameObjects.add(detectionGameObject);

            // Call the onInitialize() method of the detection game object
            detectionGameObject.onInitialize();
        }
    }

    /**
     * Remove geometry game object method. This method removes an {@link GeometryGameObject} from the game.
     *
     * @param geometryGameObject The {@link GeometryGameObject} to remove from the game.
     */
    public void removeGeometryGameObject(final GeometryGameObject geometryGameObject) {

        // Check if the list of geometry game objects contains the geometry game object
        if (this.geometryGameObjects.contains(geometryGameObject)) {

            // Remove the geometry game object from the list of geometry game objects.
            this.geometryGameObjects.remove(geometryGameObject);

            // Call the onDestroy() method of the geometry game object
            geometryGameObject.onDestroy();
        }
    }

    /**
     * Remove detection game object method. This method removes an {@link DetectionGameObject} from the game.
     *
     * @param detectionGameObject The {@link DetectionGameObject} to remve from the game.
     */
    public void removeDetectionGameObject(final DetectionGameObject detectionGameObject) {

        // Check if the list of detection game objects contains the detection game object
        if (this.detectionGameObjects.contains(detectionGameObject)) {

            // Remove the detection game object from the list of detection game objects
            this.detectionGameObjects.remove(detectionGameObject);

            // Call the onDestroy() method of the detection game object
            detectionGameObject.onDestroy();
        }
    }

    /**
     * Create robots method. This method creates ROBOTS_PER_TEAM number of robots on the field.
     */
    private void createRobots() {

        // Loop through the number of robots per team
        for (int i = 0; i < ROBOTS_PER_TEAM; i++) {

            // Creating blue robot
            RobotGO allyRobots = new RobotGO(this, null);
            // Create yellow robot
            RobotGO opponentRobot = new RobotGO(this, null);

            // Add robots to the list of detection game objects
            this.addDetectionGameObject(allyRobots);
            this.addDetectionGameObject(opponentRobot);

            // Add the blue robot to the blue robot list
            this.allyRobots.add(allyRobots);
            // Add the yellow robot to the yellow robot list
            this.opponentRobots.add(opponentRobot);
        }
    }

    /**
     * Add ball method. This method adds an {@link BallGameObject} to the game.
     *
     * @param ballGameObject The {@link BallGameObject} to add to the game
     */
    public void addBall(final BallGameObject ballGameObject) {

        // Check if the detection game object list does not contain the ball game object
        if (!this.detectionGameObjects.contains(ballGameObject)) {

            // Add ball to the list of detection game objects
            this.addDetectionGameObject(ballGameObject);

            // Add ball game object to list of balls
            this.balls.add(ballGameObject);
        }
    }

    /**
     * Remove ball method. This method removes an {@link BallGameObject} from the game.
     *
     * @param ballGameObject The {@link BallGameObject} to remove from the game.
     */
    public void removeBall(final BallGameObject ballGameObject) {

        // Check if we need to remove the ball from the game
        if (this.detectionGameObjects.contains(ballGameObject)) {

            // Remove ball game object form the balls list
            this.balls.remove(ballGameObject);

            // Remove from detection game objects
            this.removeDetectionGameObject(ballGameObject);
        }
    }

    /**
     * Clear ball method. This method clears all the {@link BallGameObject} in the game.
     */
    public void clearBalls() {

        // Filter the list of detection game objects, get the ball game objects, loop through them and remove them
        this.detectionGameObjects.stream().filter(detectionGameObject -> detectionGameObject instanceof BallGameObject)
                .forEach(detectionGameObject -> {
                    // Remove ball
                    this.removeBall((BallGameObject) detectionGameObject);
                });
    }

    /**
     * Add to world if absent method. Adds a {@link Node} to the world group if absent.
     * @param node The {@link Node} to add.
     */
    private void addToWorldIfAbsent(Node node) {

        if (!this.getWorldGroup().getChildren().contains(node))
            this.getWorldGroup().getChildren().add(node);
    }
}
