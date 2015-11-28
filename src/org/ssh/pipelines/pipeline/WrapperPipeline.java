package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.WrapperPacket;

/**
 * Pipeline for {@link WrapperPacket WrapperPackets}
 * 
 * @author Jeroen de Jong
 *
 */
public class WrapperPipeline extends Pipeline<WrapperPacket> {
    
    public WrapperPipeline(String name) {
        super(name);
    }
    
}
