package org.ssh.models;

import java.util.ArrayList;
import java.util.List;

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
public class Field extends Model {
    
    /**
     * Field object from protobuf packet
     */
    private GeometryFieldSize field;
                              
    /**
     * Instantiates a field.
     */
    public Field() {
        super("field");
    }
    
    @Override
    public String getSuffix() {
        return "";
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
     * Get a specific {@link FieldLineSegments line segment}.
     * 
     * @param index
     *            index of the {@link FieldLineSegments line segment}
     * @return a specific {@link FieldLineSegments line segment}.
     */
    public FieldLineSegment getFieldLine(final int index) {
        return this.field.getFieldLines(index);
    }
    
    /**
     * @return all {@link FieldLineSegments line segments} on the field.
     */
    public List<FieldLineSegment> getFieldLines() {
        return this.field.getFieldLinesList();
    }
    
    /**
     * Gets the {@link List} of the goals on the field.
     * @return The {@link List} of the goals on the field.
     */
    public List<Goal> getFieldGoals() {
        return Models.<Goal> getAll("goal");
    }
    
    /**
     * @return Width of the field.
     */
    public int getFieldWidth() {
        return this.field.getFieldWidth();
    }
}