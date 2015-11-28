package org.ssh.models;

import org.ssh.util.Alias;

import javafx.geometry.Point2D;

/**
 * Describes an object on the {@link Field}. Such as a {@link Robot}, or a {@link Ball}
 *
 * @author Jeroen de Jong
 *         
 */
public abstract class FieldObject extends Model {
    
    /** certainty of detection by ssl-vision */
    private Float   confidence;
                    
    /**
     * X Position of this object on the {@link Field} in mm, according to the Cartesian system with
     * the origin in the center.
     */
    @Alias ("x")
    protected float xPosition;
    /**
     * Y Position of this object on the {@link Field} in mm, according to the Cartesian system with
     * the origin in the center.
     */
    @Alias ("y")
    protected float yPosition;
                    
    /**
     * Create a fieldObject
     * 
     * @param name
     *            name of the object
     */
    public FieldObject(final String name) {
        super(name);
    }
    
    /**
     * position of this object according to the Cartesian system with the origin in the center.
     * 
     * @return position of this object in mm.
     */
    public Point2D getPosition() {
        return new Point2D(xPosition, yPosition);
    }
    
    /**
     * @return certainty of detection by ssl-vision
     */
    public Float getConfidence() {
        return this.confidence;
    }
}