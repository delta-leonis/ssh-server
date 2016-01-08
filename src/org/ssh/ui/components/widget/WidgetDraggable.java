package org.ssh.ui.components.widget;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.windows.WidgetWindow;
import org.ssh.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * {@link WidgetDraggable} is a class for making a {@link AbstractWidget widget} draggable in the
 * {@link WidgetWindow}. Besides dragging the widget around in the {@link WidgetWindow}, it also
 * places the {@link AbstractWidget}s in an own stage when dragged outside the {@link WidgetWindow}.
 *
 * @author Joost Overeem
 */
public class WidgetDraggable extends UIComponent<StackPane> {

    /**
     * The logger of this class
     */
    private final Logger LOG = Logger.getLogger();

    /**
     * The {@link StackPane} that contains the widget and can be dragged.
     */
    @FXML
    private StackPane dragPane;

    /**
     * The {@link AbstractWidget} that is the widget in the {@link #dragPane} that is dragged.
     */
    private AbstractWidget content;

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
     * Constructor for {@link WidgetDraggable}.
     *
     * @param content
     *          The {@link AbstractWidget} that has to be draggable.
     */
    public WidgetDraggable(AbstractWidget content) {
        // name is depending on the content name, this way not all draggables have the same name
        // The file draggable.fxml is used by the other draggables too, so watch out with editing!
        super("draggable for " + content.getName(), "widget/draggable.fxml");

        // Set the widget as the content variable
        this.content = content;
        // And add it to the dragPane
        dragPane.getChildren().add(content.getComponent());
        // The size of the widget should determine what the size of the dragpane is,
        // so that is why the these sizes are set
        dragPane.setMinSize(content.getComponent().getMinWidth(), content.getComponent().getMinHeight());
        // And that is also, why the parent node is bound to the child node in stead of the other
        // way around. The runLater() is to keep the effect of the sizing to minWidth that we have just done
        Platform.runLater(() -> UI.bindSize(dragPane, content.getComponent()));

        // Add the handlers for the dragging events
        addDragHandlers();
        // Add a resize button for resizing the widgets within the WidgetWindow
        addResizingButton();
        // Initialize to own stage false because it starts in WidgetWindow
        toOwnStage = false;
    }

    /**
     * "To block or not to block, that is the question." Sets the {@link #blockDragging}.
     * @param block
     *              Whether to block the dragging or not.
     * @see #blockDragging
     */
    public void setBlockDragging(boolean block) {
        // Set the blockDragging
        blockDragging = block;
    }

    /**
     * Sets the {@link AbstractWidget content} back in the {@link #dragPane} and sets the
     * {@link #dragPane} in the {@link WidgetWindow}.
     */
    protected void setInWidgetPane() {
        // Check for duplicate adding before adding
        // Start making a stream of the children..
        if(!dragPane.getChildren().stream()
                // Then filter the node that is the content
                .filter(node -> node.equals(content.getComponent()))
                // Then see if there is any
                .findFirst().isPresent())
            // If not, we add the content to the dragPane
            dragPane.getChildren().add(content.getComponent());

        // Check for duplicate adding before adding
        // Start making a stream of the children..
        if(!WidgetWindow.getInstance().getWidgetPane().getChildren().stream()
                // Then filter the node that is the dragPane
                .filter(node -> node.equals(dragPane))
                // Then see if there is any
                .findFirst().isPresent())
            // If not, we add the dragPane to the WidgetWindow
            WidgetWindow.getInstance().getWidgetPane().getChildren().add(dragPane);
        LOG.fine("Widget is set in the WidgetWindow");
    }

    /**
     * Adds an overlaying {@link WidgetResizer} to the {@link #dragPane} with an icon with which
     * the draggable pane can be resized.
     */
    private void addResizingButton() {
        // Make the resizer overlay
        WidgetResizer resizer = new WidgetResizer(this, content);
        // Add it to the children of the dragPane, this results in the resizer being on top
        dragPane.getChildren().add(resizer.getComponent());
        // Bind the height and width properties so that the overlay resizes properly
        UI.bindSize(resizer.getComponent(), dragPane);
    }

    /**
     * Function for adding handlers to the {@link #dragPane}. Adds three handlers:
     * <li>
     *     <ul>Handler that makes the {@link #dragPane} transparent while dragging it.</ul>
     *     <ul>Handler for when the mouse is released that checks if the {@link #dragPane}
     *     should move to an own stage. Also sets the transparency back.</ul>
     *     <ul>Handler for handling the dragging itself.</ul>
     * </li>
     */
    private void addDragHandlers() {
        // Add handler for when drag is detected
        dragPane.setOnDragDetected(event -> {
            // First check if we are allowed to drag the widget
            if(blockDragging) return;
            // Make it transparent while dragging
            ((StackPane)event.getSource()).setOpacity(((StackPane)event.getSource()).getOpacity() * 0.25);
            // Give signal that processing of event is complete
            event.consume();
        });
        // add handler for when mouse is released
        dragPane.setOnMouseReleased(event -> {
                // First check if we are allowed to drag the widget
                if(blockDragging) return;
                // Set the opacity back to 1.0
                ((StackPane)event.getSource()).setOpacity(((StackPane)event.getSource()).getOpacity() / 0.25);
                // If it is dragged out of the WidgetWindow..
                if(toOwnStage) {
                    // ..set it in an own stage at the point of the mouse
                    setInExternalStage(toOwnStageX, toOwnStageY);
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
            drag(event);
            // Give signal that processing of event is complete
            event.consume();
        });
    }

    /**
     * Takes the widget out of the {@link WidgetWindow} and puts it in an own {@link Stage}.

     * @param x
     *              The x coordinate on the screen.
     * @param y
     *              The y coordinate on the screen.
     */
    protected void setInExternalStage(double x, double y) {
        // Start with removing the widget from the WidgetWindow
        // Make a stream of the children of dragPane
        dragPane.getChildren().stream()
                // Filter to find the node that is the content
                .filter(node -> node.equals(content.getComponent())).findFirst()
                // If is is there, we remove it
                .ifPresent(node -> dragPane.getChildren().remove(node));
        // Make a stream of the children of dragPane
        WidgetWindow.getInstance().getWidgetPane().getChildren().stream()
                // Filter to find the node that is the dragPane
                .filter(node -> node.equals(dragPane)).findFirst()
                // If it is there, we remove it
                .ifPresent(node -> WidgetWindow.getInstance().getWidgetPane().getChildren().remove(node));

        // Create new stage for widget
        Stage widgetstage = new Stage();
        // Create root pane for the scene (this for automatic resizing)
        Pane root = new Pane();
        // Create scene with the root pane as root, size of the scene is the pref size of the widget
        Scene widgetscene = new Scene(root,
                content.getComponent().getPrefWidth(), content.getComponent().getPrefHeight());
        // Set the widget in the root
        root.getChildren().add(content.getComponent());
        // Let the widget keep the same width as the root pane
        root.widthProperty().addListener((observable, oldValue, newValue) ->
                content.getComponent().setPrefWidth((double) newValue));
        // Let the widget keep the same height as the root pane
        root.heightProperty().addListener((observable, oldValue, newValue) ->
                content.getComponent().setPrefHeight((double) newValue));
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
        widgetstage.setOnCloseRequest(event -> setInWidgetPane());
        // Tell the user the widget got his own stage
        LOG.fine("Widget was taken out of the WidgetWindow and was put in his own stage");
    }

    /**
     * Handles the dragging of the {@link #dragPane}.
     *
     * @param event
     *              The {@link MouseEvent} of the dragging
     */
    private void drag(MouseEvent event) {
        // See if the dragging is outside the WidgetWindow or inside the window
        // Get the bounds of the node where the dragging pane is in, then see if it
        // contains the x and y coordinates of the mouse event
        Node rootnode = WidgetWindow.getInstance().getRootNode();
        if(!rootnode.localToScene(rootnode.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
            // Outside, so we go to own stage when released
            toOwnStage = true;
            // Set the position to drop the stage when mouse released
            // y should be y of the mouse event, but from the x the half of the
            // width of the dragging pane should be subtracted to release it centered
            toOwnStageY = event.getScreenY();
            toOwnStageX = event.getScreenX() - dragPane.getWidth() / 2;
        }
        else {
            // Inside, so we do not go to an own stage when mouse released
            toOwnStage = false;

            // First getUIController the list of draggable widgets to prevent a lack of calls to the WidgetWindow
            List<StackPane> draggablePanes = WidgetWindow.getInstance().getDraggableWidgets();
            // And so for the content of the reference to the children of the widget container too
            ObservableList widgetContainerChildren = WidgetWindow.getInstance().getWidgetPane().getChildren();

            // Find the StackPane we are dragging over
            Optional<StackPane> optionalDraggedOver = draggablePanes.parallelStream()
                    .filter(nodeC -> nodeC.localToScene(dragPane.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())).findFirst();
            // See if we are dragging over an other node
            if(optionalDraggedOver.isPresent()) {
                // If so, we get it
                StackPane draggedOver = optionalDraggedOver.get();
                // And then do the dragging over
                dragOverOther(event, draggedOver, widgetContainerChildren);
            }

        }
    }

    /**
     * Function to handle the dragging over another widget.
     *
     * @param event
     *              The {@link MouseEvent} for retrieving the coordinates.
     * @param draggedOver
     *              The {@link Node} where we are dragging over.
     * @param widgetContainerChildren
     *              The {@link ObservableList} of all children that are in {@link WidgetWindow}'s widgetPane.
     */
    private void dragOverOther(MouseEvent event, StackPane draggedOver, ObservableList widgetContainerChildren) {
        // Get the index of the child where is dragged over, the index is the order in the
        // FlowPane from left to right like in a text
        int draggedOverIndex = widgetContainerChildren.indexOf(
                widgetContainerChildren.parallelStream()
                        .filter(nodeC -> nodeC.equals(draggedOver)).findFirst().get());
        // Also getUIController the index of the child that is dragged
        int draggedIndex = widgetContainerChildren.indexOf(
                widgetContainerChildren.parallelStream()
                        .filter(nodeC -> nodeC.equals(dragPane)).findFirst().get());

        // *****************************************************************************
        // We are going to do all matching of coordinates on scene coordinates, this
        // is because of the scrollPane is being a bitch when using relative coordinates
        // *****************************************************************************

        // Get the bounds in the scene from the node we are dragging over
        Bounds draggedOverBounds = draggedOver.localToScene(draggedOver.getBoundsInLocal());
        // Get the bounds in the scene from the node we are dragging
        Bounds draggedBounds = dragPane.localToScene(dragPane.getBoundsInLocal());

        // We want to know if we are dragging over the right or left side of the widget where we are dragging over
        Side sideWhereMouseIsInDraggedOver = draggedOverBounds.getMaxX() - event.getSceneX()
                < draggedOverBounds.getWidth() / 2 ? Side.RIGHT : Side.LEFT;
        // We also want to know in what direction we are dragging relative to the dragged widget
        // We make it null so that we only have a direction when we are really dragging far enough
        Side horizontalDragDirectionTo = null;
        // if we are dragging over another widget
        if (draggedIndex - draggedOverIndex != 0)
            // See via the index if we are moving up or down
            horizontalDragDirectionTo = draggedIndex - draggedOverIndex < 0 ? Side.RIGHT : Side.LEFT;

        // Now we are going to do the same for the vertical direction as done for the horizontal direction

        // We want to know if we are dragging over the top or bottom side of the widget where we are dragging over
        Side dragSideOnNodeY = draggedOverBounds.getMaxY() - event.getSceneY()
                < draggedOverBounds.getHeight() / 2 ? Side.BOTTOM : Side.TOP;
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
        if (draggedBounds.getMinY() < event.getSceneY()
                && draggedBounds.getMaxY() > event.getSceneY())
            // If so, set the dragging orientation horizontally
            draggingOrientation = Orientation.HORIZONTAL;

        // Now we know everything that we need, we go and see if we have to swap widgets or not
        if(draggedOver != dragPane)
            handleMoving(draggingOrientation, horizontalDragDirectionTo, sideWhereMouseIsInDraggedOver,
                    widgetContainerChildren, draggedOverIndex, draggedIndex,
                    dragSideOnNodeY, verticalDragDirectionTo);
    }

    /**
     * Funtion that checks if draggable widgets should move or should stay. Inserts the widget where it
     * is dragged to if it should move.
     *
     * @param draggingOrientation
     *          {@link Orientation} for if we are dragging horizontally or vertically.
     * @param horizontalDragDirectionTo
     *          The {@link Side} to where we are dragging.
     * @param sideWhereMouseIsInDraggedOver
     *          The {@link Side} where the mouse is at the widget we are dragging over.
     * @param widgetContainerChildren
     *          The {@link ObservableList} of all children that are in {@link WidgetWindow}'s widgetPane.
     * @param draggedOverIndex
     *          The index in the list of widgets in the {@link WidgetWindow} from the widget we are dragging over.
     * @param draggedIndex
     *          The index in the list of widgets in the {@link WidgetWindow} from the dragged widget.
     * @param dragSideOnNodeY
     *          The {@link Side} for if our mouse is at the {@link Side#TOP} or at the {@link Side#BOTTOM} of
     *          the widget we are dragging over.
     * @param verticalDragDirectionTo
     *          The vertical direction we are dragging to. So if we are dragging up or down.
     */
    private void handleMoving(Orientation draggingOrientation, Side horizontalDragDirectionTo,
                              Side sideWhereMouseIsInDraggedOver, ObservableList widgetContainerChildren,
                              int draggedOverIndex, int draggedIndex,
                              Side dragSideOnNodeY, Side verticalDragDirectionTo) {
        // If we are dragging horizontally and the dragging widget is not the same as the widget
        // We are dragging over a swap could be desirable in horizontal direction
        if (draggingOrientation == Orientation.HORIZONTAL) {
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
            if (dragSideOnNodeY.equals(verticalDragDirectionTo)) {
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
