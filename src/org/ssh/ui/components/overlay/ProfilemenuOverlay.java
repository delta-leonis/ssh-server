package org.ssh.ui.components.overlay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.Models;
import org.ssh.models.Settings;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.Enroller;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class ProfilemenuOverlay extends UIComponent<GridPane> {
    private Enroller profilemenu;

    @FXML
    private Pane profilemenuWrapper;

    @FXML
    private Button profileButton;

    public ProfilemenuOverlay() {
        super("profile menu", "overlay/profilemenuoverlay.fxml");

        ProfileMenuContents cont = new ProfileMenuContents();
        profilemenu = new Enroller(cont.getComponent(), Enroller.ExtendDirection.DOWN,
                profilemenuWrapper.widthProperty(), cont.getComponent().heightProperty());

        //

        // Set a style class for the profilemenuEnroller
        profilemenu.getStyleClass().add("profilemenuEnroller");

        // add menu to wrapper
        profilemenuWrapper.getChildren().add(profilemenu);

        Models.<Settings>get("settings")
                .ifPresent(settings ->
                        profileButton.textProperty().bind(settings.getCurrentProfileProperty())
                );
    }

    /**
     * Extends and collapses the profilemenu
     */
    @FXML
    private void enrollProfilemenu() {
        profilemenu.handleEnrollment();
    }
}
