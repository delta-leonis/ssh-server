package org.ssh.field3d.core.gameobjects;

import org.ssh.field3d.core.game.Game;

/**
 * GameObject class. This class is the abstract class for the game objects in the game.
 * 
 * @see Game
 *      
 * @author Mark Lefering
 */
public abstract class GameObject {
    
    /** The {@link Game}. */
    private final Game game;
    
    /**
     * Instantiates a new game object.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public GameObject(final Game game) {
        
        // Setting game
        this.game = game;
    }
    
    /**
     * Abstract destroy method. This is called from {@link Game}.
     */
    public abstract void onDestroy();
    
    /**
     * Abstract initialize method. This is called from {@link Game}.
     */
    public abstract void onInitialize();
    
    /**
     * Abstract update method. This is called from {@link Game}.
     *
     * @param timeDivNano
     *            The time difference in nanoseconds.
     */
    public abstract void onUpdate(long timeDivNano);
    
    /**
     * Gets the game of the {@link GameObject}.
     *
     * @return The {@link Game} of the {@link GameObject}.
     */
    public Game getGame() {
        return this.game;
    }
}
