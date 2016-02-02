package org.ssh.ui.components.centersection;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.*;
import javafx.scene.paint.Color;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.FieldObject;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.ui.components.centersection.gamescene.GameSceneCamera;
import org.ssh.ui.components.centersection.gamescene.GameSceneCameraController;
import org.ssh.ui.components.centersection.gamescene.shapes.SurfaceChart;

import java.util.function.Consumer;

/**
 * Scene containing the 3d representation of the current field
 *
 * @author Jeroen
 * @date 26-1-2016
 */
public class GameScene extends SubScene {
    /**
     * Group containing everything (lights, camera and gameGroup)
     */
    private Group world;
    /**
     * Group containing all elements for of the game (robots, field, balls etc.)
     */
    private Group gameGroup;

    /**
     * Controller for changing the position of a camera based on mouse movement
     */
    private GameSceneCameraController cameraController;
    /**
     * The Y position of the chart, as modifiable by a slider found in {@link CenterSection}
     */
    private FloatProperty chartTranslateY;

    /**
     * Consumer for removing FieldObject from the gameGroup
     */
    private Consumer<FieldObject> removeObject = fieldObject ->
        Platform.runLater(() ->
            gameGroup.getChildren().remove(fieldObject.getNode()));
    /**
     * Consumer for adding FieldObjects if they're not currently added
     */
    private Consumer<FieldObject> addIfAbsent = fieldObject ->
        Platform.runLater(() -> {
            if (!gameGroup.getChildren().contains(fieldObject.getNode()))
                gameGroup.getChildren().add(fieldObject.getNode());
        });


    public GameScene(double width, double height) {
        //create a new SubScene based on a new group
        super(new Group(), width, height, true, SceneAntialiasing.BALANCED);
        // group for all objects related to the game
        gameGroup = new Group();
        // group containing all objects within this SubScene
        world = (Group)getRoot();

        //setManaged(false);

        // property containing the Y position of the chart
        chartTranslateY = new SimpleFloatProperty(200f);

        //background color
        this.setFill(Color.BLACK);

        // add the game objects to the world
        Platform.runLater(() ->
        world.getChildren().add(gameGroup));

        Models.getAll().stream()
                .filter(model -> model instanceof FieldObject)
                .map(model -> (FieldObject)model)
                .forEach(addIfAbsent);
        Models.addSubscription(ManagerEvent.CREATE, addIfAbsent, FieldObject.class);
        Models.addSubscription(ManagerEvent.DELETE, removeObject, FieldObject.class);

        createSurfaceChart();
        createCamera();
    }

    public FloatProperty chartTranslateY(){
        return chartTranslateY;
    }

    private void createSurfaceChart() {
        SurfaceChart chart = new SurfaceChart();
        chart.translateYProperty().bind(chartTranslateY);
        Platform.runLater(() ->
            gameGroup.getChildren().add(chart));
    }

    private void createCamera(){
        this.setCamera(new GameSceneCamera());
        cameraController = new GameSceneCameraController(this);

        Platform.runLater(() ->
            world.getChildren().add(cameraController));
    }
}