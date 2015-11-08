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
 *
 * ContextMenuGO class. This class is the base class for a 3d context menu.
 *
 * @see GameObject
 *      
 * @author Mark Lefering
 */
public class ContextMenuGO extends GameObject {
    
    private static final double MIN_SCALAR    = 0.14;
    private static final int    SCALAR_SCALAR = 5;
                                              
    private final Rectangle     rectangle;
    private final Xform         xform, xform2, xform3;
    private final Group         contextMenuGroup, controlsGroup;
    private final double        width, height;
                                
    private Vector3f            loc;
                                
    /**
     * Constructor
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
        this.loc = new Vector3f();
        
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
        this.xform3.rx.setAngle(180);
        this.xform3.ry.setAngle(180);
        
        // Setting not visible
        this.contextMenuGroup.setVisible(false);
    }
    
    /**
     * Initialize method. This method adds the context menu to the world group.
     */
    @Override
    public void Initialize() {
        
        // Add context menu to world
        Platform.runLater(() -> this.GetGame().getWorldGroup().getChildren().add(this.xform));
    }
    
    /**
     * Update method. This method rotates the context menu towards the camera and also scale it
     * according to the zoom of the camera.
     * 
     * @param timeDivNano
     *            The time difference in nanoseconds.
     */
    @Override
    public void Update(final long timeDivNano) {
        
        // TODO: remove magic numbers
        // Calculate scale of the context menu
        double scale = 1 - ((this.GetGame().getMouseInputHandler().GetScrollWheelYValue() + 1000.0) / 2000.0);
        scale += MIN_SCALAR; // Add minimal scale
        
        // Rotate towards camera
        this.rotate(this.GetGame().getThirdPersonCamera().GetRotateX(),
                this.GetGame().getThirdPersonCamera().GetRotateY(),
                0.0);
                
        // Translate to location
        this.xform.setTranslate(this.loc.x, this.loc.y + (scale * SCALAR_SCALAR * (this.height / 2.0)), this.loc.z);
        // Scale
        this.xform3.setScale(scale * SCALAR_SCALAR, scale * SCALAR_SCALAR, scale * SCALAR_SCALAR);
    }
    
    /**
     * Destroy method
     */
    @Override
    public void Destroy() {
        
        // Check if we need to remove the context menu from the world group
        if (this.GetGame().getWorldGroup().getChildren().contains(this.xform)) {
            
            // Remove context menu from the world group
            Platform.runLater(() -> this.GetGame().getWorldGroup().getChildren().remove((this.xform)));
        }
    }
    
    /**
     * This method hides the context menu.
     */
    public void hide() {
        this.contextMenuGroup.setVisible(false);
    }
    
    /**
     * This method shows the context menu
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
        this.loc = new Vector3f((float) x, (float) y, (float) z);
    }
    
    protected Group getControlsGroup() {
        return this.controlsGroup;
    }
    
    protected Group getGroup() {
        return this.contextMenuGroup;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public double getWidth() {
        return this.width;
    }
}
