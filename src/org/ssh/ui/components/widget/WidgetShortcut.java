package org.ssh.ui.components.widget;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;

/**
 * Class for widget shortcuts. These shortcuts are displayed in the toolbox. This class has no functionality,
 * but is just for the view part.
 *
 * @author Joost Overeem
 */
public class WidgetShortcut extends UIComponent {

    /**
     * The {@link Logger} of this class.
     */
    private static final Logger LOG = Logger.getLogger();

    /**
     * The root of this component.
     */
    @FXML
    private BorderPane root;

    /**
     * {@link Label} to display the widget's name in the shortcut.
     */
    @FXML
    private Label widgetname;

    /**
     * Constructor for the {@link WidgetShortcut}s that are normally displayed the
     * {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer}.
     *
     * @param widgetName
     *              The name of the widget.
     * @param category
     *              The {@link org.ssh.ui.components.widget.AbstractWidget.WidgetCategory} the widget belongs to.
     */
    public WidgetShortcut(String widgetName, AbstractWidget.WidgetCategory category) {
        // Call super. Name is shortcut plus the widget name so that it can easily be found
        // When you know the name of the widget
        super("shortcut " + widgetName, "widget/widgetshortcut.fxml");

        // Set the name in the Label
        widgetname.setText(widgetName);
        // Add the style class to root for nice view
        root.getStyleClass().add("widget");
        // Switch on the category
        switch(category) {
            case SETTINGS:
                // If setting, we add it to style class settings to get right background
                root.getStyleClass().add("widget_settings");
                break;
            case ANALYSIS:
                // If setting, we add it to style class analysis to get right background
                root.getStyleClass().add("widget_analysis");
                break;
            case CONTROL:
                // If setting, we add it to style class control to get right background
                root.getStyleClass().add("widget_control");
                break;
            case TESTS:
                // If setting, we add it to style class tests to get right background
                root.getStyleClass().add("widget_tests");
                break;
            default:
                // Tell that an unknown widget category came to pass
                LOG.warning("An unknown WidgetCategory was found in WidgetShortcut");
        }
    }
}
