package org.ssh.field3d.core.gameobjects.input;

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
 *      
 */
public class MouseInputHandler extends GameObject {
    
    /** The Constant NUM_BUTTONS. */
    public static final int           NUM_BUTTONS            = 3;
                                                             
    /** The Constant MAX_SCROLL_WHEEL_VALUE. */
    public static final long          MAX_SCROLL_WHEEL_VALUE = 100000000;
                                                             
    /** The Constant MIN_SCROLL_WHEEL_VALUE. */
    public static final long          MIN_SCROLL_WHEEL_VALUE = -MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
                                                             
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
                                      
    /** The button states. */
    private final boolean             buttonStates[];
                                      
    /** The mouse x-coordinate & y-coordinate. */
    private double                    mouseX, mouseY;
                                      
    /** The current mouse x-coordinate & y-coordinate. */
    private double                    curMouseX, curMouseY;
                                      
    /** The previous mouse x-coordinate & y-coordinate */
    private double                    prevMouseX, prevMouseY;
                                      
    /** The scroll wheel x & y value. */
    private long                      scrollWheelXValue, scrollWheelYValue;
                                      
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
        
        // Creating array for the mouse button states
        this.buttonStates = new boolean[MouseInputHandler.NUM_BUTTONS];
        
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
    public void initialize() {
        
        // Hook events
        this.getGame().setOnMouseDragged(this.onMouseDragged);
        this.getGame().setOnMouseMoved(this.onMouseMoved);
        this.getGame().setOnScroll(this.onMouseWheelChanged);
        this.getGame().setOnMousePressed(this.onMousePressed);
        this.getGame().setOnMouseReleased(this.onMouseReleased);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long timeDivNano) {
        
        // Update previous location
        this.prevMouseX = this.curMouseX;
        this.prevMouseY = this.curMouseY;
        
        this.curMouseX = this.mouseX;
        this.curMouseY = this.mouseY;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
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
     * Checks to see if a button is down.
     *
     * @param buttonNumber
     *            The number of the button, 0 for left, 1 for mid and 2 for right.
     * @return True, if is button down.
     */
    public boolean isButtonDown(final int buttonNumber) {
        return this.buttonStates[buttonNumber];
    }
    
    /**
     * Checks if is button up.
     *
     * @param buttonNumber
     *            The number of the button, 0 for left, 1 for mid and 2 for right.
     * @return True, if the button is up.
     */
    public boolean isButtonUp(final int buttonNumber) {
        return !this.buttonStates[buttonNumber];
    }
    
    /**
     * Checks if the left mouse button is down.
     *
     * @return True, if the left mouse button is down.
     */
    public boolean isLeftButtonDown() {
        return this.buttonStates[0];
    }
    
    /**
     * Checks if the left mouse button is up.
     *
     * @return True, if the left mouse button is up.
     */
    public boolean isLeftButtonUp() {
        return !this.buttonStates[0];
    }
    
    /**
     * Checks if the middle mouse button is down.
     *
     * @return True, if the middle mouse button is down.
     */
    public boolean isMidButtonDown() {
        return this.buttonStates[1];
    }
    
    /**
     * Checks if the middle mouse button is up.
     * 
     * @return True, if the middle mouse button is up.
     */
    public boolean isMidButtonUp() {
        return !this.buttonStates[1];
    }
    
    /**
     * Checks if the right mouse button is down.
     *
     * @return True, if the right mouse button is down.
     */
    public boolean isRightButtonDown() {
        return this.buttonStates[2];
    }
    
    /**
     * Checks if the right mouse button is up.
     *
     * @return True, if the right mouse button is up.
     */
    public boolean isRightButtonUp() {
        return !this.buttonStates[2];
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
     * The Class OnMouseMoved.
     */
    class OnMouseMoved implements EventHandler<MouseEvent> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.setMouseY(mouseEvent.getScreenY());
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
            
            switch (mouseEvent.getButton()) {
                
                // Left mouse button
                case PRIMARY: {
                    MouseInputHandler.this.buttonStates[0] = true;
                    break;
                }
                    
                    // Middle mouse button
                case MIDDLE: {
                    MouseInputHandler.this.buttonStates[1] = true;
                    break;
                }
                    
                    // Right mouse button
                case SECONDARY: {
                    MouseInputHandler.this.buttonStates[2] = true;
                    break;
                }
                    
                    // Default case
                default:
                    break;
            }
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
            
            switch (mouseEvent.getButton()) {
                
                // Left mouse button
                case PRIMARY: {
                    MouseInputHandler.this.buttonStates[0] = false;
                    break;
                }
                    
                    // Middle mouse button
                case MIDDLE: {
                    MouseInputHandler.this.buttonStates[1] = false;
                    break;
                }
                    
                    // Right mouse button
                case SECONDARY: {
                    MouseInputHandler.this.buttonStates[2] = false;
                    break;
                }
                    
                    // Default case
                default:
                    break;
            }
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
            MouseInputHandler.this.setMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.setMouseY(mouseEvent.getScreenY());
        }
    }
}
