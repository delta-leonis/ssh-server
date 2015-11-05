package org.ssh.ui;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Class UIController.
 *
 * This class represents a window.
 *
 * @author Rimon Oz
 * @param <T> The generic type of Pane used as a root Node for this window
 */
abstract public class UIController<T extends Pane> {

    /** The main scene of the window. */
    private Scene mainScene;

    /** The root node of the window. */
    private Parent rootNode;

    /** The stage. */
    private Stage stage;

    /** The title of the window. */
    private String title;

    /** The name of the window object. */
    private String name;

    /** The components used in the window. */
    private final List<UIComponent> components;

    /** The reflected TypeToken (o¬‿¬o ). */
    /* This is how we defeat Generics */
    @SuppressWarnings("serial")
    public TypeToken<T> type = new TypeToken<T>(this.getClass()) {};

    // a logger for good measure
    private static Logger logger = Logger.getLogger();

    /**
     * Instantiates a new window/UI controller.
     *
     * @param name     The name of the window.
     * @param fxmlFile The fxml file
     * @param width    The width of the window.
     * @param height   The height of the window.
     */
    public UIController(final String name, final String fxmlFile, final int width, final int height) {
        // set attributes
        this.setName(name);
        this.setStage(new Stage());
        this.components = new ArrayList<UIComponent>();

        // load the template from file
        this.loadFXML(fxmlFile);
        this.setTitle(this.getName());
        this.setIcon();
        this.setScene(new Scene(this.getRootNode(), width, height));
    }

    /**
     * Instantiates a new window/UI controller.
     *
     * @param name     The name of the window.
     * @param fxmlFile The fxml file
     * @param stage    The primary stage.
     */
    public UIController(final String name, final String fxmlFile, final Stage stage) {
        // set attributes
        this.name = name;
        this.setStage(stage);
        this.components = new ArrayList<UIComponent>();

        // load the template from file
        this.loadFXML(fxmlFile);
        this.setTitle(name);
        this.setIcon();
        this.setScene(new Scene(this.getRootNode(), 600, 400));
        this.loadCSS("org.ssh.managers.css");
    }

    /**
     * Adds a Node to the window's children.
     *
     * @param <N>  The Node type
     * @param node The Node itself.
     * @return     true, if successful
     */
    public <N extends Node> boolean add(final N node) {
        UIController.logger.info("Adding a Node to UIController %s", this.getName());
        return this.getChildren().add(node);
    }

    /**
     * Adds a list of Nodes to the window's children.
     *
     * @param <N>   The Node type
     * @param nodes The nodes as an array
     */
    @SuppressWarnings("unchecked")
    public <N extends Node> void add(final N... nodes) {
        Stream.of(nodes).forEach(node -> Platform.runLater(() -> this.add(node)));
    }

    /**
     * Gets the children of this window.
     *
     * @return The children of this window.
     */
    @SuppressWarnings("unchecked")
    public ObservableList<Node> getChildren() {
        return ((T) this.getRootNode()).getChildren();
    }

    /**
     * Gets the name of the window/UI controller.
     *
     * @return The name of the window.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the root node of the window.
     *
     * @return The root node of the window.
     */
    public Parent getRootNode() {
        return this.rootNode;
    }

    /**
     * Gets the main Scene of the window.
     *
     * @return The main scene.
     */
    public Scene getScene() {
        return this.mainScene;
    }

    /**
     * Gets the Stage.
     *
     * @return The stage.
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Gets the title of the window.
     *
     * @return The title of the window
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return this.type.getType();
    }

    /**
     * Hides the window.
     */
    public void hide() {
        UIController.logger.info("Hiding the window for UIController %s", this.getName());
        this.getStage().hide();
    }

    /**
     * Loads a CSS-file from /org.ssh.view/css/ into the window.
     * 
     * @param fileName
     *            the name of the file (e.g. "org.ssh.managers.css")
     */
    public void loadCSS(final String fileName) {
        UIController.logger.info("Loaded CSS file %s into UIController %s.", fileName, this.getName());
        // apply the stylesheet
        this.getScene().getStylesheets().add(this.getClass().getResource("/org.ssh.view/css/" + fileName).toExternalForm());
    }

    /**
     * Load an FXML template and injects it into the window.
     *
     * @param fileName The file name
     * @return         true, if successful
     */
    public boolean loadFXML(final String fileName) {
        try {
        	
            // load the file
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/org.ssh.view/windows/" + fileName));
            // set this class to be the controller so the subclass will link its methods and attributes
            fxmlLoader.setController(this);
            // extract the Nodes
            final Parent documentRoot = (Parent) fxmlLoader.load();
            // if the root node is of the wrong type
            if (!documentRoot.getClass().equals(this.getType())) {
                // warn the user
                UIController.logger.warning("Incorrect type of root Pane! Expected %s but found a %s in %s.",
                        this.getType().toString(), documentRoot.getClass().toString(), "/org.ssh.view/" + fileName);
            }
            // put the nodes in the window
            this.setRootNode(documentRoot);
        } catch (final IOException exception) {
        	exception.printStackTrace();
            UIController.logger.warning("Couldn't load FXML file: " + fileName);
            return false;
        }
        UIController.logger.info("Loaded FXML file %s into UIController %s", fileName, this.getName());
        return true;
    }

    /**
     * Sets the minimum width and height of the window.
     * 
     * @param width  The width in pixels
     * @param height The height in pixels
     */
    public void setMinimumDimensions(final int width, final int height) {
        UIController.logger.info(
                "Setting the minimum dimensions of UIController %s to %d px by %d px (width x height).", this.getName(),
                width, height);
        this.getStage().setMinWidth(width);
        this.getStage().setMinHeight(height);
    }

    /**
     * Sets the name of the window/UI controller.
     *
     * @param name The new name of the window/UI controller.
     */
    public void setName(final String name) {
        UIController.logger.info("Changing name of UIController %s to %s.", this.getName(), name);
        this.name = name;
    }

    /**
     * Sets the root node of the window.
     *
     * @param rootNode The new root Node.
     */
    public void setRootNode(final Parent rootNode) {
        UIController.logger.info("Replacing the root Node for UIController %s.", this.getName());
        this.rootNode = rootNode;
    }

    /**
     * Sets the main Scene of the window.
     *
     * @param scene The new scene
     */
    public void setScene(final Scene scene) {
        UIController.logger.info("Replacing the root Scene for UIController %s.", this.getName());
        this.getStage().setScene(scene);
        this.mainScene = scene;
    }

    /**
     * Sets the stage of the window.
     *
     * @param stage The new Stage.
     */
    public void setStage(final Stage stage) {
        UIController.logger.info("Replacing the root Stage for UIController %s.", this.getName());
        this.stage = stage;
    }

    /**
     * Sets the title of the window.
     *
     * @param title The new title.
     */
    public void setTitle(final String title) {
        UIController.logger.info("Setting the title for window belonging to UIController %s to '%s'.", this.getName(),
                title);
        this.title = title;
        this.getStage().setTitle(this.getTitle());
    }

    /**
     * Sets the icon of the stage (the icon shown in the taskbar).
     */
	public void setIcon() {
		this.getStage().getIcons().add(new Image("/org.ssh.view/icon/icon32.png"));
	}

    /**
     * Shows the window.
     */
    public void show() {
        UIController.logger.info("Showing the window for UIController %s", this.getName());
        this.getStage().show();
    }

    /**
     * Spawns the window by setting the Scene, the title, and calling .show() on
     * the Stage.
     */
    public void spawnWindow() {
        UIController.logger.info("Spawning a window for UIController %s", this.getName());
        this.getStage().setScene(this.getScene());
        this.getStage().setTitle(this.getTitle());
        this.show();
    }
}
