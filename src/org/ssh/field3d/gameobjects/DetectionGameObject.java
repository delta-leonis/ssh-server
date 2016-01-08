package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;

/**
 * Detection game object class. This class is used for every game object in the 3d field that needs detection data.
 *
 * @see VisionGameObject
 * @author Mark Lefering
 */
public abstract class DetectionGameObject extends VisionGameObject {

    /**
     * Constructor. This instantiates a new DetectionGameObject.
     *
     * @param game
     *              The {@link Game} of the {@link VisionGameObject}
     */
    public DetectionGameObject(Game game) {

        // Initialize super class
        super(game);
    }

    /**
     * On update detection method. This method needs to be called whenever there is new detection data available.
     */
    public abstract void onUpdateDetection();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void onInitialize();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void onDestroy();
}
