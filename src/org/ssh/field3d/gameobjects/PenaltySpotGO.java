package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;


// TODO: Javadoc, cleanup, comment
public class PenaltySpotGO extends GameObject {
	
	private Vector3f _location;
	private Circle _model;
	
	private double _radius;

	
	public PenaltySpotGO(Game game, Vector3f location, double radius) {
		
		super(game);
		
		// Creating new circle
		_model = new Circle(radius);
		
		// Setting values
		_location = location;
		_radius = radius;
		
		// Rotate 90 deg around x-axis so it is flat on the ground
		_model.setRotationAxis(Rotate.X_AXIS);
		_model.setRotate(90);
		
		// Set color
		_model.setFill(Color.WHITE);	
	
		// Translate to location
		_model.setTranslateX(location.x);
		_model.setTranslateY(location.y);
		_model.setTranslateZ(location.z);
	}

	@Override
	public void Initialize() {
	
		// Add org.ssh.models to the world group
		GetGame().GetWorldGroup().getChildren().add(_model);
	}

	@Override
	public void Update(long timeDivNano) { }

	@Override
	public void Destroy() {

		// Check if org.ssh.models is in the world
		if (GetGame().GetWorldGroup().getChildren().contains(_model)) {
			
			// Remove org.ssh.models from world
			GetGame().GetWorldGroup().getChildren().remove(_model);
		}
	}
	
	
	public Vector3f GetLocation() { return _location; }
	public double GetRadius() { return _radius; }
	
	
	public void SetLocation(Vector3f location) {
		
		_location = location;
		
		_model.setTranslateX(_location.x);
		_model.setTranslateY(_location.y);
		_model.setTranslateZ(_location.z);
	}
	
	public void SetRadius(double radius) {
		
		_radius = radius;
		
		_model.setRadius(radius);
	}
}
