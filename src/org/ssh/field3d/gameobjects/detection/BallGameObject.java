package org.ssh.field3d.gameobjects.detection;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.DetectionGameObject;
import org.ssh.models.Ball;

import javafx.application.Platform;
import javafx.scene.shape.Sphere;

/**
 * Ball game object class. This class is responsible for the ball on the 3D field.
 *
 * @author marklef2
 * @see GameObject
 */
public class BallGameObject extends DetectionGameObject {
    
    /** The ball model. */
    private final Sphere ballModel;
    
    /** The ball vision model. */
    private Ball         ballVisionModel;
                         
    /**
     * Instantiates a new ball game object.
     *
     * @param game The {@link Game game}.
     * @param ballVisionModel The vision model of the {@link Ball ball}.
     */
    public BallGameObject(Game game, Ball ballVisionModel) {
        
        // Initialize super class
        super(game);
        
        // Creating sphere for the ball
        this.ballModel = new Sphere(Ball.BALL_DIAMETER * 4);
        
        // Setting ball vision model
        this.ballVisionModel = ballVisionModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        Platform.runLater(() -> {
            
            // Check if we need to add the ball model to the world
            if (!this.getGame().getWorldGroup().getChildren().contains(this.ballModel)) {
                
                // Add the ball model to the world
                this.getGame().getWorldGroup().getChildren().add(this.ballModel);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        Platform.runLater(() -> {
           
            // Check if we need to remove the ball model from the world group
            if (this.getGame().getWorldGroup().getChildren().contains(this.ballModel)) {
                
                // Remove the ball model from the world group
                this.getGame().getWorldGroup().getChildren().remove(this.ballModel);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateDetection() {
        
        if (this.ballVisionModel != null && this.ballVisionModel.getPosition() != null) {
            
            // Translate ball into the position
            this.ballModel.setTranslateX(this.ballVisionModel.getPosition().getX());
            this.ballModel.setTranslateY(this.ballVisionModel.getZPos());
            this.ballModel.setTranslateZ(this.ballVisionModel.getPosition().getY());
        }
    }    
}
