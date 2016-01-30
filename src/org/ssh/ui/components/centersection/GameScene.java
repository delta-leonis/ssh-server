package org.ssh.ui.components.centersection;

import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.ssh.managers.manager.Models;
import org.ssh.models.FieldObject;
import org.ssh.models.Game;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.ui.components.centersection.gamescene.GameSceneCamera;

import java.util.function.Consumer;

/**
 * @author Jeroen
 * @date 26-1-2016
 */
public class GameScene extends SubScene {
    private Group root;
    private Group world;
    private Game gameModel;

    private Camera camera;
    private double mouseOldX, mouseOldY, mousePosX = 0, mousePosY = 0, mouseDeltaX, mouseDeltaY;
    private Cam controller2;
    private double zoomlevel = -1000;
    private double pitch = Math.PI/2;
    private double yaw   = Math.PI/2;
    private double divider = 100;
    private double scale = 100;
    private Consumer<FieldObject> addIfAbsent = (fieldobject) -> {
        if (!world.getChildren().contains(fieldobject.getMeshView()))
            Platform.runLater(() ->
            world.getChildren().add(fieldobject.getMeshView()));
    };


    public GameScene(double width, double height) {
        super(new Group(), width, height, true, SceneAntialiasing.BALANCED);
        world = new Group();
        root = (Group)getRoot();

        this.setFill(Color.BLACK);
        this.setManaged(false);

        Models.<Game>get("game").ifPresent(game -> gameModel = game);
//        if(gameModel == null)
//            Models.addSubscription(ManagerEvent.CREATE, (model) -> {
//                this.gameModel = (Game)model;
//                Models.removeSubscription(ManagerEvent.CREATE, this, Game.class);
//            }, Game.class);

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
                    Platform.runLater(() -> {
                        controller2.ry.setAngle(-Math.toDegrees(yaw));
                        controller2.rz.setAngle(-Math.toDegrees(-pitch * Math.sin(yaw + Math.PI)));
                        controller2.rx.setAngle(-Math.toDegrees(-pitch * Math.cos(-yaw + Math.PI)));
                    });
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

        createLights();
        createCamera();
        camera.setRotate(180);
        camera.setTranslateZ(-7000);
    }

    private void createLights(){

    }

    private double getYaw(){
        return this.yaw + mouseDeltaX/this.divider;
    }

    private double getPitch(){
        return this.pitch + mouseDeltaY/this.divider;
    }


    private void createCamera(){
        camera = new GameSceneCamera();
        controller2 = new Cam();

        this.setCamera(camera);

        controller2.getChildren().add(camera);
        root.getChildren().add(controller2);
    }

    class Cam extends Group {
        Translate t  = new Translate();
        Translate p  = new Translate();
        Translate ip = new Translate();
        Rotate rx = new Rotate();
        { rx.setAxis(Rotate.X_AXIS); }
        Rotate ry = new Rotate();
        { ry.setAxis(Rotate.Y_AXIS); }
        Rotate rz = new Rotate();
        { rz.setAxis(Rotate.Z_AXIS); }
        Scale s = new Scale();
        public Cam() { super(); getTransforms().addAll(t, p, rx, rz, ry, s, ip); }


        public void debug() {
            System.out.println("t = (" +
                    t.getX() + ", " +
                    t.getY() + ", " +
                    t.getZ() + ")  " +
                    "r = (" +
                    rx.getAngle() + ", " +
                    ry.getAngle() + ", " +
                    rz.getAngle() + ")  " +
                    "s = (" +
                    s.getX() + ", " +
                    s.getY() + ", " +
                    s.getZ() + ")  " +
                    "p = (" +
                    p.getX() + ", " +
                    p.getY() + ", " +
                    p.getZ() + ")  " +
                    "ip = (" +
                    ip.getX() + ", " +
                    ip.getY() + ", " +
                    ip.getZ() + ")");
        }
    }
}
