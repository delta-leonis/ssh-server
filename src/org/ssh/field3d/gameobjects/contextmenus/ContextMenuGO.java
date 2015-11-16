package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.math.Xform;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * ContextMenuGO class. This class is the base class for a 3d context menu.
 *
 * @see GameObject
 *      
 * @author Mark Lefering
 */
public class ContextMenuGO extends GameObject {
    
    /** The minimal scalar. */
    private static final double MIN_SCALAR    = 0.14;
                                              
    /** The scalar of the scalar. */
    private static final int    SCALAR_SCALAR = 5;
                                              
    /** The rectangle for the background. */
    private final Rectangle     rectangle;
                                
    /** The transformations. */
    private final Xform         xform, xform2, xform3;
                                
    /** The group for the entire context menu. */
    private final Group         contextMenuGroup;
                                
    /** The controls group. */
    private final Group         controlsGroup;
                                
    /** The width of the context menu. */
    private final double        width;
                                
    /** The height of the context menu. */
    private final double        height;
                                
    /** The location of the context menu */
    private Vector3f            location;
                                
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}
     * @param width
     *            The width of the context menu.
     * @param height
     *            The height of the context menu.
     */
    public ContextMenuGO(final Game game, final double width, final double height) {
        
        // Initialize super class
        super(game);
        
        // Creating Xforms
        this.xform = new Xform();
        this.xform2 = new Xform();
        this.xform3 = new Xform();
        
        // Creating groups
        this.contextMenuGroup = new Group();
        this.controlsGroup = new Group();
        
        // Creating new vector for the location of the context menu
        this.location = new Vector3f();
        
        // Setting dimensions
        this.width = width;
        this.height = height;
        
        // Create rectangle for the background
        this.rectangle = new Rectangle(width, height);
        this.rectangle.setFill(new Color(0.0, 0.0, 1.0, 0.75));
        // Translate rectangle a bit backward
        this.rectangle.setTranslateZ(10);
        
        // Adding xforms to each other
        this.xform.getChildren().add(this.xform2);
        this.xform2.getChildren().add(this.xform3);
        this.xform3.getChildren().add(this.contextMenuGroup);
        // Setting controls group as child of the context menu group
        this.contextMenuGroup.getChildren().add(this.controlsGroup);
        // Add rectangle to the controls group
        this.controlsGroup.getChildren().add(this.rectangle);
        
        // Rotate and flip (so the controls are facing the right direction)
        this.xform3.rotationX.setAngle(180);
        this.xform3.rotationY.setAngle(180);
        
        // Setting not visible
        this.contextMenuGroup.setVisible(false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Add context menu to world
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(this.xform));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
        // TODO: remove magic numbers
        // Calculate scale of the context menu
        double scale = 1 - ((this.getGame().getMouseInputHandler().getScrollWheelYValue() + 1000.0) / 2000.0);
        scale += MIN_SCALAR; // Add minimal scale
        
        // Rotate towards camera
        this.rotate(this.getGame().getThirdPersonCamera().getRotateX(),
                this.getGame().getThirdPersonCamera().getRotateY(),
                0.0);
                
        // Translate to location
        this.xform.setTranslate(this.location.x,
                this.location.y + (scale * SCALAR_SCALAR * (this.height / 2.0)),
                this.location.z);
        // Scale
        this.xform3.setScale(scale * SCALAR_SCALAR, scale * SCALAR_SCALAR, scale * SCALAR_SCALAR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if we need to remove the context menu from the world group
        if (this.getGame().getWorldGroup().getChildren().contains(this.xform)) {
            
            // Remove context menu from the world group
            Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().remove((this.xform)));
        }
    }
    
    /**
     * This method hides the context menu.
     */
    public void hide() {
        this.contextMenuGroup.setVisible(false);
    }
    
    /**
     * This method shows the context menu.
     */
    public void show() {
        this.contextMenuGroup.setVisible(true);
    }
    
    /**
     * This method rotates the context menu.
     * 
     * @param x
     *            The angle to rotate around the x-axis.
     * @param y
     *            The angle to rotate around the y-axis.
     * @param z
     *            The angle to rotate around the z-axis.
     */
    public void rotate(final double x, final double y, final double z) {
        
        this.xform2.setRotate(x, y, z);
    }
    
    /**
     * This method translates the context menu.
     * 
     * @param x
     *            The x-coordinate to translate to.
     * @param y
     *            The y-coordinate to translate to.
     * @param z
     *            The z-coordinate to translate to.
     */
    public void translate(final double x, final double y, final double z) {
        
        this.xform.setTranslate(x, y, z);
        this.location = new Vector3f((float) x, (float) y, (float) z);
    }
    
    /**
     * Gets the controls group.
     *
     * @return The controls {@link Group}.
     */
    protected Group getControlsGroup() {
        return this.controlsGroup;
    }
    
    /**
     * Gets the context menu group.
     *
     * @return The context menu {@link Group}.
     */
    protected Group getGroup() {
        return this.contextMenuGroup;
    }
    
    /**
     * Gets the height of the context menu.
     *
     * @return The height of the context menu.
     */
    public double getHeight() {
        return this.height;
    }
    
    /**
     * Gets the width of the context menu.
     *
     * @return The width of the context menu.
     */
    public double getWidth() {
        return this.width;
    }
}
