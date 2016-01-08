package org.ssh.ui.components.widget;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.ui.UIComponent;

/**
 * Class that can be used to display an overlaying resize icon on a widget or other {@link UIComponent}.
 * It displays an 24x24 px icon that, when dragged, lets the given content grow or shrink.
 */
public class WidgetResizer extends UIComponent<GridPane> {

    /**
     * The root pane in the FXML file. This {@link GridPane} is used to bind height and width for resizing.
     */
    @FXML
    private GridPane rootPane;

    /**
     * {@link Pane Widget} that is to be resizable.
     */
    private Pane content;

    /**
     * {@link Draggable} that has the resizable content. Variable used in this class for calling
     * {@link Draggable#setBlockDragging(boolean)}.
     */
    private Draggable draggable;

    /**
     * The x location in the {@link javafx.scene.Scene} where the dragging is started.
     */
    private double dragStartpointX;
    /**
     * The y location in the {@link javafx.scene.Scene} where the dragging is started.
     */
    private double dragStartpointY;
    /**
     * The width of the widget when the dragging is started
     */
    private double dragStartWidth;
    /**
     * The height of the widget when the dragging is started
     */
    private double dragStartHeight;

    /**
     * Constructor for {@link WidgetResizer}. Handles the needed initialization and binds
     * the width and height properties so that the root automatically resizes.
     *
     * @param draggable
     *              The {@link Draggable} that has the resizable content.
     * @param content
     *              The widget that where the resizing is about.
     */
    public WidgetResizer(Draggable draggable, Pane content) {
        super("widgetresizer", "widget/widgetresizer.fxml");
        // set the content
        this.content = content;
        // set the draggable
        this.draggable = draggable;
        // Initialize mouse position on 0.0
        dragStartpointX = dragStartpointY = dragStartWidth = dragStartHeight = 0.0;
    }

    /**
     * When the mouse is released, the starting point has to be set (0,0) again. The {@link #drag(MouseEvent)}
     * uses the starting point when it is not (0,0), so when it keeps the values of a previous dragging, a
     * shock will appear when another starting point is used because drag events are earlier produced than the
     * dragDetected events.
     */
    @FXML
    private void resetStartPoints() {
        // Set
        dragStartpointX = 0.0;
        dragStartpointY = 0.0;
        // Release the drag blocking
        draggable.setBlockDragging(false);
    }

    /**
     * Function for handling the dragging, is sets the new size of the {@link #content}.
     * @param event
     *              The {@link MouseEvent} where the coordinates are retrieved from.
     */
    @FXML
    private void drag(MouseEvent event) {
        // If the drag starting point is (0,0) there should be done nothing
        if (dragStartpointX <= 0 && dragStartpointY <= 0) {
            // Set the start point
            dragStartpointX = event.getSceneX();
            dragStartpointY = event.getSceneY();
            // Set the start size
            dragStartWidth = content.getWidth();
            dragStartHeight = content.getHeight();

            draggable.setBlockDragging(true);
        }
        // The new width is the old width plus the difference in x from where the dragging is started and
        // where the mouse is now
        setWidgetWidth(dragStartWidth + event.getSceneX() - dragStartpointX);
        // The new height is the old height plus the difference in y from where the dragging is started and
        // where the mouse is now
        setWidgetHeight(dragStartHeight + event.getSceneY() - dragStartpointY);
    }

    /**
     * Sets the height of the {@link #content} with a minimum of 30.
     * @param height
     *              The height of the {@link #content} should get.
     */
    private void setWidgetHeight(double height) {
        // Check for small values and set it 30 if small
        // This is because too small sizes are undesirable
        if(height < 30) height = 30;
        // Set pref, min and max to make sure the size is set
        content.setPrefHeight(height);
        content.setMinHeight(height);
        content.setMaxHeight(height);
    }

    /**
     * Sets the width of the {@link #content} with a minimum of 30.
     * @param width
     *              The width of the {@link #content} should get.
     */
    private void setWidgetWidth(double width) {
        // Check for small values and set it 30 if small
        // This is because too small sizes are undesirable
        if(width < 30) width = 30;
        // Set pref, min and max to make sure the size is set
        content.setPrefWidth(width);
        content.setMinWidth(width);
        content.setMaxWidth(width);
    }
}
