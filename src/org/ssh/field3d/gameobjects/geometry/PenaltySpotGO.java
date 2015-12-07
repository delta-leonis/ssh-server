package org.ssh.field3d.gameobjects.geometry;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.GeometryGameObject;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
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
public class PenaltySpotGO extends GeometryGameObject {
    
    /** The location of the penalty spot. */
    private Point3D     location;
                         
    /** The model (a circle). */
    private final Circle model;
    
    /** The group for the field */
    private final Group fieldGroup;
                         
    /** The radius of the penalty spot. */
    private double       radius;
                         
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     * @param location
     *            The {@link Point3D location} of the penalty spot.
     * @param radius
     *            The radius of the penalty spot.
     */
    public PenaltySpotGO(final Game game, final Point3D location, final double radius, Group fieldGroup) {
        
        // Initialize super class
        super(game);
        
        // Creating new circle
        this.model = new Circle(radius);
        
        // Setting values
        this.location = location;
        this.radius = radius;
        this.fieldGroup = fieldGroup;
        
        // Rotate 90 deg around x-axis so it is flat on the ground
        this.model.setRotationAxis(Rotate.X_AXIS);
        this.model.setRotate(90);
        
        // Set color
        this.model.setFill(Color.WHITE);
        
        // Translate to location
        this.model.setTranslateX(location.getX());
        this.model.setTranslateY(location.getY());
        this.model.setTranslateZ(location.getZ());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Add model to the world group
        Platform.runLater(() -> {
            
            // If the world group does not contain the 3D model
            if (!this.fieldGroup.getChildren().contains(this.model)) {
                
                // Add 3d model to the world group
                this.fieldGroup.getChildren().add(this.model);
            }
        });
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Remove models from world
        Platform.runLater(() -> {
            
            // Check if the world group contains the 3d model
            if (this.fieldGroup.getChildren().contains(this.model)) {
                
                // Remover 3d model from the world group
                this.fieldGroup.getChildren().remove(this.model);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateGeometry() {
    }
    
    /**
     * Gets the location of the penalty spot.
     *
     * @return Returns a {@link Point3D} containing the location of the penalty spot.
     */
    public Point3D getLocation() {
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
     *            The {@link Point3D} of the location of the penalty spot.
     */
    public void setLocation(final Point3D location) {
        
        // Setting location
        this.location = location;
        
        // Translate model to location
        this.model.setTranslateX(this.location.getX());
        this.model.setTranslateY(this.location.getY());
        this.model.setTranslateZ(this.location.getZ());
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
