package org.ssh.field3d.core.gameobjects.input;

import java.util.HashMap;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyInputHandler extends GameObject {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Inner classes
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    class KeyPressedHandler implements EventHandler<KeyEvent> {
        
        @Override
        public void handle(final KeyEvent event) {
            
            KeyInputHandler.this.setState(event.getCode(), true);
        }
    }
    
    class KeyReleasedHandler implements EventHandler<KeyEvent> {
        
        @Override
        public void handle(final KeyEvent event) {
            
            KeyInputHandler.this.setState(event.getCode(), false);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private final HashMap<KeyCode, Boolean> _curKeyState;
                                            
    private final KeyPressedHandler         _keyPressedHandler;
                                            
    private final KeyReleasedHandler        _keyReleasedHandler;
                                            
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public KeyInputHandler(final Game game) {
        
        super(game);
        
        // Creating new hash map
        this._curKeyState = new HashMap<KeyCode, Boolean>();
        
        this._keyPressedHandler = new KeyPressedHandler();
        this._keyReleasedHandler = new KeyReleasedHandler();
        
    }
    
    @Override
    public void Destroy() {
    }
    
    /////////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Overridden methods from GameObject
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void Initialize() {
        
        System.out.println("KeyInputHandler()");
        
        // Hook on key pressed
        this.GetGame().setOnKeyPressed(this._keyPressedHandler);
        
        // Hook on key released
        this.GetGame().setOnKeyReleased(this._keyReleasedHandler);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isKeyDown(final KeyCode keyCode) {
        
        if (this._curKeyState.containsKey(keyCode)) {
            
            return this._curKeyState.get(keyCode);
        }
        
        return false;
    }
    
    public boolean isKeyUp(final KeyCode keyCode) {
        
        if (this._curKeyState.containsKey(keyCode)) {
            
            return !this._curKeyState.get(keyCode);
        }
        
        return true;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setState(final KeyCode keyCode, final boolean state) {
        
        if (this._curKeyState.containsKey(keyCode)) {
            
            // Replace state
            this._curKeyState.replace(keyCode, state);
            
        }
        else {
            
            // Add state
            this._curKeyState.put(keyCode, state);
        }
    }
    
    @Override
    public void Update(final long timeDivNano) {
    }
}
