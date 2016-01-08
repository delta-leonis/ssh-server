package org.ssh.ui.components.widget;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.ssh.ui.UIComponent;

/**
 * Created by joost on 1/7/16.
 */
public class WidgetShortcut extends UIComponent {

    @FXML
    private Pane root;

    @FXML
    private Label widgetname;

    public WidgetShortcut(String widgetName, AbstractWidget.WidgetCategory category) {
        super("shortcut " + widgetName, "widget/widgetshortcut.fxml");
        widgetname.setText(widgetName);
        switch(category) {
            case SETTINGS:
//                root.getStyleClass().add("widget_setting");
                break;
            case ANALYSES:
//                root.getStyleClass().add("widget_analyses");
                break;
            case CONTROL:
//                root.getStyleClass().add("widget_control");
                break;
            case TESTS:
//                root.getStyleClass().add("widget_tests");
                break;
        }
    }
}
