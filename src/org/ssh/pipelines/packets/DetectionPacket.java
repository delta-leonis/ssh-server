package org.ssh.pipelines.packets;

import org.ssh.pipelines.AbstractPipelinePacket;
import protobuf.Detection.DetectionFrame;

/**
 * Detection packet class.
 * 
 * @author Jeroen
 *         
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link AbstractPipelinePacket PipelinePackets}
 * @see {@link org.ssh.pipelines.packets.ProtoPacket ProtoPacket}
 *      
 */
public class DetectionPacket extends ProtoPacket<DetectionFrame> {
    
    public DetectionPacket(DetectionFrame detectionFrame) {
        super(detectionFrame);
    }
    
}
