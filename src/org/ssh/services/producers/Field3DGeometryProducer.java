package org.ssh.services.producers;

import java.util.ArrayList;
import java.util.List;

import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.Goal;
import org.ssh.models.enums.Direction;
import org.ssh.models.enums.ProducerType;

import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.services.service.Producer;

import com.sun.javafx.geom.Point2D;

import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.GeometryData;
import protobuf.Geometry.GeometryFieldSize;
import protobuf.Geometry.Vector2f;

/**
 * 3D field geometry producer. This class is responsible for creating test geometry data
 * 
 * @author marklef2
 *
 */
public class Field3DGeometryProducer extends Producer<GeometryPacket> {
    
    /**
     * Constructor.
     */
    public Field3DGeometryProducer() {
        
        // Initialize super class
        super("geometryproducer", ProducerType.SINGLE);
        
        // Setting callable
        this.setCallable(() -> {
            
            // Creating geometry field size
            GeometryFieldSize geometryFieldSize = createGeometryFieldSize();
            // Creating geometry data
            GeometryData geometryData = GeometryData.newBuilder().setField(geometryFieldSize).build();
            // Creating geometry packet
            GeometryPacket packet = new GeometryPacket(geometryData);
            
            System.out.println("geometryProducer");
            
            // Return the packets
            return packet;
        });
    }
    
    /**
     * This method is used to create the test data. 
     */
    @SuppressWarnings ("unused")
    private void createTestData() {
        
        // Creating field model
        Field fieldVisionModel = (Field) Models.create(Field.class);
        
        List<Goal> goals = new ArrayList<Goal>();
        Goal goalEast = (Goal) Models.create(Goal.class, Direction.EAST);
        Goal goalWest = (Goal) Models.create(Goal.class, Direction.WEST);
        
        goalEast.update("goalWidth", new Integer(1000));
        goalEast.update("goalHeight", new Integer(160));
        goalEast.update("goalDepth", new Integer(180));
        goalEast.update("position", new Point2D(4500.0f + 90.0f, 0.0f));
        
        goalWest.update("goalWidth", new Integer(1000));
        goalWest.update("goalHeight", new Integer(160));
        goalWest.update("goalDepth", new Integer(180));
        goalWest.update("position", new Point2D(-4500.0f - 90.0f, 0.0f));
        
        goals.add(goalWest);
        goals.add(goalEast);
        
        // Update field, create field geometry field size
        fieldVisionModel.update("field", createGeometryFieldSize());
        fieldVisionModel.update("goals", goals);
        
        fieldVisionModel.save();
        fieldVisionModel.saveAsDefault();
    }
    
    /**
     * Creates geometry field size method. This method creates a sample {@link GeometryFieldSize}.
     * 
     * @return The created {@link GeometryFieldSize}.
     */
    private GeometryFieldSize createGeometryFieldSize() {
        
        // Middle line
        Vector2f midLineStart = Vector2f.newBuilder().setX(0.0f).setY(3000.0f).build();
        Vector2f midLineEnd = Vector2f.newBuilder().setX(0.0f).setY(-3000.0f).build();
        FieldLineSegment midLine = FieldLineSegment.newBuilder().setP1(midLineStart).setP2(midLineEnd)
                .setThickness(10.0f).build();
                
        // North line
        Vector2f northLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(3000.0f).build();
        Vector2f northLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(3000.0f).build();
        FieldLineSegment northLine = FieldLineSegment.newBuilder().setP1(northLineStart).setP2(northLineEnd)
                .setThickness(10.0f).build();
                
        // South line
        Vector2f southLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(-3000.0f).build();
        Vector2f southLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(-3000.0f).build();
        FieldLineSegment southLine = FieldLineSegment.newBuilder().setP1(southLineStart).setP2(southLineEnd)
                .setThickness(10.0f).build();
                
        // East line
        Vector2f eastLineStart = Vector2f.newBuilder().setX(4500.0f).setY(-3000.0f).build();
        Vector2f eastLineEnd = Vector2f.newBuilder().setX(4500.0f).setY(3000.0f).build();
        FieldLineSegment eastLine = FieldLineSegment.newBuilder().setP1(eastLineStart).setP2(eastLineEnd)
                .setThickness(10.0f).build();
                
        // East defense line
        Vector2f eastDefenseLineStart = Vector2f.newBuilder().setX(3500.0f).setY(250.0f).build();
        Vector2f eastDefenseLineEnd = Vector2f.newBuilder().setX(3500.0f).setY(-250.0f).build();
        FieldLineSegment eastDefenseLine = FieldLineSegment.newBuilder().setP1(eastDefenseLineStart)
                .setP2(eastDefenseLineEnd).setThickness(10.0f).build();
                
        // West line
        Vector2f westLineStart = Vector2f.newBuilder().setX(-4500.0f).setY(-3000.0f).build();
        Vector2f westLineEnd = Vector2f.newBuilder().setX(-4500.0f).setY(3000.0f).build();
        FieldLineSegment westLine = FieldLineSegment.newBuilder().setP1(westLineStart).setP2(westLineEnd)
                .setThickness(10.0f).build();
                
        // West defense line
        Vector2f westDefenseLineStart = Vector2f.newBuilder().setX(-3500.0f).setY(250.0f).build();
        Vector2f westDefenseLineEnd = Vector2f.newBuilder().setX(-3500.0f).setY(-250.0f).build();
        FieldLineSegment westDefenseLine = FieldLineSegment.newBuilder().setP1(westDefenseLineStart)
                .setP2(westDefenseLineEnd).setThickness(10.0f).build();
                
        // Mid circle
        FieldCicularArc midCircle = FieldCicularArc.newBuilder().setA1(0.0f).setA2(360.0f).setThickness(10.0f)
                .setRadius(500.0f).build();
                
        // East defense arc left
        Vector2f eastDefenseArcLeftCenter = Vector2f.newBuilder().setX(-4500.0f).setY(250.0f).build();
        FieldCicularArc eastDefenseArcLeft = FieldCicularArc.newBuilder().setA1(0.0f).setA2(90.0f).setThickness(10.0f)
                .setRadius(1000.0f).setCenter(eastDefenseArcLeftCenter).build();
                
        // East defense arc right
        Vector2f eastDefenseArcRightCenter = Vector2f.newBuilder().setX(-4500.0f).setY(-250.0f).build();
        FieldCicularArc eastDefenseArcRight = FieldCicularArc.newBuilder().setA1(270.0f).setA2(360.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(eastDefenseArcRightCenter).build();
                
        // West defense arc left
        Vector2f westDefenseArcLeftCenter = Vector2f.newBuilder().setX(4500.0f).setY(-250.0f).build();
        FieldCicularArc westDefenseArcLeft = FieldCicularArc.newBuilder().setA1(180.0f).setA2(270.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(westDefenseArcLeftCenter).build();
                
        // West defense arc right
        Vector2f westDefenseArcRightCenter = Vector2f.newBuilder().setX(4500.0f).setY(250.0f).build();
        FieldCicularArc westDefenseArcRight = FieldCicularArc.newBuilder().setA1(90.0f).setA2(180.0f)
                .setThickness(10.0f).setRadius(1000.0f).setCenter(westDefenseArcRightCenter).build();
                
        GeometryFieldSize fieldSize = GeometryData.newBuilder().getFieldBuilder().setFieldWidth(6000)
                .setFieldLength(9000).addFieldLines(midLine).addFieldLines(northLine).addFieldLines(southLine)
                .addFieldLines(eastLine).addFieldLines(eastDefenseLine).addFieldLines(westLine)
                .addFieldLines(westDefenseLine).addFieldArcs(midCircle).addFieldArcs(eastDefenseArcLeft)
                .addFieldArcs(eastDefenseArcRight).addFieldArcs(westDefenseArcLeft).addFieldArcs(westDefenseArcRight)
                .build();
                
        return fieldSize;
    }
}
