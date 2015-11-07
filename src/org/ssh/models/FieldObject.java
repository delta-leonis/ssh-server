package org.ssh.models;

import javafx.geometry.Point2D;

/**
 * Describes an object on the {@link Field}. Such as a {@link Robot}, or a {@link Ball}
 *
 * @author Jeroen de Jong
 *        
 */
public class FieldObject extends Model {
    
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
     * @param suffix
     *            (unique) suffix for the object
     */
    public FieldObject(final String name, final String suffix) {
        super(name, suffix);
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