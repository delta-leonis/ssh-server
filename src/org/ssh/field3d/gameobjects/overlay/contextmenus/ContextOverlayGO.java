package org.ssh.field3d.gameobjects.overlay.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.overlay.OverlayGO;

import javafx.application.Platform;
import javafx.scene.Group;

/**
 * ContextOverlayGO class. This class is responsible for the 2d context menu overlay.
 * 
 * @see GameObjects
 *      
 * @author marklef2
 */
public class ContextOverlayGO extends OverlayGO {
    
    /** The fxml file for the layout **/
    private static final String LAYOUT_FXML_FILE = "contextoverlay.fxml";
                                                 
    /** The group for the context menu */
    private Group               contextMenuGroup;
                                
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public ContextOverlayGO(Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating new group for the context menu
        contextMenuGroup = new Group();
        
        // Load fxml
        this.loadFXML(LAYOUT_FXML_FILE, contextMenuGroup);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
             
        // Adding context menu to the 2d group
        this.getGame().get2DGroup().getChildren().add(contextMenuGroup);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(long timeDivNano) {        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if the 2d group contains the context menu group
        if (this.getGame().get2DGroup().getChildren().contains(contextMenuGroup)) {
            
           this.getGame().get2DGroup().getChildren().remove(contextMenuGroup);
        }
    }
}
