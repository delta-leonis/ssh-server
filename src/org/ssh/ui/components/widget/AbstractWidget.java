package org.ssh.ui.components.widget;

import org.ssh.ui.UIComponent;

/**
 * Created by joost on 1/5/16.
 */
public abstract class AbstractWidget extends UIComponent {

    private WidgetCategory category;

    public enum WidgetCategory {
        SETTINGS, ANALYSES, CONTROL, TESTS
    }

    public AbstractWidget(String name, String fxml, WidgetCategory category) {
        super(name, fxml);
        this.category = category;
    }

    public WidgetCategory getCategory() {
        return category;
    }
}
