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
 * @see DetectionGameObject
 * @author marklef2
 */
public class BallGameObject extends DetectionGameObject {

    private static final int STARTING_OFFSET_Y = 10;
    
    /** The 3D ball model. */
    private final Sphere ballModel;
    
    /** The ball vision model. */
    private Ball         ballVisionModel;
                         
    /**
     * Constructor. This instantiates a new BallGameObject object.
     *
     * @param game
     *              The {@link Game game}.
     * @param ballVisionModel
     *              The vision model of the {@link Ball ball}.
     */
    public BallGameObject(Game game, Ball ballVisionModel) {
        
        // Initialize super class
        super(game);
        
        // Creating sphere for the ball
        this.ballModel = new Sphere(Ball.BALL_DIAMETER);
        
        // Setting ball vision model
        this.ballVisionModel = ballVisionModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {

        // Sync with UI thread
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

        // Sync with UI thread
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

        // Check if the vision model is not null and also if the position of the vision model is not null
        if (this.ballVisionModel != null && this.ballVisionModel.getPosition() != null) {

            // Sync with UI thread
            Platform.runLater(() -> {
                
                // Translate ball into the position
                this.ballModel.setTranslateX(-this.ballVisionModel.getPosition().getX());
                this.ballModel.setTranslateY(this.ballVisionModel.getZPos() + this.ballModel.getRadius() + STARTING_OFFSET_Y);
                this.ballModel.setTranslateZ(this.ballVisionModel.getPosition().getY());
            });
        }
    }    
}
