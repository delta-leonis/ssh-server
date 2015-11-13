package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.models.Robot;
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

/**
 * RobotGO class This class is for robot game objects. This class loads the 3d model, textures and
 * the selection arc.
 * 
 * @see GameObject
 *      
 * @author Mark Lefering
 *         
 */
public class RobotGO extends GameObject {
    
    /** The thickness of the selection circle. */
    private static final double ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0;
                                                                 
    /** The starting y-coordinate for the robot model */
    private static final float  ROBOT_STARTING_Y                 = 10.0f;
                                                                 
    /** The number of segments in the selection circle */
    private static final int    ROBOT_SEL_CIRCLE_NUM_OF_SEGMENTS = 100;
                                                                 
    /** The angle in degrees for a full circle */
    private static final double FULL_CIRCLE                      = 360.0;
                                                                 
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
                                
    private final Robot         visionRobotModel;
                                
    /** The model. */
    private MeshView            model;
                                
    /** The location of the robot model. */
    private Vector3f            location;
                                
    /** The selected state */
    private boolean             isSelected;
                                
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
        
        // Creating model importer
        final ObjModelImporter modelImporter = new ObjModelImporter();
        
        // Create new location vector
        this.location = new Vector3f(0.0f, ROBOT_STARTING_Y, 0.0f);
        
        // Creating selection circle
        this.selectionArc = new FlatArc3D(0.0,
                FULL_CIRCLE,
                Robot.ROBOT_RADIUS,
                ROBOT_SELECTION_CIRCLE_THICKNESS,
                ROBOT_SEL_CIRCLE_NUM_OF_SEGMENTS);
                
        // Creating PhongMaterials
        this.material = new PhongMaterial(Color.WHITE);
        this.selectionCircleMaterial = new PhongMaterial();
        
        // Setting vision robot model
        this.visionRobotModel = visionRobotModel;
        
        // Getting arc mesh
        this.selectionArcMesh = this.selectionArc.MeshView();
        
        // Setting selection circle diffuse & specular color to Blue
        this.selectionCircleMaterial.setDiffuseColor(Color.BLUE);
        this.selectionCircleMaterial.setSpecularColor(Color.BLUE);
        // Setting specular power
        this.selectionCircleMaterial.setSpecularPower(SPECULAR_POWER);
        // Setting selection circle material
        this.selectionArcMesh.setMaterial(this.selectionCircleMaterial);
        
        // Generating texture file name
        String textureFilename = ROBOT_TEXTURE_DIR + "robot" + visionRobotModel.getTeamColorIdentifier()
                + visionRobotModel.getRobotId() + ".png";
        
        // Read model into model importer
        modelImporter.read(this.getClass().getResource(ROBOT_MODEL_FILE));
        
        // Check if we have loaded something
        if (modelImporter.getImport().length > 0) {
            
            // Getting model from the model importer
            this.model = modelImporter.getImport()[0];
            
            InputStream textureInputStream = this.getClass().getResourceAsStream(textureFilename);
            
            if (textureInputStream != null) {
                
                // Loading texture & setting diffuse map of the model material
                this.material.setDiffuseMap(new Image(textureInputStream));
                
            } else {
                
                LOG.warning("Could not load " + textureFilename);
            }
            
            // Setting model material
            this.model.setMaterial(this.material);
        }
        else {
            
            // Log error
            LOG.info("Could not load " + ROBOT_MODEL_FILE);
            return;
        }
        
        this.model.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                
                // Setting selected state
                setSelected(!isSelected);
            }
            
        });
        
        // Setting not selected
        this.isSelected = false;
        this.setSelected(this.isSelected);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        
        // Create a model group
        final Group modelGroup = new Group();
        
        // Add models to model group
        modelGroup.getChildren().add(this.model);
        modelGroup.getChildren().add(this.selectionArcMesh);
        
        // Add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().addAll(modelGroup));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long timeDivNano) {
        
        // Checking if we have got valid data
        if (visionRobotModel != null && visionRobotModel.getPosition() != null) {
            
            // Setting location
            this.location.x = (float) visionRobotModel.getPosition().getX();
            this.location.y = (float) (Robot.ROBOT_HEIGHT / 2.0f);
            this.location.z = (float) visionRobotModel.getPosition().getY();
        }
        
        // Translate to location
        this.model.setTranslateX(this.location.x);
        this.model.setTranslateY(this.location.y);
        this.model.setTranslateZ(this.location.z);
        
        // Translate selection circle to location
        this.selectionArcMesh.setTranslateX(this.location.x);
        this.selectionArcMesh.setTranslateY(this.location.y);
        this.selectionArcMesh.setTranslateZ(this.location.z);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }
    
    /**
     * Gets the location of the robot.
     * 
     * @return Returns the location of the robot as a {@link Vector3f}.
     */
    public Vector3f getLocation() {
        return this.location;
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
     * Sets the location of the robot.
     * 
     * @param location
     *            The new location of the robot as {@link Vector3f}.
     */
    public void setLocation(final Vector3f location) {
        this.location = location;
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
    
}
