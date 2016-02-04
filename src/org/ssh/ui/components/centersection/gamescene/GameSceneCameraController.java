package org.ssh.ui.components.centersection.gamescene;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.ssh.ui.components.centersection.GameScene;
import org.ssh.ui.components.centersection.gamescene.shapes.SurfaceChart;

/**
 * @author Jeroen
 * @date 30-1-2016
 */
public class GameSceneCameraController extends Group {

    /**
     * max possible zoom for the camera
     */
    private static final float MAX_ZOOM = -10000;
    /**
     * minimum possible zoom for the camera
     */
    private static final float MIN_ZOOM = -1000;

    /**
     * Minimum possible height of the {@link SurfaceChart}
     */
    private static final float MIN_CHART_HEIGHT = 100;

    /**
     * Maximum possible height of the {@link SurfaceChart}
     */
    private static final float MAX_CHART_HEIGHT = 1500;


    private double mouseOldX, mouseOldY,    // previous x and y
            mousePosX, mousePosY,   // new x and y position
            mouseDeltaX, mouseDeltaY;       // delta between x and y

    /**
     * camera object to control
     */
    private Camera camera;

    /**
     * Current zoom level
     */
    private FloatProperty zoomLevel;

    /**
     * Height of the {@link SurfaceChart}
     */
    private FloatProperty chartHeight;

    /**
     * Current pitch
     */
    private double pitch = Math.PI/2;
    /**
     * Current yaw
     */
    private double yaw   = Math.PI/2;
    /**
     * Division
     */
    private double scale = 100;

    /**
     * point of translation
     */
    private Translate t  = new Translate();
    /**
     * pivot point
     */
    public Translate p  = new Translate();
    /**
     * Inverted pivot point
     */
    private Translate ip = new Translate();
    /**
     * camera scale
     */
    private Scale s = new Scale();
    /**
     * Rotate over X axis
     */
    private Rotate rx = new Rotate();
    /**
     * Rotate over Y axis
     */
    private Rotate ry = new Rotate();
    /**
     * Rotate over Z axis
     */
    private Rotate rz = new Rotate();
     { rz.setAxis(Rotate.Z_AXIS);
       rx.setAxis(Rotate.X_AXIS);
       ry.setAxis(Rotate.Y_AXIS); }

    /**
     *
     * Creates a controller that uses a
     *
     * @param scene Scene to bind mouse events to and get camera from
     */
    public GameSceneCameraController(GameScene scene) {
        super();
        getTransforms().addAll(t, p, rx, rz, ry, s, ip);

        camera = scene.getCamera();
        this.getChildren().add(camera);

        zoomLevel = new SimpleFloatProperty(MAX_ZOOM*0.7f);
        // bind zoomLevel to y property of the camera
        camera.translateZProperty().bind(zoomLevel);
        camera.setRotate(180f);

        chartHeight = new SimpleFloatProperty(400f);
        scene.chartTranslateY().bind(chartHeight);
        bindEvents(scene);
    }

    /**
     * Binds all mouse and scroll events to the given SubScene
     *
     * @param scene SubScene to bind the eventhandlers to
     */
    private void bindEvents(SubScene scene){

        // scrolling should scale the camera
        scene.setOnScroll(se -> {
            if(se.isControlDown())
                chartHeight.setValue(clamp(chartHeight.getValue() + se.getDeltaY(), MAX_CHART_HEIGHT, MIN_CHART_HEIGHT));
            else
                zoomLevel.setValue(clamp(zoomLevel.getValue() + se.getDeltaY()*25, MIN_ZOOM, MAX_ZOOM));
        });

        // whenever the scene is clicked, the current mouse position should be set
        // this way the old mouse positions get set accordingly
        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });

        // on a drag event
        scene.setOnMouseDragged(me -> {
            //only allow dragging with left mouse button
            if (!me.isPrimaryButtonDown())
                return;

            // set previous mouse position
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            // read new mouse position
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            //calculate delta distance
            mouseDeltaX = mousePosX - mouseOldX;
            mouseDeltaY = mousePosY - mouseOldY;
            //if control is down,
            //the pivot point should be changed
            if (me.isControlDown()) {
                p.setX(p.getX() - mouseDeltaX);
                p.setZ(p.getZ() - mouseDeltaY);
            } else {

                //limit the pitch between -90 and 0 deg
                this.pitch = clamp(getPitch(), 0, -Math.PI/2);

                // yaw should reset past 360 or 0 deg
                if (getYaw() >= 2*Math.PI) {
                    this.yaw = 0;
                }
                else if (getYaw() < 0) {
                    this.yaw = 2*Math.PI;
                }
                else {
                    this.yaw = getYaw();
                }
                // otherwise just change the camera position
                setEuler(pitch, yaw, 0);
            }

        });
    }

    public void setZoom(double zoomValue){
        zoomLevel.setValue(zoomValue);
    }

    private double getYaw(){
        return this.yaw + mouseDeltaX/this.scale;
    }

    private double getPitch(){
        return this.pitch + mouseDeltaY/this.scale;
    }

    private double clamp(double value, double min, double max){
        return Math.min(Math.max(value, max), min);
    }

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

    public void setPivot(double x, double y, double z){
        Platform.runLater(() -> {
            p.setX(x);
            p.setY(y);
            p.setZ(z);
        });
    }

    public void setEuler(double pitch, double yaw, int roll) {
        Platform.runLater(() -> {
            ry.setAngle(-Math.toDegrees(yaw));
            rz.setAngle(-Math.toDegrees(-pitch * Math.sin(yaw + Math.PI)));
            rx.setAngle(-Math.toDegrees(-pitch * Math.cos(-yaw + Math.PI)));
        });
    }

    public void setRotate(double rx , double ry , double rz){
        this.ry.setAngle(ry);
        this.rx.setAngle(rx);
        this.rz.setAngle(rz);
    }

    public Rotate getRotateY(){
        return ry;
    }
    public Rotate getRotateX(){
        return rx;
    }
    public Rotate getRotateZ(){
        return rz;
    }

    public void setRotateY(double value){
        ry.setAngle(value);
    }
    public void setRotateX(double value){
        rx.setAngle(value);
    }
    public void setRotateZ(double value){
        rz.setAngle(value);
    }

    public double getZoom() {
        return zoomLevel.get();
    }
}
