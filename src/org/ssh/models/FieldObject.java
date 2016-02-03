package org.ssh.models;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Box;

/**
 * Describes an object on the {@link Field}. Such as a {@link Robot}, or a {@link Ball}
 *
 * @author Jeroen de Jong
 */
public class FieldObject extends AbstractModel {

    /**
     * certainty of detection by ssl-vision
     */
    private Float confidence;

    protected transient Node model3D;

    /**
     * X Position of this object on the {@link Field} in mm, according to the Cartesian system with
     * the origin in the center.
     */
    @Alias("x")
    protected FloatProperty xPosition;
    /**
     * Y Position of this object on the {@link Field} in mm, according to the Cartesian system with
     * the origin in the center.
     */
    @Alias("y")
    protected FloatProperty yPosition;

    /**
     * Create a fieldObject
     *
     * @param name name of the object
     */
    public FieldObject(final String name, final String identifier) {
        super(name, identifier);
    }

    @Override
    public void initialize() {
        xPosition = new SimpleFloatProperty(0f);
        yPosition = new SimpleFloatProperty(0f);
    }

    /**
     * @return certainty of detection by ssl-vision
     */
    public Float getConfidence() {
        return this.confidence;
    }

    /**
     * @return the xPosition as a property, so when it is modified, the changes are fed through to its binded properties
     */
    public ReadOnlyFloatProperty xPositionProperty() {
        return xPosition;
    }

    /**
     * @return the yPosition as a property, so when it is modified, the changes are fed through to its binded properties
     */
    public ReadOnlyFloatProperty yPositionProperty() {
        return yPosition;
    }

    /**
     * @return the xPosition as a float
     */
    public Float getXPosition() {
        return xPosition.get();
    }

    /**
     * @return the yPosition as a float
     */
    public Float getYPosition() {
        return yPosition.get();
    }

    /**
     * position of this object according to the Cartesian system with the origin in the center.
     *
     * @return position of this object in mm.
     */
    public Point2D getPosition() {
        return new Point2D(xPosition.get(), yPosition.get());
    }

    /**
     * Method to get the {@link Node} for this object. Calls {@link #createNode()} if no model has been set yet.
     * @return {@link Node} representing this object
     */
    public Node getNode(){
        if(model3D == null) {
            // create a new node
            model3D = createNode();
            // bind the x an y
            model3D.translateXProperty().bind(this.xPositionProperty().multiply(-1f));
            model3D.translateZProperty().bind(this.yPositionProperty());
        }
        // return the model
        return model3D;
    }

    /**
     * @return Creates a new node representing this field object
     */
    protected Node createNode(){
        return new Box(50, 50, 50);
    }
}