package org.ssh.ui;

import java.io.IOException;
import java.util.stream.Stream;

import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

/**
 * The Class UIComponent.
 * 
 * This class represents a component that is placed inside a window (see {@link UIController}).
 *
 * @author Rimon Oz
 * 
 * @TODO add remove()
 */
public abstract class UIComponent extends Region {
                                 
    /** The name of the component. */
    private String              name;

    // a logger for good measure
    private static final Logger LOG = Logger.getLogger();
    
    /**
     * Instantiates a new UI component.
     *
     * @param name
     *            The name of the component
     * @param fxmlFile
     *            The FXML file.
     */
    public UIComponent(final String name, final String fxmlFile) {
        // set attributes
        this.name = name;
        // load the template
        this.loadFXML(fxmlFile);
    }
    
    /**
     * Adds a {@link Node} to the component's children.
     *
     * @param <N>
     *            The {@link Node} type
     * @param node
     *            The {@link Node} itself.
     * @return true, if successful
     */
    public <N extends Node> void add(final N node) {
        UIComponent.LOG.info("Adding a Node to UIController %s", this.getName());
        // add the child on the UI thread
        Platform.runLater(() -> this.getChildren().add(node));
    }
    
    /**
     * Adds a list of Nodes to the window's children.
     *
     * @param <N>
     *            The Node type
     * @param nodes
     *            The nodes as an array
     */
    @SuppressWarnings ("unchecked")
    public <N extends Node> void add(final N... nodes) {
        Stream.of(nodes).forEach(node -> this.add(node));
    }
    
    /**
     * Gets the name of the component.
     *
     * @return The name of the component.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Sets a CSS-file from /org/ssh/view/css/ on the UI component.
     *
     * @param fileName
     *            The name of the file (e.g. "application.css")
     */
    public void loadCSS(final String fileName) {
        UIComponent.LOG.fine("Loaded CSS file /org/ssh/view/css/%s into UIComponent %s.", fileName, this.getName());
        // apply the stylesheet
        Platform.runLater(() -> this.getScene().getStylesheets()
                .addAll(this.getClass().getResource("/org/ssh/view/css/" + fileName).toExternalForm()));
    }
    
    /**
     * Load FXML file into the component.
     *
     * @param fileName
     *            The name of the file.
     * @return true, if successful
     */
    public boolean loadFXML(final String fileName) {
        try {
            // load the file
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/org/ssh/view/components/" + fileName));
            // set this class to be the controller so the subclass will link its methods and
            // attributes
            fxmlLoader.setController(this);
            // extract the Nodes
            final Parent documentRoot = (Parent) fxmlLoader.load();
            // update the list of children
            Platform.runLater(() -> this.getChildren().add(documentRoot));
        }
        catch (final IOException exception) {
            UIComponent.LOG.warning("Couldn't load FXML file /org/ssh/view/components/%s into UIComponent %s",
                    fileName,
                    this.getName());
            UIComponent.LOG.exception(exception);
            return false;
        }
        UIComponent.LOG.fine("Loaded FXML file %s into UIComponent %s", fileName, this.getName());
        return true;
    }
    
    /**
     * Sets the name of the component.
     *
     * @param name
     *            The new name of the component.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
