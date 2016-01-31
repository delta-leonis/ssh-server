package org.ssh.ui.components.centersection;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.models.Field;
import org.ssh.models.FieldObject;
import org.ssh.models.Game;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.ui.components.centersection.gamescene.GameSceneCamera;
import org.ssh.ui.components.centersection.gamescene.GameSceneCameraController;
import org.ssh.ui.components.centersection.gamescene.shapes.SurfaceChart;
import sun.java2d.Surface;

import java.util.function.Consumer;

/**
 * @author Jeroen
 * @date 26-1-2016
 */
public class GameScene extends SubScene {
    private Group root;
    private Group world;

    private Camera camera;
    private double mouseOldX, mouseOldY, mousePosX = 0, mousePosY = 0, mouseDeltaX, mouseDeltaY;
    private GameSceneCameraController controller2;
    private double zoomlevel = -1000;
    private double pitch = Math.PI/2;
    private double yaw   = Math.PI/2;
    private double divider = 100;
    private double scale = 100;
    private FloatProperty chartTranslateY;

    private Consumer<FieldObject> removeObject = fieldObject ->
            world.getChildren().remove(fieldObject.getMeshView());
    private Consumer<FieldObject> addIfAbsent = fieldObject -> {
        if (!world.getChildren().contains(fieldObject.getMeshView()))
            Platform.runLater(() ->
            world.getChildren().add(fieldObject.getMeshView()));
    };


    public GameScene(double width, double height) {
        super(new Group(), width, height, true, SceneAntialiasing.BALANCED);
        world = new Group();
        root = (Group)getRoot();
        chartTranslateY = new SimpleFloatProperty(200f);

        this.setFill(Color.BLACK);
        this.setManaged(false);

        root.getChildren().add(world);

        this.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        this.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = mousePosX - mouseOldX;
            mouseDeltaY = mousePosY - mouseOldY;

            if (getPitch() > 0) {
                this.pitch = 0;
            } else if(getPitch() < -Math.PI/2){
                this.pitch = -Math.PI/2;
            }
            else {
                this.pitch = getPitch();
            }

            if (getYaw() >= 2*Math.PI) {
                this.yaw = 0;
            }
            else if (getYaw() < 0) {
                this.yaw = 2*Math.PI;
            }
            else {
                this.yaw = getYaw();
            }

            if (me.isPrimaryButtonDown()) {
                if (me.isControlDown()) {
                    Platform.runLater(() -> {
                        controller2.p.setZ(this.scale * Math.sin(this.pitch));
                        controller2.p.setX(this.scale * Math.cos(this.pitch) * Math.cos(this.yaw));
                        controller2.p.setY(this.scale * Math.cos(this.pitch) * Math.sin(this.yaw));
                    });
                } else {
                    Platform.runLater(() -> controller2.setEuler(pitch, yaw, 0));
                }
            }
        });

        this.setOnScroll(se -> {
            zoomlevel = Math.min(Math.max(zoomlevel + se.getDeltaY()*25, -10000), -1000);

            Platform.runLater(() -> camera.setTranslateZ(zoomlevel));

        });

        Models.getAll().stream()
                .filter(model -> model instanceof FieldObject)
                .map(model -> (FieldObject)model)
                .forEach(addIfAbsent);
        Models.addSubscription(ManagerEvent.CREATE, addIfAbsent, FieldObject.class);
        Models.addSubscription(ManagerEvent.DELETE, removeObject, FieldObject.class);
        Models.addSubscription(ManagerEvent.UPDATE, field -> {
            Group newModel = ((Field)field).createMeshView();
            Node oldModel = ((Field)field).getMeshView();
            if(newModel.equals(oldModel))
                return;

            Platform.runLater(() -> {
                world.getChildren().add(newModel);
                Platform.runLater(() -> {
                    world.getChildren().remove(oldModel);
                });
            });
        }, Field.class);

        createSurfaceChart();
        createCamera();
        camera.setRotate(180);
        camera.setTranslateZ(-7000);
    }

    public FloatProperty chartTranslateY(){
        return chartTranslateY;
    }

    private void createSurfaceChart() {
        SurfaceChart chart = new SurfaceChart();
        chart.translateYProperty().bind(chartTranslateY);

        world.getChildren().add(chart);
    }

    private double getYaw(){
        return this.yaw + mouseDeltaX/this.divider;
    }

    private double getPitch(){
        return this.pitch + mouseDeltaY/this.divider;
    }

    private void createCamera(){
        camera = new GameSceneCamera();
        controller2 = new GameSceneCameraController();

        this.setCamera(camera);

        controller2.getChildren().add(camera);
        root.getChildren().add(controller2);
    }
}