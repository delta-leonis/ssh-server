package org.ssh.ui.components.widget;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.ui.components.bottomsection.WidgetShortcutContainer;
import org.ssh.ui.windows.WidgetWindow;

import java.util.List;
import java.util.Optional;

/**
 * {@link ShortcutDraggable} is a class for making a {@link WidgetShortcut} draggable in the
 * {@link WidgetShortcutContainer} of the {@link org.ssh.ui.components.bottomsection.Toolbox}.
 * When the shortcuts are double clicked, a new stage with the target widget opens.
 *
 * @author Joost Overeem
 */
public class ShortcutDraggable extends UIComponent<StackPane> {

    /**
     * The {@link StackPane} that contains the widget and can be dragged.
     */
    @FXML
    private StackPane dragPane;

    /**
     * Constructor for {@link ShortcutDraggable}.
     *
     * @param content
     *              The widget that has to be draggable.
     * @param target
     *              The {@link AbstractWidget} where the shortcut is for.
     */
    public ShortcutDraggable(WidgetShortcut content, AbstractWidget target) {
        super("draggable for " + content.getName(), "widget/draggable.fxml");
        dragPane.getChildren().add(content.getComponent());
        UI.bindSize(content.getComponent(), dragPane);
        addDragHandlers(target);
    }

    /**
     * Function for adding handlers to the {@link #dragPane}. Adds three handlers:
     * <li>
     *     <ul>Handler that makes the {@link #dragPane} transparent while dragging it.</ul>
     *     <ul>Handler for when the mouse is released, that sets the transparency back.</ul>
     *     <ul>Handler for handling the dragging itself.</ul>
     *     <ul>Handler for the double click that opens the widget in a stage</ul>
     * </li>
     */
    private void addDragHandlers(AbstractWidget target) {
        // Add handler for when drag is detected
        dragPane.setOnDragDetected(event -> {
            // Make it transparent while dragging
            ((StackPane)event.getSource()).setOpacity(((StackPane)event.getSource()).getOpacity() * 0.2);
            // Give signal that processing of event is complete
            event.consume();
        });
        // add handler for when mouse is released
        dragPane.setOnMouseReleased(event -> {
            // Set the opacity back to 1.0
            ((StackPane)event.getSource()).setOpacity(((StackPane)event.getSource()).getOpacity() / 0.2);
            // If it is dragged out of the WidgetWindow..
            // Give signal that processing of event is complete
            event.consume();
        });
        // Drag the pane when dragging
        dragPane.setOnMouseDragged(event -> {
            // Drag the widget
            drag(event);
        });
        // Handle the double clicks
        dragPane.setOnMouseClicked(mouseEvent -> {
            // See if the button is the left click..
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                // When it is a double click..
                if(mouseEvent.getClickCount() == 2) {
                    // initialize WidgetWindow if it is not done yet, this is a bit beun^2
                    WidgetWindow.getInstance();
                    // Get the WidgetDraggable for where the shortcut is for, if it is there..
                    UI.<WidgetDraggable>getComponent("draggable for " + target.getName()).ifPresent(
                            // call function to set in external stage
                            widgetDraggable -> widgetDraggable.setInExternalStage(
                                    // just a little within the margin of the caller stage
                                    this.getStage().getX() + 20, this.getStage().getY() + 20));
                }
            }
        });
    }

    /**
     * Handles the dragging of a shortcut.
     *
     * @param event
     *              The {@link MouseEvent} of the dragging.
     */
    private void drag(MouseEvent event) {
        Optional<WidgetShortcutContainer> OptionalShortcutContainer = UI.getComponent("shortcutcontainer");
        if(!OptionalShortcutContainer.isPresent())
            return;
        WidgetShortcutContainer shortcutContainer = OptionalShortcutContainer.get();

        // First getUIController the list of draggable widgets to prevent a lack of calls to the WidgetWindow
        List<StackPane> draggablePanes = shortcutContainer.getDraggableWidgets();
        // And so for the content of the reference to the children of the widget container too
        ObservableList shortcutContainerChildren = shortcutContainer.getWidgetcontainer().getChildren();

        // Find the StackPane we are dragging over
        Optional<StackPane> optionalDraggedOver = draggablePanes.parallelStream()
                .filter(nodeC -> nodeC.localToScene(dragPane.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())).findFirst();

        // See if we are dragging over an other node
        if(optionalDraggedOver.isPresent()) {
            // If so, we getUIController it
            StackPane draggedOver = optionalDraggedOver.get();

            // Get the index of the child where is dragged over, the index is the order in the
            // FlowPane from left to right like in a text
            int draggedOverIndex = shortcutContainerChildren.indexOf(
                    shortcutContainerChildren.parallelStream()
                            .filter(nodeC -> nodeC.equals(draggedOver)).findFirst().get());
            // Also getUIController the index of the child that is dragged
            int draggedIndex = shortcutContainerChildren.indexOf(
                    shortcutContainerChildren.parallelStream()
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
                        shortcutContainerChildren, draggedOverIndex, draggedIndex,
                        dragSideOnNodeY, verticalDragDirectionTo);

        }
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
