package org.ssh.services.consumers;

import java.lang.reflect.Type;

import org.ssh.pipelines.PipelinePacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Consumer;

/**
 * The Class StringConsumer.
 *
 * This is an example implementation of a Consumer.
 *
 * @author Rimon Oz
 */
public class StringConsumer<P extends PipelinePacket<? extends Object>> extends Consumer<P> {
    private Type type;
    
    
    /**
     * Instantiates a new Consumer that consumes RadioPackets
     *
     * @param name
     *            The name of the StringConsumer
     */
    public StringConsumer(final String name, Type type) {
        super(name);
        this.type = type;
    }
    
    @Override
    public Type getType(){
        return type;
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.Consumer#consume(org.ssh.services.pipeline.PipelinePacket)
     */
    @Override
    public boolean consume(P pipelinePacket) {
        Service.LOG.info("The StringConsumer ate a packet that looked like: \n%s",
                pipelinePacket.read().toString());
        return true;
    }
    
}
