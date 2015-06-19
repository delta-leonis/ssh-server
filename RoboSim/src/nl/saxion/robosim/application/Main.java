package nl.saxion.robosim.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) throws IOException {
//        TODO new LogReader();
//        new AIListener().start();
//        new MultiCastServerThread(16).start();
        //new LogReader();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/layout.fxml"));
        root.getStylesheets().add(this.getClass().getResource("/resources/style.css").toExternalForm());

        GridPane gp = (GridPane) root.lookup("#grid");
        Canvas c = (Canvas) root.lookup("#canvas");

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("RoboSim");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();

    }



}

