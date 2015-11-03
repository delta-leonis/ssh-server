package field3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Main extends Application {
	
	private FieldGame _fieldGame;
	
	
	private Label _label;		
	private Scene _scene;
	private Group _rootSceneGroup;
	private Group _rootFieldSceneGroup;
	
	
	public static void main(String[] args) {
		
		launch(args);
	}

	
	@Override
	public void start(Stage stage) throws Exception {
		
		// Create groups
		_rootSceneGroup = new Group();
		_rootFieldSceneGroup = new Group();
		
		// Create main scene
		_scene = new Scene(_rootSceneGroup, 1600, 900);
		// Create field game (sub scene)
		_fieldGame = new FieldGame(_rootFieldSceneGroup, 1600, 900, SceneAntialiasing.BALANCED);	
		//_fieldScene = new FieldScene(_scene, _rootFieldSceneGroup, 1600, 900, true, SceneAntialiasing.DISABLED);
		
		// Create label
		_label = new Label("Primary Scene");
		
		
		
		
		// Add label to root scene
		_rootSceneGroup.getChildren().add(_label);		
		// Add field scene
		_rootSceneGroup.getChildren().add(_fieldGame);
		// _rootSceneGroup.getChildren().add(_fieldScene);
				
		
		// Initialize game
		_fieldGame.InternalInitialize();
		
		// Setting scene
		stage.setScene(_scene);
		// Show stage
		stage.show();
	}
}
