package field3d.core.gameobjects.input;

import java.util.HashMap;

import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class KeyInputHandler extends GameObject {	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private HashMap<KeyCode, Boolean> _curKeyState;
	private KeyPressedHandler _keyPressedHandler;
	private KeyReleasedHandler _keyReleasedHandler;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public KeyInputHandler(Game game) {
		
		super(game);
		
		// Creating new hash map
		_curKeyState = new HashMap<KeyCode, Boolean>();
		
		_keyPressedHandler = new KeyPressedHandler();
		_keyReleasedHandler = new KeyReleasedHandler();		
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Overridden methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize() {
		
		System.out.println("KeyInputHandler()");
		
		// Hook on key pressed
		GetGame().setOnKeyPressed(_keyPressedHandler);
	
		// Hook on key released
		GetGame().setOnKeyReleased(_keyReleasedHandler);
	}



	@Override
	public void Update(long timeDivNano) { }
	@Override
	public void Destroy() { }
		
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isKeyDown(KeyCode keyCode) {
		
		if (_curKeyState.containsKey(keyCode)) {
			
			return _curKeyState.get(keyCode);
		}
		
		return false;
	}
	public boolean isKeyUp(KeyCode keyCode) {
		
		if (_curKeyState.containsKey(keyCode)) {
			
			return !_curKeyState.get(keyCode);
		}
		
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void setState(KeyCode keyCode, boolean state) {
	
		if (_curKeyState.containsKey(keyCode)) {
		
			// Replace state
			_curKeyState.replace(keyCode, state);
		
		} else {
		
			// Add state
			_curKeyState.put(keyCode, state);
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Inner classes
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	class KeyPressedHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			
			setState(event.getCode(), true);
		}
	}
	
	
	
	class KeyReleasedHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			
			setState(event.getCode(), false);
		}
	}
}
