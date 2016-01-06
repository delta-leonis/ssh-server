package org.ssh.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ssh.managers.manager.Models;

import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryFieldSize;

/**
 * Describes a field with {@link Goal goals} and a {@link GeometryFieldSize field size}.
 *
 * @author Jeroen de Jong
 *         
 */
public class Field extends AbstractModel {
    
    /**
     * Field object from protobuf packet
     */
    private GeometryFieldSize field;
                              
    /**
     * Instantiates a field.
     */
    public Field() {
        super("field", "");
    }

    @Override
    public void initialize(){
        //no default values
    }
    
    /**
     * @return Width of the boundary around the field.
     */
    public int getBoundaryWidth() {
        return this.field.getBoundaryWidth();
    }
    
    /**
     * Get a specific {@link FieldCicularArc arc segment}.
     * 
     * @param index
     *            index of the {@link FieldCicularArc arc segment}
     * @return a specific {@link FieldCicularArc arc segment}.
     */
    public FieldCicularArc getFieldArc(final int index) {
        return this.field.getFieldArcs(index);
    }
    
    /**
     * @return all {@link FieldCicularArc arc segments} on the field.
     */
    public List<FieldCicularArc> getFieldArcs() {
        return this.field.getFieldArcsList();
    }
    
    /**
     * @return Length of the field.
     */
    public int getFieldLength() {
        return this.field.getFieldLength();
    }
    
    /**
     * Get a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     * 
     * @param index
     *            index of the {@link protobuf.Geometry.FieldLineSegment line segment}
     * @return a specific {@link protobuf.Geometry.FieldLineSegment line segment}.
     */
    public FieldLineSegment getFieldLine(final int index) {
        return this.field.getFieldLines(index);
    }

    /**
     * @return all {@link protobuf.Geometry.FieldLineSegment line segments} on the field.
     */
    public List<FieldLineSegment> getFieldLines() {
        return this.field.getFieldLinesList();
    }

    /**
     * @return Width of the field.
     */
    public int getFieldWidth() {
        return this.field.getFieldWidth();
    }
    
    /**
     * @return Protobuf object containing latest information about field/goals.
     */
    public GeometryFieldSize getField(){
        return field;
    }
}