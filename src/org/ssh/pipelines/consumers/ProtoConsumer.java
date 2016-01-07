package org.ssh.pipelines.consumers;

import java.lang.reflect.Type;

import org.ssh.pipelines.packets.ProtoPacket;
import org.ssh.services.AbstractService;
import org.ssh.services.AbstractConsumer;

import com.google.common.reflect.TypeToken;

/**
 * Verbose output for protopackets
 * 
 * @author Jeroen de Jong
 */
public class ProtoConsumer extends AbstractConsumer<ProtoPacket<?>> {
    
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
        this.genericType = (TypeToken<ProtoPacket<?>>) TypeToken.of(type);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.AbstractConsumer#consume(org.ssh.services.pipeline.AbstractPipelinePacket)
     */
    @Override
    public boolean consume(ProtoPacket<?> pipelinePacket) {
        AbstractService.LOG.info("The ProtoConsumer<%s> ate a packet that looked like: \n%s",
                this.genericType,
                pipelinePacket.read().toString());
        return true;
    }
    
}
