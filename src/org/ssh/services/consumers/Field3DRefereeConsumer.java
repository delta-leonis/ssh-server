package org.ssh.services.consumers;

import org.ssh.pipelines.packets.RefereePacket;
import org.ssh.services.service.Consumer;

/**
 * Field3DRefereeConsumer. This is the {@link Consumer} for the {@link RefereePacket}.
 * 
 * @author marklef2
 *        
 */
public class Field3DRefereeConsumer extends Consumer<RefereePacket> {
    
    /**
     * Constructor
     */
    public Field3DRefereeConsumer() {
        
        // Initialize super class
        super("field3drefereeconsumer");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean consume(RefereePacket pipelinePacket) {
        
        return true;
    }
    
}
