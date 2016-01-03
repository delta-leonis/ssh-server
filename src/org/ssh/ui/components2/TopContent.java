package org.ssh.ui.components2;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.LoggerConsole;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class TopContent extends UIComponent2<GridPane> {

    public TopContent() {
        super("topElementswrapper", "topsection/topcontent.fxml");
    }
}
