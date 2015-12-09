package org.ssh.field3d.gameobjects.detection;

import java.io.InputStream;
import java.util.Optional;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.field3d.gameobjects.DetectionGameObject;
import org.ssh.managers.manager.Models;
import org.ssh.models.Robot;
import org.ssh.models.enums.TeamColor;
import org.ssh.util.Logger;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

/**
 * RobotGO class This class is for robot game objects. This class loads the 3d model, textures and
 * the selection arc.
 * 
 * @see GameObject
 *      
 * @author Mark Lefering
 */
public class RobotGO extends DetectionGameObject {
    
    /** The conversion from radians to degrees */
    private static final float RAD_TO_DEG = (float)(2.0f * Math.PI) / 180.0f;
    
    /** The thickness of the selection circle. */
    private static final float ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0f;
                                                                 
    /** The starting y-coordinate for the robot model */
    private static final float  ROBOT_STARTING_Y                 = 10.0f;
                                                                 
    /** The selection circle offset */
    private static final float  SELECTION_CIRCLE_OFFSET          = 40.0f;
                                                                 
    /** The number of segments in the selection circle */
    private static final int    ROBOT_SEL_CIRCLE_NUM_OF_SEGMENTS = 100;
                                                                 
    /** The angle in degrees for a full circle */
    private static final float FULL_CIRCLE                      = 360.0f;
                                                                 
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
                                
    private Robot               visionRobotModel;
                                
    /** The model. */
    private MeshView            model;
                                
    /** The model group */
    private final Group         modelGroup;
                                
    /** The selected state */
    private boolean             isSelected;
                                
    private float               robotYOffset;
                                
    private int                 lastID;
                                
    /**
     * 
     * Constructor of {@link RobotGO}.
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
                
        // Creating PhongMaterials
        this.material = new PhongMaterial(Color.WHITE);
        this.selectionCircleMaterial = new PhongMaterial();
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
            this.loadTexture(this.visionRobotModel.getRobotId(), this.visionRobotModel.getTeamColor());
            
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
        
        // Add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().addAll(modelGroup));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
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
        if (this.visionRobotModel != null && this.model != null) {
            
            if (!this.model.isVisible()) this.model.setVisible(true);
            
            this.setSelected(this.visionRobotModel.isSelected());
            
            // Check if position is not null
            if (visionRobotModel.getPosition() != null) {
                
                Platform.runLater(() -> {
                    
                    if (this.visionRobotModel.getOrientation() != null) {
                        
                        // Set the orientation of the robot
                        this.model.setRotate(this.visionRobotModel.getOrientation() * RAD_TO_DEG);
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
            
            if (this.model.isVisible()) this.model.setVisible(false);
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
    
    public void setRobotVisionModel(int id, TeamColor color) {
        
        // Convert team color to char
        char teamColorAsChar = color.toString().charAt(0);
        // Try to get robot model
        Optional<Robot> tmpOptionalVisionRobot = Models.<Robot> get("robot " + teamColorAsChar + id);
        
        if (this.lastID == id) {
            return;
        }
        
        // Check if we've found
        if (tmpOptionalVisionRobot.isPresent()) {
            
            // Setting vision robot model
            this.visionRobotModel = tmpOptionalVisionRobot.get();
            
            // Check if the last id differs
            if (this.lastID != id) {
                
                // Load texture
                this.loadTexture(id, color);
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
    
    private void loadTexture(int id, TeamColor teamColor) {
        
        // Generating texture file name
        String textureFilename = ROBOT_TEXTURE_DIR + "robot" + visionRobotModel.getTeamColorIdentifier()
                + visionRobotModel.getRobotId() + ".png";
                
        // Getting texture as input stream
        InputStream textureInputStream = this.getClass().getResourceAsStream(textureFilename);
        
        // If the texture input stream is not null
        if (textureInputStream != null) {
            
            // Loading texture & setting diffuse map of the model material
            this.material.setDiffuseMap(new Image(textureInputStream));
        }
        else {
            
            // Log error
            LOG.warning("Could not load texture: " + textureFilename);
        }
        
        // Setting model material
        this.model.setMaterial(this.material);
    }
    
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
            
            // Add mouse clicked event
            this.model.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    
                    // Setting selected state
                    setSelected(!isSelected);
                }
            });
            
            // Calculating offset of the robot on the y-axis
            this.robotYOffset = (float) (this.model.getBoundsInLocal().getHeight() / 2.0f + ROBOT_STARTING_Y);
            
            // Close model importer
            modelImporter.close();
        }
        else {
            
            // Log error
            LOG.info("Could not load " + ROBOT_MODEL_FILE);
            
            // Break out of constructor
            return;
        }
    }
}
