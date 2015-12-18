package examples.field3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;

/**
 * Created by marklef2 on 12/18/15.
 */
public class BareBone3DExample extends Application {

    private Group rootGroup;
    private Group fieldGameGroup;
    private Scene scene;

    public static void main(String[] args) {

        // Starting models
        Models.start();

        // Launch the program
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Creating groups
        rootGroup = new Group();
        fieldGameGroup = new Group();

        // Creating scene
        scene = new Scene(rootGroup, 800, 600);

        // Creating field game
        FieldGame fieldGame = new FieldGame(fieldGameGroup, 800, 600, SceneAntialiasing.BALANCED);

        // Add the field game to the root group
        rootGroup.getChildren().add(fieldGame);

        // Initialize field game
        fieldGame.internalInitialize();

        // Setting scene of the stage
        primaryStage.setScene(scene);
        // Show the stage
        primaryStage.show();
    }
}
