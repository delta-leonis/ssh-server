package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

// TODO: Javadoc, cleanup & comment
public class SkyboxGO extends GameObject {
    
    private final Sphere        _model;
    private final PhongMaterial _blueMaterial;
                                
    public SkyboxGO(final Game game) {
        super(game);
        
        FileInputStream fs = null;
        
        try {
            
            fs = new FileInputStream("./assets/textures/skybox.jpg");
            
        }
        catch (final FileNotFoundException e) {
            
            // TODO: Logger handling
            e.printStackTrace();
        }
        
        this._model = new Sphere(300000);
        
        this._blueMaterial = new PhongMaterial(Color.WHITE);
        this._blueMaterial.setDiffuseMap(new Image(fs));
        
        this._model.setMaterial(this._blueMaterial);
        this._model.setCullFace(CullFace.NONE);
        
        this._model.setRotationAxis(Rotate.X_AXIS);
        this._model.setRotate(180);
    }
    
    @Override
    public void Destroy() {
        
        // Check if org.ssh.models is in the world group
        if (this.GetGame().GetWorldGroup().getChildren().contains(this._model)) {
            
            // Remove from world
            this.GetGame().GetWorldGroup().getChildren().remove(this._model);
        }
    }
    
    @Override
    public void Initialize() {
        
        // Add org.ssh.models to the world group
        this.GetGame().GetWorldGroup().getChildren().add(this._model);
    }
    
    @Override
    public void Update(final long timeDivNano) {
    }
}
