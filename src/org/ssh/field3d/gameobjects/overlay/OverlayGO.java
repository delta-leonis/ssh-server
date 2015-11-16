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
public class OverlayGO extends GameObject {
    
    // TODO Move resources
    /** The FXML directory. */
    private static final String FXML_DIR = "/org/ssh/view/components/overlay";
                                         
    /** The CSS directory. */
    private static final String CSS_DIR  = "/org/ssh/view/css/";
                                         
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
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(long timeDivNano) {
    }
    
    /**
     * Gets the container of the overlay.
     *
     * @return The container as {@link Group}.
     */
    protected Group getContainer() {
        
        return this.container;
    }
    
    /**
     * loadFXML method. This method loads a FXML file.
     *
     * @param fileName
     *            The filename.
     * @param container
     *            The container to put the nodes into.
     * @return true, if successful
     */
    protected boolean loadFXML(String fileName, Group container) {
        
        URL url = this.getClass().getResource(FXML_DIR + fileName);
        
        final FXMLLoader fxmlLoader = new FXMLLoader(url);
        
        // Check if url not null
        if (url == null) {
            
            // Log error
            LOG.warning("Couldn't load, url == null: " + FXML_DIR + fileName);
            
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
            
            return false;
        }
        
        return true;
    }
}
