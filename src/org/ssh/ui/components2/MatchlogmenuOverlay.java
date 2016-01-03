package org.ssh.ui.components2;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.MatchlogSelector;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class MatchlogmenuOverlay extends UIComponent2<GridPane> {

    @FXML
    private Pane matchlogWrapper;

    @FXML
    private Pane matchlogButtonSizer;

    public MatchlogmenuOverlay() {
        super("overlay", "overlay/matchlogmenuoverlay.fxml");

        // MatchlogSelector wrapped in an Enroller for fancy up and down sliding
        Enroller matchlogEnroller = new Enroller(new MatchlogSelector(), Enroller.ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
                matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true);
        // Set a style class for the profilemenuEnroller
        matchlogEnroller.getStyleClass().add("matchlogEnroller");
        this.matchlogWrapper.getChildren().add(matchlogEnroller);
    }
}

