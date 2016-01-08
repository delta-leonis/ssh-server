package org.ssh.managers.controllers;

import javafx.scene.layout.Pane;
import org.ssh.managers.ManagerController;
import org.ssh.managers.manager.Models;
import org.ssh.models.WidgetSettings;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.widget.AbstractWidget;

import java.util.List;

/**
 * Created by joost on 1/5/16.
 */
public class UIComponentController extends ManagerController<UIComponent<? extends Pane>> {

    WidgetSettings widgetSettings;

    public UIComponentController() {
        super();
        widgetSettings = Models.create(WidgetSettings.class);

    }

    public void addWidget(AbstractWidget widget) {
        widgetSettings.addWidget(widget);
    }

    public List<AbstractWidget> getOrderedShortcuts() {
        return widgetSettings.getOrderedShortcuts();
    }

    public List<AbstractWidget> getOrderedWidgetsForWindow() {
        return widgetSettings.getOrderedWidgetsForWindow();
    }

    public void swapWindowOrder(int row1, int row2) {
        widgetSettings.swapWindowOrder(row1, row2);
    }

    public void swapShortcutOrder(int col1, int col2) {
        widgetSettings.swapShortcutOrder(col1, col2);
    }

    public int getNumberOfWidgets() {
        return widgetSettings.getNumberOfWidgets();
    }
}
