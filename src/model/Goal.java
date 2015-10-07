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
	private Logger logger = Logger.getLogger();

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

	/**
	 * update goal dimensions
	 * 
	 * @param goalWidth width of goal in mm
	 * @param goalDepth depth of goal in mm
	 * 
	 * @return succes value
	 */
	public boolean update(int goalWidth, int goalDepth) {
		this.goalDepth = goalDepth;
		this.goalWidth = goalWidth;
		logger.info("Updated goal dimensions");
		return true;
	}

	/**
	 * update goal position
	 * 
	 * @param newDirection fieldhalf to place goal on
	 * @return succes value
	 */
	public boolean update(Direction newDirection){
		this.fieldHalf = newDirection;
		//change name suffix for model searching
		setSuffix(fieldHalf.toString());

		//change position coordinates of the goal
		update(new Point2D(((Field) Models.get("field")).getFieldLength()/2, 0));

		return true;
	}

}