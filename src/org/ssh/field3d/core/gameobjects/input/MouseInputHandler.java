package org.ssh.field3d.core.gameobjects.input;

import java.util.Arrays;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * MouseInputHandler class. This class handles mouse input.
 *
 * @author Mark Lefering
 * @see GameObject
 */
public class MouseInputHandler extends GameObject {
    
    /** The number of mouse buttons. */
    public static final int           NUM_BUTTONS            = 3;
                                                             
    /** The default maximal scroll wheel value. */
    public static final long          MAX_SCROLL_WHEEL_VALUE = 100000000;
                                                             
    /** The default minimal scroll wheel value. */
    public static final long          MIN_SCROLL_WHEEL_VALUE = -MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
                                                             
    /** The index of the button state arrays for the left mouse button. */
    public static final int           MOUSE_BUTTON_LEFT      = 0;
                                                             
    /** The index of the button state arrays for the middle mouse button. */
    public static final int           MOUSE_BUTTON_MID       = 1;
                                                             
    /** The index of the button state arrays for the right mouse button. */
    public static final int           MOUSE_BUTTON_RIGHT     = 2;
                                                             
    /** The on mouse wheel changed handler. */
    private final OnMouseWheelChanged onMouseWheelChanged;
                                      
    /** The on mouse moved handler. */
    private final OnMouseMoved        onMouseMoved;
                                      
    /** The on mouse pressed handler. */
    private final OnMousePressed      onMousePressed;
                                      
    /** The on mouse released handler. */
    private final OnMouseReleased     onMouseReleased;
                                      
    /** The on mouse dragged handler. */
    private final OnMouseDragged      onMouseDragged;
                                      
    /** The current button states. */
    private boolean                   curButtonStates[];
                                      
    /** The previous button states. */
    private boolean                   prevButtonStates[];
                                      
    /** The mouse event button states. */
    private boolean                   mouseEventBtnStates[];
                                      
    /** The state for checking if the mouse wheel x value has changed since the last frame. */
    private boolean                   mouseWheelXChanged;
                                      
    /** The state for checking if the mouse wheel y value has changed since the last frame. */
    private boolean                   mouseWheelYChanged;
                                      
    /** The mouse x-coordinate & y-coordinate. */
    private double                    mouseX, mouseY;
                                      
    /** The current mouse x-coordinate & y-coordinate. */
    private double                    curMouseX, curMouseY;
                                      
    /** The previous mouse x-coordinate & y-coordinate. */
    private double                    prevMouseX, prevMouseY;
                                      
    /** The scroll wheel x & y value. */
    private long                      scrollWheelXValue, scrollWheelYValue;
                                      
    /** The previous scroll wheel x & y value. */
    private long                      prevScrollWheelXValue, prevScrollWheelYValue;
                                      
    /** The current scroll wheel x & y value. */
    private long                      curScrollWheelXValue, curScrollWheelYValue;
                                      
    /** The min & max scroll wheel value. */
    private long                      maxScrollWheelValue, minScrollWheelValue;
                                      
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public MouseInputHandler(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating new event handlers
        this.onMouseWheelChanged = new OnMouseWheelChanged();
        this.onMouseMoved = new OnMouseMoved();
        this.onMousePressed = new OnMousePressed();
        this.onMouseReleased = new OnMouseReleased();
        this.onMouseDragged = new OnMouseDragged();
        
        // Creating array for the current mouse button states
        this.curButtonStates = new boolean[MouseInputHandler.NUM_BUTTONS];
        // Creating array for the previous mouse button states
        this.prevButtonStates = new boolean[MouseInputHandler.NUM_BUTTONS];
        // Creating array for the mouse event button states
        this.mouseEventBtnStates = new boolean[MouseInputHandler.NUM_BUTTONS];
        
        // Setting default values
        this.scrollWheelXValue = this.scrollWheelYValue = 0;
        this.mouseX = this.mouseY = 0;
        this.maxScrollWheelValue = MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
        this.minScrollWheelValue = MouseInputHandler.MIN_SCROLL_WHEEL_VALUE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Hook events
        this.getGame().setOnMouseDragged(this.onMouseDragged);
        this.getGame().setOnMouseMoved(this.onMouseMoved);
        this.getGame().setOnScroll(this.onMouseWheelChanged);
        this.getGame().setOnMousePressed(this.onMousePressed);
        this.getGame().setOnMouseReleased(this.onMouseReleased);
        this.getGame().setOnMouseClicked(new OnMouseClicked());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
        // Update previous location
        this.prevMouseX = this.curMouseX;
        this.prevMouseY = this.curMouseY;
        
        // Update current location
        this.curMouseX = this.mouseX;
        this.curMouseY = this.mouseY;
        
        // Update previous scroll wheel values
        this.prevScrollWheelXValue = this.curScrollWheelXValue;
        this.prevScrollWheelYValue = this.curScrollWheelYValue;
        
        // Update current scroll wheel values
        this.curScrollWheelXValue = this.scrollWheelXValue;
        this.curScrollWheelYValue = this.scrollWheelYValue;
        
        // Check if the mouse wheel x value has changed since the last frame
        if (this.prevScrollWheelXValue != this.curScrollWheelXValue)
            this.mouseWheelXChanged = true;
        else
            this.mouseWheelXChanged = false;
            
        // Check if the mouse wheel y value has changed since the last frame
        if (this.prevScrollWheelYValue != this.curScrollWheelYValue)
            this.mouseWheelYChanged = true;
        else
            this.mouseWheelYChanged = false;
            
        // Copy current button states to the previous button states
        this.prevButtonStates = Arrays.copyOf(this.curButtonStates, this.curButtonStates.length);
        // Copy mouse event button states to the current button states (update it)
        this.curButtonStates = Arrays.copyOf(this.mouseEventBtnStates, this.mouseEventBtnStates.length);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateGeometry() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateDetection() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Gets the mouse delta x.
     *
     * @return The mouse delta x.
     */
    public double getMouseDeltaX() {
        return this.curMouseX - this.prevMouseX;
    }
    
    /**
     * Gets the mouse delta y.
     *
     * @return The mouse delta y.
     */
    public double getMouseDeltaY() {
        return this.curMouseY - this.prevMouseY;
    }
    
    /**
     * Gets the mouse x-coordinate.
     *
     * @return The mouse x-coordinate.
     */
    public double getMouseX() {
        return this.mouseX;
    }
    
    /**
     * Gets the mouse y-coordinate.
     *
     * @return The mouse y-coordinate.
     */
    public double getMouseY() {
        return this.mouseY;
    }
    
    /**
     * Gets the scroll wheel x value.
     *
     * @return The scroll wheel x value.
     */
    public long getScrollWheelXValue() {
        return this.scrollWheelXValue;
    }
    
    /**
     * Gets the scroll wheel y value.
     *
     * @return The scroll wheel y value.
     */
    public long getScrollWheelYValue() {
        return this.scrollWheelYValue;
    }
    
    /**
     * Gets the minimal scroll wheel value
     * 
     * @return The scroll wheel minimal value.
     */
    public long getScrollWheelMinValue() {
        return this.minScrollWheelValue;
    }
    
    /**
     * Gets the maximal scroll wheel value
     * 
     * @return The scroll wheel max value.
     */
    public long getScrollWheelMaxValue() {
        return this.maxScrollWheelValue;
    }
    
    /**
     * Checks to see if a button is down.
     *
     * @param buttonNumber
     *            The number of the button, 0 for left, 1 for mid and 2 for right.
     * @return True, if is button down.
     */
    public boolean isButtonDown(final int buttonNumber) {
        return this.curButtonStates[buttonNumber];
    }
    
    /**
     * Checks if is button up.
     *
     * @param buttonNumber
     *            The number of the button, 0 for left, 1 for mid and 2 for right.
     * @return True, if the button is up.
     */
    public boolean isButtonUp(final int buttonNumber) {
        return !this.curButtonStates[buttonNumber];
    }
    
    /**
     * Checks if the left mouse button is down.
     *
     * @return True, if the left mouse button is down.
     */
    public boolean isLeftButtonDown() {
        return this.curButtonStates[MOUSE_BUTTON_LEFT];
    }
    
    /**
     * Checks if the left mouse button is up.
     *
     * @return True, if the left mouse button is up.
     */
    public boolean isLeftButtonUp() {
        return !this.curButtonStates[MOUSE_BUTTON_LEFT];
    }
    
    /**
     * Checks if the middle mouse button is down.
     *
     * @return True, if the middle mouse button is down.
     */
    public boolean isMidButtonDown() {
        return this.curButtonStates[MOUSE_BUTTON_MID];
    }
    
    /**
     * Checks if the middle mouse button is up.
     * 
     * @return True, if the middle mouse button is up.
     */
    public boolean isMidButtonUp() {
        return !this.curButtonStates[MOUSE_BUTTON_MID];
    }
    
    /**
     * Checks if the right mouse button is down.
     *
     * @return True, if the right mouse button is down.
     */
    public boolean isRightButtonDown() {
        return this.curButtonStates[MOUSE_BUTTON_RIGHT];
    }
    
    /**
     * Checks if the right mouse button is up.
     *
     * @return True, if the right mouse button is up.
     */
    public boolean isRightButtonUp() {
        return !this.curButtonStates[MOUSE_BUTTON_RIGHT];
    }
    
    /**
     * Checks if the left mouse button is being pressed.
     *
     * @return True, if the left mouse button is being pressed.
     */
    public boolean isLeftButtonPressing() {
        return isButtonPressing(MOUSE_BUTTON_LEFT);
    }
    
    /**
     * Checks if the left mouse button is being released.
     *
     * @return True, if the left mouse button is being released.
     */
    public boolean isLeftButtonReleasing() {
        return isButtonReleasing(MOUSE_BUTTON_LEFT);
        
    }
    
    /**
     * Checks if the middle mouse button is being pressed.
     *
     * @return True, if the middle mouse button is being pressed.
     */
    public boolean isMidButtonPressing() {
        return isButtonPressing(MOUSE_BUTTON_MID);
    }
    
    /**
     * Checks if the middle mouse button is being released.
     *
     * @return True, if the middle mouse button is being released.
     */
    public boolean isMidButtonReleasing() {
        return isButtonReleasing(MOUSE_BUTTON_MID);
    }
    
    /**
     * Checks if the right mouse button is being pressed.
     *
     * @return True, if the right mouse button is being pressed.
     */
    public boolean isRightButtonPressing() {
        return isButtonPressing(MOUSE_BUTTON_RIGHT);
    }
    
    /**
     * Checks if the right mouse button is being released.
     *
     * @return True, if the right mouse button is being released.
     */
    public boolean isRightButtonReleasing() {
        return isButtonReleasing(MOUSE_BUTTON_RIGHT);
    }
    
    /**
     * Checks if a mouse button is being pressed.
     *
     * @param index
     *            The index of the mouse button.
     * @return True, if the mouse button is being pressed.
     */
    public boolean isButtonPressing(int index) {
        return !this.prevButtonStates[index] && this.curButtonStates[index];
    }
    
    /**
     * Checks if a mouse button is being released.
     *
     * @param index
     *            The index of the mouse button.
     * @return True, if the mouse button is being released.
     */
    public boolean isButtonReleasing(int index) {
        return this.prevButtonStates[index] && !this.curButtonStates[index];
    }
    
    /**
     * Checks if the mouse wheel x value has changed since the last frame.
     *
     * @return True, if the mouse wheel x value has changed since the last frame.
     */
    public boolean isMouseWheelXChanged() {
        
        return this.mouseWheelXChanged;
    }
    
    /**
     * Checks if the mouse wheel y value has changed since the last frame.
     *
     * @return True, if the mouse wheel y value has changed since the last frame.
     */
    public boolean isMouseWheelYChanged() {
        
        return this.mouseWheelYChanged;
    }
    
    /**
     * Sets the maximal mouse wheel value.
     *
     * @param value
     *            The new maximal mouse wheel value.
     */
    public void setMaxMouseWheelValue(final long value) {
        
        // Update current mouse wheel value if needed
        if (this.scrollWheelXValue > value) this.scrollWheelXValue = value;
        if (this.scrollWheelYValue > value) this.scrollWheelYValue = value;
        
        // Setting maximal value
        this.maxScrollWheelValue = value;
    }
    
    /**
     * Sets the minimal mouse wheel value.
     *
     * @param value
     *            The new minimal mouse wheel value.
     */
    public void setMinMouseWheelValue(final long value) {
        
        // Update current values if needed
        if (this.scrollWheelXValue < value) this.scrollWheelXValue = value;
        if (this.scrollWheelYValue < value) this.scrollWheelYValue = value;
        
        // Setting minimal value
        this.minScrollWheelValue = value;
    }
    
    /**
     * Sets the mouse wheel x value.
     *
     * @param value
     *            The new mouse wheel x value.
     */
    public void setMouseWheelXValue(final long value) {
        
        if (value >= this.minScrollWheelValue && value <= this.maxScrollWheelValue) {
            
            this.scrollWheelXValue = value;
        }
    }
    
    /**
     * Sets the mouse wheel y value.
     *
     * @param value
     *            The new mouse wheel y value.
     */
    public void setMouseWheelYValue(final long value) {
        
        // Check if value is within bounds
        if (value > this.minScrollWheelValue && value < this.maxScrollWheelValue) {
            
            // Setting value
            this.scrollWheelYValue = value;
        }
        else {
            
            if (value < minScrollWheelValue) {
                
                // Set minimal scroll wheel value
                this.scrollWheelYValue = minScrollWheelValue;
            }
            else if (value > maxScrollWheelValue) {
                
                // Set maximal scroll wheel value
                this.scrollWheelYValue = maxScrollWheelValue;
            }
        }
    }
    
    /**
     * Sets the mouse x-coordinate.
     *
     * @param value
     *            The new mouse x-coordinate.
     */
    public synchronized void setMouseX(final double value) {
        this.mouseX = value;
    }
    
    /**
     * Sets the mouse y-coordinate.
     *
     * @param value
     *            The new mouse y-coordinate.
     */
    public synchronized void setMouseY(final double value) {
        this.mouseY = value;
    }
    
    /**
     * Sets the mouse button states.
     *
     * @param mouseEvent
     *            the new mouse button states
     */
    public synchronized void setMouseButtonStates(MouseEvent mouseEvent) {
        
        // Setting mouse button states
        setMouseButtonState(MOUSE_BUTTON_LEFT, mouseEvent.isPrimaryButtonDown());
        setMouseButtonState(MOUSE_BUTTON_MID, mouseEvent.isMiddleButtonDown());
        setMouseButtonState(MOUSE_BUTTON_RIGHT, mouseEvent.isSecondaryButtonDown());
    }
    
    /**
     * Sets the mouse button state.
     *
     * @param index
     *            the index
     * @param value
     *            the value
     */
    public synchronized void setMouseButtonState(final int index, boolean value) {
        this.mouseEventBtnStates[index] = value;
    }
    
    /**
     * The Class OnMouseMoved.
     */
    class OnMouseMoved implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getSceneX());
            MouseInputHandler.this.setMouseY(mouseEvent.getSceneY());
        }
    }
    
    /**
     * The on mouse button pressed event handler.
     */
    class OnMousePressed implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getSceneX());
            MouseInputHandler.this.setMouseY(mouseEvent.getSceneY());
            
            setMouseButtonStates(mouseEvent);
        }
    }
    
    /**
     * The Class OnMouseClicked.
     */
    class OnMouseClicked implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(MouseEvent mouseEvent) {
            
            setMouseButtonStates(mouseEvent);
        }
    }
    
    /**
     * The on mouse button released event handler.
     */
    class OnMouseReleased implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getSceneX());
            MouseInputHandler.this.setMouseY(mouseEvent.getSceneY());
            
            setMouseButtonStates(mouseEvent);
        }
    }
    
    /**
     * The on mouse wheel changed event handler.
     */
    class OnMouseWheelChanged implements EventHandler<ScrollEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final ScrollEvent scrollEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(scrollEvent.getSceneX());
            MouseInputHandler.this.setMouseY(scrollEvent.getSceneY());
            
            // Update scroll wheel value
            MouseInputHandler.this.scrollWheelXValue += scrollEvent.getDeltaX();
            MouseInputHandler.this.scrollWheelYValue += scrollEvent.getDeltaY();
            
            // Limit horizontal scroll
            if (MouseInputHandler.this.scrollWheelXValue < MouseInputHandler.this.minScrollWheelValue)
                MouseInputHandler.this.scrollWheelXValue = MouseInputHandler.this.minScrollWheelValue;
            else if (MouseInputHandler.this.scrollWheelXValue > MouseInputHandler.this.maxScrollWheelValue)
                MouseInputHandler.this.scrollWheelXValue = MouseInputHandler.this.maxScrollWheelValue;
                
            // Limit vertical scroll
            if (MouseInputHandler.this.scrollWheelYValue < MouseInputHandler.this.minScrollWheelValue)
                MouseInputHandler.this.scrollWheelYValue = MouseInputHandler.this.minScrollWheelValue;
            else if (MouseInputHandler.this.scrollWheelYValue > MouseInputHandler.this.maxScrollWheelValue)
                MouseInputHandler.this.scrollWheelYValue = MouseInputHandler.this.maxScrollWheelValue;
        }
    }
    
    /**
     * The on mouse dragged event handler.
     */
    class OnMouseDragged implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getSceneX());
            MouseInputHandler.this.setMouseY(mouseEvent.getSceneY());
            
            setMouseButtonStates(mouseEvent);
        }
    }
}
