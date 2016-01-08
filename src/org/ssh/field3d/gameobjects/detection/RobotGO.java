package org.ssh.field3d.gameobjects.detection;

import java.io.InputStream;
import java.util.Optional;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.field3d.gameobjects.DetectionGameObject;
import org.ssh.managers.manager.Models;
import org.ssh.models.Robot;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.TeamColor;
import org.ssh.util.Logger;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

/**
 * RobotGO class. This class is for robot game objects. This class loads the 3D model, textures and
 * the selection arc.
 * 
 * @see DetectionGameObject
 * @author Mark Lefering
 */
public class RobotGO extends DetectionGameObject {
    
    /** The conversion from radians to degrees */
    private static final float RAD_TO_DEG = (float)(2.0f * Math.PI) / 180.0f;
    
    /** The thickness of the selection circle. */
    private static final float  ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0f;
                                                                 
    /** The starting y-coordinate for the robot model */
    private static final float  ROBOT_STARTING_Y                 = 10.0f;
                                                                 
    /** The selection circle offset */
    private static final float  SELECTION_CIRCLE_OFFSET          = 40.0f;
                                                                 
    /** The number of segments in the selection circle */
    private static final int    ROBOT_SEL_CIRCLE_NUM_OF_SEGMENTS = 100;
                                                                 
    /** The angle in degrees for a full circle */
    private static final float  FULL_CIRCLE                      = (float) (2.0 * Math.PI);
                                                                 
    /** The specular power of the robot (shininess) */
    private static final double SPECULAR_POWER                   = 20.0;
                                                                 
    /** The file for the robot model. */
    private static final String ROBOT_MODEL_FILE                 = "/org/ssh/view/3dmodels/robot_model.obj";
                                                                 
    /** The directory for the robot texture. */
    private static final String ROBOT_TEXTURE_DIR                = "/org/ssh/view/textures/robots/";
                                                                 
    /** The logger. */
    private static final Logger LOG                              = Logger.getLogger();
                                                                 
    /** The material for the robot model. */
    private final PhongMaterial material;
                                
    /** The selection circle material. */
    private final PhongMaterial selectionCircleMaterial;
                                
    /** The selection arc. */
    private final FlatArc3D     selectionArc;
                                
    /** The selection arc mesh. */
    private final MeshView      selectionArcMesh;

    /** The vision model of the robot. */
    private Robot               visionRobotModel;
                                
    /** The model. */
    private MeshView            model;

    private org.ssh.models.Game game;
                                
    /** The model group */
    private final Group         modelGroup;
                                
    /** The selected state */
    private boolean             isSelected;

    /** The offset on the y-axis of the robot. */
    private float               robotYOffset;

    /** The last id of the robot. */
    private int                 lastID;
                                
    /**
     * Constructor. This instantiates a new RobotGO object.
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public RobotGO(final Game game, final Robot visionRobotModel) {
        
        // Initialize super class
        super(game);
        
        // Creating selection circle
        this.selectionArc = new FlatArc3D(0.0f,
                FULL_CIRCLE,
                Robot.ROBOT_DIAMETER + ROBOT_SELECTION_CIRCLE_THICKNESS + SELECTION_CIRCLE_OFFSET,
                ROBOT_SELECTION_CIRCLE_THICKNESS,
                ROBOT_SEL_CIRCLE_NUM_OF_SEGMENTS);

                
        // Creating PhongMaterial
        this.material = new PhongMaterial(Color.WHITE);
        this.selectionCircleMaterial = new PhongMaterial();

        // Creating group for the robot and selection arc
        this.modelGroup = new Group();
        
        // Setting vision robot model
        this.visionRobotModel = visionRobotModel;
        // Getting arc mesh
        this.selectionArcMesh = this.selectionArc.getMeshView();
        // Setting last id
        this.lastID = -1;
        
        // Load the 3d model
        this.loadModel();
        
        // Check if vision model is set
        if (this.visionRobotModel != null) {
            
            // Load texture
            this.loadTexture();
            
            // Setting last vision model id
            this.lastID = this.visionRobotModel.getRobotId();
        }
        
        // Setting selection circle diffuse & specular color to Blue
        this.selectionCircleMaterial.setDiffuseColor(Color.BLUE);
        this.selectionCircleMaterial.setSpecularColor(Color.BLUE);
        // Setting specular power
        this.selectionCircleMaterial.setSpecularPower(SPECULAR_POWER);
        // Setting selection circle material
        this.selectionArcMesh.setMaterial(this.selectionCircleMaterial);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Add models to model group
        modelGroup.getChildren().add(this.selectionArcMesh);

        // Sync with UI thread, Add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().addAll(modelGroup));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {

        // Sync with UI thread
        Platform.runLater(() -> {
            
            // Check if world group contains the 3d model of the robot
            if (this.getGame().getWorldGroup().getChildren().contains(this.model)) {
                
                // Remove 3d model from the world group
                this.getGame().getWorldGroup().getChildren().remove(this.model);
            }
            
            // Check if world group contains the 3d model of the selection arc
            if (this.getGame().getWorldGroup().getChildren().contains(this.selectionArcMesh)) {
                
                // Remove the selection arc from the world group
                this.getGame().getWorldGroup().getChildren().remove(this.selectionArcMesh);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateDetection() {

        // Checking if we have got valid data
        if (this.visionRobotModel != null && this.modelGroup != null) {

            // Show robot if needed
            if (!this.modelGroup.isVisible()) this.modelGroup.setVisible(true);

            // Bind visible property of the selection arc to the vision model
            this.selectionArcMesh.visibleProperty().bind(this.visionRobotModel.isSelectedProperty());
            
            // Check if position is not null
            if (visionRobotModel.getPosition() != null) {

                // Sync with UI thread
                Platform.runLater(() -> {
                    
                    if (this.visionRobotModel.getOrientation() != null) {
                        
                        // Set the orientation of the robot
                        this.modelGroup.setRotate(this.visionRobotModel.getOrientation() * RAD_TO_DEG);
                    }
                    
                    // Translate to location
                    this.model.setTranslateX(-this.visionRobotModel.getPosition().getX());
                    this.model.setTranslateY(this.robotYOffset);
                    this.model.setTranslateZ(this.visionRobotModel.getPosition().getY());
                    
                    // Translate selection circle to location
                    this.selectionArcMesh.setTranslateX(-this.visionRobotModel.getPosition().getX());
                    this.selectionArcMesh.setTranslateY(ROBOT_STARTING_Y);
                    this.selectionArcMesh.setTranslateZ(this.visionRobotModel.getPosition().getY());
                });
            }
        }
        else {

            // Check if model group not null and visible
            if (this.modelGroup != null && this.modelGroup.isVisible()) {

                // Set not visible
                this.modelGroup.setVisible(false);
            }
        }
    }
    
    /**
     * Gets the selected state of the robot.
     * 
     * @return True, if selected.
     */

    public boolean getSelected() {
        return this.isSelected;
    }
    
    /**
     * Sets the selected state of the robot.
     * 
     * @param isSelected
     *            the new selected state of the robot as boolean.
     */
    public void setSelected(final boolean isSelected) {
        
        // Setting selected state
        this.isSelected = isSelected;
        // Setting visibility of the selection arc mesh
        this.selectionArcMesh.setVisible(this.isSelected);
    }
    
    /**
     * This method set the color of the selection circle material.
     * 
     * @param color
     *            The {@link Color} of the circle.
     */

    public void setSelectionCircleColor(final Color color) {
        
        // Update material diffuse & specular color
        this.selectionCircleMaterial.setDiffuseColor(color);
        this.selectionCircleMaterial.setSpecularColor(color);
    }

    /**
     * Set robot vision model method. This method sets the vision model of the robot.
     *
     * @param id
     *          The id of the robot vision model.
     * @param color
     *          The {@link TeamColor color} of the robot.
     */
    public void setRobotVisionModel(int id, Allegiance color) {

        if (game == null) {

            Optional<org.ssh.models.Game> optionalGame = Models.<org.ssh.models.Game>get("game");

            if (optionalGame.isPresent()) {

                this.game = optionalGame.get();
            }
        }

        // Try to get robot model
        Optional<Robot> tmpOptionalVisionRobot = Models.<Robot> get("robot " + color.identifier() + id);

        // If the id is the same, do nothing
        if (this.lastID == id) return;
        
        // Check if we've found
        if (tmpOptionalVisionRobot.isPresent()) {
            
            // Setting vision robot model
            this.visionRobotModel = tmpOptionalVisionRobot.get();
            
            // Check if the last id differs
            if (this.lastID != id) {
                
                // Load texture
                this.loadTexture();
                // Update last id
                this.lastID = id;
            }
        }
        else {
            
            // Setting vision model to null
            this.visionRobotModel = null;
            // Setting last id
            this.lastID = -1;
            this.modelGroup.setVisible(false);
        }
    }

    /**
     * Load texture method. This method loads the correct texture according to the vision model.
     */
    private void loadTexture() {

        if (this.visionRobotModel != null) {

            String textureFilename = ROBOT_TEXTURE_DIR
                    + visionRobotModel.getIdentifier()
                    .replaceFirst("(A|O)", this.game.getTeamColor(visionRobotModel.getAllegiance()).identifier())
                    .replace(" ", "")
                    + ".png";

            // Getting texture as input stream
            InputStream textureInputStream = this.getClass().getResourceAsStream(textureFilename);

            // If the texture input stream is not null
            if (textureInputStream != null) {

                // Loading texture & setting diffuse map of the model material
                this.material.setDiffuseMap(new Image(textureInputStream));
            } else {

                // Log error
                LOG.warning("Could not load texture: " + textureFilename);
            }

            // Setting model material
            this.model.setMaterial(this.material);
        }
    }

    /**
     * Load model method. This method loads the 3D model of the robot.
     */
    private void loadModel() {
        
        // Creating model importer
        final ObjModelImporter modelImporter = new ObjModelImporter();
        
        // Read model into model importer
        modelImporter.read(this.getClass().getResource(ROBOT_MODEL_FILE));
        
        // Check if we have loaded something
        if (modelImporter.getImport().length > 0) {
            
            // Getting model from the model importer
            this.model = modelImporter.getImport()[0];
            
            // Setting rotation axis
            this.model.setRotationAxis(Rotate.Y_AXIS);
            
            // Add model to model group
            this.modelGroup.getChildren().add(this.model);
            
            // Calculating offset of the robot on the y-axis
            this.robotYOffset = (float) (this.model.getBoundsInLocal().getHeight() / 2.0f + ROBOT_STARTING_Y);
            
            // Close model importer
            modelImporter.close();
        }
        else {
            
            // Log error
            LOG.info("Could not load " + ROBOT_MODEL_FILE);
        }
    }
}
