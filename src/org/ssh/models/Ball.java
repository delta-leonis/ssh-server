package org.ssh.models;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;


/**
 * Describes a ball {@link FieldObject object}.
 *
 * @author Jeroen de Jong
 */
public class Ball extends FieldObject {

    /**
     * The radius of the ball
     */
    public static final float RADIUS = 43/2;

    /**
     * Height of the ball as provided by ssl-vision
     */
    @Alias("z")
    private FloatProperty zPosition;

    /**
     * Instantiates a ball
     */
    public Ball() {
        super("ball", "");
    }

    @Override
    public void initialize(){
        zPosition = new SimpleFloatProperty(0);
    }

    /**
     * @return Height of the ball as provided by ssl-vision
     */
    public Float getZPosition() {
        return zPosition.getValue();
    }
    
    public FloatProperty zPositionProperty(){
        return zPosition;
    }

    @Override
    public Sphere createNode(){
        Sphere ball = new Sphere(Ball.RADIUS);
        ball.setMaterial(new PhongMaterial(Color.ORANGE));
        ball.translateYProperty().bind(zPositionProperty().add(Ball.RADIUS));
        return ball;
    }


}