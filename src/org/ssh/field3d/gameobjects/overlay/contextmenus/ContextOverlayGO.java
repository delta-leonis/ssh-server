package org.ssh.field3d.gameobjects.overlay.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.overlay.OverlayGO;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * ContextOverlayGO class. This class is responsible for the 2d context menu overlay.
 * 
 * @see GameObjects
 *      
 * @author marklef2
 */
public class ContextOverlayGO extends OverlayGO {
    
    /** The FXML file for the layout **/
    private static final String LAYOUT_FXML_FILE = "contextoverlay.fxml";
                                                                                 
    @FXML
    private Label               menuLabel;
    
    @FXML
    private Pane                containerPane;
                                
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public ContextOverlayGO(Game game) {
        
        // Initialize super class
        super(game, LAYOUT_FXML_FILE);
        
        // Hide
        this.hide();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Call on initialize method of the super class
        super.onInitialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(long timeDivNano) {
        
        if (this.getGame().getMouseInputHandler().isRightButtonPressing()) {
            
            Point2D sceneLoc = this.getGame().sceneToLocal(this.getGame().getMouseInputHandler().getMouseX(),
                    this.getGame().getMouseInputHandler().getMouseY());
                    
            if (!this.isVisible()) {
                
                this.getContainer().setTranslateX(sceneLoc.getX());
                this.getContainer().setTranslateY(sceneLoc.getY());
            }
            
            // Show context menu
            this.toggleVisibility();
        }
        
        if (this.getGame().getMouseInputHandler().isLeftButtonPressing()) {
            
            Point2D locOnContext = this.getGame().sceneToLocal(
                    this.getGame().getMouseInputHandler().getMouseX(),
                    this.getGame().getMouseInputHandler().getMouseY());
                    
            if (locOnContext.getX() < this.getContainer().getTranslateX() || locOnContext.getX() > this.getContainer().getTranslateX() + this.containerPane.widthProperty().doubleValue()
                    || locOnContext.getY() < this.getContainer().getTranslateY() || locOnContext.getY() > this.getContainer().getTranslateY() + this.containerPane.heightProperty().doubleValue()) {
                
                // Hide context menu if needed
                if (this.isVisible()) this.hide();
            }
        }
        
        if (this.getGame().getMouseInputHandler().isMidButtonPressing()) {
            
            // Hide context menu if needed
            if (this.isVisible()) this.hide();
        }
        
        // Check if the any of the mouse wheel values has changed
        if (this.getGame().getMouseInputHandler().isMouseWheelXChanged()
                || this.getGame().getMouseInputHandler().isMouseWheelYChanged()) {
                
            // Hide context menu if needed
            if (this.isVisible()) this.hide();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Call on destroy method of the super class
        super.onDestroy();
    }
    
    @Override
    public void show() {
        
        this.getContainer().setTranslateX(this.getGame().getMouseInputHandler().getMouseX());
        this.getContainer().setTranslateY(this.getGame().getMouseInputHandler().getMouseY());
        
        super.show();
    }
}
