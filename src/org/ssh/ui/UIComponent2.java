package org.ssh.ui;

import com.google.common.eventbus.Subscribe;
import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.ssh.util.Logger;

import java.io.IOException;

/**
 * @author Jeroen de Jong
 * @date 12/19/2015
 */
public class UIComponent2<N extends Pane> {

    private N component;

    private Stage stage;

    private String name;

    // a logger for good measure
    private static final Logger LOG = Logger.getLogger();

    public UIComponent2 ( String name, String fxmlFile) {
        this.name = name;

        loadFXML(fxmlFile);

        Platform.runLater(() ->
                stage = ((Stage)getComponent().getScene().getWindow()));
    }

    @Nullable
    public Stage getStage() {
        return stage;
    }

    @Nullable
    public N getComponent(){
        return component;
    }

    public String getName(){
        return name;
    }


    public <M extends Node> boolean add(M component){
        return add(component, false);
    }
    public <M extends Node> boolean add(M component, boolean bind){
        if(this.getComponent().getChildren().contains(component)) {
            UIComponent2.LOG.info("Could not add %s, since it already has been added", component);
            return false;
        }

        this.getComponent().getChildren().add(component);

        if(bind)
            bindSize(component);

        return true;
    }

    public <M extends Node> void bindSize(M component){
        if(component instanceof Region)
            this.bindSize((Region) component);
        else if(component instanceof SubScene)
            this.bindSize((SubScene) component);
    }

    public boolean add(UIComponent2 childComponent) {
        return add(childComponent, false);
    }
    public boolean add(UIComponent2 childComponent, boolean bind) {
        return add(childComponent.getComponent(), bind);
    }

    public <M extends SubScene> void bindSize(M childComponent){
        childComponent.heightProperty().bind(component.heightProperty());
        childComponent.widthProperty().bind(component.widthProperty());
    }

    public <M extends Region> void bindSize(M childComponent){
        childComponent.minWidthProperty().bind(component.widthProperty());
        childComponent.maxWidthProperty().bind(component.widthProperty());
        childComponent.prefWidthProperty().bind(component.widthProperty());
        childComponent.minHeightProperty().bind(component.heightProperty());
        childComponent.maxHeightProperty().bind(component.heightProperty());
        childComponent.prefHeightProperty().bind(component.heightProperty());
    }

    /**
     * Sets a CSS-file from /org/ssh/view/css/ on the UI component.
     *
     * @param fileName
     *            The name of the file (e.g. "application.css")
     */
    public void loadCSS(final String fileName) {
        UIComponent2.LOG.fine("Loaded CSS file /org/ssh/view2/css/%s into UIComponent %s.", fileName, this.name);
        // apply the stylesheet
        Platform.runLater(() -> component.getStylesheets()
                .addAll(this.getClass().getResource("/org/ssh/view2/css/" + fileName).toExternalForm()));
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
                    this.getClass().getResource("/org/ssh/view2/components/" + fileName));
            // set this class to be the controller so the subclass will link its methods and attributes
            fxmlLoader.setController(this);

            // extract the Nodes
            this.component = fxmlLoader.load();
        }
        catch (final IOException exception) {
            UIComponent2.LOG.warning("Couldn't load FXML file /org/ssh/view2/components/%s into UIComponent %s",
                    fileName,
                    this.getName());
            UIComponent2.LOG.exception(exception);
            return false;
        }
        UIComponent2.LOG.fine("Loaded FXML file %s into UIComponent %s", fileName, this.getName());
        return true;
    }
}
