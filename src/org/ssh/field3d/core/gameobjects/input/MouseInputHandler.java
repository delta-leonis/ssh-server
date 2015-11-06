/**
 *
 * MouseInputHandler class
 *
 * - This class handles mouse input
 *
 * TODO: Javadoc
 *
 * @author marklef2
 * @date 13-10-2015
 */
package org.ssh.field3d.core.gameobjects.input;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseInputHandler extends GameObject {
    
    class OnMouseDragged implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.SetMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.SetMouseY(mouseEvent.getScreenY());
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Inner classes
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    class OnMouseMoved implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            // Update mouse x & y
            MouseInputHandler.this.SetMouseX(mouseEvent.getScreenX());
            MouseInputHandler.this.SetMouseY(mouseEvent.getScreenY());
        }
    }
    
    class OnMousePressed implements EventHandler<MouseEvent> {
        
        @Override
        public void handle(final MouseEvent mouseEvent) {
            
            switch (mouseEvent.getButton()) {
                
                // Left
                case PRIMARY: {
                    MouseInputHandler.this._buttonStates[0] = true;
                    break;
                }
                    
                    // Mid
                case MIDDLE: {
                    MouseInputHandler.this._buttonStates[1] = true;
                    break;
                }
                    
                    // Right
                case SECONDARY: {
                    MouseInputHandler.this._buttonStates[2] = true;
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
                    MouseInputHandler.this._buttonStates[0] = false;
                    break;
                }
                    
                    // Mid
                case MIDDLE: {
                    MouseInputHandler.this._buttonStates[1] = false;
                    break;
                }
                    
                    // Right
                case SECONDARY: {
                    MouseInputHandler.this._buttonStates[2] = false;
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
            MouseInputHandler.this._scrollWheelXValue += scrollEvent.getDeltaX();
            MouseInputHandler.this._scrollWheelYValue += scrollEvent.getDeltaY();
            
            // Limit horizontal scroll
            if (MouseInputHandler.this._scrollWheelXValue < MouseInputHandler.this._minScrollWheelValue)
                MouseInputHandler.this._scrollWheelXValue = MouseInputHandler.this._minScrollWheelValue;
            else if (MouseInputHandler.this._scrollWheelXValue > MouseInputHandler.this._maxScrollWheelValue)
                MouseInputHandler.this._scrollWheelXValue = MouseInputHandler.this._maxScrollWheelValue;
                
            // Limit vertical scroll
            if (MouseInputHandler.this._scrollWheelYValue < MouseInputHandler.this._minScrollWheelValue)
                MouseInputHandler.this._scrollWheelYValue = MouseInputHandler.this._minScrollWheelValue;
            else if (MouseInputHandler.this._scrollWheelYValue > MouseInputHandler.this._maxScrollWheelValue)
                MouseInputHandler.this._scrollWheelYValue = MouseInputHandler.this._maxScrollWheelValue;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Public statics
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final int           NUM_BUTTONS            = 3;
    public static final long          MAX_SCROLL_WHEEL_VALUE = 100000000;
    public static final long          MIN_SCROLL_WHEEL_VALUE = -MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
                                                             
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private final OnMouseWheelChanged _onMouseWheelChanged;
    private final OnMouseMoved        _onMouseMoved;
    private final OnMousePressed      _onMousePressed;
    private final OnMouseReleased     _onMouseReleased;
    private final OnMouseDragged      _onMouseDragged;
                                      
    private final boolean             _buttonStates[];
                                      
    private double                    _mouseX, _mouseY;
                                      
    private double                    _curMouseX, _curMouseY, _prevMouseX, _prevMouseY;
    private long                      _scrollWheelXValue, _scrollWheelYValue;
                                      
    private long                      _maxScrollWheelValue, _minScrollWheelValue;
                                      
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public MouseInputHandler(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating new event handlers
        this._onMouseWheelChanged = new OnMouseWheelChanged();
        this._onMouseMoved = new OnMouseMoved();
        this._onMousePressed = new OnMousePressed();
        this._onMouseReleased = new OnMouseReleased();
        this._onMouseDragged = new OnMouseDragged();
        
        // Creating array for the mouse button states
        this._buttonStates = new boolean[MouseInputHandler.NUM_BUTTONS];
        
        // Setting default values
        this._scrollWheelXValue = this._scrollWheelYValue = 0;
        this._mouseX = this._mouseY = 0;
        this._maxScrollWheelValue = MouseInputHandler.MAX_SCROLL_WHEEL_VALUE;
        this._minScrollWheelValue = MouseInputHandler.MIN_SCROLL_WHEEL_VALUE;
    }
    
    @Override
    public void Destroy() {
    }
    
    public double GetMouseDeltaX() {
        return this._curMouseX - this._prevMouseX;
    }
    
    public double GetMouseDeltaY() {
        return this._curMouseY - this._prevMouseY;
    }
    
    public double GetMouseX() {
        return this._mouseX;
    }
    
    public double GetMouseY() {
        return this._mouseY;
    }
    
    public long GetScrollWheelXValue() {
        return this._scrollWheelXValue;
    }
    
    public long GetScrollWheelYValue() {
        return this._scrollWheelYValue;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Overridden methods from GameObject
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void Initialize() {
        
        // Hook events
        this.GetGame().setOnMouseDragged(this._onMouseDragged);
        this.GetGame().setOnMouseMoved(this._onMouseMoved);
        this.GetGame().setOnScroll(this._onMouseWheelChanged);
        this.GetGame().setOnMousePressed(this._onMousePressed);
        this.GetGame().setOnMouseReleased(this._onMouseReleased);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public boolean IsButtonDown(final int buttonNumber) {
        return this._buttonStates[buttonNumber];
    }
    
    public boolean IsButtonUp(final int buttonNumber) {
        return !this._buttonStates[buttonNumber];
    }
    
    public boolean IsLeftButtonDown() {
        return this._buttonStates[0];
    }
    
    public boolean IsLeftButtonUp() {
        return !this._buttonStates[0];
    }
    
    public boolean IsMidButtonDown() {
        return this._buttonStates[1];
    }
    
    public boolean IsMidButtonUp() {
        return !this._buttonStates[1];
    }
    
    public boolean IsRightButtonDown() {
        return this._buttonStates[2];
    }
    
    public boolean IsRightButtonUp() {
        return !this._buttonStates[2];
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void SetMaxMouseWheelValue(final long value) {
        
        if (this._scrollWheelXValue > value) this._scrollWheelXValue = value;
        
        if (this._scrollWheelYValue > value) this._scrollWheelYValue = value;
        
        this._maxScrollWheelValue = value;
    }
    
    public void SetMinMouseWheelValue(final long value) {
        
        if (this._scrollWheelXValue < value) this._scrollWheelXValue = value;
        
        if (this._scrollWheelYValue < value) this._scrollWheelYValue = value;
        
        this._minScrollWheelValue = value;
    }
    
    public synchronized void SetMouseX(final double value) {
        this._mouseX = value;
    }
    
    public synchronized void SetMouseY(final double value) {
        this._mouseY = value;
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        // Update previous location
        this._prevMouseX = this._curMouseX;
        this._prevMouseY = this._curMouseY;
        
        this._curMouseX = this._mouseX;
        this._curMouseY = this._mouseY;
    }
}
