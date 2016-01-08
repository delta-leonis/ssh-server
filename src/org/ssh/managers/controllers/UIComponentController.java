package org.ssh.managers.controllers;

import javafx.scene.layout.Pane;
import org.ssh.managers.AbstractManagerController;
import org.ssh.managers.manager.Models;
import org.ssh.models.WidgetSettings;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.widget.AbstractWidget;

import java.util.List;

/**
 * The Class UIComponentController.
 * <p>
 * UIComponentController is responsible for maintaining {@link UIComponent}s.
 *
 * @author Joost Overeem
 */
public class UIComponentController extends AbstractManagerController<UIComponent<? extends Pane>> {

    /**
     * The {@link WidgetSettings} that are controlled by this class.
     */
    WidgetSettings widgetSettings;

    /**
     * Constructor for {@link UIComponentController}.
     */
    public UIComponentController() {
        super();
        widgetSettings = Models.create(WidgetSettings.class);

    }

    /**
     * Adds an {@link AbstractWidget} to the {@link WidgetSettings}.
     *
     * @param widget The {@link AbstractWidget} to be added.
     */
    public void addWidget(AbstractWidget widget) {
        widgetSettings.addWidget(widget);
    }

    /**
     * Gets the ordered list with shortcuts from the model. This is the order in which they should be displayed
     * in the {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer}.
     *
     * @return The ordered list of shortcuts.
     */
    public List<AbstractWidget> getOrderedShortcuts() {
        return widgetSettings.getOrderedShortcuts();
    }

    /**
     * Gets the ordered list with widgets from the model. This is the order in which they should be displayed
     * in the {@link org.ssh.ui.windows.WidgetWindow}.
     *
     * @return The ordered list of widgets.
     */
    public List<AbstractWidget> getOrderedWidgetsForWindow() {
        return widgetSettings.getOrderedWidgetsForWindow();
    }

    /**
     * Swaps the order of two given widgets.
     *
     * @param widgetIndex1 The index of the Widget in the {@link org.ssh.ui.windows.WidgetWindow}.
     * @param widgetIndex2 The other index of the Widget in the {@link org.ssh.ui.windows.WidgetWindow}.
     */
    public void swapWindowOrder(int widgetIndex1, int widgetIndex2) {
        widgetSettings.swapWindowOrder(widgetIndex1, widgetIndex2);
    }

    /**
     * Swaps the order of two given shortcuts.
     *
     * @param shortcutIndex1 The index of the shortcut in the {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer}.
     * @param shortcutIndex2 The other index of the shortcut in the {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer}.
     */
    public void swapShortcutOrder(int shortcutIndex1, int shortcutIndex2) {
        widgetSettings.swapShortcutOrder(shortcutIndex1, shortcutIndex2);
    }

    /**
     * Getter for the number of widgets.
     *
     * @return The number of widgets.
     */
    public int getNumberOfWidgets() {
        return widgetSettings.getNumberOfWidgets();
    }
}
