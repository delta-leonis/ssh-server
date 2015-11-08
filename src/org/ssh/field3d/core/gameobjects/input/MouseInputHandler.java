package org.ssh.field3d.core.gameobjects.input;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * MouseInputHandler class. This class handles mouse input.
 *
 * @see GameObject
 *      
 * @author Mark Lefering
 */
// TODO: Comment
// TODO: Javadoc
// TODO: Cleanup
public class MouseInputHandler extends GameObject {
    
    public static final int           NUM_BUTTONS            = 3;
    public static final long          MAX_SCROLL_WHEEL_VALUE = 100000000;
    public static final long          MIN_SCROLL_WHEEL_VALUE = -MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
                                                           
    private final OnMouseWheelChanged onMouseWheelChanged;
    private final OnMouseMoved        onMouseMoved;
    private final OnMousePressed      onMousePressed;
    private final OnMouseReleased     onMouseReleased;
    private final OnMouseDragged      onMouseDragged;
                                      
    private final boolean             buttonStates[];
                                      
    private double                    mouseX, mouseY;
                                      
    private double                    curMouseX, curMouseY, prevMouseX, prevMouseY;
    private long                      scrollWheelXValue, scrollWheelYValue;
                                      
    private long                      maxScrollWheelValue, minScrollWheelValue;
                                      
    /**
     * Constructor
     * 
     * @param game The {@link Game} of the {@link GameObject}.
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
    
    @Override
    public void Initialize() {
        
        // Hook events
        this.GetGame().setOnMouseDragged(this.onMouseDragged);
        this.GetGame().setOnMouseMoved(this.onMouseMoved);
        this.GetGame().setOnScroll(this.onMouseWheelChanged);
        this.GetGame().setOnMousePressed(this.onMousePressed);
        this.GetGame().setOnMouseReleased(this.onMouseReleased);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        // Update previous location
        this.prevMouseX = this.curMouseX;
        this.prevMouseY = this.curMouseY;
        
        this.curMouseX = this.mouseX;
        this.curMouseY = this.mouseY;
    }
    
    @Override
    public void Destroy() {
    }
    
    
    
    public double getMouseDeltaX() {
        return this.curMouseX - this.prevMouseX;
    }
    
    public double getMouseDeltaY() {
        return this.curMouseY - this.prevMouseY;
    }
    
    public double getMouseX() {
        return this.mouseX;
    }
    
    public double getMouseY() {
        return this.mouseY;
    }
    
    public long getScrollWheelXValue() {
        return this.scrollWheelXValue;
    }
    
    public long getScrollWheelYValue() {
        return this.scrollWheelYValue;
    }
    
    public boolean isButtonDown(final int buttonNumber) {
        return this.buttonStates[buttonNumber];
    }
    
    public boolean isButtonUp(final int buttonNumber) {
        return !this.buttonStates[buttonNumber];
    }
    
    public boolean isLeftButtonDown() {
        return this.buttonStates[0];
    }
    
    public boolean isLeftButtonUp() {
        return !this.buttonStates[0];
    }
    
    public boolean isMidButtonDown() {
        return this.buttonStates[1];
    }
    
    public boolean isMidButtonUp() {
        return !this.buttonStates[1];
    }
    
    public boolean isRightButtonDown() {
        return this.buttonStates[2];
    }
    
    public boolean isRightButtonUp() {
        return !this.buttonStates[2];
    }
    
    public void setMaxMouseWheelValue(final long value) {
        
        if (this.scrollWheelXValue > value) this.scrollWheelXValue = value;
        
        if (this.scrollWheelYValue > value) this.scrollWheelYValue = value;
        
        this.maxScrollWheelValue = value;
    }
    
    public void setMinMouseWheelValue(final long value) {
        
        if (this.scrollWheelXValue < value) this.scrollWheelXValue = value;
        
        if (this.scrollWheelYValue < value) this.scrollWheelYValue = value;
        
        this.minScrollWheelValue = value;
    }
    
    public synchronized void setMouseX(final double value) {
        this.mouseX = value;
    }
    
    public synchronized void setMouseY(final double value) {
        this.mouseY = value;
    }  
    
    class OnMouseMoved implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.setMouseY(mouseEvent.getScreenY());
        }
    }
    
    class OnMousePressed implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            switch (mouseEvent.getButton()) {
                
                // Left
                case PRIMARY: {
                    MouseInputHandler.this.buttonStates[0] = true;
                    break;
                }
                    
                    // Mid
                case MIDDLE: {
                    MouseInputHandler.this.buttonStates[1] = true;
                    break;
                }
                    
                    // Right
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
    
    class OnMouseReleased implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            switch (mouseEvent.getButton()) {
                
                // Left
                case PRIMARY: {
                    MouseInputHandler.this.buttonStates[0] = false;
                    break;
                }
                    
                    // Mid
                case MIDDLE: {
                    MouseInputHandler.this.buttonStates[1] = false;
                    break;
                }
                    
                    // Right
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
    
    class OnMouseWheelChanged implements EventHandler<ScrollEvent> {
        
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
    
    class OnMouseDragged implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.setMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.setMouseY(mouseEvent.getScreenY());
        }
    }
}
