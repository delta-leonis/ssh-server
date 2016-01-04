package org.ssh.field3d.gameobjects.overlay;

import java.io.IOException;
import java.net.URL;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.util.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;

/**
 * OverlayGO class. This is the base class for the 2d overlay components.
 *
 * @see GameObject
 *      
 * @author Mark Lefering
 */
public abstract class OverlayGO extends GameObject {
    
    /** The FXML directory. */
    private static final String FXML_DIR = "/org/ssh/view/components/centersection/overlay/";
                                         
    /** The logger. */
    private static final Logger LOG      = Logger.getLogger();
                                         
    /** The container for the overlay. */
    private Group               container;
                                
    /**
     * Instantiates a new overlay game object.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public OverlayGO(Game game) {
        
        // Initialize super class
        super(game);
        
        // Create container group
        container = new Group();
    }
    
    /**
     * Instantiates a new overlay go.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     * @param fxmlResourceFile
     *            The FXML resource file for the layout.
     */
    public OverlayGO(Game game, String fxmlResourceFile) {
        
        // Initialize super class
        super(game);
        
        // Create container group
        container = new Group();
        
        // Load fxml
        this.loadFXML(fxmlResourceFile, container);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if we need to remove the container from the 2d group
        if (this.getGame().get2DGroup().getChildren().contains(container)) {
            
            // Remove container from the 2d group
            this.getGame().get2DGroup().getChildren().remove(container);
        }
    }
    
    /**
     * {@inheritDoc} When inheriting from this class, and overriding this method a super call to
     * this method is needed.
     */
    @Override
    public void onInitialize() {
        
        // Add to 2d group
        this.getGame().get2DGroup().getChildren().add(container);
    }
    
    /**
     * {@inheritDoc} When inheriting from this class, and overriding this method a super call to
     * this method is needed.
     */
    @Override
    public void onUpdate(long timeDivNano) {
    }

    
    /**
     * Show method. This method makes the container visible.
     */
    public void show() {
        
        // Setting container to visible
        this.container.setVisible(true);
    }
    
    /**
     * Hide method. This method makes the container hidden.
     */
    public void hide() {
        
        // Hide container
        this.container.setVisible(false);
    }
    
    /**
     * Toggle visibility method. This method toggles the visibility of the container.
     */
    public void toggleVisibility() {
        
        // Toggle visibility
        this.container.setVisible(!this.container.isVisible());
    }
    
    /**
     * Checks if the container is visible.
     * 
     * @return True, if the container is visible.
     */
    public boolean isVisible() {
        
        // Returns the visible state of the container
        return this.container.isVisible();
    }
    
    /**
     * Gets the container of the overlay.
     *
     * @return The container as {@link Group}.
     */
    protected Group getContainer() {
        
        // Returns the container
        return this.container;
    }
    
    /**
     * loadFXML method. This method loads a FXML file.
     *
     * @param fileName
     *            The filename.
     * @param container
     *            The container to put the nodes into.
     * @return True, if the FXML was loaded successfully.
     */
    protected boolean loadFXML(String fileName, Group container) {
        
        // Creating URL
        URL url = this.getClass().getResource(FXML_DIR + fileName);
        
        // Creating FXML loader
        final FXMLLoader fxmlLoader = new FXMLLoader(url);
        
        // Check if URL not null
        if (url == null) {
            
            // Log error
            LOG.warning("Couldn't load, url == null: " + FXML_DIR + fileName);
            
            // Not loaded successfully
            return false;
        }
        
        // Setting controller
        fxmlLoader.setController(this);
        
        try {
            
            // Load the fxml nodes into the container
            container.getChildren().add(fxmlLoader.load());
        }
        catch (IOException ioException) {
            
            // Log error
            LOG.warning("Couldn't load: " + FXML_DIR + fileName);
            // Log exception
            LOG.exception(ioException);
            
            // Not loaded successfully
            return false;
        }
        
        // Loaded successfully
        return true;
    }
}
