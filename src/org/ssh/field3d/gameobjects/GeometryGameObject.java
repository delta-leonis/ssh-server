package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;

/**
 * Geometry game object class. This class is used for every game object in the 3d field that needs geometry data.
 *
 * @see VisionGameObject
 * @author Mark Lefering
 */
public abstract class GeometryGameObject extends VisionGameObject {

    /**
     * Constructor. This instantiates a new GeometryGameObject object.
     *
     * @param game
     *              The {@link Game} of the {@link VisionGameObject}.
     */
    public GeometryGameObject(Game game) {

        // Initialize super class
        super(game);
    }

    /**
     * On update geometry method. This method needs to be called whenever there is new geometry data available.
     */
    public abstract void onUpdateGeometry();

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
