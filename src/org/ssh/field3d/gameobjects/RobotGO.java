package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.Arc3D;
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
 * 
 * RobotGO This class is for robot game objects. This class loads the 3d model, textures and the
 * selection arc.
 * 
 * @see GameObject
 *      
 * @author marklef2
 *         
 */
public class RobotGO extends GameObject {
    
    // TODO: MOVE public static to config, or get from model
    public static final double  ROBOT_HEIGHT                     = 200.0;
    public static final double  ROBOT_RADIUS                     = 250.0;
    public static final double  ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0;
    public static final float   ROBOT_STARTING_Y                 = 10.0f;
    public static final int     ROBOT_NUM_OF_SEGMENTS            = 100;
    public static final String  ROBOT_MODEL_FILE                 = "./assets/models/robot_model.obj";
    public static final String  ROBOT_TEXTURE_FILE               = "./assets/textures/robotTextureTest2.png";
                                                                 
    private static final Logger LOG                              = Logger.getLogger("RobotGameObject");
                                                                 
    private final PhongMaterial material;
    private final PhongMaterial selectionCircleMaterial;
    private final Arc3D         selectionArc;
    private final MeshView      selectionArcMesh;
                                
    private MeshView            model;
    private Vector3f            location;
                                
    private boolean             isSelected;
                                
    /**
     * 
     * Constructor of {@link RobotGO }.
     * 
     * @param game
     *            The robot's {@link Game }.
     */
    public RobotGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating model importer
        final ObjModelImporter modelImporter = new ObjModelImporter();
        
        // Create new location vector
        this.location = new Vector3f(0.0f, ROBOT_STARTING_Y, 0.0f);
        
        // Creating selection circle
        this.selectionArc = new Arc3D(0.0,
                360.0,
                RobotGO.ROBOT_RADIUS,
                RobotGO.ROBOT_SELECTION_CIRCLE_THICKNESS,
                ROBOT_NUM_OF_SEGMENTS);
                
        // Creating PhongMaterials
        this.material = new PhongMaterial(Color.WHITE);
        this.selectionCircleMaterial = new PhongMaterial();
        
        // Getting arc mesh
        this.selectionArcMesh = this.selectionArc.MeshView();
        // Rotate 90 degrees around x-axis
        this.selectionArcMesh.setRotationAxis(Rotate.X_AXIS);
        this.selectionArcMesh.setRotate(90.0);
        
        // Setting selection circle diffuse & specular color to Blue
        this.selectionCircleMaterial.setDiffuseColor(Color.BLUE);
        this.selectionCircleMaterial.setSpecularColor(Color.BLUE);
        // Setting specular power
        this.selectionCircleMaterial.setSpecularPower(20.0);
        // Setting selection circle material
        this.selectionArcMesh.setMaterial(this.selectionCircleMaterial);
        
        // Read model into model importer
        modelImporter.read(ROBOT_MODEL_FILE);
        
        // Check if we have loaded something
        if (modelImporter.getImport().length > 0) {
            
            // Getting model from the model importer
            this.model = modelImporter.getImport()[0];
            
            try {
                
                // Loading texture & setting diffuse map of the model material
                this.material.setDiffuseMap(new Image(new FileInputStream(ROBOT_TEXTURE_FILE)));
            }
            catch (final FileNotFoundException fileNotFoundException) {
                
                // TODO: log exception
                // Log error
                LOG.info("Could not load " + ROBOT_TEXTURE_FILE);
                LOG.finer(fileNotFoundException.getStackTrace().toString());
                return;
            }
            
            // Setting model material
            this.model.setMaterial(this.material);
        }
        else {
            
            // Log error
            LOG.info("Could not load " + ROBOT_MODEL_FILE);
            return;
        }
        
        // Setting not selected
        this.isSelected = false;
        this.setSelected(this.isSelected);
    }
    
    /**
     * Initialize method, overridden from {@link GameObject}.
     */
    @Override
    public void initialize() {
        
        // Create a org.ssh.models group
        final Group modelGroup = new Group();
        
        // Add our org.ssh.models to the org.ssh.models group
        modelGroup.getChildren().add(this.model);
        modelGroup.getChildren().add(this.selectionArcMesh);
        
        // Add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().addAll(modelGroup));
    }
    
    /**
     * Update method, overridden from {@link GameObject}
     * 
     * @param timeDivNano
     *            The time difference in nanoseconds
     */
    @Override
    public void update(final long timeDivNano) {
        
        // TODO: Update location according to the vision model
        
        // Translate to location
        this.model.setTranslateX(this.location.x);
        this.model.setTranslateY(this.location.y);
        this.model.setTranslateZ(this.location.z);
        
        // Translate selection circle to location
        this.selectionArcMesh.setTranslateX(this.location.x);
        this.selectionArcMesh.setTranslateY(this.location.y - (model.getBoundsInLocal().getHeight() / 1.8));
        this.selectionArcMesh.setTranslateZ(this.location.z);
    }
    
    /**
     * Destroy method, overridden from {@link GameObject}.
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
     * @return Returns the selected state of the robot as boolean.
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
