package org.ssh.ui.windows;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ssh.ui.UIController;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The Class WidgetWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class WidgetWindow extends UIController<FlowPane> {

    /**
     * The singleton instance from this class.
     */
    private static WidgetWindow instance;

    /**
     * The {@link FlowPane} that contains all the widgets as children.
     */
    @FXML
    private FlowPane widgetcontainer;

    /**
     * Instantiates a new WidgetWindow.
     *
     */
    private WidgetWindow() {
        super("widgetwindow", "widgetwindow.fxml", 800, 600);
        // setup in here
        this.getStage().setOnCloseRequest(event -> hideWidgetWindow());
        this.getStage().initStyle(StageStyle.UTILITY);
        this.getStage().setAlwaysOnTop(true);
        widgetcontainer.setRowValignment(VPos.TOP);
        // spawn the window
        this.spawnWindow();
    }

    /**
     * Function to get the instance of the singleton {@link WidgetWindow}.
     * @return The singleton instance of {@link WidgetWindow}
     */
    public static WidgetWindow getInstance() {
        if (instance == null) instance = new WidgetWindow();
        return instance;
    }

    /**
     * Finds second biggest screen and than displays the {@link WidgetWindow} fullscreen on it.
     *
     * @param primaryStage The {@Link Stage} where the {@link MainWindow} is displayed
     */
    public void showWidgetWindow(Stage primaryStage) {
        // Find the screen of the stage with the main window
        ObservableList<Screen> stagepoint = Screen.getScreensForRectangle(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(),
                primaryStage.getHeight());
        // There is only one, so the first one of the list should be the screen with the main window
        Screen applicationscreen = stagepoint.get(0);
        // Find the biggest screen
        BinaryOperator<Screen> reduceBiggestScreen = (biggest,
                                                      iterator) -> (iterator.getBounds().getWidth()
                        * iterator.getBounds().getHeight() > biggest.getBounds().getWidth()
                                * biggest.getBounds().getHeight()) ? iterator : biggest;
        // The second screen is the should be the biggest without counting the one already used for the main window
        Screen secondscreen = Screen.getScreens().stream().filter(screen -> !screen.equals(applicationscreen))
                .reduce(Screen.getPrimary(), reduceBiggestScreen);

        // Let the stage start at within the second screen
        this.getStage().setX(secondscreen.getVisualBounds().getMinX());
        this.getStage().setY(secondscreen.getVisualBounds().getMinY());
        // Set full screen
        this.getStage().setMaximized(true);
        // Show the stage
        this.show();
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
     * @return All the {@link StackPane}s in the {@link #widgetcontainer}, this are the draggable widgets.
     */
    public List<StackPane> getDraggableWidgets() {
        // Make a stream of all the children
        return widgetcontainer.getChildren().stream()
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
    public FlowPane getWidgetcontainer() {
        return widgetcontainer;
    }
}
