package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.DetectionPacket;


public class DetectionPipeline extends Pipeline<DetectionPacket> {

    public DetectionPipeline(String name) {
        super(name);
    }

}
