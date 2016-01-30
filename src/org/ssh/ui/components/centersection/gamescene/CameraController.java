package org.ssh.ui.components.centersection.gamescene;

// CameraController.java is a class that extends the Group class. It is used in the
// MoleculeSampleApp application that is built using the Getting Started with JavaFX
// 3D Graphics tutorial. The method allows you to add your own transforms and rotation.

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.*;
import javafx.util.Callback;
import org.ssh.managers.manager.Models;

/**
 * Xform classe used to allow the user to create thier own transforms and rotation.
 * This class has been provided by Oracle in the "Getting Started with JavaFX 3D Graphics" tutorial.<br /><br />
 *
 * Note: This class has been intergrated into our ecosystem by using a {@link org.ssh.models.AbstractModel}. To be specific a {@link CameraModel}.
 *
 * @see <a href="https://docs.oracle.com/javafx/8/3d_graphics/Xform.java.html">https://docs.oracle.com/javafx/8/3d_graphics/Xform.java.html</a>
 *
 * @author Jeroen de Jong
 */
public abstract class CameraController extends Group {
    protected double speed;
    protected double previousX, previousY;
    private Camera camera;

    public enum RotateOrder {
        XYZ, XZY, YXZ, YZX, ZXY, ZYX
    }

    public Translate t  = new Translate();
    public Translate p  = new Translate();
    public Translate ip = new Translate();
    public Rotate rx = new Rotate();
    { rx.setAxis(Rotate.X_AXIS); }
    public Rotate ry = new Rotate();
    { ry.setAxis(Rotate.Y_AXIS); }
    public Rotate rz = new Rotate();
    { rz.setAxis(Rotate.Z_AXIS); }
    public Scale s = new Scale();

    public CameraController(Camera camera) {
        this(RotateOrder.XYZ, camera);
    }

    public CameraController(RotateOrder rotateOrder, Camera camera) {
        super();
        this.camera = camera;

        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder) {
            case XYZ:
                getTransforms().addAll(t, p, rz, ry, rx, s, ip);
                break;
            case XZY:
                getTransforms().addAll(t, p, ry, rz, rx, s, ip);
                break;
            case YXZ:
                getTransforms().addAll(t, p, rz, rx, ry, s, ip);
                break;
            case YZX:
                getTransforms().addAll(t, p, rx, rz, ry, s, ip);  // For Camera
                break;
            case ZXY:
                getTransforms().addAll(t, p, ry, rx, rz, s, ip);
                break;
            case ZYX:
                getTransforms().addAll(t, p, rx, ry, rz, s, ip);
                break;
        }
    }

    public Camera getCamera(){
        return camera;
    }

    public void setTranslate(double x, double y, double z) {
        t.setX(x);
        t.setY(y);
        t.setZ(z);
    }

    public void setTranslate(double x, double y) {
        t.setX(x);
        t.setY(y);
    }

    // Cannot override these methods as they are final:
    // public void setTranslateX(double x) { t.setX(x); }
    // public void setTranslateY(double y) { t.setY(y); }
    // public void setTranslateZ(double z) { t.setZ(z); }
    // Use these methods instead:
    public void setTx(double x) { t.setX(x); }
    public void setTy(double y) { t.setY(y); }
    public void setTz(double z) { t.setZ(z); }

    public void setRotate(double x, double y, double z) {
        rx.setAngle(x);
        ry.setAngle(y);
        rz.setAngle(z);
    }

    public void setRotateX(double x) { rx.setAngle(x); }
    public void setRotateY(double y) { ry.setAngle(y); }
    public void setRotateZ(double z) { rz.setAngle(z); }
    public void setRx(double x) { rx.setAngle(x); }
    public void setRy(double y) { ry.setAngle(y); }
    public void setRz(double z) { rz.setAngle(z); }

    public void setScale(double scaleFactor) {
        s.setX(scaleFactor);
        s.setY(scaleFactor);
        s.setZ(scaleFactor);
    }

    public void setScale(double x, double y, double z) {
        s.setX(x);
        s.setY(y);
        s.setZ(z);
    }

    // Cannot override these methods as they are final:
    // public void setScaleX(double x) { s.setX(x); }
    // public void setScaleY(double y) { s.setY(y); }
    // public void setScaleZ(double z) { s.setZ(z); }
    // Use these methods instead:
    public void setSx(double x) { s.setX(x); }
    public void setSy(double y) { s.setY(y); }
    public void setSz(double z) { s.setZ(z); }

    public void setPivot(double x, double y, double z) {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
    }

    public void reset() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        rx.setAngle(0.0);
        ry.setAngle(0.0);
        rz.setAngle(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    public void resetTSP() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
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


    //Abstract Methods
    protected abstract void update(); // called each frame handle movement/ button clicks here

    protected abstract void updateTransition(double now);

    // Following methods should update values for use in update method etc...

    protected abstract void handleKeyEvent(KeyEvent event, boolean handle);

    protected abstract void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handlePrimaryMouseClick(MouseEvent e);

    protected abstract void handleSecondaryMouseClick(MouseEvent e);

    protected abstract void handleMiddleMouseClick(MouseEvent e);

    protected abstract void handlePrimaryMousePress(MouseEvent e);

    protected abstract void handleSecondaryMousePress(MouseEvent e);

    protected abstract void handleMiddleMousePress(MouseEvent e);

    protected abstract void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier);

    protected abstract void handleScrollEvent(ScrollEvent event);

    protected abstract double getSpeedModifier(KeyEvent event);

    //Self contained Methods
    private void handleKeyEvent(KeyEvent t) {
        if (t.getEventType() == KeyEvent.KEY_PRESSED ||
                t.getEventType() == KeyEvent.KEY_RELEASED) {
            handleKeyEvent(t, true);
        }
        speed = getSpeedModifier(t);
    }

    private void handleMouseEvent(MouseEvent t) {
        if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMousePress(t);
                    break;
                case MIDDLE:
                    handleMiddleMousePress(t);
                    break;
                case SECONDARY:
                    handleSecondaryMousePress(t);
                    break;
                default:
                    throw new AssertionError();
            }
            handleMousePress(t);
        } else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            Point2D d = getMouseDelta(t);

            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseDrag(t, d, speed);
                    break;
                case MIDDLE:
                    handleMiddleMouseDrag(t, d, speed);
                    break;
                case SECONDARY:
                    handleSecondaryMouseDrag(t, d, speed);
                    break;
                default:
                    throw new AssertionError();
            }
        } else if (t.getEventType() == MouseEvent.MOUSE_MOVED) {
            handleMouseMoved(t, getMouseDelta(t), speed);
        } else if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            switch (t.getButton()) {
                case PRIMARY:
                    handlePrimaryMouseClick(t);
                    break;
                case MIDDLE:
                    handleMiddleMouseClick(t);
                    break;
                case SECONDARY:
                    handleSecondaryMouseClick(t);
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }

    public void setScene(Scene scene) {
        setEventHandlers(scene);
    }

    public void setScene(SubScene subScene) {
        setEventHandlers(subScene);
    }

    private void setEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void setEventHandlers(SubScene scene) {
        scene.addEventHandler(KeyEvent.ANY, k -> handleKeyEvent(k));
        scene.addEventHandler(MouseEvent.ANY, m -> handleMouseEvent(m));
        scene.addEventHandler(ScrollEvent.ANY, s -> handleScrollEvent(s));
    }

    private void handleMousePress(MouseEvent event) {
        previousX = event.getSceneX();
        previousY = event.getSceneY();
        event.consume();
    }

    private Point2D getMouseDelta(MouseEvent event) {
        Point2D res = new Point2D(event.getSceneX() - previousX, event.getSceneY() - previousY);
        previousX = event.getSceneX();
        previousY = event.getSceneY();

        return res;
    }


    //advanced transform
    public Affine affine = new Affine();

    //position
    Callback<Transform, Point3D> positionCallback = (a) ->{
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };
    public Point3D getPosition(){
        return positionCallback.call(getCamera().getLocalToSceneTransform());
    }
}