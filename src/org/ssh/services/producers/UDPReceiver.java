package org.ssh.services.producers;

import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.services.service.Producer;

public class UDPReceiver<T extends PipelinePacket<ProtoPacket<?>>> extends Producer<ProtoPacket<?>> {
    
    public UDPReceiver(String hostname, int port) {
        super(hostname, ProducerType.SINGLE);
    }
    
}
