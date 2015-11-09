package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.util.Logger;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.application.Platform;
import javafx.scene.Group;

/**
 * CarGO class. This class represents the easter egg car. It shows when the right mouse button is
 * clicked.
 * 
 * @see GameObject
 *      
 * @author Mark Lefering
 */
public class CarGO extends GameObject {
    
    private static final Logger LOG            = Logger.getLogger("CarGO");
    private static final String CAR_MODEL_FILE = "./assets/cars/Avent2.obj";
                                               
    private Group               modelGroup;
                                
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public CarGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating new model importer
        final ObjModelImporter modelImporter = new ObjModelImporter();
        // Creating new group for the model
        this.modelGroup = new Group();
        
        // Read model
        modelImporter.read(CAR_MODEL_FILE);
        
        // Check if the model importer read something
        if (modelImporter.getImport().length > 0) {
            
            // Loop through imports
            for (int i = 0; i < modelImporter.getImport().length; i++) {
                
                // Adding imports to the model group
                this.modelGroup.getChildren().add(modelImporter.getImport()[i]);
            }
        }
        else {
            
            // Log error
            LOG.info("Could not load: " + CAR_MODEL_FILE);
        }
    }
    
    /**
     * Initialize method. This method adds the model group to the world.
     */
    @Override
    public void initialize() {
        
        // Add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(this.modelGroup));
    }
    
    /**
     * Update method. This method check if the car should be displayed or not.
     * 
     * @param timeDivNano
     *            The time difference in nanoseconds.
     */
    @Override
    public void update(final long timeDivNano) {
        
        // Check if right mouse button is down
        if (this.getGame().getMouseInputHandler().isRightButtonDown()) {
            
            // Set model group to be visible
            this.modelGroup.setVisible(true);
        }
        else {
            
            // Set model group to be not visible
            this.modelGroup.setVisible(false);
        }
        
    }
    
    /**
     * Destroy method. This method removes the model group from the world group if needed.
     */
    @Override
    public void destroy() {
        
        // Check if we need to remove model group from the world group
        if (this.getGame().getWorldGroup().getChildren().contains(modelGroup)) {
            
            // Remove model group from the world group
            Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().remove(modelGroup));
        }
    }
}
