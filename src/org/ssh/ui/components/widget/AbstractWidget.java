package org.ssh.ui.components.widget;

import org.ssh.ui.UIComponent;

/**
 * Abstract class for the widgets. All widgets must inherit this class.
 *
 * @author Joost Overeem
 */
public abstract class AbstractWidget extends UIComponent {

    /**
     * The {@link WidgetCategory} of the widget.
     */
    private WidgetCategory category;

    /**
     * The category that the widget belongs to. This is used to display a user friendly
     * background at the shortcuts for clarity.
     */
    public enum WidgetCategory{SETTINGS, ANALYSIS, CONTROL, TESTS}

    /**
     * Constructor for a widget.
     *
     * @param name
     *              The name of the widget. Is passed through to the super class.
     * @param fxml
     *              The fxml file.
     * @param category
     *              The {@link WidgetCategory} the widget belongs to.
     */
    public AbstractWidget(String name, String fxml, WidgetCategory category) {
        super(name, fxml);
        this.category = category;
    }

    /**
     * Getter for the {@link #category}.
     *
     * @return The {@link WidgetCategory} of the widget
     */
    public WidgetCategory getCategory() {
        return category;
    }
}
