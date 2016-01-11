package org.ssh.network.transmit.radio.consumers;

import org.ssh.pipelines.AbstractPipelinePacket;
import org.ssh.services.AbstractConsumer;
import org.ssh.services.AbstractService;

import java.lang.reflect.Type;

/**
 * The Class StringConsumer.
 *
 * This is an example implementation of a AbstractConsumer.
 *
 * @author Rimon Oz
 */
public class StringConsumer<P extends AbstractPipelinePacket<? extends Object>> extends AbstractConsumer<P> {
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
     * @see org.ssh.services.AbstractConsumer#consume(org.ssh.services.pipeline.AbstractPipelinePacket)
     */
    @Override
    public boolean consume(P pipelinePacket) {
        AbstractService.LOG.info("The StringConsumer ate a packet that looked like: \n%s",
                pipelinePacket.read().toString());
        return true;
    }
    
}
