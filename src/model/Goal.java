package model;

import java.util.logging.Logger;

import javafx.geometry.Point2D;
import model.enums.Direction;

public class Goal extends FieldObject {
	private Direction fieldHalf;
	private Logger logger = Logger.getLogger(Goal.class.toString());
	private int goalDepth, goalWidth;

	public Goal(Direction fieldHalf){
		this.fieldHalf = fieldHalf;
	}
	
	public Direction getSide(){
		return fieldHalf;
	}

	public int getGoalWidth(){
		return goalWidth;
	}

	public int getGoalDepth(){
		return goalDepth;
	}
	
	public boolean update(int goalDepth, int goalWidth) {
		this.goalDepth = goalDepth;
		this.goalWidth = goalWidth;
		logger.info("Updated goal dimensions");
		
		return true;
	}
	
	@Override
	public Point2D getPosition(){
		if(fieldHalf.equals(Direction.EAST))
			return new Point2D(modelDing.getModel("field").getFieldLength()/2, 0);
		else
			return new Point2D(-1* modelDing.getModel("field").getFieldLength()/2, 0);
	}
	
}