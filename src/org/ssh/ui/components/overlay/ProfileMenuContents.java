package org.ssh.ui.components.overlay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.ssh.managers.manager.Models;
import org.ssh.models.Settings;
import org.ssh.ui.UIComponent;

import java.io.File;
import java.util.stream.Stream;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class ProfileMenuContents extends UIComponent<GridPane> {

    private Settings settings;

    @FXML
    private VBox profiles, workspaces;

    @FXML
    private ScrollPane workspacesPane,
                       profilesPane;

    public ProfileMenuContents() {
        super("profilemenucontents", "topsection/profilemenucontents.fxml");


        Models.<Settings>get("settings").ifPresent(settings -> this.settings = settings);

        buildMenu();
    }

    private void buildMenu(){
        if(settings == null)
            Models.<Settings>get("settings").ifPresent(settings -> this.settings = settings);

        if(settings == null)
            return;

        profiles.getChildren().clear();
        workspaces.getChildren().clear();

        Stream.of(new File("./" + settings.getBasePath()).listFiles())
                .filter(File::isDirectory)
                .forEach(profileFolder -> {
                    Button profilesButton = new Button(profileFolder.getName());

                    profilesButton.onMouseClickedProperty().setValue(event -> {
                        if(!(settings.getCurrentWorkspace().equals(((Button)event.getSource()).getText()))) {
                            settings.update("currentProfile", ((Button) event.getSource()).getText());
                            settings.resetWorkspace();
                            buildMenu();
                            if (settings.getDefaultPath().equals(settings.getProfilesPath()))
                                Models.reinitializeAll();
                        }
                    });

                    profiles.getChildren().add(profilesButton);

                    if (profileFolder.getName().equals(settings.getCurrentProfile())) {
                        profilesButton.getStyleClass().add("selected");

                        Stream.of(profileFolder.listFiles())
                                .filter(File::isDirectory)
                                .forEach(profile -> {
                                    Button workspaceButton = new Button(profile.getName());
                                    if(profile.getName().equals(settings.getCurrentWorkspace()))
                                        workspaceButton.getStyleClass().add("selected");
                                    workspaceButton.onMouseClickedProperty().setValue(event -> {
                                        if(!(settings.getCurrentProfile().equals(((Button)event.getSource()).getText()))){
                                            settings.update("currentWorkspace", ((Button) event.getSource()).getText());
                                            buildMenu(); //rebuild to recolor menuentries
                                            Models.reinitializeAll();
                                        }
                                    });
                                    workspaces.getChildren().add(workspaceButton);
                                });
                    }
                });
    }
}
