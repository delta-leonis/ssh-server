package org.ssh.ui;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.ssh.managers.manager.UI;
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
 * @param <T>
 *            The generic genericType of Pane used as a root Node for this window
 */
public abstract class UIController<T extends Pane> {
    
    /** The main scene of the window. */
    private Scene                   mainScene;
                                    
    /** The root node of the window. */
    private Parent                  rootNode;
                                    
    /** The stage. */
    private Stage                   stage;
                                    
    /** The title of the window. */
    private String                  title;
                                    
    /** The name of the window object. */
    private String                  name;
                                    
    /** The components used in the window. */
    private final List<UIComponent> components;
                                    
    /**
     * The parameterized genericType of the window (<T extends Pane>).
     * 
     * @see {@link https://github.com/google/guava/wiki/ReflectionExplained}
     */
    @SuppressWarnings ("serial")
    public TypeToken<T>             genericType = new TypeToken<T>(this.getClass()) {
                                                };
                                                
    // a logger for good measure
    private static final Logger     LOG         = Logger.getLogger();
                                                
    /**
     * Instantiates a new window/UI controller.
     *
     * @param name
     *            The name of the window.
     * @param fxmlFile
     *            The FXML file
     * @param width
     *            The width of the window.
     * @param height
     *            The height of the window.
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
        // insert the scene
        this.setScene(new Scene(this.getRootNode(), width, height));
        this.loadCSS("application.css");
    }

    public ArrayList<Node> getAllNodes(){
        return UI.getAllNodes(this.getRootNode());
    }

    /**
     * Instantiates a new window/UI controller.
     *
     * @param name
     *            The name of the window.
     * @param fxmlFile
     *            The FXML file
     * @param stage
     *            The primary stage.
     */
    public UIController(final String name, final String fxmlFile, final Stage stage) {
        // set attributes
        this.name = name;
        this.setStage(stage);
        this.components = new ArrayList<UIComponent>();
        
        // load the template from file
        this.loadFXML(fxmlFile);
        // set the title
        this.setTitle(name);
        this.setIcon();
        // insert the scene into the window
        this.setScene(new Scene(this.getRootNode(), 600, 400));
        this.loadCSS("application.css");
    }

    /**
     * Adds a Node to the window's children.
     *
     * @param <N>
     *            The Node genericType
     * @param node
     *            The Node itself.
     * @return true, if successful
     */
    public <N extends Node> boolean add(final N node) {
        UIController.LOG.fine("Adding a Node to UIController %s", this.getName());
        return this.getChildren().add(node);
    }


    /**
     * Add a component at a specific place identified by the identifier
     *
     * @param component
     * @param identifier unique name of a node
     * @param bind true if binding (max/min height/width) is required to the parent
     */
    public <C extends UIComponent<?>> void add(C component, String identifier, boolean bind){
        UI.getByName(identifier, this.getRootNode()).ifPresent(node -> {
            if(!(node instanceof Pane)) {
                UIController.LOG.info("Could not add component at '%s' since it's not an instance of Pane.", identifier);
                return;
            }
            Pane pane = (Pane) node;

            pane.getChildren().add(component.getComponent());

            if(bind)
                UI.bindSize(component.getComponent(), pane);

        });
    }
    
    /**
     * Adds a list of Nodes to the window's children.
     *
     * @param <N>
     *            The Node genericType
     * @param nodes
     *            The nodes as an array
     */
    @SuppressWarnings ("unchecked")
    public <N extends Node> void add(final N... nodes) {
        Stream.of(nodes).forEach(node -> Platform.runLater(() -> this.add(node)));
    }
    
    /**
     * Gets the children of this window.
     *
     * @return The children of this window.
     */
    @SuppressWarnings ("unchecked")
    public ObservableList<Node> getChildren() {
        // cast the root Node from Parent to the correct genericType and return its children
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
     * Gets the root {@link Node} of the window as a {@link Parent}.
     *
     * @return The root {@link Node} of the window.
     */
    public Parent getRootNode() {
        return this.rootNode;
    }
    
    /**
     * Gets the main {@link Scene} of the window.
     *
     * @return The main {@link Scene}.
     */
    public Scene getScene() {
        return this.mainScene;
    }
    
    /**
     * Gets the {@link Stage}.
     *
     * @return The {@link Stage} of the window.
     */
    public Stage getStage() {
        return this.stage;
    }
    
    /**
     * Gets the title of the window.
     *
     * @return The title of the window.
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * Gets the {@link Type} of the root {@link Node}.
     *
     * @return The parameterized {@link Type} of the window.
     */
    public Type getType() {
        return this.genericType.getType();
    }
    
    /**
     * Hides the window.
     */
    public void hide() {
        UIController.LOG.fine("Hiding the window for UIController %s", this.getName());
        this.getStage().hide();
    }
    
    /**
     * Loads a CSS-file from /org/ssh/view/css/ into the window.
     *
     * @param fileName
     *            the name of the file (e.g. "application.css")
     */
    public void loadCSS(final String fileName) {
        UIController.LOG.fine("Loaded CSS file %s into UIController %s.", fileName, this.getName());
        // apply the stylesheet
        this.getScene().getStylesheets()
                .add(this.getClass().getResource("/org/ssh/view/css/" + fileName).toExternalForm());
    }
    
    /**
     * Load an FXML template and injects it into the window.
     *
     * @param fileName
     *            The file name
     * @return true, if successful
     */
    public boolean loadFXML(final String fileName) {
        try {
            
            // load the file
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/org/ssh/view/windows/" + fileName));
            // set this class to be the controller so the subclass will link its methods and
            // attributes
            fxmlLoader.setController(this);
            // extract the Nodes
            final Parent documentRoot = (Parent) fxmlLoader.load();
            // if the root node is of the wrong genericType
            if (!documentRoot.getClass().equals(this.getType())) {
                // warn the user
                UIController.LOG.warning("Incorrect genericType of root Pane! Expected %s but found a %s in %s.",
                        this.getType().toString(),
                        documentRoot.getClass().toString(),
                        "/org/ssh/view/" + fileName);
            }
            // put the nodes in the window
            this.setRootNode(documentRoot);
        }
        catch (final IOException exception) {
            // show an error
            UIController.LOG.warning("Couldn't load FXML file: %s", fileName);
            // handle the exception
            UIController.LOG.exception(exception);
            return false;
        }
        UIController.LOG.fine("Loaded FXML file %s into UIController %s", fileName, this.getName());
        return true;
    }
    
    /**
     * Sets the icon of the stage (the icon shown in the taskbar).
     */
    public void setIcon() {
        this.getStage().getIcons().add(new Image("/org/ssh/view/icon/icon32.png"));
    }
    
    /**
     * Sets the minimum width and height of the window.
     *
     * @param width
     *            The width in pixels.
     * @param height
     *            The height in pixels.
     */
    public void setMinimumDimensions(final int width, final int height) {
        UIController.LOG.fine("Setting the minimum dimensions of UIController %s to %d px by %d px (width x height).",
                this.getName(),
                width,
                height);
        this.getStage().setMinWidth(width);
        this.getStage().setMinHeight(height);
    }
    
    /**
     * Sets the name of the window/UI controller.
     *
     * @param name
     *            The new name of the window/UI controller.
     */
    public void setName(final String name) {
        UIController.LOG.info("Changing name of UIController %s to %s.", this.getName(), name);
        this.name = name;
    }
    
    /**
     * Sets the root {@link Node} of the window.
     *
     * @param rootNode
     *            The new root {@link Node}.
     */
    public void setRootNode(final Parent rootNode) {
        UIController.LOG.info("Replacing the root Node for UIController %s.", this.getName());
        this.rootNode = rootNode;
    }
    
    /**
     * Sets the main {@link Scene} of the window.
     *
     * @param scene
     *            The new {@link Scene}.
     */
    public void setScene(final Scene scene) {
        UIController.LOG.info("Replacing the root Scene for UIController %s.", this.getName());
        // update the scene on the UI thread
        Platform.runLater(() -> this.getStage().setScene(scene));
        this.mainScene = scene;
    }
    
    /**
     * Sets the {@link Stage} of the window.
     *
     * @param stage
     *            The new {@link Stage}.
     */
    public void setStage(final Stage stage) {
        UIController.LOG.info("Replacing the root Stage for UIController %s.", this.getName());
        this.stage = stage;
    }
    
    /**
     * Sets the title of the window.
     *
     * @param title
     *            The new title of the window.
     */
    public void setTitle(final String title) {
        UIController.LOG.fine("Setting the title for window belonging to UIController %s to '%s'.",
                this.getName(),
                title);
        this.title = title;
        this.getStage().setTitle(this.getTitle());
    }
    
    /**
     * Shows the window.
     */
    public void show() {
        UIController.LOG.fine("Showing the window for UIController %s", this.getName());
        this.getStage().show();
    }
    
    /**
     * Spawns the window by calling .show() on the {@link Stage}.
     */
    public void spawnWindow() {
        UIController.LOG.fine("Spawning a window for UIController %s", this.getName());
        Platform.runLater(()->
            this.show()
        );
    }
    
    /**
     * Returns the list of {@link UIComponent UIComponents} stored in this window.
     * 
     * @return the components
     */
    public List<UIComponent> getComponents() {
        return components;
    }
}
