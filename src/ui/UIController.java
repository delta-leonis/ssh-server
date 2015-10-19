package ui;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

import util.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Class UIController.
 * 
 * This class represents a window.
 * 
 * @author Rimon Oz
 */
abstract public class UIController<T extends Pane> {

    /** The main scene of the window. */
    private Scene  mainScene;

    /** The root node of the window. */
    private Parent rootNode;

    /** The stage. */
    private Stage  stage;

    /** The title of the window. */
    private String title;

    /** The name of the window object. */
    private String name;
    
    /**  The reflected TypeToken (o¬‿¬o ). */
    /*   This is how we defeat Generics    */
    @SuppressWarnings("serial")
    public TypeToken<T> type = new TypeToken<T>(this.getClass()) {};
        
    // a logger for good measure
    private static Logger logger = Logger.getLogger();

    /**
     * Instantiates a new window/UI controller.
     *
     * @param name  The name of the window.
     * @param stage The primary stage.
     */
    public UIController(String name, Stage stage) {
        this.name  = name;
        this.title = name;
        this.stage = stage;
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
     * Sets the name of the window/UI controller.
     *
     * @param name The new name of the window/UI controller.
     */
    public void setName(String name) {
        UIController.logger.info("Changing name of UIController %s to %s.", this.getName(), name);
        this.name = name;
    }

    /**
     * Sets the root node of the window.
     *
     * @param rootNode The new root Node.
     */
    public void setRootNode(Parent rootNode) {
        UIController.logger.info("Replacing the root Node for UIController %s.", this.getName());
        this.rootNode = rootNode;
    }

    /**
     * Sets the main Scene of the window.
     *
     * @param mainScene The new Scene.
     */
    public void setScene(Scene scene) {
        UIController.logger.info("Replacing the root Scene for UIController %s.", this.getName());
        this.getStage().setScene(scene);
        this.mainScene = scene;
    }

    /**
     * Sets the stage of the window.
     *
     * @param stage The new Stage.
     */
    public void setStage(Stage stage) {
        UIController.logger.info("Replacing the root Stage for UIController %s.", this.getName());
        this.stage = stage;
    }

    /**
     * Sets the title of the window.
     *
     * @param title The new title.
     */
    public void setTitle(String title) {
        UIController.logger.info("Setting the title for window belonging to UIController %s to '%s'.", this.getName(), title);
        this.title = title;
        this.getStage().setTitle(this.getTitle()); 
    }
    
    /**
     * Sets the minimum width and height of the window.
     * 
     * @param width  The width in pixels
     * @param height The height in pixels
     */
    public void setMinimumDimensions(int width, int height) {
        UIController.logger.info("Setting the minimum dimensions of UIController %s to %d px by %d px (width x height).", this.getName(), width, height);
        this.getStage().setMinWidth(width);
        this.getStage().setMinHeight(height);
    }
    
    /**
     * Loads a CSS-file from /view/css/ into the window.
     * 
     * @param fileName the name of the file (e.g. "application.css")
     */
    public void loadCSS(String fileName) {
        // apply the stylesheet
        this.getScene().getStylesheets().add(this.getClass().getResource("/view/css/" + fileName).toExternalForm());
        UIController.logger.info("Loaded CSS file %s into UIController %s.", fileName, this.getName());
    }
    
    public boolean loadFXML(String fileName) {
        try {
        	Parent documentRoot = FXMLLoader.load(this.getClass().getResource("/view/" + fileName));
        	if (!documentRoot.getClass().equals(this.getType())) {
        		UIController.logger.warning("Incorrect type of root Pane! Expected %s but found a %s in %s.",
    				this.getType().toString(),
    				documentRoot.getClass().toString(),
    				"/view/" + fileName);
        	}
            this.setRootNode(documentRoot);
        } catch (IOException exception) {
            UIController.logger.warning("Couldn't load FXML file: " + fileName);
            return false;
        }
        UIController.logger.info("Loaded FXML file %s into UIController %s", fileName, this.getName());
        return true;
    }
    
    /**
     * Spawns the window by setting the Scene, the title, and calling .show() on the Stage.
     */
    public void spawnWindow() {
        UIController.logger.info("Spawning a window for UIController %s", this.getName());
        this.getStage().setScene(this.getScene());
        this.getStage().setTitle(this.getTitle());
        this.show();
    }
    
    /**
     * Shows the window.
     */
    public void show() {
        UIController.logger.info("Showing the window for UIController %s", this.getName());
        this.getStage().show();
    }

    /**
     * Hides the window.
     */
    public void hide() {
        UIController.logger.info("Hiding the window for UIController %s", this.getName());
        this.getStage().hide();
    }
    
    @SuppressWarnings("unchecked")
	public ObservableList<Node> getChildren() {
    	return ((T)this.getRootNode()).getChildren();
    }
    
    public Type getType() {
    	return this.type.getType();
    }
}
