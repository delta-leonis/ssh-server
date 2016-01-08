package org.ssh.models;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.ssh.ui.components.widget.AbstractWidget;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * cols = shortcuts
 * rows = window
 *
 */
public class WidgetSettings extends Model {

    private Table<Integer, Integer, AbstractWidget> widgets;

    public WidgetSettings() {
        super("widgetsettings", "");
    }

    /**
     * Should contains all initial values as declared normally in the constructor
     */
    @Override public void initialize() {
        widgets = HashBasedTable.create();
    }

    public List<AbstractWidget> getOrderedShortcuts() {
        return widgets.columnMap().entrySet().stream()
                .map(entry -> entry.getValue().entrySet())
                .flatMap(Collection::stream)
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }

    public List<AbstractWidget> getOrderedWidgetsForWindow() {
        return widgets.rowMap().entrySet().stream()
                .map(entry -> entry.getValue().entrySet())
                .flatMap(Collection::stream)
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }

    public void swapWindowOrder(int row1, int row2) {
        Integer col1 = widgets.row(row1).keySet().stream().findFirst().get();
        Integer col2 = widgets.row(row2).keySet().stream().findFirst().get();

        AbstractWidget widget1 = widgets.get(row1, col1);
        AbstractWidget widget2 = widgets.get(row2, col2);

        widgets.remove(row1, col1);
        widgets.remove(row2, col2);

        widgets.put(row2, col1, widget1);
        widgets.put(row1, col2, widget2);
    }

    public void swapShortcutOrder(int col1, int col2) {
        Integer row1 = widgets.column(col1).keySet().stream().findFirst().get();
        Integer row2 = widgets.column(col2).keySet().stream().findFirst().get();

        AbstractWidget widget1 = widgets.get(row1, col1);
        AbstractWidget widget2 = widgets.get(row2, col2);

        widgets.remove(row1, col1);
        widgets.remove(row2, col2);

        widgets.put(row1, col2, widget1);
        widgets.put(row2, col1, widget2);
    }

    public void addWidget(AbstractWidget widget) {
        widgets.put(widgets.size(), widgets.size(), widget);
    }

    public int getNumberOfWidgets() {
        return widgets.size();
    }

}