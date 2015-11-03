/**
 * 
 * 	MouseInputHandler class
 *
 * 		- This class handles mouse input
 * 
 * 	TODO: Javadoc
 * 
 * @author marklef2
 * @date 13-10-2015
 */
package field3d.core.gameobjects.input;

import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;



public class MouseInputHandler extends GameObject {
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Public statics
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static final int NUM_BUTTONS = 3;
	public static final long MAX_SCROLL_WHEEL_VALUE = 100000000;
	public static final long MIN_SCROLL_WHEEL_VALUE = -MAX_SCROLL_WHEEL_VALUE;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private OnMouseWheelChanged _onMouseWheelChanged;
	private OnMouseMoved _onMouseMoved;
	private OnMousePressed _onMousePressed;
	private OnMouseReleased _onMouseReleased;
	private OnMouseDragged _onMouseDragged;
	
	private boolean _buttonStates[];
	private double _mouseX, _mouseY;
	private double _curMouseX, _curMouseY, _prevMouseX, _prevMouseY;
	private long _scrollWheelXValue, _scrollWheelYValue;
	private long _maxScrollWheelValue, _minScrollWheelValue;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public MouseInputHandler(Game game) {
		
		// Initialize super class
		super(game);
		
		// Creating new event handlers
		_onMouseWheelChanged = new OnMouseWheelChanged();
		_onMouseMoved = new OnMouseMoved();
		_onMousePressed = new OnMousePressed();
		_onMouseReleased = new OnMouseReleased();
		_onMouseDragged = new OnMouseDragged();
		
		// Creating array for the mouse button states
		_buttonStates = new boolean[NUM_BUTTONS];		
		
		
		// Setting default values
		_scrollWheelXValue = _scrollWheelYValue = 0;
		_mouseX = _mouseY = 0;
		_maxScrollWheelValue = MAX_SCROLL_WHEEL_VALUE;
		_minScrollWheelValue = MIN_SCROLL_WHEEL_VALUE;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Overridden methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize() {
		
		// Hook events
		GetGame().setOnMouseDragged(_onMouseDragged);
		GetGame().setOnMouseMoved(_onMouseMoved);
		GetGame().setOnScroll(_onMouseWheelChanged);
		GetGame().setOnMousePressed(_onMousePressed);
		GetGame().setOnMouseReleased(_onMouseReleased);	
	}

	@Override
	public void Update(long timeDivNano) { 
		
		// Update previous location
		_prevMouseX = _curMouseX;
		_prevMouseY = _curMouseY;
		
		_curMouseX = _mouseX;
		_curMouseY = _mouseY;
	}
	@Override
	public void Destroy() { }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public boolean IsButtonDown(int buttonNumber) { return _buttonStates[buttonNumber]; }	
	public boolean IsLeftButtonDown()  { return _buttonStates[0]; }
	public boolean IsMidButtonDown() { return _buttonStates[1]; }
	public boolean IsRightButtonDown() { return _buttonStates[2]; }
	
	public boolean IsButtonUp(int buttonNumber) { return !_buttonStates[buttonNumber]; }
	public boolean IsLeftButtonUp() { return !_buttonStates[0]; }
	public boolean IsMidButtonUp() { return !_buttonStates[1]; }
	public boolean IsRightButtonUp() { return !_buttonStates[2]; }
	
	public double GetMouseX() { return _mouseX; }
	public double GetMouseY() { return _mouseY; }
	public double GetMouseDeltaX() { return _curMouseX - _prevMouseX; }
	public double GetMouseDeltaY() { return _curMouseY - _prevMouseY; }
	
	public long GetScrollWheelXValue() { return _scrollWheelXValue; }
	public long GetScrollWheelYValue() { return _scrollWheelYValue; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void SetMaxMouseWheelValue(long value) { 
		
		if (_scrollWheelXValue > value)
			_scrollWheelXValue = value;
		
		if (_scrollWheelYValue > value) 	
			_scrollWheelYValue = value;
		
		_maxScrollWheelValue = value;
	}
	public void SetMinMouseWheelValue(long value) {
		
		if (_scrollWheelXValue < value) 
			_scrollWheelXValue = value;
		
		if (_scrollWheelYValue < value)
			_scrollWheelYValue = value;		
		
		_minScrollWheelValue = value;
	}
	public synchronized void SetMouseX(double value) { _mouseX = value; }
	public synchronized void SetMouseY(double value) { _mouseY = value; }



	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Inner classes
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	class OnMouseMoved implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent mouseEvent) {
			
			// Update mouse x & y
			SetMouseX(mouseEvent.getScreenX());
			SetMouseY(mouseEvent.getScreenY());
		}		
	}
	
	class OnMouseDragged implements EventHandler<MouseEvent> {
		
		@Override
		public void handle(MouseEvent mouseEvent) {
			
			// Update mouse x & y
			SetMouseX(mouseEvent.getScreenX());
			SetMouseY(mouseEvent.getScreenY());
		}
	}
	
	class OnMouseWheelChanged implements EventHandler<ScrollEvent> {
		
		@Override
		public void handle(ScrollEvent scrollEvent) {
			
			// Update scroll wheel value
			_scrollWheelXValue += scrollEvent.getDeltaX();
			_scrollWheelYValue += scrollEvent.getDeltaY();
			
			// Limit horizontal scroll
			if (_scrollWheelXValue < _minScrollWheelValue) 
				_scrollWheelXValue = _minScrollWheelValue;
			else if (_scrollWheelXValue > _maxScrollWheelValue)
				_scrollWheelXValue = _maxScrollWheelValue;
			
			// Limit vertical scroll
			if (_scrollWheelYValue < _minScrollWheelValue)
				_scrollWheelYValue = _minScrollWheelValue;
			else if (_scrollWheelYValue > _maxScrollWheelValue)
				_scrollWheelYValue = _maxScrollWheelValue;
		}
	}
	
	class OnMousePressed implements EventHandler<MouseEvent> {
		
		@Override
		public void handle(MouseEvent mouseEvent) {
			
			switch (mouseEvent.getButton()) {
			
			// Left
			case PRIMARY:	{ _buttonStates[0] = true; break; }
				
			// Mid
			case MIDDLE: 	{ _buttonStates[1] = true; break; }
				
			// Right
			case SECONDARY: { _buttonStates[2] = true; break; }
			
			// Default case
			default:
				break;
			}
		}
	}
	
	class OnMouseReleased implements EventHandler<MouseEvent> {
		
		@Override
		public void handle(MouseEvent mouseEvent) {
			
			switch (mouseEvent.getButton()) {
			
			// Left
			case PRIMARY:	{ _buttonStates[0] = false; break; }
				
			// Mid
			case MIDDLE: 	{ _buttonStates[1] = false; break; }
				
			// Right
			case SECONDARY: { _buttonStates[2] = false; break; }
			
			// Default case
			default:
				break;
			}
		}
	}
}
