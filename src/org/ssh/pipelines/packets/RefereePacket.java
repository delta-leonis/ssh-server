package org.ssh.pipelines.packets;

import protobuf.RefereeOuterClass.Referee;

/**
 * The Referee class.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 * 
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link org.ssh.pipelines.PipelinePacket PipelinePackets}
 * @see {@link org.ssh.pipelines.packets.ProtoPacket ProtoPacket}
 */
public class RefereePacket extends ProtoPacket<Referee> {

    public RefereePacket(Referee data) {
        super(data);
    }
    
}
