package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.scene.Group;
import javafx.scene.shape.MeshView;

public class CarGO extends GameObject {
    
    MeshView model = null;
    Group    group = null;
                   
    public CarGO(final Game game) {
        
        super(game);
        
        this.group = new Group();
        final ObjModelImporter modelImporter = new ObjModelImporter();
        
        modelImporter.read("./assets/cars/Avent2.obj");
        
        if (modelImporter.getImport().length > 0) {
            
            // System.out.println(modelImporter.getImport().length);
            
            for (int i = 0; i < modelImporter.getImport().length; i++) {
                
                final MeshView model = modelImporter.getImport()[i];
                this.group.getChildren().add(model);
            }
        }
        
    }
    
    @Override
    public void Destroy() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void Initialize() {
        // TODO Auto-generated method stub
        
        this.GetGame().GetWorldGroup().getChildren().add(this.group);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        // TODO Auto-generated method stub
        
        if (this.GetGame().GetMouseInputHandler().IsRightButtonDown()) {
            
            this.group.setVisible(true);
        }
        else {
            
            this.group.setVisible(false);
        }
        
    }
    
}
