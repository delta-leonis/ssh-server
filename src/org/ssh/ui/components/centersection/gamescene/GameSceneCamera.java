package org.ssh.ui.components.centersection.gamescene;

import javafx.collections.ListChangeListener;
import javafx.scene.DepthTest;
import javafx.scene.PerspectiveCamera;

/**
 * Class for generic settings of the camera (nearClip, farClip and FOV)
 *
 * @author Jeroen de Jong
 * @date 26-1-2016
 */
public class GameSceneCamera extends PerspectiveCamera {

    public GameSceneCamera(){
        super(true);
        setNearClip(10.0);
        setFarClip(30000.0);
    }
}
