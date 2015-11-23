package org.ssh.pipelines.packets;

import protobuf.Detection.DetectionFrame;

/**
 * Detection packet class.
 * 
 * @author Jeroen
 * 
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link org.ssh.pipelines.PipelinePacket PipelinePackets}
 * @see {@link org.ssh.pipelines.packets.ProtoPacket ProtoPacket}
 *
 */
public class DetectionPacket extends ProtoPacket<DetectionFrame> {
    public DetectionPacket(DetectionFrame detectionFrame) {
        super(detectionFrame);
    }

}
