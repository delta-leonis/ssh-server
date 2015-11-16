package org.ssh.services.producers;

import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Producer;

public class UDPReceiver<T extends PipelinePacket> extends Producer<PipelinePacket> {
    
    public UDPReceiver(String hostname, int port) {
        super(hostname, ProducerType.SINGLE);
    }
    
}
