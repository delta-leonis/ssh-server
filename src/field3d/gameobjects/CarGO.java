package field3d.gameobjects;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.MeshView;

public class CarGO extends GameObject {
	
	MeshView model = null;
	Group group = null;

	public CarGO(Game game) {
		
		super(game);
		
		group = new Group();
		ObjModelImporter modelImporter = new ObjModelImporter();
		
		modelImporter.read("./assets/cars/Avent2.obj");
		
		if (modelImporter.getImport().length > 0) {
			
			//System.out.println(modelImporter.getImport().length);
			
			for (int i = 0; i < modelImporter.getImport().length; i++) {
				
				MeshView model = modelImporter.getImport()[i];
				group.getChildren().add(model);
			}			
		}
		
		
	}

	@Override
	public void Initialize() {
		// TODO Auto-generated method stub

		GetGame().GetWorldGroup().getChildren().add(group);
	}

	@Override
	public void Update(long timeDivNano) {
		// TODO Auto-generated method stub
		
		if (GetGame().GetMouseInputHandler().IsRightButtonDown()) {
						
			group.setVisible(true);
		} else {
			
			group.setVisible(false);
		}

	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub

	}

}
