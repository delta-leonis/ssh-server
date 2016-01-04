package org.ssh.ui.components2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.Timeslider;
import org.ssh.ui.components.Toolbox;
import org.ssh.ui.windows.WidgetWindow;
import org.ssh.util.Logger;

import java.awt.event.ActionEvent;
import java.util.Optional;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class BottomSection extends UIComponent2<GridPane> {

    private static final Logger LOG = Logger.getLogger();

    private Enroller toolboxEnroller;

    @FXML
    private Pane timesliderWrapper;
    /**
     * {@link Pane} to wrap the toolbox in and bind its sizeproperties to.
     */
    @FXML
    private Pane toolboxWrapper;
    /**
     * {@link ImageView} where the enrollment icon is displayed in. When extending Toolbox, the icon can be flipped.
     */
    @FXML
    private ImageView enrollToolboxImage;

    public BottomSection() {
        super("baseBottom", "bottomsection/bottom.fxml");

        timesliderWrapper.setStyle("-fx-background-color: rgba(255, 0, 0, 1.0);");

        Platform.runLater(() -> {
            Optional<Pane> oToolboxWrapper = UI.getByName("toolboxWrapper", UI.getHighestParent(this.getComponent()));

            if (!oToolboxWrapper.isPresent()) {
                BottomSection.LOG.warning("Could not create bottom section because #toolboxWrapper could not be found.");
                return;
            }
            toolboxWrapper = oToolboxWrapper.get();

            // Toolbox wrapped in an Enroller for fancy up and down sliding
            toolboxEnroller = new Enroller(new Toolbox(), Enroller.ExtendDirection.UP, toolboxWrapper.widthProperty(),
                    toolboxWrapper.heightProperty());
            this.toolboxWrapper.getChildren().add(toolboxEnroller);

            Timeslider t = new Timeslider();
            add(t, "#timesliderWrapper", true);
            UI.bindSize(t, timesliderWrapper);
        });
    }


    /**
     * Extends and collapses the toolbox
     */
    @FXML
    private void enrollToolbox() {
        // Call Enroller function to handle enrollment
        toolboxEnroller.handleEnrollment();
        UI.flipImage(enrollToolboxImage);
    }

    @FXML
    private void toggleSecondScreen() {
        if (WidgetWindow.getInstance().getStage().isShowing()) {
            WidgetWindow.getInstance().hideWidgetWindow();
        } else {
            WidgetWindow.getInstance().showWidgetWindow(this.getStage());
        }
        //TODO remove this ExampleWidget shit (is for test purpose)
        for (int i = 0; i < 3; i++) {
            ExampleWidget e = new ExampleWidget();
            new Draggable(e);
        }
    }
}
