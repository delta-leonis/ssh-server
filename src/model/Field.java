package model;

import java.util.ArrayList;
import java.util.List;
import util.Logger;

import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryData;
import protobuf.Geometry.GeometryFieldSize;

/**
 * Describes a field with {@link Goal goals} and a {@link GeometryFieldSize field size} 
 * 
 * @author Jeroen
 *
 */
public class Field extends Model{
	// respective logger
	private Logger logger = Logger.getLogger();
	
	/**
	 * Field object from protobuf packet
	 */
	private GeometryFieldSize field = null;
	/**
	 * All the goals on this field
	 */
	private ArrayList<Goal> goals = new ArrayList<Goal>();
	
	/**
	 * instansiates a field
	 */
	public Field(){
		super("field");
	}

	/**
	 * @return all {@link FieldLineSegments line segments} on the field
	 */
	public List<FieldLineSegment> getFieldLines(){
		return field.getFieldLinesList();
	}

	/**
	 * Get a specific {@link FieldLineSegments line segment}.
	 * @param index	index of the {@link FieldLineSegments line segment}
	 * @return a specific {@link FieldLineSegments line segment}.
	 */
	public FieldLineSegment getFieldLine(int index){
		return field.getFieldLines(index);
	}

	/**
	 * @return all {@link FieldCicularArc arc segments} on the field
	 */
	public List<FieldCicularArc> getFieldArcs(){
		return field.getFieldArcsList();
	}

	/**
	 * Get a specific {@link FieldCicularArc arc segment}.
	 * @param index	index of the {@link FieldCicularArc arc segment}
	 * @return a specific {@link FieldCicularArc arc segment}.
	 */
	public FieldCicularArc getFieldArc(int index){
		return field.getFieldArcs(index);
	}

	/**
	 * @return Length of the field
	 */
	public int getFieldLength() {
		return field.getFieldLength();
	}

	/**
	 * @return Width of the field
	 */
	public int getFieldWidth() {
		return field.getFieldWidth();
	}

	/**
	 * @return Width of the boundary around the field
	 */
	public int getBoundaryWidth() {
		return field.getBoundaryWidth();
	}
}