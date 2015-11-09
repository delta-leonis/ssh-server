package org.ssh.ui.components;

import java.util.Optional;

import org.ssh.ui.UIComponent;
import org.ssh.ui.lua.console.Console;
import org.ssh.ui.lua.editor.ScriptEditor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Joost Overeem
 *
 * @TODO create public <P extends Pane> void enroll(P pane)
 * @TODO write javadoc
 *
 */
public class Toolbox extends UIComponent {
    
    @FXML
    private GridPane toolboxcontainer;
                     
    Pane             widgetPane   = new Pane(new Label("Widgets"));
    UIComponent      consolePane  = new Console("bottom-console");
    Pane             luaPane      = new ScriptEditor();
    Pane             settingsPane = new Pane(new Label("Settings"));
                                  
    public Toolbox() {
        super("toolbox", "toolbox.fxml");
        this.toolboxcontainer.minHeightProperty().bind(this.heightProperty());
        this.toolboxcontainer.maxHeightProperty().bind(this.heightProperty());
        this.toolboxcontainer.minWidthProperty().bind(this.widthProperty());
        this.toolboxcontainer.maxWidthProperty().bind(this.widthProperty());
        this.toolboxcontainer.add(this.widgetPane, 1, 0);
    }
    
    @FXML
    private void enrollConsole(final ActionEvent e) {
        final Optional<Node> optionalNode = this.toolboxcontainer.getChildren().parallelStream()
                .filter(node -> GridPane.getRowIndex(node) == 0).filter(node -> GridPane.getColumnIndex(node) == 1)
                .findFirst();
        if (optionalNode.isPresent()) {
            this.toolboxcontainer.getChildren().remove(optionalNode.get());
            this.toolboxcontainer.add(this.consolePane, 1, 0);
        }
    }
    
    @FXML
    private void enrollLua(final ActionEvent e) {
        final Optional<Node> optionalNode = this.toolboxcontainer.getChildren().parallelStream()
                .filter(node -> GridPane.getRowIndex(node) == 0).filter(node -> GridPane.getColumnIndex(node) == 1)
                .findFirst();
        if (optionalNode.isPresent()) {
            this.toolboxcontainer.getChildren().remove(optionalNode.get());
            this.toolboxcontainer.add(this.luaPane, 1, 0);
        }
    }
    
    @FXML
    private void enrollSettings(final ActionEvent e) {
        final Optional<Node> optionalNode = this.toolboxcontainer.getChildren().parallelStream()
                .filter(node -> GridPane.getRowIndex(node) == 0).filter(node -> GridPane.getColumnIndex(node) == 1)
                .findFirst();
        if (optionalNode.isPresent()) {
            this.toolboxcontainer.getChildren().remove(optionalNode.get());
            this.toolboxcontainer.add(this.settingsPane, 1, 0);
        }
    }
    
    @FXML
    private void enrollWidgets(final ActionEvent e) {
        final Optional<Node> optionalNode = this.toolboxcontainer.getChildren().parallelStream()
                .filter(node -> GridPane.getRowIndex(node) == 0).filter(node -> GridPane.getColumnIndex(node) == 1)
                .findFirst();
        if (optionalNode.isPresent()) {
            this.toolboxcontainer.getChildren().remove(optionalNode.get());
            this.toolboxcontainer.add(this.widgetPane, 1, 0);
        }
    }
}
