package org.ssh.field3d.gameobjects.overlay;

import java.io.IOException;

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
    
    /** The FXML directory. */
    private static final String FXML_DIR = "/org/ssh/view/components/";
                                         
    /** The CSS directory. */
    private static final String CSS_DIR  = "/org/ssh/view/css/";
                                         
    /** The logger. */
    private static final Logger LOG      = Logger.getLogger();
                                         
    /**
     * Instantiates a new overlay game object.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public OverlayGO(Game game) {
        
        // Initializie super class
        super(game);
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
     * loadFXML method. This method loads a FXML file.
     *
     * @param filename
     *            The filename.
     * @param container
     *            The container to put the nodes into.
     */
    protected void loadFXML(String filename, Group container) {
        
        // Creating new fxml loader
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(FXML_DIR + filename));
        
        // Setting controller
        fxmlLoader.setController(this);
        
        try {
            
            // Load the fxml nodes into the container
            container.getChildren().add(fxmlLoader.load());
        }
        catch (IOException ioException) {
            
            // Log error
            LOG.warning("Couldn't load: " + FXML_DIR + filename);
            // Log exception
            LOG.exception(ioException);
        }
    }
}
