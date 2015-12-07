package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;


public abstract class GeometryGameObject extends VisionGameObject {
    
    public GeometryGameObject(Game game) {
        super(game);
    }
    
    public abstract void onUpdateGeometry();
    
    @Override
    public abstract void onInitialize();
    
    @Override
    public abstract void onDestroy();
}
