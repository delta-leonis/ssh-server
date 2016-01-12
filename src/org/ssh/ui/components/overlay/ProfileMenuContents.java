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
 * Class for generating the different menuentries for all Profiles and Workspaces
 *
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class ProfileMenuContents extends UIComponent<GridPane> {

    /**
     * Settings containing information about profiles/workspaces
     */
    private Settings settings;

    /**
     * the Panes containing individual buttons
     */
    @FXML
    private VBox profiles, workspaces;

    @FXML
    private ScrollPane workspacesPane,
            profilesPane;

    public ProfileMenuContents() {
        super("profilemenucontents", "topsection/profilemenucontents.fxml");

        // retrieve the settings
        Models.<Settings>get("settings").ifPresent(set -> this.settings = set);

        // build the menu items
        buildMenu();
    }

    private void buildMenu() {
        // if the settings aren't set yet, we should try to get them
        if (settings == null)
            Models.<Settings>get("settings").ifPresent(set -> this.settings = set);

        // still not found? fuck it
        if (settings == null)
            return;

        //clear previous profiles and workspaces
        profiles.getChildren().clear();
        workspaces.getChildren().clear();

        // create a stream of all profiles
        Stream.of(new File("./" + settings.getBasePath()).listFiles())
                // make sure these are only directories
                .filter(File::isDirectory)
                // process every profile folder
                .forEach(profileFolder -> {
                    //create a button
                    Button profilesButton = new Button(profileFolder.getName());

                    //when the button gets pressed
                    profilesButton.onMouseClickedProperty().setValue(event -> {
                        //retrieve the text from the clicked button
                        String buttonText = ((Button) event.getSource()).getText();

                        //if the value changed
                        if (!(settings.getCurrentWorkspace().equals(buttonText))) {

                            //update the current profile
                            settings.update("currentProfile", buttonText);
                            //reset the workspace for the new profile
                            settings.resetWorkspace();
                            // rebuild the menu since workspaces are most likely to be changed
                            buildMenu();

                            // Default menu setting doesn't actually have a workspace, so the
                            // models should be reinitialized as soon as it's selected
                            if (settings.getDefaultPath().equals(settings.getProfilesPath()))
                                Models.reinitializeAll();
                        }
                    });

                    //add this profile button to its container
                    profiles.getChildren().add(profilesButton);

                    // Is this the iteration of the current profile?
                    // if so, we should create the workspace buttons
                    if (profileFolder.getName().equals(settings.getCurrentProfile())) {
                        // add the class giving the profilebutton the right style
                        profilesButton.getStyleClass().add("selected");

                        //list all files in the profile folder, thus the workspaces
                        Stream.of(profileFolder.listFiles())
                                //filter for all directories
                                .filter(File::isDirectory)
                                //process each workspace
                                .forEach(workspace -> {
                                    //create the button
                                    Button workspaceButton = new Button(workspace.getName());
                                    //if it is the current workspace is equal to the selected workspace
                                    if (workspace.getName().equals(settings.getCurrentWorkspace()))
                                        //it should receive a special style
                                        workspaceButton.getStyleClass().add("selected");

                                    //when this button is clicked
                                    workspaceButton.onMouseClickedProperty().setValue(event -> {
                                        String buttonText = ((Button) event.getSource()).getText();
                                        // if it is not the same workspace as before
                                        if (!(settings.getCurrentWorkspace().equals(buttonText))) {
                                            // update the current workspace
                                            settings.update("currentWorkspace", buttonText);
                                            //rebuild to recolor menuentries
                                            buildMenu();
                                            // re-initialize all models
                                            Models.reinitializeAll();
                                        }
                                    });

                                    //add the button to the container
                                    workspaces.getChildren().add(workspaceButton);
                                });
                    }
                });
    }
}
