package org.ssh.services.consumers;

import java.util.Optional;

import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Field;
import org.ssh.models.Model;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.pipeline.Field3DDetectionPipeline;
import org.ssh.services.service.Consumer;
import org.ssh.ui.UIController;
import org.ssh.ui.windows.MainWindow;

import protobuf.Geometry.GeometryData;

/**
 * Field3DGeometryConsumer class. This class is responsible for handling {@link GeometryPacket} at
 * the {@link Field3DDetectionPipeline}.
 * 
 * @author marklef2
 *        
 */
public class Field3DGeometryConsumer extends Consumer<GeometryPacket> {
    
    /** The {@link FieldGame} to call the updateGeometry method for the 3d field. */
    private final FieldGame fieldGame;
    
    /**
     * Constructor.
     * 
     * @param fieldGame
     *            The {@link FieldGame}.
     */
    public Field3DGeometryConsumer() {
        
        // Initialize super class
        super("field3dgeometryconsumer");
        
        MainWindow mainWindow = (MainWindow) ((UIController<?>) UI.get("main").get());
        
        // Setting field game
        this.fieldGame = mainWindow.field;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean consume(GeometryPacket pipelinePacket) {
        
        // Read geometry data from the geometry packet
        GeometryData geometryData = pipelinePacket.read();
        
        Optional<Model> optionalModel = Models.get("field");
        
        // Check if we already have a model for the field 
        if (optionalModel.isPresent()) {
            
            // Update field
            optionalModel.get().update("field", geometryData.getField());
        }
        else {
            
            // Create new field model
            Field tmpModel = (Field) Models.create(Field.class);
            
            // Update 'field' attribute with the geometry data of the pipeline packet
            tmpModel.update("field", geometryData.getField());
            
            // Notify the 3d field we have new geometry model classes
            this.fieldGame.updateGeometry();
        }
        
        return true;
    }
    
}
