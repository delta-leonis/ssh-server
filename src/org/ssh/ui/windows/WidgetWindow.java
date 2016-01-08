package org.ssh.ui.windows;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIController;
import org.ssh.ui.components.widget.AbstractWidget;
import org.ssh.ui.components.widget.WidgetDraggable;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Window that contains a {@link ScrollPane} with all the existing
 * {@link AbstractWidget widget}s in it.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class WidgetWindow extends UIController<Pane> {

    /**
     * The singleton instance from this class.
     */
    private static WidgetWindow instance;

    /**
     * The {@link Pane root pane} of this Stage. It is there because the root
     * always must inherit region, so cant be a {@link ScrollPane}.
     */
    @FXML
    private Pane rootPane;

    /**
     * The {@link ScrollPane} to scroll through the {@link #widgetPane} to see all widgets.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * The {@link FlowPane} that contains all the widgets as children.
     */
    @FXML
    private FlowPane widgetPane;

    /**
     * Function to getUIController the instance of the singleton {@link WidgetWindow}.
     * @return The singleton instance of {@link WidgetWindow}
     */
    public static WidgetWindow getInstance() {
        if (instance == null) instance = new WidgetWindow();
        return instance;
    }

    /**
     * Singleton constructor for the {@link WidgetWindow}. It creates the stage and scene via
     * a super call and then calls {@link #displayWidgets()}.
     */
    private WidgetWindow() {
        // Call super. Size is randomly chosen, may be changed
        super("widgetwindow", "widgetwindow.fxml", 800, 600);

        // We want to hide the WidgetWindow when closed, not remove it
        this.getStage().setOnCloseRequest(event -> hideWidgetWindow());
        // We make it a utility because it one
        this.getStage().initStyle(StageStyle.UTILITY);
        // We set it always on top to not lose the important window
        this.getStage().setAlwaysOnTop(true);

        // We want to align the widgets all top-left
        widgetPane.setRowValignment(VPos.TOP);
        // Bind the size of the scrollPane to the rootPane
        UI.bindSize(scrollPane, rootPane);
        // We only want to scroll vertically
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Bind the width of the widgetPane to the scrollBar, this is
        // because scrolling is only vertically and we dont want widgets
        // to be displayed at the right just outside the visible bounds of
        // the window
        widgetPane.minWidthProperty().bind(scrollPane.widthProperty());
        widgetPane.maxWidthProperty().bind(scrollPane.widthProperty());

        // Display the widgets
        displayWidgets();
    }

    /**
     * Queries all the widgets ordered for the {@link WidgetWindow} and then loops
     * through the widget to add every widget to the children of {@link #widgetPane}.
     */
    private void displayWidgets() {
        // Get the widgets on the right order
        List<AbstractWidget> widgets = UI.getOrderedWidgetsForWindow();
        // Loop through them all
        for(AbstractWidget widget : widgets) {
            // Make the widget draggable
            WidgetDraggable draggable = new WidgetDraggable(widget);
            // And add the draggable widget to the widgetPane's children
            widgetPane.getChildren().add(draggable.getComponent());
        }
    }

    /**
     * Finds second biggest screen and than displays the {@link WidgetWindow} fullscreen on it.
     *
     * @param primaryStage The {@Link Stage} where the {@link MainWindow} is displayed
     */
    public void showWidgetWindow(Stage primaryStage) {
        // Find the screen of the stage with the main window
        // Get the screens for the rectangle that has the position and size of the primarystage
        ObservableList<Screen> stagepoint = Screen.getScreensForRectangle(
                primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
        // There could be more, but we just take the first screen where the stage is displayed
        Screen applicationscreen = stagepoint.get(0);
        // Find the biggest screen
        BinaryOperator<Screen> reduceBiggestScreen = (biggest, iterator) -> (
                // Compare the total screensize of the iterator to the biggest screen found
                iterator.getBounds().getWidth() * iterator.getBounds().getHeight() >
                        biggest.getBounds().getWidth() * biggest.getBounds().getHeight())
                // Return the biggest one
                ? iterator : biggest;
        // The second screen should be the biggest without counting the one already used for the main window
        // Make a stream of the screens
        Screen secondscreen = Screen.getScreens().stream()
                // Filter the screens that are application screen out
                .filter(screen -> !screen.equals(applicationscreen))
                // Then get the biggest
                .reduce(Screen.getPrimary(), reduceBiggestScreen);

        // Let the stage start at within the second screen
        this.getStage().setX(secondscreen.getVisualBounds().getMinX());
        this.getStage().setY(secondscreen.getVisualBounds().getMinY());
        // Set full screen
        this.getStage().setMaximized(true);
        // Show the stage
        this.spawnWindow();
    }

    /**
     * Hides the {@link WidgetWindow}.
     */
    public void hideWidgetWindow() {
        // Hide the stage
        this.hide();
    }

    /**
     * Getter for a {@link List} of {@link StackPane}s that contain widgets
     *
     * @return All the {@link StackPane}s in the {@link #widgetPane}, this are the draggable widgets.
     */
    public List<StackPane> getDraggableWidgets() {
        // Make a stream of all the children
        return widgetPane.getChildren().stream()
                // Get only the StackPanes in case there is something else
                .filter(StackPane.class::isInstance)
                // Cast them to StackPanes because children are nodes, would not fail because
                // there is filtered on the StackPane instance
                .map(node -> StackPane.class.cast(node))
                // Collect it to a list of StackPanes
                .collect(Collectors.toList());
    }

    /**
     * Getter for the {@link FlowPane} that contains all the Widgets.
     *
     * @return The {@link FlowPane} where all widgets are displayed in as children.
     */
    public FlowPane getWidgetPane() {
        return widgetPane;
    }
}
