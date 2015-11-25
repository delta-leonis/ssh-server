package org.ssh.pipelines.pipeline;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.WrapperPacket;


public class WrapperPipeline extends Pipeline<WrapperPacket> {

    public WrapperPipeline(String name) {
        super(name);
    }

}
