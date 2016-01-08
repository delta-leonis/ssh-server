package org.ssh.ui.components.bottomsection;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.widget.AbstractWidget;
import org.ssh.ui.components.widget.ShortcutDraggable;
import org.ssh.ui.components.widget.WidgetShortcut;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Component that contains a {@link ScrollPane} with shortcuts to all the existing
 * {@link AbstractWidget widget}s.
 *
 * @author Joost Overeem
 */
public class WidgetShortcutContainer extends UIComponent<Pane> {

    /**
     * The {@link Pane root pane} of this component. It is there because the root
     * always must inherit region, so cant be a {@link ScrollPane}.
     */
    @FXML
    private Pane rootPane;

    /**
     * The {@link ScrollPane} to scroll through the {@link #shortcutPane} to see all shortcuts.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * The {@link FlowPane} that contains all the draggable shortcuts as children.
     */
    @FXML
    private FlowPane shortcutPane;

    /**
     * The number of columns of shortcuts to be displayed
     */
    private double numberOfColumns;
    /**
     * The horizontal summed padding of the {@link #shortcutPane}
     */
    private double hpadding;
    /**
     * The horizontal space between all the children of {@link #shortcutPane}
     */
    private double hgap;
    /**
     * The vertical summed padding of the {@link #shortcutPane}
     */
    private double vpadding;
    /**
     * The vertical space between all the children of {@link #shortcutPane}
     */
    private double vgap;

    /**
     * Constructor for the {@link WidgetShortcutContainer}. Makes the component and fills it with
     * {@link WidgetShortcut}s by calling {@link #displayWidgets()} in {@link Platform#runLater(Runnable)}.
     */
    public WidgetShortcutContainer() {
        // Call the super
        super("shortcutcontainer", "bottomsection/widgetshortcutcontainer.fxml");

        // Initialise the number of columns on 5
        numberOfColumns = 5.0;
        // Retrieve the horizontal padding from the fxml
        hpadding = shortcutPane.getPadding().getLeft() + shortcutPane.getPadding().getRight();
        // Retrieve the horizontal spacing from the fxml
        hgap = shortcutPane.getHgap();
        // Retrieve the vertical padding from the fxml
        vpadding = shortcutPane.getPadding().getTop() + shortcutPane.getPadding().getBottom();
        // Retrieve the vertical spacing from the fxml
        vgap = shortcutPane.getVgap();

        // Bind the size of the scrollPane to the rootPane
        UI.bindSize(scrollPane, rootPane);
        // We only want to scroll vertically
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Bind the width of the shortcutPane to the scrollBar, this is
        // because scrolling is only vertically and we don't want shortcuts
        // to be displayed at the right just outside the visible bounds of
        // the pane
        shortcutPane.minWidthProperty().bind(scrollPane.widthProperty());
        shortcutPane.maxWidthProperty().bind(scrollPane.widthProperty());

        // Display the widgets
        Platform.runLater(this::displayWidgets);
    }

    /**
     * Queries all the widgets ordered for the {@link WidgetShortcutContainer} and then loops
     * through the widget to add a shortcut for every widget to the children of {@link #shortcutPane}.
     */
    private void displayWidgets() {
        // Get the list of ordered widgets for shortcut purposes
        List<AbstractWidget> widgets = UI.getOrderedWidgetsForShortcuts();
        // Loop through all the widgets
        for(AbstractWidget widget : widgets) {
            // Make a shortcut with the right category
            WidgetShortcut shortcut = new WidgetShortcut(widget.getName(), widget.getCategory());
            // And put it in a draggable
            ShortcutDraggable draggable = new ShortcutDraggable(shortcut, widget);

            // Set the width of the shortcut depending to the width of the containter
            draggable.getComponent().minWidthProperty().bind(
                    shortcutPane.widthProperty().subtract(hpadding + hgap * (numberOfColumns - 1)).divide(numberOfColumns).subtract(1));

            // Set the height of the shortcuts 70 px
            draggable.getComponent().setMinHeight(70.0);
            draggable.getComponent().setMaxHeight(70.0);

            // Set the height of the shortcutPane, in runLater because then the used components have their size
            Platform.runLater(() -> {
                // Math.ceil is because we want to display the last shortcut completely in stead
                // of e.g. one fifth
                shortcutPane.setMinHeight(vpadding / 2 + (Math.ceil(widgets.size() / numberOfColumns) *
                        (draggable.getComponent().getHeight() + vgap)));
                shortcutPane.setMaxHeight(vpadding / 2 + (Math.ceil(widgets.size() / numberOfColumns) *
                        (draggable.getComponent().getHeight()) + vgap));
            });
            // Add the draggable to the children of the shortcutPane
            shortcutPane.getChildren().add(draggable.getComponent());
        }
    }

    /**
     * Getter for a {@link List} of {@link StackPane}s that contain shortcuts to widgets.
     *
     * @return All the {@link StackPane}s in the {@link #shortcutPane}, this are the draggable shortcuts
     *              for the widgets.
     */
    public List<StackPane> getDraggableWidgets() {
        // Make a stream of all the children
        return shortcutPane.getChildren().stream()
                // Get only the StackPanes in case there is something else
                .filter(StackPane.class::isInstance)
                // Cast them to StackPanes because children are nodes, would not fail because
                // there is filtered on the StackPane instance
                .map(node -> StackPane.class.cast(node))
                // Collect it to a list of StackPanes
                .collect(Collectors.toList());
    }

    /**
     * Getter for the {@link FlowPane} that contains all the shortcuts to widgets.
     *
     * @return The {@link FlowPane} where all widgets are displayed in as children.
     */
    public FlowPane getWidgetcontainer() {
        return shortcutPane;
    }
}
