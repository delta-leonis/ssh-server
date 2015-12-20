package org.ssh.ui;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
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
        loadFXML(fxmlFile);

        this.name = name;
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

    public boolean add(UIComponent2 childComponent, boolean bind) {
        if(this.getComponent().getChildren().contains(childComponent.getComponent())) {
            UIComponent2.LOG.info("Could not add %s, since it already has been added", childComponent.getName());
            return false;
        }

        this.getComponent().getChildren().add(childComponent.getComponent());

        if(bind)
            this.bindSize(childComponent);

        return true;
    }

    public boolean add(UIComponent2 childComponent) {
        return add(childComponent, false);
    }

    public <M extends Pane> void bindSize(M childComponent){
        childComponent.minWidthProperty().bind(component.minWidthProperty());
        childComponent.maxWidthProperty().bind(component.maxWidthProperty());
        childComponent.prefWidthProperty().bind(component.prefWidthProperty());
        childComponent.minHeightProperty().bind(component.minHeightProperty());
        childComponent.maxHeightProperty().bind(component.maxHeightProperty());
        childComponent.prefHeightProperty().bind(component.prefHeightProperty());
    }

    public UIComponent2 bindSize(UIComponent2 childComponent){
        bindSize(childComponent.getComponent());
        return childComponent;
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
