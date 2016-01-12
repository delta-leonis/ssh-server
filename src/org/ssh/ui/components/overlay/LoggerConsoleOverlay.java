package org.ssh.ui.components.overlay;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.Enroller;

/**
 * Class containing the wrapper and {@link Enroller} for the {@link LoggerConsole}.
 *
 * @author Jeroen de Jong
 * @date 12/23/2015
 * @see LoggerConsole
 */
public class LoggerConsoleOverlay extends UIComponent<GridPane> {

    @FXML
    private Pane loggingconsoleextendedWrapper;

    @FXML
    private Pane loggingconsolecollapsedWrapper;

    @FXML
    private ImageView enrollLogconsoleImage;

    private Enroller loggingconsoleEnroller;

    public LoggerConsoleOverlay() {
        super("loggerconsoleoverlay", "overlay/loggerconsoleoverlay.fxml");
        // Add the logger an Enroller for enlarging when you want to see more lines at a time.
        loggingconsoleEnroller = new Enroller(new LoggerConsole().getComponent(), Enroller.ExtendDirection.DOWN,
                loggingconsolecollapsedWrapper.widthProperty(), loggingconsolecollapsedWrapper.heightProperty(),
                loggingconsoleextendedWrapper.heightProperty(), false);
        // Set a style class for the loggingconsoleEnroller
        loggingconsoleEnroller.getStyleClass().add("loggingconsoleEnroller");
        loggingconsoleextendedWrapper.getChildren().add(loggingconsoleEnroller);
    }

    /**
     * Extends and collapses the loggingconsole
     */
    @FXML
    private void enrollLoggingconsole() {
        // Call Enroller function to handle enrollment
        loggingconsoleEnroller.handleEnrollment(() -> UI.flipImage(enrollLogconsoleImage));
    }
}
