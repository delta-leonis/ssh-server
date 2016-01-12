package examples.field3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.models.*;
import org.ssh.models.enums.Allegiance;

import java.util.stream.IntStream;

/**
 * Created by marklef2 on 12/18/15.
 */
public class Full3DExample extends Application {

    private Group rootGroup;
    private Group fieldGameGroup;
    private Scene scene;

    public static void main(String[] args) {

        // Starting models
        Models.start();

        // Creating models
        Models.create(Game.class);
        Models.<Goal>create(Goal.class, Allegiance.ALLY);
        Models.<Goal>create(Goal.class, Allegiance.OPPONENT);
        Models.create(Team.class, Allegiance.ALLY);
        Models.create(Team.class, Allegiance.OPPONENT);
        Field field = Models.create(Field.class);
        Ball ball = Models.<Ball>create(Ball.class);

        // Getting field dimensions
        float fieldWidth = field.getFieldWidth();
        float fieldLength = field.getFieldLength();

        // Create some robots
        IntStream.range(0, 8).forEach(id -> {
            Models.create(Robot.class, id, Allegiance.OPPONENT)
                    .update("x", fieldLength / 2 - id * 200f - 180f, "y", -fieldWidth / 2 + 180f);
            Models.create(Robot.class, id, Allegiance.ALLY)
                    .update("x", id * 200f - fieldLength / 2 + 180f, "y", fieldWidth / 2 - 180f);

        });


        ball.update("x", 0,
                "y", 0,
                "z", 0);

        // Launch the program
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

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
