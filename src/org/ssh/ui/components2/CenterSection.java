package org.ssh.ui.components2;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.ssh.field3d.FieldGame;
import org.ssh.ui.UIComponent2;
import org.ssh.util.Logger;

/**
 * @author Jeroen de Jong
 * @date 12/22/2015
 */
public class CenterSection extends UIComponent2<StackPane> {

    private static final Logger LOG = Logger.getLogger();

    public CenterSection() {
        super("center", "centersection/center.fxml");

        add(new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED), true);
    }
}
