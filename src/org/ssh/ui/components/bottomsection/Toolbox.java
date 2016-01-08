package org.ssh.ui.components.bottomsection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.ssh.ui.UIComponent;
import org.ssh.ui.lua.console.ConsoleManager;
import org.ssh.ui.lua.editor.ScriptEditor;

import java.util.Optional;

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

    UIComponent widgetComponent = new WidgetShortcutContainer();
    UIComponent consoleComponent = new ConsoleManager("bottom-console");
    UIComponent luaComponent = new ScriptEditor("bottom-editor");
    UIComponent settingsComponent = new SettingsPane();

    public Toolbox() {
        super("toolbox", "bottomsection/toolbox.fxml");
        this.toolboxcontainer.add(widgetComponent.getComponent(), 1, 0);
    }

    @FXML
    private void enrollConsole(final ActionEvent e) {
        changeToolboxTab(consoleComponent);
    }
    
    @FXML
    private void enrollLua(final ActionEvent e) {
        changeToolboxTab(luaComponent);
    }
    
    @FXML
    private void enrollSettings(final ActionEvent e) {
        changeToolboxTab(settingsComponent);
    }

    @FXML
    private void enrollWidgets(final ActionEvent e) {
        changeToolboxTab(widgetComponent);
    }

    private void changeToolboxTab(UIComponent newContent) {
        final Optional<Node> optionalNode = toolboxcontainer.getChildren().parallelStream()
                .filter(node -> GridPane.getRowIndex(node) == 0).filter(node -> GridPane.getColumnIndex(node) == 1)
                .findFirst();
        if (optionalNode.isPresent()) {
            this.toolboxcontainer.getChildren().remove(optionalNode.get());
            this.toolboxcontainer.add(newContent.getComponent(), 1, 0);
        }
    }
}
