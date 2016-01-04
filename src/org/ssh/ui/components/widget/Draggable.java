package org.ssh.ui.components.widget;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ssh.managers.manager.UI;
import org.ssh.ui.windows.WidgetWindow;
import org.ssh.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * {@link Draggable} is a class for making a widget draggable in the {@link WidgetWindow}. Besides dragging
 * the widget around in the {@link WidgetWindow}, it also places widgets in an own stage when dragged outside
 * the {@link WidgetWindow}.
 *
 * @author Joost Overeem
 */
public class Draggable {

    /**
     * The logger of this class
     */
    private Logger LOG = Logger.getLogger();

    /**
     * Boolean for if we are allowed to drag the widget.
     * @see {@link WidgetResizer#drag(MouseEvent)}
     */
    private boolean blockDragging;
    /**
     * Boolean for if content is displayed in the {@link WidgetWindow} or in an own {@link Stage}.
     */
    private boolean toOwnStage;

    /**
     * X position of the {@link Stage} that wraps the content when displayed in an own {@link Stage}.
     */
    private double toOwnStageX;
    /**
     * X position of the {@link Stage} that wraps the content when displayed in an own {@link Stage}.
     */
    private double toOwnStageY;

    /**
     * Constructor for {@link Draggable}. Sets the widget inside the {@link WidgetWindow}.
     *
     * @param content
     *          The widget that has to be draggable.
     */
    public Draggable(Pane content) {
        // Set the widget in the WidgetWindow
        setInWidgetPane(content);
        // Initialize to own stage false because it starts in WidgetWindow
        toOwnStage = false;
    }

    /**
     * "To block or not to block, that is the question." Sets the {@link #blockDragging}.
     * @param block
     *              Whether to block the dragging or not.
     */
    public void setBlockDragging(boolean block) {
        // Set the blockDragging
        blockDragging = block;
    }

    /**
     * Sets the Widget given in the parameter as content in a new draggable {@link StackPane}.
     * It adds an overlaying resize icon in the left bottom corner for resizing purposes in the
     * {@link WidgetWindow}.
     *
     * @param contentPane
     *              The Widget that is to be draggable in the {@link WidgetWindow}.
     */
    protected void setInWidgetPane(Pane contentPane) {
        // Make dragPane to put content in, content is the real widget
        StackPane dragPane = new StackPane(contentPane);
        // Bind the height and width properties so that the dragPane has the same size
        // as the content
        dragPane.minHeightProperty().bind(contentPane.heightProperty());
        dragPane.maxHeightProperty().bind(contentPane.heightProperty());
        dragPane.minWidthProperty().bind(contentPane.widthProperty());
        dragPane.maxWidthProperty().bind(contentPane.widthProperty());
        // Add resize icon to the dragPane
        addResizingButton(dragPane, contentPane);
        // Add handlers for the dragging on the dragPane
        addDragHandlers(dragPane, contentPane);
        // Add the dragPane to the WidgetWindow
        WidgetWindow.getInstance().getWidgetcontainer().getChildren().add(dragPane);
    }

    /**
     * Adds an overlaying {@link WidgetResizer} to the draggable pane with an icon with which
     * the draggable pane can be resized.
     *
     * @param dragPane
     *              The {@link StackPane} where the overlaying {@link WidgetResizer} should be
     *              added to.
     */
    private void addResizingButton(StackPane dragPane, Pane contentPane) {
        // Make the resizer overlay
        WidgetResizer resizer = new WidgetResizer(this, contentPane);
        // Add it to the children of the dragPane, this results in the resizer being on top
        dragPane.getChildren().add(resizer.getComponent());
        // Bind the height and width properties so that the overlay resizes properly
        UI.bindSize(resizer.getComponent(), dragPane);
//        resizer.minHeightProperty().bind(dragPane.heightProperty());
//        resizer.maxHeightProperty().bind(dragPane.heightProperty());
//        resizer.minWidthProperty().bind(dragPane.widthProperty());
//        resizer.maxHeightProperty().bind(dragPane.widthProperty());
    }

    /**
     * Function for adding handlers to a {@link Pane} that should be draggable. Adds three handlers:
     * <li>
     *     <ul>Handler that makes the {@link Pane} transparent while dragging it.</ul>
     *     <ul>Handler for when the mou is released that checks if the {@link Pane}
     *     should move to an own stage.</ul>
     *     <ul>Handler for handling the dragging itself.</ul>
     * </li>
     *
     * @param dragPane
     *              A {@link StackPane} that is the pane whose events should be caught and handled.
     * @param contentPane
     *              The {@link Pane} that is the Widget to be displayed.
     */
    private void addDragHandlers(StackPane dragPane, Pane contentPane) {
        // Check if the dragPane is really a dragPane with a widget in it
        if(!dragPane.getChildren().stream().anyMatch(child -> child instanceof ExampleWidget)) {
            // Log that shit went wrong
            LOG.warning("A dragPane without a Widget as content tried to add draghandlers");
            // Return to prevent risk of getting broke
            return;
        }
        // Add handler for when drag is detected
        dragPane.setOnDragDetected(event -> {
            // First check if we are allowed to drag the widget
            if(blockDragging) return;
            // Make it transparent while dragging
            ((StackPane)event.getSource()).setOpacity(0.25);
            // Give signal that processing of event is complete
            event.consume();
        });
        // add handler for when mouse is released
        dragPane.setOnMouseReleased(event -> {
                // First check if we are allowed to drag the widget
                if(blockDragging) return;
                // Set the opacity back to 1.0
                ((StackPane)event.getSource()).setOpacity(1.0);
                // If it is dragged out of the WidgetWindow..
                if(toOwnStage) {
                    // ..set it in an own stage at the point of the mouse
                    setInExternalStage(contentPane, toOwnStageX, toOwnStageY);
                    // remove the dragPane from the WidgetWindow because it is in external stage now
                    WidgetWindow.getInstance().getChildren().remove(dragPane);
                    // set the boolean false again
                    toOwnStage = false;
                }
                // Give signal that processing of event is complete
                event.consume();
        });
        // Drag the pane when dragging
        dragPane.setOnMouseDragged(event -> {
            // First check if we are allowed to drag the widget
            if(blockDragging) return;
            drag(dragPane, event);
        });
    }

    /**
     * Sets the widget in an own {@link Stage}.
     * @param contentPane
     *              The widget that is to be displayed.
     * @param x
     *              The x coordinate on the screen.
     * @param y
     *              The y coordinate on the screen.
     */
    protected void setInExternalStage(Pane contentPane, double x, double y) {
        // Create new stage for widget
        Stage widgetstage = new Stage();
        // Create root pane for the scene (this for automatic resizing)
        Pane root = new Pane();
        // Create scene with the root pane as root, size of the scene is the pref size of the widget
        Scene widgetscene = new Scene(root, contentPane.getPrefWidth(), contentPane.getPrefHeight());
        // Set the widget in the root
        root.getChildren().add(contentPane);
        // Let the widget keep the same width as the root pane
        root.widthProperty().addListener((observable, oldValue, newValue) ->
            contentPane.setPrefWidth((double) newValue));
        // Let the widget keep the same height as the root pane
        root.heightProperty().addListener((observable, oldValue, newValue) ->
            contentPane.setPrefHeight((double) newValue));
        // Set the scene of the stage
        widgetstage.setScene(widgetscene);
        // Let the stage fit around the scene, so the scene size is determinative
        widgetstage.sizeToScene();
        // Make it a utility so that when the MainWindow is shut down this one is too and there are no
        // extra icons in the task bar
        widgetstage.initStyle(StageStyle.UTILITY);
        // Always on top because it should be displayed on top of the main stage or other stages
        widgetstage.setAlwaysOnTop(true);
        // Set the position on where the mouse was released
        widgetstage.setX(x);
        widgetstage.setY(y);
        // Show the stage
        widgetstage.show();
        // When closed it should be put back in the WidgetWindow
        widgetstage.setOnCloseRequest(event -> setInWidgetPane(contentPane));
    }

    /**
     * Handles the dragging of a dragging pane.
     *
     * @param draggingPane
     *              The {@link Pane} that is dragged
     * @param event
     *              The {@link MouseEvent} of the dragging
     */
    private void drag(StackPane draggingPane, MouseEvent event) {
        // See if the dragging is outside the WidgetWindow or inside the window
        // Get the bounds of the node where the dragging pane is in, then see if it
        // contains the x and y coordinates of the mouse event
        if(!draggingPane.getParent().getLayoutBounds().contains(event.getSceneX(), event.getSceneY())) {
            // Outside, so we go to own stage when released
            toOwnStage = true;
            // Set the position to drop the stage when mouse released
            // y should be y of the mouse event, but from the x the half of the
            // width of the dragging pane should be subtracted to release it centered
            toOwnStageY = event.getScreenY();
            toOwnStageX = event.getScreenX() - draggingPane.getWidth() / 2;
        }
        else {
            // Inside, so we do not go to an own stage when mouse released
            toOwnStage = false;

            // First get the list of draggable widgets to prevent a lack of calls to the WidgetWindow
            List<StackPane> draggablePanes = WidgetWindow.getInstance().getDraggableWidgets();
            // And so for the content of the reference to the children of the widget container too
            ObservableList widgetContainerChildren = WidgetWindow.getInstance().getWidgetcontainer().getChildren();

            // Find the StackPane we are dragging over
            Optional<StackPane> optionalDraggedOver = draggablePanes.parallelStream()
                    .filter(nodeC -> nodeC.getBoundsInParent().contains(event.getSceneX(), event.getSceneY())).findFirst();
            // See if we are dragging over an other node
            if(optionalDraggedOver.isPresent()) {
                // If so, we get it
                StackPane draggedOver = optionalDraggedOver.get();

                // Get the index of the child where is dragged over, the index is the order in the
                // FlowPane from left to right like in a text
                int draggedOverIndex = widgetContainerChildren.indexOf(
                        widgetContainerChildren.parallelStream()
                                .filter(nodeC -> nodeC.equals(draggedOver)).findFirst().get());
                // Also get the index of the child that is dragged
                int draggedIndex = widgetContainerChildren.indexOf(
                        widgetContainerChildren.parallelStream()
                                .filter(nodeC -> nodeC.equals(draggingPane)).findFirst().get());

                // We want to know if we are dragging over the right or left side of the widget where we are dragging over
                Side sideWhereMouseIsInDraggedOver = draggedOver.getBoundsInParent().getMaxX() - event.getSceneX()
                        < draggedOver.getBoundsInParent().getWidth() / 2 ? Side.RIGHT : Side.LEFT;
                // We also want to know in what direction we are dragging relative to the dragged widget
                // We make it null so that we only have a direction when we are really dragging far enough
                Side horizontalDragDirectionTo = null;
                // if we are dragging over another widget
                if (draggedIndex - draggedOverIndex != 0)
                    // See via the index if we are moving up or down
                    horizontalDragDirectionTo = draggedIndex - draggedOverIndex < 0 ? Side.RIGHT : Side.LEFT;

                // Now we are going to do the same for the vertical direction as done for the horizontal direction

                // We want to know if we are dragging over the top or bottom side of the widget where we are dragging over
                Side dragSideOnNodeY = draggedOver.getBoundsInParent().getMaxY() - event.getSceneY()
                        < draggedOver.getBoundsInParent().getHeight() / 2 ? Side.BOTTOM : Side.TOP;
                // We want to know in what direction we are dragging relative to the dragged widget
                // We make it null so that we only have a direction when we are really dragging far enough
                Side verticalDragDirectionTo = null;
                // if we are dragging over another widget
                if (draggedIndex - draggedOverIndex != 0) {
                    // See via the index if we are moving up or down
                    verticalDragDirectionTo = draggedIndex - draggedOverIndex < 0 ? Side.BOTTOM : Side.TOP;
                }

                // Now we know all the directions and sides of surrounding widgets we are dragging over,
                // We want to know if we are dragging vertically or horizontally

                // Take vertical as default and then check if it is not horizontal (vertical in stead of null
                // can be done because we do not let the dragging depend on whether there is a direction or not)
                Orientation draggingOrientation = Orientation.VERTICAL;
                // Now see if we are dragging horizontally
                if (draggingPane.getBoundsInParent().getMinY() < event.getSceneY()
                        && draggingPane.getBoundsInParent().getMaxY() > event.getSceneY())
                    // If so, set the dragging orientation horizontally
                    draggingOrientation = Orientation.HORIZONTAL;

                // Now we know everything that we need, we go and see if we have to swap widgets or not

                // If we are dragging horizontally and the dragging widget is not the same as the widget
                // We are dragging over a swap could be desirable in horizontal direction
                if (draggingOrientation == Orientation.HORIZONTAL && draggedOver != draggingPane) {
                    // If the side of the widget we are dragging over is the opposite side from the side we are
                    // dragging from, we do the swap
                    if (sideWhereMouseIsInDraggedOver.equals(horizontalDragDirectionTo)) {
                        // We copy the list of children to a working copy, because we cant edit the children as
                        // we want to
                        ObservableList workingCollection = FXCollections.observableArrayList(widgetContainerChildren);
                        // Swap them in the temporary list
                        workingCollection.add(draggedOverIndex, workingCollection.remove(draggedIndex));
                        // Set the list where the swapping is done as the children
                        widgetContainerChildren.setAll(workingCollection);
                    }
                }
                else {
                    // We know now that if we have to swap, it is in a vertical direction, so now check if we
                    // have to swap.
                    // If the side of the widget we are dragging over is the opposite side from the side we are
                    // dragging from, we do the swap
                    if (dragSideOnNodeY.equals(verticalDragDirectionTo) && draggedOver != draggingPane) {
                        // We copy the list of children to a working copy, because we cant edit the children as
                        // we want to
                        ObservableList workingCollection = FXCollections.observableArrayList(widgetContainerChildren);
                        // Swap them in the temporary list
                        workingCollection.add(draggedOverIndex, workingCollection.remove(draggedIndex));
                        // Set the list where the swapping is done as the children
                        widgetContainerChildren.setAll(workingCollection);
                    }
                }
            }

        }
    }
}
