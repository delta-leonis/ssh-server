package org.ssh.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.ssh.managers.AbstractManageable;
import org.ssh.managers.manager.UI;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Containerclass for a {@link java.awt.Component} of type {@link Pane}. This way the UI components are able to be managed
 * by a coordinating class (in this case {@link UI})
 *
 * @author Jeroen de Jong
 * @date 12/19/2015
 */
public class UIComponent<N extends Pane> extends AbstractManageable {

    /** component to manage */
    private N component;

    /**
     * Instantiates a new UIComponent
     * @param name unique name of this component
     * @param fxmlFile file that describes the structure of this component
     */
    public UIComponent(String name, String fxmlFile) {
        super(name);

        //auto assign to the manager
        UI.add(this);

        //load the fxml
        loadFXML(fxmlFile);

    }

    /**
     * @return current stage of this component
     */
    @Nullable
    public Stage getStage() {
        return (Stage) getComponent().getScene().getWindow();
    }

    /**
     * @return The containing component for actual use in GUI
     */
    @Nullable
    public N getComponent() {
        return component;
    }

    /**
     * Add a component to this pane
     * @param component component to add
     * @param <M> Type of component
     * @return success value, true if added successfully
     */
    public <M extends Node> boolean add(M component) {
        return add(component, false);
    }

    /**
     * Adds a component at a specific place in the GUI {@see fx:id}
     * @param component component to add
     * @param identifier identifier to place the component in or at
     * @param bind whether to bind the size to the parent
     * @param <M> type of component
     * @return success value, true if added succesfully
     */
    public <M extends Node> boolean add(M component, String identifier, boolean bind) {
        if (this.getComponent().getChildren().contains(component)) {
            UIComponent.LOG.info("Could not add %s, since it already has been added", component);
            return false;
        }

        // find the node based on the identifier
        Node selected = this.getComponent().lookup(identifier);
        if (selected == null) {
            UIComponent.LOG.info("Could not find %s, thus element has not been added.", identifier);
            return false;
        }

        //check how it can be added
        if (selected instanceof Pane)
            ((Pane) selected).getChildren().add(component);
        else if (selected instanceof Group)
            ((Group) selected).getChildren().add(component);
        else {
            UIComponent.LOG.info("identifier doesn't refer to a Pane or Group, element could not be added");
            return false;
        }

        //bind if necessary
        if (bind)
            UI.bindSize(component, selected);

        // it has been added
        return true;
    }

    /**
     * Add a component at a certain index. This may be useful for use in {@link FlowPane}s
     * @param component component to add
     * @param index index at which the specified element is to be inserted
     * @param bind whether to bind the size to the parent
     * @param <M> type of component
     * @return success value, true if successful
     */
    public <M extends Node> boolean add(M component, int index, boolean bind) {
        // should be unique
        if (this.getComponent().getChildren().contains(component)) {
            UIComponent.LOG.info("Could not add %s, since it already has been added", component);
            return false;
        }

        // add at the given index
        this.getComponent().getChildren().add(index, component);

        //bind if necessary
        if (bind)
            UI.bindSize(component, this.component);

        // successful add
        return true;

    }

    /**
     * Add component to a Pane
     * @param component component to add
     * @param bind whether to bind the size to the parent
     * @param <M> type of component
     * @return success value, true if successful
     */
    public <M extends Node> boolean add(M component, boolean bind) {
        return add(component, getComponent().getChildren().size() - 1, bind);
    }

    /**
     * binds height and width to this Pane
     * @param child child to bind from
     * @param <M> type of component
     */
    public <M extends Node> void bindSize(M child) {
        if (child instanceof Region)
            UI.bindSize((Region) child, this.component);
        else if (child instanceof SubScene)
            this.bindSubSceneSize((SubScene) child);
    }

    /**
     * Adds a UIComponent to this component
     * @param childComponent UIComponent to add
     * @return success value
     */
    public boolean add(UIComponent childComponent) {
        return add(childComponent, false);
    }

    /**
     * Adds a UIComponent to this component
     * @param childComponent UIComponent to add
     * @param bind whether to bind the height and width
     * @return success value
     */
    public boolean add(UIComponent childComponent, boolean bind) {
        return add(childComponent.getComponent(), bind);
    }


    /**
     * Specific implementation how to bind height and width to a SubScene
     * @param childComponent childComponent to bind from
     * @param <M> type of component
     */
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
