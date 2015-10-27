package model;

import javafx.geometry.Point2D;
import util.Logger;

/**
 * Describes a object on the {@link Field}  
 * 
 * @author Jeroen
 *
 */
public class FieldObject extends Model{
	// respective logger
	private transient Logger logger = Logger.getLogger();
	/**
	 * Position of this object on the {@link Field} in mm,
	 * according to the Cartesian system with the origin in the center  
	 */
	private Point2D position;

	/**
	 * Create a fieldObject
	 * @param name		name of the object
	 * @param suffix	(unique) suffix for the object
	 */
	public FieldObject(String name, String suffix) {
		super(name, suffix);
	}

	/**
	 * position of this object
	 * according to the Cartesian system with the origin in the center  
	 * 
	 * @return position of this object in mm
	 */
	public Point2D getPosition(){
		return position;
	}
	
	/**
	 * update this object with a new position in mm
	 * according to the Cartesian system with the origin in the center  
	 * 
	 * @param newPosition	
	 * @return succes value
	 */
	public boolean update(Point2D newPosition)
	{
		position = newPosition;
		logger.fine("new position set (%d, %d)", position.getX(), position.getY());
		return true;
	}
	
}