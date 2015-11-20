package org.ssh.field3d.gameobjects.overlay.contextmenus;

import java.util.List;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.overlay.OverlayGO;
import org.ssh.models.Robot;

import javafx.event.ActionEvent;
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
    
    /** The FXML file for the layout. */
    private static final String LAYOUT_FXML_FILE = "contextoverlay.fxml";
                                                 
    /** The container pane. */
    @FXML
    private Pane                containerPane;
                                
    /** The field location. */
    private Point2D             fieldLoc;
                                
    /** The list of robots. */
    private List<Robot>         robots;
                                
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     * @param robots
     *            The {@link List} of robots active in the game.
     */
    public ContextOverlayGO(Game game, List<Robot> robots) {
        
        // Initialize super class
        super(game, LAYOUT_FXML_FILE);
        
        // Creating new 2d point for the location clicked on the field
        fieldLoc = new Point2D(0.0, 0.0);
        
        // Setting robots
        this.robots = robots;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Hide
        this.hide();
        
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
                
        // Or the individual checks to get the result
        return xCheck || yCheck;
    }
    
    /**
     * Sets the field location.
     *
     * @param fieldLoc
     *            The new field location.
     */
    public void setFieldLoc(Point2D fieldLoc) {
        
        this.fieldLoc = fieldLoc;
    }
    
    /**
     * Gets the field location.
     *
     * @return The field location.
     */
    public Point2D getFieldLoc() {
        
        return this.fieldLoc;
    }
    
    /**
     * On move here button click event.
     *
     * @param actionEvent
     *            The {@link ActionEvent action event}.
     */
    @FXML
    protected void onMoveHereButtonClick(ActionEvent actionEvent) {
        
        // Check if robot not null
        if (this.robots != null) {
            
            // Loop through robot
            for (Robot robot : this.robots) {
                
                // If the robot is selected, move
                if (robot.isSelected()) {
                    
                    // TODO: send pipeline packet to update the position of the robot
                    
                    // Update robot position
                    robot.update("position", this.fieldLoc);
                    // No more work to do
                    break;
                }
            }
        }
        
        // Hide
        this.hide();
    }
    
    /**
     * On change sides button click event.
     *
     * @param actionEvent
     *            The {@link ActionEvent action event}.
     */
    @FXML
    protected void onChangeSidesButtonClick(ActionEvent actionEvent) {
        
        // TODO: send pipeline packet to update the sides
        
        // Hide
        this.hide();
    }
}
