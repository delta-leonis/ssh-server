package org.ssh.field3d.gameobjects;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Platform;
import javafx.scene.Group;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.util.Logger;

/**
 * Car game object class. This class represents the easter egg car. It shows when the right mouse button is
 * held down.
 *
 * @see GameObject
 *      
 * @author Mark Lefering
 */

public class CarGO extends GameObject {

    /** The logger. */
    private static final Logger LOG            = Logger.getLogger("CarGO");

    /** The file name for the car model. */
    private static final String CAR_MODEL_FILE = "/org/ssh/view/3dmodels/cars/Avent2.obj";

    /** The model group. */
    private Group               modelGroup;

    /**
     * Constructor. Instantiates a new CarGO object.
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
        modelImporter.read(this.getClass().getResource(CAR_MODEL_FILE));
        
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
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Execute on UI thread; add model group to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(this.modelGroup));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
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
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {

        // Execute on UI thread
        Platform.runLater(() -> {

            // Check if we need to remove model group from the world group
            if (this.getGame().getWorldGroup().getChildren().contains(modelGroup)) {

                // Remove model group from the world group
                this.getGame().getWorldGroup().getChildren().remove(modelGroup);
            }
        });
    }
}
