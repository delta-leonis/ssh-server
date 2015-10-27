package model;

import util.Logger;
import javafx.geometry.Point2D;
import model.enums.Direction;
import application.*;

/**
 * Describes a goal on the {@link Field}
 * 
 * @author Jeroen
 */
public class Goal extends FieldObject {
	/**
	 * respective logger unit
	 */
	private transient Logger logger = Logger.getLogger();

	/**
	 * Location of this goal
	 */
	private Direction fieldHalf;
	
	/**
	 * Dimensions of the goal
	 */
	private int goalDepth, goalWidth;

	/**
	 * Creates a goal on a specified fieldhalf
	 * 
	 * @param fieldHalf location of the goal
	 */
	public Goal(Direction fieldHalf){
		super("goal", fieldHalf.toString());
		this.fieldHalf = fieldHalf;
	}
	
	/**
	 * @return side that the goal is on
	 */
	public Direction getSide(){
		return fieldHalf;
	}

	/**
	 * @return width of the goal in mm
	 */
	public int getGoalWidth(){
		return goalWidth;
	}

	/**
	 * @return depth of the goal in mm
	 */
	public int getGoalDepth(){
		return goalDepth;
	}
}