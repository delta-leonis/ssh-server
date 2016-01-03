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

    public BottomSection() {
        super("baseBottom", "bottomsection/bottom.fxml");
    }
}
