package org.ssh.services.consumers;

import java.lang.reflect.Type;

import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Consumer;

import com.google.common.reflect.TypeToken;
import com.google.protobuf.GeneratedMessage;

/**
 *
 */
public class ProtoConsumer<P extends ProtoPacket<? extends GeneratedMessage>> extends Consumer<P> {
    /**
     * Instantiates a new Consumer that consumes RadioPackets
     *
     * @param name
     *            The name of the StringConsumer
     */
    public ProtoConsumer(final String name, Type type) {
        super(name);
        // This way anonymous ProtoConsumers can be created,
        // without creating a empty class defining the type
        this.genericType = (TypeToken<P>) TypeToken.of(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.Consumer#consume(org.ssh.services.pipeline.PipelinePacket)
     */
    @Override
    public boolean consume(P pipelinePacket) {
        Service.LOG.info("The ProtoConsumer ate a packet that looked like: \n%s",
                pipelinePacket.read().toString());
        return true;
    }
    
}
