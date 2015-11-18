package org.ssh.models;

import javafx.geometry.Point2D;

/**
 * Describes an object on the {@link Field}. Such as a {@link Robot}, or a {@link Ball}
 *
 * @author Jeroen de Jong
 *         
 */
public abstract class FieldObject extends Model {
    
    /**
     * Position of this object on the {@link Field} in mm, according to the Cartesian system with
     * the origin in the center.
     */
    protected Point2D position;
    
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
        return this.position;
    }
}