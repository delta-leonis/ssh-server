package view.components.field.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import view.components.field.core.game.Game;
import view.components.field.core.gameobjects.GameObject;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;


// TODO: Javadoc, cleanup & comment
public class SkyboxGO extends GameObject {
	
	
	private Sphere _model;
	private PhongMaterial _blueMaterial;

	
	public SkyboxGO(Game game){
		super(game);
		
		FileInputStream fs = null;
		
		try {
			
			fs = new FileInputStream("./assets/textures/skybox.png");
			
		} catch (FileNotFoundException e) {
			
			// TODO: Logger handling
			e.printStackTrace();
		}
	
		
		_model = new Sphere(300000);
		

		_blueMaterial = new PhongMaterial(Color.WHITE);
		_blueMaterial.setDiffuseMap(new Image(fs));
		
		_model.setMaterial(_blueMaterial);
		_model.setCullFace(CullFace.NONE);
		
		_model.setRotationAxis(Rotate.X_AXIS);
		_model.setRotate(180);
	}
	
	

	@Override
	public void Initialize() {

		// Add model to the world group
		GetGame().GetWorldGroup().getChildren().add(_model);
	}

	@Override
	public void Update(long timeDivNano) { }
	
	@Override
	public void Destroy() { 
		
		// Check if model is in the world group
		if (GetGame().GetWorldGroup().getChildren().contains(_model)) {
			
			// Remove from world
			GetGame().GetWorldGroup().getChildren().remove(_model);
		}
	}
}
