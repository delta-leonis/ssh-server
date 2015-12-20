package org.ssh.ui.components2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.ssh.ui.UIComponent2;

/**
 * @author Jeroen de Jong
 * @date 12/19/2015
 */
public class TopSection extends UIComponent2<GridPane> {

    public TopSection() {
        super("topbar", "topsection/top.fxml");

        add(new TopbarIcons());
    }
}
