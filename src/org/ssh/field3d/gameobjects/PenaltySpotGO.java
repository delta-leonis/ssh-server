package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

// TODO: Javadoc, cleanup, comment
public class PenaltySpotGO extends GameObject {
    
    private Vector3f     _location;
    private final Circle _model;
                         
    private double       _radius;
                         
    public PenaltySpotGO(final Game game, final Vector3f location, final double radius) {
        
        super(game);
        
        // Creating new circle
        this._model = new Circle(radius);
        
        // Setting values
        this._location = location;
        this._radius = radius;
        
        // Rotate 90 deg around x-axis so it is flat on the ground
        this._model.setRotationAxis(Rotate.X_AXIS);
        this._model.setRotate(90);
        
        // Set color
        this._model.setFill(Color.WHITE);
        
        // Translate to location
        this._model.setTranslateX(location.x);
        this._model.setTranslateY(location.y);
        this._model.setTranslateZ(location.z);
    }
    
    @Override
    public void Destroy() {
        
        // Check if org.ssh.models is in the world
        if (this.GetGame().GetWorldGroup().getChildren().contains(this._model)) {
            
            // Remove org.ssh.models from world
            this.GetGame().GetWorldGroup().getChildren().remove(this._model);
        }
    }
    
    public Vector3f GetLocation() {
        return this._location;
    }
    
    public double GetRadius() {
        return this._radius;
    }
    
    @Override
    public void Initialize() {
        
        // Add org.ssh.models to the world group
        this.GetGame().GetWorldGroup().getChildren().add(this._model);
    }
    
    public void SetLocation(final Vector3f location) {
        
        this._location = location;
        
        this._model.setTranslateX(this._location.x);
        this._model.setTranslateY(this._location.y);
        this._model.setTranslateZ(this._location.z);
    }
    
    public void SetRadius(final double radius) {
        
        this._radius = radius;
        
        this._model.setRadius(radius);
    }
    
    @Override
    public void Update(final long timeDivNano) {
    }
}
