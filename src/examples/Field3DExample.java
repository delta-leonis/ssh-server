package examples;

import org.ssh.field3d.FieldGame;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Field3DExample extends Application {
    
    public static void main(final String[] args) {
        
        Application.launch(args);
    }
    
    private FieldGame _fieldGame;
    private Label     _label;
    private Scene     _scene;
    private Group     _rootSceneGroup;
                      
    private Group     _rootFieldSceneGroup;
                      
    @Override
    public void start(final Stage stage) throws Exception {
        
        // Create groups
        this._rootSceneGroup = new Group();
        this._rootFieldSceneGroup = new Group();
        
        // Create main scene
        this._scene = new Scene(this._rootSceneGroup, 1600, 900);
        // Create field game (sub scene)
        this._fieldGame = new FieldGame(this._rootFieldSceneGroup, 1600, 900, SceneAntialiasing.BALANCED);
        // _fieldScene = new FieldScene(_scene, _rootFieldSceneGroup, 1600, 900, true,
        // SceneAntialiasing.DISABLED);
        
        // Create label
        this._label = new Label("Primary Scene");
        
        // Add label to root scene
        this._rootSceneGroup.getChildren().add(this._label);
        // Add field scene
        this._rootSceneGroup.getChildren().add(this._fieldGame);
        // _rootSceneGroup.getChildren().add(_fieldScene);
        
        // Initialize game
        this._fieldGame.InternalInitialize();
        
        // Setting scene
        stage.setScene(this._scene);
        // Show stage
        stage.show();
    }
}
