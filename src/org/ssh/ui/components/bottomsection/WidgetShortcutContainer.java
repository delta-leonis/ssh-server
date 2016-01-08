package org.ssh.ui.components.bottomsection;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.
        GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.widget.AbstractWidget;
import org.ssh.ui.components.widget.TestWidget;
import org.ssh.ui.components.widget.WidgetShortcut;

import java.util.List;

/**
 * Created by joost on 1/5/16.
 */
public class WidgetShortcutContainer extends UIComponent<Pane> {

    @FXML
    private Pane rootPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane shortcutPane;

    public WidgetShortcutContainer() {
        super("widget shortcuts", "bottomsection/widgetshortcutcontainer.fxml");
        UI.bindSize(scrollPane, rootPane);
        shortcutPane.minWidthProperty().bind(scrollPane.prefViewportWidthProperty());
        Platform.runLater(() -> System.out.println(scrollPane.prefViewportWidthProperty().doubleValue() + " " + scrollPane.minViewportWidthProperty()));
        Platform.runLater(this::displayWidgets);
    }

    private void displayWidgets() {
        List<AbstractWidget> widgets = UI.getOrderedWidgetsForShortcuts();

        for(int i = 0; i < (widgets.size() / 5); i++) {
            RowConstraints newRow = new RowConstraints();
            newRow.setMinHeight(70);
            shortcutPane.getRowConstraints().add(newRow);
        }

        int counter = 0;

        for(AbstractWidget widget : widgets) {
            int row = counter / 5;
            int col = counter % 5;
            shortcutPane.add(new WidgetShortcut(widget.getName(), widget.getCategory()).getComponent(), col, row);
            counter++;
        }
    }

}
