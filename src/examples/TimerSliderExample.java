package examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.components.timerslider.TimerPane;

/**
 * Example that uses the {@link TimerPane} and {@link TimerSlider}
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class TimerSliderExample extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        Label testLabel = new Label("Kippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\n");
        testLabel.setStyle("-fx-background-color: orange;");
        Scene scene = new Scene(stackPane);
        stage.setTitle("Game Logs");
        stage.setWidth(600);
        stage.setHeight(430);
        
        TimerPane timerPane = new TimerPane();
        borderPane.setBottom(timerPane);
        
        stackPane.getChildren().addAll(testLabel,borderPane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}