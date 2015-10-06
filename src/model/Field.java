package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryData;
import protobuf.Geometry.GeometryFieldSize;

public class Field{
	private Logger logger = Logger.getLogger(Field.class.toString());
	private GeometryFieldSize field = null;
	private ArrayList<Goal> goals = new ArrayList<Goal>();
	
	public boolean update(GeometryData newData){
		if(!newData.isInitialized())
			return false;

		field = newData.getField();
		logger.info("Updated to new field dimensions");

		return goals.stream()
				.map(goal -> goal.update(field.getGoalDepth(), field.getGoalWidth()))
				.reduce(true, (accumulator, succes) -> accumulator && succes);
	}

	public List<FieldLineSegment> getFieldLines(){
		return field.getFieldLinesList();
	}

	public FieldLineSegment getFieldLine(int index){
		return field.getFieldLines(index);
	}
	public List<FieldCicularArc> getFieldArcs(){
		return field.getFieldArcsList();
	}

	public FieldCicularArc getFieldArc(int index){
		return field.getFieldArcs(index);
	}

	public int getFieldLength() {
		return field.getFieldLength();
	}

	public int getFieldWidth() {
		return field.getFieldWidth();
	}
	
	public int getBoundaryWidth() {
		return field.getBoundaryWidth();
	}
	

}