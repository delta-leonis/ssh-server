package org.ssh.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.ssh.managers.AbstractManageable;
import org.ssh.managers.manager.UI;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Jeroen de Jong
 * @date 12/19/2015
 */
public class UIComponent<N extends Pane> extends AbstractManageable {

    private N component;

    public UIComponent(String name, String fxmlFile) {
        super(name);

        UI.add(this);

        loadFXML(fxmlFile);

    }

    @Nullable
    public Stage getStage() {
        return (Stage) getComponent().getScene().getWindow();
    }

    @Nullable
    public N getComponent() {
        return component;
    }

    public <M extends Node> boolean add(M component) {
        return add(component, false);
    }

    public <M extends Node> boolean add(M component, String identifier, boolean bind) {
        if (this.getComponent().getChildren().contains(component)) {
            UIComponent.LOG.info("Could not add %s, since it already has been added", component);
            return false;
        }

        Node selected = this.getComponent().lookup(identifier);
        if (selected == null) {
            UIComponent.LOG.info("Could not find %s, thus element has not been added.", identifier);
            return false;
        }

        if (selected instanceof Pane)
            ((Pane) selected).getChildren().add(component);
        else if (selected instanceof Group)
            ((Group) selected).getChildren().add(component);
        else {
            UIComponent.LOG.info("identifier doesn't refer to a Pane or Group, element could not be added");
            return false;
        }

        if (bind)
            UI.bindSize(component, selected);

        return true;
    }

    public <M extends Node> boolean add(M component, int index, boolean bind) {
        if (this.getComponent().getChildren().contains(component)) {
            UIComponent.LOG.info("Could not add %s, since it already has been added", component);
            return false;
        }

        this.getComponent().getChildren().add(index, component);

        if (bind)
            UI.bindSize(component, this.component);

        return true;

    }

    public <M extends Node> boolean add(M component, boolean bind) {
        return add(component, getComponent().getChildren().size() - 1, bind);
    }

    public <M extends Node> void bindSize(M component) {
        if (component instanceof Region)
            UI.bindSize((Region) component, this.component);
        else if (component instanceof SubScene)
            this.bindSubSceneSize((SubScene) component);
    }

    public boolean add(UIComponent childComponent) {
        return add(childComponent, false);
    }

    public boolean add(UIComponent childComponent, boolean bind) {
        return add(childComponent.getComponent(), bind);
    }


    public <M extends SubScene> void bindSubSceneSize(M childComponent) {
        childComponent.heightProperty().bind(component.heightProperty());
        childComponent.widthProperty().bind(component.widthProperty());
    }

    /**
     * Sets a CSS-file from /org/ssh/view/css/ on the UI component.
     *
     * @param fileName The name of the file (e.g. "application.css")
     */
    public void loadCSS(final String fileName) {
        UIComponent.LOG.fine("Loaded CSS file /org/ssh/view2/css/%s into UIComponent %s.", fileName, this.getName());
        // apply the stylesheet
        Platform.runLater(() -> component.getStylesheets()
                .addAll(this.getClass().getResource("/org/ssh/view2/css/" + fileName).toExternalForm()));
    }

    /**
     * Load FXML file into the component.
     *
     * @param fileName The name of the file.
     * @return true, if successful
     */
    public boolean loadFXML(final String fileName) {
        try {
            // load the file
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/org/ssh/view/components/" + fileName));
            // set this class to be the controller so the subclass will link its methods and attributes
            fxmlLoader.setController(this);

            // extract the Nodes
            this.component = fxmlLoader.load();
        } catch (final IOException exception) {
            UIComponent.LOG.warning("Couldn't load FXML file /org/ssh/view2/components/%s into UIComponent %s",
                    fileName,
                    this.getName());
            UIComponent.LOG.exception(exception);
            return false;
        }
        UIComponent.LOG.fine("Loaded FXML file %s into UIComponent %s", fileName, this.getName());
        return true;
    }
}
