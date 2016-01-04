package org.ssh.ui.components2;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.MatchlogSelector;
import org.ssh.ui.components.Toolbox;
import org.ssh.util.Logger;

/**
 * @author Jeroen de Jong
 * @date 12/22/2015
 */
public class CenterSection extends UIComponent2<StackPane> {

    private static final Logger LOG = Logger.getLogger();

    @FXML
    private Pane matchlogWrapper;

    @FXML
    private Pane matchlogButtonSizer;

    public CenterSection() {
        super("center", "centersection/center.fxml");

        add("#fieldBase", new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED), true);

        // MatchlogSelector wrapped in an Enroller for fancy up and down sliding
        Enroller matchlogEnroller = new Enroller(new MatchlogSelector(), Enroller.ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
                matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true);
        // Set a style class for the profilemenuEnroller
        matchlogEnroller.getStyleClass().add("matchlogEnroller");
        this.matchlogWrapper.getChildren().add(matchlogEnroller);
    }
}
