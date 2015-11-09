package org.ssh.field3d.core.gameobjects.input;

import java.util.HashMap;
import java.util.Map;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * KeyInputHandler class. This class is responsible for the handling the keyboard input for the 3d
 * field.
 * 
 * @author Mark Lefering
 */
public class KeyInputHandler extends GameObject {
    
    private final Map<KeyCode, Boolean> curKeyState;
    private final KeyPressedHandler     keyPressedHandler;
    private final KeyReleasedHandler    keyReleasedHandler;
                                        
    /**
     * Constructor
     * 
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public KeyInputHandler(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating new hash map
        this.curKeyState = new HashMap<KeyCode, Boolean>();
        
        // Creating key pressed and released handler
        this.keyPressedHandler = new KeyPressedHandler();
        this.keyReleasedHandler = new KeyReleasedHandler();
    }
    
    /**
     * Initialize method. This method hooks the OnKeyPressed & OnKeyReleased events.
     */
    @Override
    public void initialize() {
        
        // Hook keyboard events
        this.getGame().setOnKeyPressed(this.keyPressedHandler);
        this.getGame().setOnKeyReleased(this.keyReleasedHandler);
    }
    
    /**
     * Update method.
     */
    @Override
    public void update(final long timeDivNano) {
    }
    
    /**
     * Destroy method. This method unhooks the keyboard events.
     */
    @Override
    public void destroy() {
        
        // Unhook events
        this.getGame().setOnKeyPressed(null);
        this.getGame().setOnKeyReleased(null);
    }
    
    public boolean isKeyDown(final KeyCode keyCode) {
        
        // Check if the map contains the key code
        if (this.curKeyState.containsKey(keyCode)) {
            
            // Return the state
            return this.curKeyState.get(keyCode);
        }
        
        // Return false
        return false;
    }
    
    public boolean isKeyUp(final KeyCode keyCode) {
        
        // Check if the map contains the key code
        if (this.curKeyState.containsKey(keyCode)) {
            
            // Return the not state
            return !this.curKeyState.get(keyCode);
        }
        
        // Return true
        return true;
    }
    
    /**
     * setState method. This method set the state of a key.
     * 
     * @param keyCode
     *            The {@link KeyCode} to set the state of.
     * @param state
     *            The state of the button, true for pressed, false for not pressed.
     */
    public void setState(final KeyCode keyCode, final boolean state) {
        
        // Check if the map contains the key code
        if (this.curKeyState.containsKey(keyCode)) {
            
            // Replace state
            this.curKeyState.replace(keyCode, state);
        }
        else {
            
            // Add state
            this.curKeyState.put(keyCode, state);
        }
    }
    
    /**
     * KeyPressedHandler class. This class handles the key pressed event.
     * 
     * @author Mark Lefering
     */
    class KeyPressedHandler implements EventHandler<KeyEvent> {
        
        @Override
        public void handle(final KeyEvent event) {
            
            // Setting pressed stated
            KeyInputHandler.this.setState(event.getCode(), true);
        }
    }
    
    /**
     * KeyReleasedHandler class. This class handles the key released event.
     * 
     * @author Mark Lefering
     */
    class KeyReleasedHandler implements EventHandler<KeyEvent> {
        
        @Override
        public void handle(final KeyEvent event) {
            
            // Setting released state
            KeyInputHandler.this.setState(event.getCode(), false);
        }
    }
}
