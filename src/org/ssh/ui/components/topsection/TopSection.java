package org.ssh.ui.components.topsection;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.ui.UIComponent;

/**
 * @author Jeroen de Jong
 * @date 12/19/2015
 */
public class TopSection extends UIComponent<GridPane> {

    @FXML
    Pane profilemenuWrapper;

    public TopSection() {
        super("topbar", "topsection/top.fxml");

        add(new TopbarIcons());

        add(new TopContent());

    }
}
