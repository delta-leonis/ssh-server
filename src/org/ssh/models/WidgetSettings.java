package org.ssh.models;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.ssh.ui.components.widget.AbstractWidget;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link WidgetSettings} is a model containing the widgets. For all widgets we want to know the order in which they
 * should be presented in the {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer} and in the
 * {@link org.ssh.ui.windows.WidgetWindow}. To store the order of both in combination with the widget itself we put
 * all widgets in a {@link Table}. The column of the {@link Table} represents the index in the list of shortcuts and
 * the row of the {@link Table} represents the index in the list of widgets for the
 * {@link org.ssh.ui.windows.WidgetWindow}.
 *
 * This class contains the functions to access the table of widgets.
 *
 * @author Joost Overeem
 */
public class WidgetSettings extends AbstractModel {

    /**
     * A {@link Table} that has {@link Integer}s telling in which row and column the {@link AbstractWidget} is.
     * The first {@link Integer} represents the row and the order of the widgets in the
     * {@link org.ssh.ui.windows.WidgetWindow}. The second {@link Integer} represents the column
     * and the order of the shortcuts in the {@link org.ssh.ui.components.bottomsection.WidgetShortcutContainer}.
     *
     * Both rows and columns are only for one widget. The put and swap functions are responsible for not adding
     * a widget in a row or column that is already used.
     *
     * <ol>
     *     <li>COLS = shortcuts</li>
     *     <li>ROWS = WidgetWindow</>
     * </ol>
     */
    private Table<Integer, Integer, AbstractWidget> widgets;

    /**
     * Constructor calls only the super constructor.
     */
    public WidgetSettings() {
        super("widgetsettings", "");
    }

    /**
     * Initializes {@link #widgets} as a {@link HashBasedTable}.
     */
    @Override
    public void initialize() {
        widgets = HashBasedTable.create();
    }

    /**
     * Makes a list of {@link AbstractWidget}s in the order in which the shortcuts should be presented.
     *
     * @return The {@link AbstractWidget}s as a {@link List} in the order in which the shortcuts should be presented.
     */
    public List<AbstractWidget> getOrderedShortcuts() {
        // stream the columns
        return widgets.columnMap().entrySet().stream()
                // make a map of it with the widgets as
                .map(entry -> entry.getValue().entrySet())
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Makes a list of {@link AbstractWidget}s in the order in which the widgets should be presented.
     *
     * @return The {@link AbstractWidget}s as a {@link List} in the order in which the widgets should be presented.
     */
    public List<AbstractWidget> getOrderedWidgetsForWindow() {
        // stream the rows
        return widgets.rowMap().entrySet().stream()
                .map(entry -> entry.getValue().entrySet())
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Swaps the order of 2 widgets in the {@link org.ssh.ui.windows.WidgetWindow}, so swaps 2 rows.
     *
     * @param row1 A row index.
     * @param row2 Another row index.
     */
    public void swapWindowOrder(int row1, int row2) {
        // Get the column of the widget in the first given row
        Integer col1 = widgets.row(row1).keySet().stream().findFirst().get();
        // Get the column of the widget in the second given row
        Integer col2 = widgets.row(row2).keySet().stream().findFirst().get();

        // Get the first widget
        AbstractWidget widget1 = widgets.get(row1, col1);
        // Get the second widget
        AbstractWidget widget2 = widgets.get(row2, col2);

        // Remove the widgets from the table
        widgets.remove(row1, col1);
        widgets.remove(row2, col2);

        // Put the first widget on his new place in the table
        widgets.put(row2, col1, widget1);
        // Put the second widget on his new place in the table
        widgets.put(row1, col2, widget2);
    }

    /**
     * Swaps the order of 2 shortcuts, so swaps the given columns.
     *
     * @param col1 A column index.
     * @param col2 Another column index.
     */
    public void swapShortcutOrder(int col1, int col2) {
        // Get the row of the widget in the first given column
        Integer row1 = widgets.column(col1).keySet().stream().findFirst().get();
        // Get the row of the widget in the second given column
        Integer row2 = widgets.column(col2).keySet().stream().findFirst().get();

        // Get the first widget
        AbstractWidget widget1 = widgets.get(row1, col1);
        // Get the second widget
        AbstractWidget widget2 = widgets.get(row2, col2);

        // Remove the widgets from the table
        widgets.remove(row1, col1);
        widgets.remove(row2, col2);

        // Put the first widget on his new place in the table
        widgets.put(row1, col2, widget1);
    }

    /**
     * Adds a widget to the {@link Table} of widgets.
     *
     * @param widget The {@link AbstractWidget} to be added.
     */
    public void addWidget(AbstractWidget widget) {
        widgets.put(widgets.size(), widgets.size(), widget);
    }

    /**
     * Getter for the number of widgets.
     *
     * @return The number of widgets.
     */
    public int getNumberOfWidgets() {
        return widgets.size();
    }

}