package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

/**
 * PenaltySpotGO class. This is the class for a penalty spot game object. It represents a penalty
 * spot on the 3d field.
 * 
 * @see GameObject
 *     
 * @author Mark Lefering
 */
public class PenaltySpotGO extends GameObject {
    
    /** The location of the penalty spot. */
    private Vector3f     location;
                         
    /** The model (a circle). */
    private final Circle model;
                         
    /** The radius of the penalty spot. */
    private double       radius;
                         
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     * @param location
     *            The location of the penalty spot as {@link Vector3f}.
     * @param radius
     *            The radius of the penalty spot.
     */
    public PenaltySpotGO(final Game game, final Vector3f location, final double radius) {
        
        // Initialize super class
        super(game);
        
        // Creating new circle
        this.model = new Circle(radius);
        
        // Setting values
        this.location = location;
        this.radius = radius;
        
        // Rotate 90 deg around x-axis so it is flat on the ground
        this.model.setRotationAxis(Rotate.X_AXIS);
        this.model.setRotate(90);
        
        // Set color
        this.model.setFill(Color.WHITE);
        
        // Translate to location
        this.model.setTranslateX(location.x);
        this.model.setTranslateY(location.y);
        this.model.setTranslateZ(location.z);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Add model to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(this.model));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if model in the world
        if (this.getGame().getWorldGroup().getChildren().contains(this.model)) {
            
            // Remove models from world
            this.getGame().getWorldGroup().getChildren().remove(this.model);
        }
    }
    
    /**
     * Gets the location of the penalty spot.
     *
     * @return Returns a {@link Vector3f} containing the location of the penalty spot.
     */
    public Vector3f getLocation() {
        
        return this.location;
    }
    
    /**
     * Gets the radius of the penalty spot.
     *
     * @return Returns a double containing the radius of the penalty spot.
     */
    public double getRadius() {
        
        return this.radius;
    }
    
    /**
     * SetLocation method. This method sets the location of the model.
     * 
     * @param location
     *            The {@link Vector3f} of the location of the penalty spot.
     */
    public void setLocation(final Vector3f location) {
        
        // Setting location
        this.location = location;
        
        // Translate model to location
        this.model.setTranslateX(this.location.x);
        this.model.setTranslateY(this.location.y);
        this.model.setTranslateZ(this.location.z);
    }
    
    /**
     * SetRadius method. This method sets the radius of the model.
     * 
     * @param radius
     *            The radius of the model as double.
     */
    public void setRadius(final double radius) {
        
        // Setting radius
        this.radius = radius;
        
        // Setting radius of the model
        this.model.setRadius(radius);
    }
    
}
