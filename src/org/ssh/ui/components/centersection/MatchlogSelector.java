package org.ssh.ui.components.centersection;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.ssh.ui.UIComponent;

public class MatchlogSelector extends UIComponent {

    @FXML
    private GridPane rootPane;

    public MatchlogSelector() {
        super("matchlogselector", "centersection/matchlogselector.fxml");
    }
}
