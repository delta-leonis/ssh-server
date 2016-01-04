package org.ssh.ui.components2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.Toolbox;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class BottomSection extends UIComponent2<GridPane> {

    private Enroller toolboxEnroller;

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

        // Toolbox wrapped in an Enroller for fancy up and down sliding
//        toolboxEnroller = new Enroller(new Toolbox(), Enroller.ExtendDirection.UP, toolboxWrapper.widthProperty(),
//                toolboxWrapper.heightProperty());
//        // Set a style class for the toolboxEnroller
//        toolboxEnroller.getStyleClass().add("toolboxEnroller");
//        this.toolboxWrapper.getChildren().add(toolboxEnroller);
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
}
