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
 * @author Rimon Oz
 */
abstract public class UIComponent extends Region {

    /** The name. */
    private String name;

    // a logger for good measure
    private static Logger logger = Logger.getLogger();

    /**
     * Instantiates a new UI component.
     *
     * @param name     The name
     * @param fxmlFile The fxml file
     */
    public UIComponent(final String name, final String fxmlFile) {
        // set attributes
        this.name = name;
        // load the template
        this.loadFXML(fxmlFile);
    }

    /**
     * Adds a Node to the component's children.
     *
     * @param <N>  The Node type
     * @param node The Node itself.
     * @return     true, if successful
     */
    public <N extends Node> void add(final N node) {
        UIComponent.logger.info("Adding a Node to UIController %s", this.getName());
        Platform.runLater(() -> this.getChildren().add(node));
    }

    /**
     * Adds a list of Nodes to the window's children.
     *
     * @param <N>   The Node type
     * @param nodes The nodes as an array
     */
    @SuppressWarnings("unchecked")
    public <N extends Node> void add(final N... nodes) {
        Stream.of(nodes).forEach(node -> this.add(node));
    }

    /**
     * Gets the name of the component.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a CSS-file from /org.ssh.view/css/ on the UI component.
     *
     * @param fileName The name of the file (e.g. "org.ssh.managers.css")
     */
    public void loadCSS(final String fileName) {
        UIComponent.logger.info("Loaded CSS file /org/ssh/view/css/%s into UIComponent %s.", fileName, this.getName());
        // apply the stylesheet
        Platform.runLater(() -> this.getScene().getStylesheets()
                .addAll(this.getClass().getResource("/org/ssh/view/css/" + fileName).toExternalForm()));
    }

    /**
     * Load fxml.
     *
     * @param fileName
     *            the file name
     * @return true, if successful
     */
    public boolean loadFXML(final String fileName) {
        try {
            // load the file
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/org/ssh/view/components/" + fileName));
            // set this class to be the controller so the subclass will link its methods and attributes
            fxmlLoader.setController(this);
            // extract the Nodes
            final Parent documentRoot = (Parent) fxmlLoader.load();
            // update the list of children
            //this.getChildren().add(documentRoot);
            Platform.runLater(() -> this.getChildren().add(documentRoot));
        } catch (final IOException exception) {
            UIComponent.logger.warning("Couldn't load FXML file /org/ssh/view/components/%s into UIComponent %s", fileName,
                    this.getName());
            exception.printStackTrace();
            return false;
        }
        UIComponent.logger.info("Loaded FXML file %s into UIComponent %s", fileName, this.getName());
        return true;
    }

    /**
     * Sets the name of the component.
     *
     * @param name The new name of the component.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
