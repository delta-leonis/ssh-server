package org.ssh.services.consumers;

import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.services.service.Consumer;

/**
 * Class that splits a {@link WrapperPacket} to a {@link DetectionPacket} and a
 * {@link GeometryPacket}, and puts them in their respective pipelines.
 * 
 * @author Jeroen de Jong
 *         
 */
public class WrapperConsumer extends Consumer<WrapperPacket> {
    
    /**
     * Start the WrapperConsumer and attach to all compatible pipelines
     */
    public WrapperConsumer() {
        super("Wrapper consumer");
        
        attachToCompatiblePipelines();
    }
    
    /**
     * Splits the {@link WrapperPacket} to a {@link GeometryPacket} and a {@link DetectionPacket}.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean consume(WrapperPacket packet) {
        // check if a Geometrypacket is present, if so it should be added to each pipeline for that
        // type.
        if (packet.hasGeometry()) Pipelines.getOfDataType(GeometryPacket.class).stream().forEach(
                pipeline -> pipeline.addPacket(new GeometryPacket(packet.read().getGeometry())).processPacket());
                
        // check if a Geometrypacket is present, if so it should be added to each pipeline for that
        // type.
        if (packet.hasDetection()) Pipelines.getOfDataType(DetectionPacket.class).stream().forEach(
                pipeline -> pipeline.addPacket(new DetectionPacket(packet.read().getDetection())).processPacket());
                
        return true;
    }
    
}
