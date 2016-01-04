package org.ssh.ui.components2;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.LoggerConsole;
import org.ssh.ui.components.MatchlogSelector;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class LoggerConsoleOverlay  extends UIComponent2<GridPane> {

    @FXML
    private Pane loggingconsoleWrapper;

    @FXML
    private ImageView enrollLogconsoleImage;

    private Enroller loggingconsoleEnroller;

    public LoggerConsoleOverlay() {
        super("loggerconsoleoverlay", "overlay/loggerconsoleoverlay.fxml");
        // Add the logger an Enroller for enlarging when you want to see more lines at a time.
        loggingconsoleEnroller = new Enroller(new LoggerConsole(), Enroller.ExtendDirection.DOWN,
                loggingconsoleWrapper.widthProperty(), getComponent().heightProperty(),
                loggingconsoleWrapper.heightProperty(), false);
        // Set a style class for the loggingconsoleEnroller
        loggingconsoleEnroller.getStyleClass().add("loggingconsoleEnroller");
        loggingconsoleWrapper.getChildren().add(loggingconsoleEnroller);

        loggingconsoleEnroller.handleEnrollment();
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
