package org.ssh.pipelines.packets;

import org.ssh.pipelines.AbstractPipelinePacket;
import protobuf.Detection.DetectionFrame;

import java.util.HashMap;

/**
 * Detection packet class.
 * 
 * @author Jeroen
 *         
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link AbstractPipelinePacket PipelinePackets}
 * @see {@link ProtoPacket ProtoPacket}
 *
 */
public class StrategyPacket extends AbstractPipelinePacket<HashMap<String, Number>> {

    public StrategyPacket(HashMap<String, Number> metrics) {
        this.save(metrics);
    }
    
}
