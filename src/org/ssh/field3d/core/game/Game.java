package org.ssh.field3d.core.game;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.util.Duration;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.gameobjects.camera.ThirdPersonCamera;
import org.ssh.field3d.core.gameobjects.input.MouseInputHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Game class. This is the core of the game. This class contains the game objects, input handlers and
 * camera. This class creates the actual game loop.
 *
 * @author Mark Lefering
 */
public abstract class Game extends SubScene {

    /**
     * The target FPS of the 3d field.
     */
    private static final int FPS = 60;
    /**
     * The target frame time of the 3d field.
     */
    private static final int FRAME_TIME_MS = (int) ((1.0 / Game.FPS) * 1000.0);

    /**
     * The timeline of the 3d field.
     */
    private Timeline timeline;
    /**
     * The animation timer handler.
     */
    private AnimationTimerHandler animationTimerHandler;

    /**
     * The queue of game objects.
     */
    private Queue<GameObject> gameObjects;

    /**
     * The mouse input handler.
     */
    private MouseInputHandler mouseInputHandler;
    /**
     * The third person camera.
     */
    private ThirdPersonCamera thirdPersonCamera;

    /**
     * The 3d SubScene for the 3d field.
     */
    private SubScene scene3D;

    /**
     * The world group.
     */
    private Group worldGroup;
    /**
     * The camera group.
     */
    private Group cameraGroup;
    /**
     * The 2d overlay group.
     */
    private Group group2d;
    /**
     * The 3d group.
     */
    private Group group3d;

    /**
     * The current time in nanoseconds.
     */
    private long curTime;
    /**
     * The previous time in nanoseconds.
     */
    private long prevTime;

    /**
     * The first frame state.
     */
    private boolean isFirstFrame;

    /**
     * Constructor. Instantiates a new Game.
     *
     * @param root   root of the {@link SubScene}.
     * @param width  width of the {@link SubScene}.
     * @param height height of the {@link SubScene}.
     */
    public Game(final Parent root, final double width, final double height) {

        // Initialize super class
        super(root, width, height);

        // Initialize
        this.initialize(width, height, true, SceneAntialiasing.DISABLED);
    }

    /**
     * Constructor
     *
     * @param root         Root of the SubScene.
     * @param width        Width of the SubScene.
     * @param height       Height of the SubScene.
     * @param depthBuffer  Depth buffer enabled state.
     * @param antiAliasing Anti-aliasing mode, SceneAntialiasing.DISABLED.
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
     * Initialize method This is the abstract method for initializing of the game, it gets called at
     * initialization.
     */
    public abstract void initialize();

    /**
     * Update method This is the abstract method for updating the game, it gets called at every
     * frame.
     *
     * @param timeDivNano Time difference in nanoseconds
     */
    public abstract void update(long timeDivNano);

    /**
     * Destroy method This is the abstract method for destroying the game, it gets called when the
     * game closes.
     */
    public abstract void destroy();

    /**
     * InternalInitialize method This is the internal initialization method for the game, this needs
     * to be called when the game needs to be started.
     */
    public void internalInitialize() {

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
     * InternalUpdate method This is the internal update method, this needs to be called every
     * frame. It gets called from AnimationTimerHandler at the end of this document.
     */
    public void internalUpdate() {

        long timeDivNano;

        // Update previous time
        this.prevTime = this.curTime;
        // Update current time
        this.curTime = System.nanoTime();

        // Calculate time difference
        timeDivNano = this.curTime - this.prevTime;

        // Update game objects
        this.updateGameObjects(timeDivNano);

        // Update our game
        this.update(timeDivNano);
    }

    /**
     * AddGameObject method This is the method for adding game objects to the game.
     *
     * @param gameObject The GameObject to add to the game.
     */
    public void addGameObject(final GameObject gameObject) {

        // Check if we need to add our game object
        if ((this.gameObjects != null) && !this.gameObjects.contains(gameObject) && gameObject != null) {

            // Initialize our game object
            gameObject.onInitialize();
            // Add to game objects
            this.gameObjects.add(gameObject);

        }
    }

    /**
     * RemoveGameObject method This is the method for removing game objects from the game.
     *
     * @param gameObject The GameObject to remove from the game.
     */
    public void removeGameObject(final GameObject gameObject) {

        // Check if we need to remove our game object
        if ((this.gameObjects != null) && this.gameObjects.contains(gameObject)) {

            // Notify the game object we are removing it from the game
            gameObject.onDestroy();
            // Remove from game objects
            this.gameObjects.remove(gameObject);
        }
    }

    /**
     * Gets the 2d {@link Group} of the game.
     *
     * @return The 2d {@link Group} of the game.
     */
    public Group get2DGroup() {
        return this.group2d;
    }

    /**
     * Gets the 3d {@link Group} of the game.
     *
     * @return The 3d {@link Group} of the game.
     */

    public Group get3DGroup() {
        return this.group3d;
    }

    /**
     * Gets the camera {@link Group} of the game.
     *
     * @return The 3d {@link Group} of the game.
     */
    public Group getCameraGroup() {
        return this.cameraGroup;
    }

    /**
     * Gets the {@link MouseInputHandler} of the game.
     *
     * @return The {@link MouseInputHandler} of the game.
     */
    public MouseInputHandler getMouseInputHandler() {
        return this.mouseInputHandler;
    }

    /**
     * Gets the {@link ThirdPersonCamera} of the game.
     *
     * @return The {@link ThirdPersonCamera} of the game.
     */
    public ThirdPersonCamera getThirdPersonCamera() {
        return this.thirdPersonCamera;
    }

    /**
     * Gets the world {@link Group} of the game.
     *
     * @return The world {@link Group} of the game.
     */
    public Group getWorldGroup() {
        return this.worldGroup;
    }

    /**
     * updateGameObjects method This method updates all the game objects in the game.
     *
     * @param timeDivNano Time difference in nanoseconds.
     */
    private void updateGameObjects(final long timeDivNano) {

        // Check if we need to update
        if (this.gameObjects != null) {

            // Get the game object which are not null, and loop through them and call the onUpdate(timeDifNano)
            this.gameObjects.stream().filter(gameObject -> gameObject != null)
                    .forEach(gameObject -> gameObject.onUpdate(timeDivNano));
        }
    }

    /**
     * destroyGameObjects method This method destroys all the game objects in the game
     */
    private void destroyGameObjects() {

        // Check if we need to update
        if (this.gameObjects != null) {

            // Get the game object who aren't null, and loop through them
            this.gameObjects.stream().filter(gameObject -> gameObject != null).forEach(gameObject -> {

                // Destroy the current game object
                gameObject.onDestroy();
                // Remove from array list
                this.removeGameObject(gameObject);
            });
        }
    }

    /**
     * Initialize method. This method is used in the constructor of this class, its used to create a
     * generic "constructor".
     *
     * @param width        The width of the {@link SubScene}.
     * @param height       The height of the {@link SubScene}.
     * @param depthBuffer  The depth buffer enabled state of the {@link SubScene}.
     * @param antiAliasing The anti-aliasing mode, {@link SceneAntialiasing#BALANCED} for anti-aliasing enabled.
     *                     {@link SceneAntialiasing#DISABLED} for anti-aliasing disabled.
     */
    private void initialize(final double width,
                            final double height,
                            final boolean depthBuffer,
                            final SceneAntialiasing antiAliasing) {
        // Creating groups
        this.cameraGroup = new Group();
        this.worldGroup = new Group();
        this.group2d = new Group();
        this.group3d = new Group();

        // Creating new array list for the game objects
        this.gameObjects = new ConcurrentLinkedQueue<>();

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

        // Bind dimensions of the 3d scene
        this.scene3D.heightProperty().bind(this.heightProperty());
        this.scene3D.widthProperty().bind(this.widthProperty());

        // Setting default values
        this.curTime = this.prevTime = 0;
        // Setting the first frame state
        this.isFirstFrame = true;
        // Get 2d group (root of this SubScene);
        this.group2d = (Group) this.getRoot();

        // Add 3d scene to scene
        this.group2d.getChildren().add(this.scene3D);
        // Adding camera group to the root group
        this.group3d.getChildren().add(this.cameraGroup);
        // Adding world group to the root group
        this.group3d.getChildren().add(this.worldGroup);

        // Setting the camera of the 3D SubScene
        this.scene3D.setCamera(this.thirdPersonCamera.getPerspectiveCamera());
    }

    /**
     * Checks if it is the first frame of the game.
     *
     * @return True, if it is the first frame of the game.
     */
    private boolean isFirstFrame() {

        return this.isFirstFrame;
    }

    /**
     * Set first frame state method. This method sets the first frame state.
     *
     * @param isFirstFrame The first frame state.
     */
    private void setIsFirstFrame(boolean isFirstFrame) {

        // Setting first frame
        this.isFirstFrame = isFirstFrame;
    }

    /**
     * AnimationTimerHandler class This class handles the animation timer and updates the game.
     *
     * @author Mark Lefering
     */
    class AnimationTimerHandler extends AnimationTimer {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final long timeDivNano) {

            // Check if it is the first frame of the game
            if (Game.this.isFirstFrame()) {

                // Initialize game
                Game.this.initialize();

                // Not the first state anymore
                Game.this.setIsFirstFrame(false);

                // Break..
                return;
            }

            // Update game
            Game.this.internalUpdate();
        }
    }
}
