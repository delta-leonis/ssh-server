package org.ssh.ui.components.topsection;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.ssh.ui.UIComponent;

/**
 * @author Jeroen de Jong
 * @date 12/20/2015
 */
public class TopbarIcons extends UIComponent<GridPane> {


    public TopbarIcons() {
        super("TopBarIcons", "topsection/topbaricons.fxml");
    }

    /**
     * Function to switch between fullscreen and normal window. Is called by the fullscreen button in main.fxml
     */
    @FXML
    private void minimize() {
        // Toggle the fullscreen of this stage.
        getStage().setFullScreen(!getStage().isFullScreen());
    }

    /**
     * Function to iconize the stage. Is called by the iconize button in main.fxml
     */
    @FXML
    private void iconize() {
        // Minimize the stage
        getStage().setIconified(true);
    }

    /**
     * Function to shut down the program. Is called by the exit button in main.fxml
     */
    @FXML
    private void exit() {
        getStage().fireEvent(
                new WindowEvent(
                        getStage(),
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }
}
