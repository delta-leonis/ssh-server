package ui;

import java.io.IOException;
import java.util.logging.Logger;

import application.Services;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Class UIController.
 */
abstract public class UIController {

    /** The scene. */
    private Scene  scene;

    /** The root node. */
    private Parent rootNode;

    /** The stage. */
    private Stage  stage;

    /** The title. */
    private String title;

    /** The name. */
    private String name;
    
    // a logger for good measure
    private static Logger logger = Logger.getLogger(Services.class.toString());

    /**
     * Instantiates a new UI controller.
     *
     * @param name the name
     * @param stage the stage
     */
    public UIController(String name, Stage stage) {
        this.name  = name;
        this.title = name;
        this.stage = stage;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the root node.
     *
     * @return the root node
     */
    public Parent getRootNode() {
        return this.rootNode;
    }

    /**
     * Gets the scene.
     *
     * @return the scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Gets the stage.
     *
     * @return the stage
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the root node.
     *
     * @param rootNode the new root node
     */
    public void setRootNode(Parent rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Sets the scene.
     *
     * @param scene the new scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Sets the stage.
     *
     * @param stage the new stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the minimum width and height of the window.
     * 
     * @param width  the width in pixels
     * @param height the height in pixels
     */
    public void setMinimumDimensions(int width, int height) {
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
        UIController.logger.info("Loaded CSS file: " + fileName);
    }
    
    public boolean loadFXML(String fileName) {
        try {
			this.setRootNode(FXMLLoader.load(this.getClass().getResource("/view/" + fileName)));
		} catch (IOException exception) {
			UIController.logger.warning("Couldn't load FXML file: " + fileName);
			return false;
		}
		UIController.logger.info("Loaded FXML file: " + fileName);
        return true;
    }
    
    /**
     * Spawns the window by setting the Scene, the title, and calling .show() on the Stage.
     */
    public void spawnWindow() {
        this.getStage().setScene(this.getScene());
        this.getStage().setTitle(this.getTitle());
        this.getStage().show();
    }

}
