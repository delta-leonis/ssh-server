package org.ssh.field3d.gameobjects.overlay.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.overlay.OverlayGO;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * ContextOverlayGO class. This class is responsible for the 2d context menu overlay.
 *
 * @author marklef2
 * @see GameObjects
 *      
 */
public class ContextOverlayGO extends OverlayGO {
    
    /** The FXML file for the layout *. */
    private static final String LAYOUT_FXML_FILE = "contextoverlay.fxml";
                                                 
    /** The menu label. */
    @FXML
    private Label               menuLabel;
                                
    /** The container pane. */
    @FXML
    private Pane                containerPane;
                                
    /**
     * Constructor.
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
        
        // If the left mouse button is being pressed
        if (this.getGame().getMouseInputHandler().isLeftButtonPressing()) {
            
            // Calculate scene location
            Point2D sceneLoc = this.getGame().sceneToLocal(this.getGame().getMouseInputHandler().getMouseX(),
                    this.getGame().getMouseInputHandler().getMouseY());
                    
            // Check if the click was outside of the context menu
            if (isNotClicked(sceneLoc)) {
                
                // Hide context menu if needed
                if (this.isVisible()) this.hide();
            }
        }
        
        // If the middle mouse button is being pressed
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        
        // Calculating scene location
        Point2D sceneLoc = this.getGame().sceneToLocal(this.getGame().getMouseInputHandler().getMouseX(),
                this.getGame().getMouseInputHandler().getMouseY());
                
        // Translate menu to location
        this.getContainer().setTranslateX(sceneLoc.getX());
        this.getContainer().setTranslateY(sceneLoc.getY());
        
        // Call show method of the super class
        super.show();
    }
    
    /**
     * Checks if the {@link Point2D mouse scene location} is inside of the context menu.
     *
     * @param mouseSceneLoc
     *            The scene location of the mouse.
     * @return True, if the point is outside of the context menu.
     */
    private boolean isNotClicked(Point2D mouseSceneLoc) {
        
        // Check if x is out of the context menu's x bounds
        boolean xCheck = mouseSceneLoc.getX() < this.getContainer().getTranslateX() || mouseSceneLoc
                .getX() > this.getContainer().getTranslateX() + this.containerPane.widthProperty().doubleValue();
                
        // Check if y is out of the context menu's y bounds
        boolean yCheck = mouseSceneLoc.getY() < this.getContainer().getTranslateY() || mouseSceneLoc
                .getY() > this.getContainer().getTranslateY() + this.containerPane.heightProperty().doubleValue();
                
        return xCheck || yCheck;
    }
}
