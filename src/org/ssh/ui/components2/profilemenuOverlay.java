package org.ssh.ui.components2;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.Enroller;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class ProfilemenuOverlay extends UIComponent2<GridPane> {
    private Enroller profilemenu;

    @FXML
    private Pane profilemenuWrapper;

    public ProfilemenuOverlay() {
        super("profile menu", "overlay/profilemenuoverlay.fxml");

        profilemenu = new Enroller(new ProfileMenu().getComponent(), Enroller.ExtendDirection.DOWN,
                profilemenuWrapper.widthProperty(), profilemenuWrapper.heightProperty());

        // Set a style class for the profilemenuEnroller
        profilemenu.getStyleClass().add("profilemenuEnroller");

        // add menu to wrapper
        profilemenuWrapper.getChildren().add(profilemenu);

    }

    /**
     * Extends and collapses the profilemenu
     */
    @FXML
    private void enrollProfilemenu() {
        profilemenu.handleEnrollment();
    }
}
