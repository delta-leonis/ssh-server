package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.DetectionPacket;

/**
 * Pipeline for {@link DetectionPacket DetectionPackets}
 * 
 * @author Jeroen de Jong
 *        
 */
public class DetectionPipeline extends Pipeline<DetectionPacket> {
    
    public DetectionPipeline(String name) {
        super(name);
    }
    
}
