package org.ssh.field3d.gameobjects;

import org.ssh.field3d.core.game.Game;


public abstract class DetectionGameObject extends VisionGameObject {
    
    public DetectionGameObject(Game game) {
        super(game);
    }
    
    public abstract void onUpdateDetection();
    
    @Override
    public abstract void onInitialize();  
    
    @Override
    public abstract void onDestroy();
}
