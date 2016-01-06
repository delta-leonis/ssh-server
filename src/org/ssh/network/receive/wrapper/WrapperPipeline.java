package org.ssh.network.receive.wrapper;

import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.WrapperPacket;

/**
 * Pipeline for {@link WrapperPacket WrapperPackets}
 * 
 * @author Jeroen de Jong
 *        
 */
public class WrapperPipeline extends AbstractPipeline<WrapperPacket> {
    
    public WrapperPipeline(String name) {
        super(name);
    }
    
}
