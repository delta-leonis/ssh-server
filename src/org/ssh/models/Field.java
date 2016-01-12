package org.ssh.models;

import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryFieldSize;

import java.util.List;

/**
 * Describes a fieldSize with {@link Goal goals} and a {@link GeometryFieldSize fieldSize size}.
 *
 * @author Jeroen de Jong
 *         
 */
public class Field extends AbstractModel {
    
    /**
     * Field object from protobuf packet
     */
    private GeometryFieldSize fieldSize;
                              
    /**
     * Instantiates a fieldSize.
     */
    public Field() {
        super("fieldSize", "");
    }

    @Override
    public void initialize(){
        //no default values
    }
    
    /**
     * @return Width of the boundary around the fieldSize.
     */
    public int getBoundaryWidth() {
        return this.fieldSize.getBoundaryWidth();
    }
    
    /**
     * Get a specific {@link FieldCicularArc arc segment}.
     * 
     * @param index
     *            index of the {@link FieldCicularArc arc segment}
     * @return a specific {@link FieldCicularArc arc segment}.
     */
    public FieldCicularArc getFieldArc(final int index) {
        return this.fieldSize.getFieldArcs(index);
    }
    
    /**
     * @return all {@link FieldCicularArc arc segments} on the fieldSize.
     */
    public List<FieldCicularArc> getFieldArcs() {
        return this.fieldSize.getFieldArcsList();
    }
    
    /**
     * @return Length of the fieldSize.
     */
    public int getFieldLength() {
        return this.fieldSize.getFieldLength();
    }
    
    /**
     * Get a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     * 
     * @param index
     *            index of the {@link protobuf.Geometry.FieldLineSegment line segment}
     * @return a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     */
    public FieldLineSegment getFieldLine(final int index) {
        return this.fieldSize.getFieldLines(index);
    }

    /**
     * @return all {@link protobuf.Geometry.FieldLineSegment line segments} on the fieldSize.
     */
    public List<FieldLineSegment> getFieldLines() {
        return this.fieldSize.getFieldLinesList();
    }

    /**
     * @return Width of the fieldSize.
     */
    public int getFieldWidth() {
        return this.fieldSize.getFieldWidth();
    }
    
    /**
     * @return Protobuf object containing latest information about fieldSize/goals.
     */
    public GeometryFieldSize getFieldSize(){
        return fieldSize;
    }
}