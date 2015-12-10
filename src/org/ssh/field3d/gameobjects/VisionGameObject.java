package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;

/**
 * Vision game object. This is the base class for {@link DetectionGameObject} and {@link GeometryGameObject}.
 *
 * @author Mark Lefering
 */
public abstract class VisionGameObject {

    /** The {@link Game} of the vision game object */
    private final Game game;

    /**
     * Constructor. This instantiates a new {@link VisionGameObject} object.
     *
     * @param game
     *              The {@link Game} of the {@link VisionGameObject}.
     */
    public VisionGameObject(Game game) {
        
        // Setting game
        this.game = game;
    }

    /**
     * On initialize method. This method should be called when the {@link VisionGameObject} is added to the game.
     */
    public abstract void onInitialize();

    /**
     * On destroy method. This method should be called when the {@link VisionGameObject} is removed from the game.
     */
    public abstract void onDestroy();
    
    /**
     * Gets the game of the {@link VisionGameObject}.
     * 
     * @return The {@link Game} of the {@link VisionGameObject}.
     */
    protected Game getGame() {
        return this.game;
    }
}
