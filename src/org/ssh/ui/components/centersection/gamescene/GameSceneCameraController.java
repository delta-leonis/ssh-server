package org.ssh.ui.components.centersection.gamescene;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * @author Jeroen
 * @date 30-1-2016
 */
public class GameSceneCameraController extends Group {
    private Translate t  = new Translate();
    public Translate p  = new Translate();
    private Translate ip = new Translate();
    private Scale s = new Scale();
    private Rotate rx = new Rotate();
    private Rotate ry = new Rotate();
    private Rotate rz = new Rotate();
     { rz.setAxis(Rotate.Z_AXIS);
       rx.setAxis(Rotate.X_AXIS);
       ry.setAxis(Rotate.Y_AXIS); }

    public GameSceneCameraController() {
        super();
        getTransforms().addAll(t, p, rx, rz, ry, s, ip);
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

    public void setEuler(double pitch, double yaw, int roll) {
        ry.setAngle(-Math.toDegrees(yaw));
        rz.setAngle(-Math.toDegrees(-pitch * Math.sin(yaw + Math.PI)));
        rx.setAngle(-Math.toDegrees(-pitch * Math.cos(-yaw + Math.PI)));
    }
}
