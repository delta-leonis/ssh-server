package org.ssh.field3d.core.game;

import java.util.ArrayList;

import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.gameobjects.camera.ThirdPersonCamera;
import org.ssh.field3d.core.gameobjects.input.MouseInputHandler;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.util.Duration;

/**
 *
 * Game class This is the core of the game. This class contains the game objects, input handlers and
 * camera. This class creates the actual game loop.
 *
 * @author Mark Lefering
 */
public abstract class Game extends SubScene {
    
    private static final int      FPS           = 60;
    private static final int      FRAME_TIME_MS = (1 / Game.FPS) * 1000;
                                                
    private Timeline              timeline;
    private AnimationTimerHandler animationTimerHandler;
    private SubScene              scene3D;
                                  
    private ArrayList<GameObject> gameObjects;
                                  
    private MouseInputHandler     mouseInputHandler;
    private ThirdPersonCamera     thirdPersonCamera;
                                  
    private Group                 worldGroup;
    private Group                 cameraGroup;
    private Group                 group2d;
    private Group                 group3d;
                                  
    private long                  curTime, prevTime;
                                  
    /**
     *
     * Constructor
     *
     * @param root
     *            root of the SubScene.
     * @param width
     *            width of the SubScene.
     * @param height
     *            height of the SubScene.
     */
    public Game(final Parent root, final double width, final double height) {
        
        // Initialize super class
        super(root, width, height);
        
        // Initialize
        this.initialize(width, height, true, SceneAntialiasing.DISABLED);
    }
    
    /**
     *
     * Constructor
     *
     * @param root
     *            Root of the SubScene.
     * @param width
     *            Width of the SubScene.
     * @param height
     *            Height of the SubScene.
     * @param depthBuffer
     *            Depth buffer enabled state.
     * @param antiAliasing
     *            Anti-aliasing mode, SceneAntialiasing.DISABLED.
     */
    public Game(final Parent root,
            final double width,
            final double height,
            final boolean depthBuffer,
            final SceneAntialiasing antiAliasing) {
            
        // Initialize super class
        super(root, width, height, false, antiAliasing);
        
        // Initialize
        this.initialize(width, height, depthBuffer, antiAliasing);
    }
    
    /**
     *
     * Initialize method This is the abstract method for initializing of the game, it gets called at
     * initialization.
     *
     */
    public abstract void initialize();
    
    /**
     *
     * Update method This is the abstract method for updating the game, it gets called at every
     * frame.
     *
     * @param timeDivNano
     *            Time difference in nanoseconds
     */
    public abstract void update(long timeDivNano);
    
    /**
     *
     * Destroy method This is the abstract method for destroying the game, it gets called when the
     * game closes.
     */
    public abstract void destroy();
      
    /**
     *
     * InternalInitialize method This is the internal initialization method for the game, this needs
     * to be called when the game needs to be started.
     */
    public void internalInitialize() {
        
        // Initialize game
        this.initialize();
        
        // Setting time
        this.curTime = this.prevTime = System.nanoTime();
        
        // Play time line
        this.timeline.play();
        
        // Start animation timer
        this.animationTimerHandler.start();
        
        // Adding game objects
        this.addGameObject(this.mouseInputHandler);
        this.addGameObject(this.thirdPersonCamera);
    }
    
    /**
     *
     * InternalDestroy method This is the internal destroy method, this can be called at the end of
     * the game.
     */
    public void internalDestroy() {
              
        // Destroy game objects
        this.destroyGameObjects();
        // Destroy our self
        this.destroy();
    }
    
    /**
     * 
     * InternalUpdate method This is the internal update method, this needs to be called every
     * frame. It gets called from AnimationTimerHandler at the end of this document.
     */
    public void internalUpdate() {
        
        long timeDivNano = 0;
        
        // Update previous time
        this.prevTime = this.curTime;
        // Update current time
        this.curTime = System.nanoTime();
        
        // Calculate time difference
        timeDivNano = this.curTime - this.prevTime;
        
        // Update our game
        this.update(timeDivNano);
        // Update game objects
        this.updateGameObjects(timeDivNano);
    }
    
    /**
    *
    * AddGameObject method This is the method for adding game objects to the game.
    *
    * @param gameObject
    *            The GameObject to add to the game.
    */
   public void addGameObject(final GameObject gameObject) {
       
       // Check if we need to add our game object
       if ((this.gameObjects != null) && !this.gameObjects.contains(gameObject)) {
           
           // Initialize our game object
           gameObject.Initialize();
           // Add to game objects
           this.gameObjects.add(gameObject);
       }
   }
    
    /**
     *
     * RemoveGameObject method This is the method for removing game objects from the game.
     *
     * @param gameObject
     *            The GameObject to remove from the game.
     */
    public void removeGameObject(final GameObject gameObject) {
        
        // Check if we need to remove our game object
        if ((this.gameObjects != null) && this.gameObjects.contains(gameObject)) {
            
            // Notify the game object we are removing it from the game
            gameObject.Destroy();
            // Remove from game objects
            this.gameObjects.remove(gameObject);
        }
    }
    
    public Group get2DGroup() {
        return this.group2d;
    }
    
    public Group get3DGroup() {
        return this.group3d;
    }
    
    public Group getCameraGroup() {
        return this.cameraGroup;
    }
    
    public MouseInputHandler getMouseInputHandler() {
        return this.mouseInputHandler;
    }
    
    public ThirdPersonCamera getThirdPersonCamera() {
        return this.thirdPersonCamera;
    }
    
    public Group getWorldGroup() {
        return this.worldGroup;
    }
    
    /**
     *
     * updateGameObjects method This method updates all the game objects in the game.
     *
     * @param timeDivNano
     *            Time difference in nanoseconds.
     */
    private void updateGameObjects(final long timeDivNano) {
        
        // Check if we need to update
        if (this.gameObjects != null) {
            
            // Loop through game objects
            for (final GameObject gameObject : this.gameObjects) {
                
                // Check if we can update the current game object
                if (gameObject != null)
                    // Update the game object
                    gameObject.Update(timeDivNano);
            }
        }
    }
    
    /**
     *
     * destroyGameObjects method This method destroys all the game objects in the game
     */
    private void destroyGameObjects() {
        
        // Check if we need to update
        if (this.gameObjects != null) {
            
            // Loop through game objects
            for (final GameObject gameObject : this.gameObjects) {
                
                // Check if we can destroy the current game object
                if (gameObject != null) {
                    
                    // Destroy the current game object
                    gameObject.Destroy();
                    // Remove from array list
                    this.removeGameObject(gameObject);
                }
            }
        }
    }
    
    /**
     *
     * initialize method This method is used in the constructor of this class, its used to create a
     * generic "constructor"
     *
     * @param root
     *            Root of the SubScene.
     * @param width
     *            Width of the SubScene.
     * @param height
     *            Height of the SubScene.
     * @param depthBuffer
     *            Depth buffer enabled state.
     * @param antiAliasing
     *            Anti-aliasing mode, SceneAntialiasing.DISABLED.
     */
    private void initialize(final double width,
            final double height,
            final boolean depthBuffer,
            final SceneAntialiasing antiAliasing) {
            
        // Create frame durations
        final Duration duration = Duration.millis(Game.FRAME_TIME_MS);
        final KeyFrame keyFrame = new KeyFrame(duration, t -> {
        });
        
        // Creating groups
        this.cameraGroup = new Group();
        this.worldGroup = new Group();
        this.group2d = new Group();
        this.group3d = new Group();
        
        // Creating new array list for the game objects
        this.gameObjects = new ArrayList<GameObject>();
        // Creating new keyboard input handler
        this.mouseInputHandler = new MouseInputHandler(this);
        // Creating third-person camera
        this.thirdPersonCamera = new ThirdPersonCamera(this);
        // Creating animation timer handler
        this.animationTimerHandler = new AnimationTimerHandler();
        // Creating time line
        this.timeline = new Timeline();
        // Create 3D Scene
        this.scene3D = new SubScene(this.group3d, width, height, depthBuffer, antiAliasing);
        
        // Setting default values
        this.curTime = this.prevTime = 0;
        // Get 2d group (root of this SubScene);
        this.group2d = (Group) this.getRoot();
        
        // Add key frame to our time line
        this.timeline.getKeyFrames().add(keyFrame);
        
        // Add 3d scene to scene
        this.group2d.getChildren().add(this.scene3D);
        // Adding camera group to the root group
        this.group3d.getChildren().add(this.cameraGroup);
        // Adding world group to the root group
        this.group3d.getChildren().add(this.worldGroup);
        
        // Setting the camera of the 3D SubScene
        this.scene3D.setCamera(this.thirdPersonCamera.GetPerspectiveCamera());
    }
    
    /**
     *
     * AnimationTimerHandler class This class handles the animation timer and updates the game.
     *
     * @author marklef2
     * @date 5-11-2015
     */
    class AnimationTimerHandler extends AnimationTimer {
        
        @Override
        public void handle(final long timeDivNano) {
            
            // Update game
            Game.this.internalUpdate();
        }
        
    }
}
