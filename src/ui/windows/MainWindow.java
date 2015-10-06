package ui.windows;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.UIController;

/**
 * The Class MainWindow.
 */
public class MainWindow extends UIController {

    /**
     * Instantiates the main window.
     *
     * @param name the name
     * @param primaryStage the primary stage
     */
    public MainWindow(String name, Stage primaryStage) {
        super (name, primaryStage);
        // this block tries to instantiate the window and fill it with graphs
        try {
            // load the FXML template
            this.loadFXML("main.fxml");
            // set the stage
            this.setStage(primaryStage);
            /****************************/
            /* All of the below needs   */
            /*  to come from config!    */
            /****************************/

            // build a Scene with width of 600px and height of 400px
            this.setScene(new Scene(this.getRootNode(), 600, 400));
            this.setTitle("Main Window");
            this.setMinimumDimensions(600, 400);
            this.loadCSS("application.css");
            this.spawnWindow();

            // this handler makes sure the application shuts down when the main
            // window closes
            primaryStage.setOnCloseRequest(windowEvent -> {
                Platform.exit();
                System.exit(0);
            });
            // handle all the errors that could've happened while rendering the
            // layout
        } catch (final Exception renderException) {
            renderException.printStackTrace();
        }
    }
}
