package org.ssh.pipelines.packets;

import org.ssh.pipelines.AbstractPipelinePacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Detection packet class.
 *
 * @author Jeroen
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link AbstractPipelinePacket PipelinePackets}
 * @see {@link ProtoPacket ProtoPacket}
 */
public class StrategyPacket extends AbstractPipelinePacket<HashMap<String, Number>> {

    public StrategyPacket(Map<String, Number> metrics) {
        this.save(metrics);
    }

}
