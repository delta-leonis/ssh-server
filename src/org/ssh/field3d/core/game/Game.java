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
 * @author Mark Lefering - 330430
 * @date 5-11-2015
 */
public abstract class Game extends SubScene {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Inner classes
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
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
            Game.this.InternalUpdate();
        }
        
    }
    
    private static final int      FPS           = 60;
                                                
    private static final int      FRAME_TIME_MS = (1 / Game.FPS) * 1000;
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Timeline              _timeline;
    private AnimationTimerHandler _animationTimerHandler;
                                  
    private ArrayList<GameObject> _gameObjects;
    private MouseInputHandler     _mouseInputHandler;
                                  
    private SubScene              _3dScene;
                                  
    private ThirdPersonCamera     _thirdPersonCamera;
    private Group                 _worldGroup;
                                  
    private Group                 _cameraGroup;
    private Group                 _2dGroup;
                                  
    private Group                 _3dGroup;
                                  
    private long                  _curTime, _prevTime;
                                  
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Add / Remove game object methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * AddGameObject method This is the method for adding game objects to the game.
     *
     * @param gameObject
     *            The GameObject to add to the game.
     */
    public void AddGameObject(final GameObject gameObject) {

        // Check if we need to add our game object
        if ((this._gameObjects != null) && !this._gameObjects.contains(gameObject)) {

            // Initialize our game object
            gameObject.Initialize();
            // Add to game objects
            this._gameObjects.add(gameObject);
        }
    }

    /**
     *
     * Destroy method This is the abstract method for destroying the game, it gets called when the
     * game closes.
     */
    public abstract void Destroy();
    
    /**
     *
     * destroyGameObjects method This method destroys all the game objects in the game
     */
    private void destroyGameObjects() {

        // Check if we need to update
        if (this._gameObjects != null) {

            // Loop through game objects
            for (final GameObject gameObject : this._gameObjects) {

                // Check if we can destroy the current game object
                if (gameObject != null) {
                    
                    // Destroy the current game object
                    gameObject.Destroy();
                    // Remove from array list
                    this.RemoveGameObject(gameObject);
                }
            }
        }
    }

    public Group Get2DGroup() {
        return this._2dGroup;
    }

    public Group Get3DGroup() {
        return this._3dGroup;
    }
    
    public Group GetCameraGroup() {
        return this._cameraGroup;
    }

    public MouseInputHandler GetMouseInputHandler() {
        return this._mouseInputHandler;
    }
    
    public ThirdPersonCamera GetThirdPersonCamera() {
        return this._thirdPersonCamera;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public Group GetWorldGroup() {
        return this._worldGroup;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
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
        this._cameraGroup = new Group();
        this._worldGroup = new Group();
        this._2dGroup = new Group();
        this._3dGroup = new Group();

        // Creating new array list for the game objects
        this._gameObjects = new ArrayList<GameObject>();
        // Creating new keyboard input handler
        this._mouseInputHandler = new MouseInputHandler(this);
        // Creating third-person camera
        this._thirdPersonCamera = new ThirdPersonCamera(this);
        // Creating animation timer handler
        this._animationTimerHandler = new AnimationTimerHandler();
        // Creating time line
        this._timeline = new Timeline();
        // Create 3D Scene
        this._3dScene = new SubScene(this._3dGroup, width, height, depthBuffer, antiAliasing);
        
        // Setting default values
        this._curTime = this._prevTime = 0;
        // Get 2d group (root of this SubScene);
        this._2dGroup = (Group) this.getRoot();

        // Add key frame to our time line
        this._timeline.getKeyFrames().add(keyFrame);
        
        // Add 3d scene to scene
        this._2dGroup.getChildren().add(this._3dScene);
        // Adding camera group to the root group
        this._3dGroup.getChildren().add(this._cameraGroup);
        // Adding world group to the root group
        this._3dGroup.getChildren().add(this._worldGroup);
        
        // Setting the camera of the 3D SubScene
        this._3dScene.setCamera(this._thirdPersonCamera.GetPerspectiveCamera());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Abstract method
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * Initialize method This is the abstract method for initializing of the game, it gets called at
     * initialization.
     *
     */
    public abstract void Initialize();

    /**
     *
     * InternalDestroy method This is the internal destroy method, this can be called at the end of
     * the game.
     */
    public void InternalDestroy() {

        // TODO: Destroy key input handler
        // TODO: Destroy mouse input handler
        // TODO: Destroy camera

        // Destroy game objects
        this.destroyGameObjects();
        // Destroy our self
        this.Destroy();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Internal methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * InternalInitialize method This is the internal initialization method for the game, this needs
     * to be called when the game needs to be started.
     */
    public void InternalInitialize() {

        // Initialize game
        this.Initialize();

        // Setting time
        this._curTime = this._prevTime = System.nanoTime();

        // Play time line
        this._timeline.play();
        
        // Start animation timer
        this._animationTimerHandler.start();

        // Adding game objects
        this.AddGameObject(this._mouseInputHandler);
        this.AddGameObject(this._thirdPersonCamera);
    }
    
    /**
     * 
     * InternalUpdate method This is the internal update method, this needs to be called every
     * frame. It gets called from AnimationTimerHandler at the end of this document.
     */
    public void InternalUpdate() {
        
        long timeDivNano = 0;

        // Update previous time
        this._prevTime = this._curTime;
        // Update current time
        this._curTime = System.nanoTime();

        // Calculate time difference
        timeDivNano = this._curTime - this._prevTime;

        // Update our game
        this.Update(timeDivNano);
        // Update game objects
        this.updateGameObjects(timeDivNano);
    }

    /**
     *
     * RemoveGameObject method This is the method for removing game objects from the game.
     *
     * @param gameObject
     *            The GameObject to remove from the game.
     */
    public void RemoveGameObject(final GameObject gameObject) {

        // Check if we need to remove our game object
        if ((this._gameObjects != null) && this._gameObjects.contains(gameObject)) {

            // Notify the game object we are removing it from the game
            gameObject.Destroy();
            // Remove from game objects
            this._gameObjects.remove(gameObject);
        }
    }

    /**
     *
     * Update method This is the abstract method for updating the game, it gets called at every
     * frame.
     *
     * @param timeDivNano
     *            Time difference in nanoseconds
     */
    public abstract void Update(long timeDivNano);
    
    /**
     *
     * updateGameObjects method This method updates all the game objects in the game.
     *
     * @param timeDivNano
     *            Time difference in nanoseconds.
     */
    private void updateGameObjects(final long timeDivNano) {

        // Check if we need to update
        if (this._gameObjects != null) {

            // Loop through game objects
            for (final GameObject gameObject : this._gameObjects) {

                // Check if we can update the current game object
                if (gameObject != null)
                    // Update the game object
                    gameObject.Update(timeDivNano);
            }
        }
    }
}