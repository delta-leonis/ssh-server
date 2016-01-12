package org.ssh.network.receive.detection;

import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.DetectionPacket;

/**
 * Pipeline for {@link DetectionPacket DetectionPackets}
 *
 * @author Jeroen de Jong
 */
public class DetectionPipeline extends AbstractPipeline<DetectionPacket> {

    public DetectionPipeline(String name) {
        super(name);
    }

}
