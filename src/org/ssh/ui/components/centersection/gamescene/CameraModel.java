package org.ssh.ui.components.centersection.gamescene;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.ssh.models.AbstractModel;

/**
 * @author Jeroen
 * @date 26-1-2016
 */
public class CameraModel extends AbstractModel {

    public Translate t;
    public Translate p;
    public Translate ip;

    public Rotate rx;
    public Rotate ry;
    public Rotate rz;
    public Scale s;

    /**
     * Instantiates a new cameraModel
     */
    public CameraModel() {
        super("cameramodel", "");
    }

    @Override
    public void initialize() {
        t  = new Translate();
        p  = new Translate();
        ip  = new Translate();
        rx = new Rotate();
        rx.setAxis(Rotate.X_AXIS);
        ry = new Rotate();
        ry.setAxis(Rotate.Y_AXIS);
        rz = new Rotate();
        rz.setAxis(Rotate.Z_AXIS);
        s = new Scale();
    }


    private Transform getRotateAxis(char axis){
        switch(Character.toUpperCase(axis)) {
            case 'X':
                return rx;
            case 'Y':
                return ry;
            case 'Z':
                return rz;
            default:
                System.out.println("Could not find character " + axis);
                return null;
        }
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

    public Transform[] getTransformSorted(CameraController.RotateOrder rotateOrder){
        Transform[] array = new Transform[7];
        array[0] = t;
        array[1] = p;
        for(int i = 2; i >= 0; i --)
            array[2+ 2-i] = getRotateAxis(rotateOrder.name().charAt(i));

        array[5] = s;
        array[6] = ip;

        return array;
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

    public void setTranslate(double x, double y, double z) {
        t.setX(x);
        t.setY(y);
        t.setZ(z);
    }

    public void setTranslate(double x, double y) {
        t.setX(x);
        t.setY(y);
    }

    public void setTx(double tx) {
        this.t.setX(tx);
    }
    public void setTy(double ty) {
        this.t.setY(ty);
    }
    public void setTz(double tz) {
        this.t.setZ(tz);
    }

    public void setAngleX(double x) {
        rx.setAngle(x);
    }
    public void setAngleY(double y) {
        ry.setAngle(y);
    }
    public void setAngleZ(double z) {
        rz.setAngle(z);
    }

    public Translate getTranslate(){
        return t;
    }

    public Scale getScale() {
        return s;
    }

    public Translate getPivot() {
        return p;
    }
    public Translate getIPivot() {
        return ip;
    }

    public Rotate getRotationX(){
        return rx;
    }
    public Rotate getRotationY(){
        return ry;
    }
    public Rotate getRotationZ(){
        return rz;
    }
}
