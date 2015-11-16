package examples;

import org.ssh.ui.components.timerslider.TimerPane;
import org.ssh.ui.components.timerslider.TimerSlider;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Example that uses the {@link TimerPane} and {@link TimerSlider}
 *
 * @author Thomas Hakkers
 *         
 */
public class TimerSliderExample extends Application {
    
    public static void main(final String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(final Stage stage) {
        final BorderPane borderPane = new BorderPane();
        final StackPane stackPane = new StackPane();
        final Label testLabel = new Label(
                "Kippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\nKippen maken het volgende geluid: \"Hallo, ik ben een kip.\"\n");
        testLabel.setStyle("-fx-background-color: orange;");
        final Scene scene = new Scene(stackPane);
        stage.setTitle("Game Logs");
        stage.setWidth(600);
        stage.setHeight(430);
        
        final TimerPane timerPane = new TimerPane();
        borderPane.setBottom(timerPane);
        
        stackPane.getChildren().addAll(testLabel, borderPane);
        stage.setScene(scene);
        stage.show();
    }
}