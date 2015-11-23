package org.ssh.services.producers;

import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.services.Service;

public class ReceiverManager extends Service<ProtoPacket<?>>  {
    
    public ReceiverManager(String hostname, int port) {
        super("Receiver<?>");
        this.setName(String.format("Receiver<%s>", getType()));

        // Magie uit config
//        new UDPReceiver<RefereePacket>();
//        new UDPReceiver<WrapperPacket>();
//        new UDPReceiver<RadioWrapperPacket>();
    }

}
