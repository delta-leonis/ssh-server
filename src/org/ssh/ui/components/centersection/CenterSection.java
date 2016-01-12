package org.ssh.ui.components.centersection;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.ssh.field3d.FieldGame;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.Enroller;
import org.ssh.util.Logger;

/**
 * @author Jeroen de Jong
 * @date 12/22/2015
 */
public class CenterSection extends UIComponent<StackPane> {

    @FXML
    private Pane matchlogWrapper;

    @FXML
    private Pane matchlogButtonSizer;

    private FieldGame fieldGame;

    public CenterSection() {
        super("center", "centersection/center.fxml");
        fieldGame = new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED);
        add(fieldGame, "#fieldBase", true);

        MatchlogSelector matchlogSelector = new MatchlogSelector();
        // MatchlogSelector wrapped in an Enroller for fancy up and down sliding
        Enroller matchlogEnroller = new Enroller(matchlogSelector.getComponent(), Enroller.ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
                matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true);


        this.matchlogWrapper.getChildren().add(matchlogEnroller);
    }

    public FieldGame getFieldGame() {
        return fieldGame;
    }
}
