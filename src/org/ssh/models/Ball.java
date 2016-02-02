package org.ssh.models;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import protobuf.Detection;

import java.util.Map;
import java.util.stream.Collectors;


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
        super.initialize();
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
    protected Sphere createNode(){
        Sphere ball = new Sphere(Ball.RADIUS);
        ball.setMaterial(new PhongMaterial(Color.ORANGE));
        ball.translateYProperty().bind(zPositionProperty().add(Ball.RADIUS));
        return ball;
    }

    /**
     * Updates a {@link Ball} using all the contents of a {@link Detection.DetectionBall}
     * @param detectionBall The {@link Detection.DetectionBall} used to update this class
     * @return success value
     */
    public boolean update(Detection.DetectionBall detectionBall){
        return update(detectionBall.getAllFields().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().getName(),
                Map.Entry::getValue
        )));
    }
}