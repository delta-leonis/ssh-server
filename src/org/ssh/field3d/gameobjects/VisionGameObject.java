package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;

public abstract class VisionGameObject {
    
    private final Game game;
    
    public VisionGameObject(Game game) {
        
        // Setting game
        this.game = game;
    }
    
    public abstract void onInitialize();
    public abstract void onDestroy();
    
    /**
     * Gets the game of the game object.
     * 
     * @return
     */
    protected Game getGame() {
        return this.game;
    }
}
