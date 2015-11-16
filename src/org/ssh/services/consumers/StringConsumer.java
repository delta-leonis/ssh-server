package org.ssh.services.consumers;

import org.ssh.services.Consumer;
import org.ssh.services.PipelinePacket;
import org.ssh.services.Service;
import org.ssh.services.pipeline.packets.RadioPacket;

/**
 * The Class StringConsumer.
 *
 * This is an example implementation of a Consumer.
 *
 * @author Rimon Oz
 */
public class StringConsumer extends Consumer<RadioPacket> {
    
    /**
     * Instantiates a new Consumer that consumes RadioPackets
     *
     * @param name
     *            The name of the StringConsumer
     */
    public StringConsumer(final String name) {
        super(name);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see org.ssh.services.Consumer#consume(org.ssh.services.pipeline.PipelinePacket)
     */
    @Override
    public boolean consume(final PipelinePacket radioPacket) {
        Service.LOG.info("The StringConsumer ate a packet that looked like: \n%s",
                ((RadioPacket) radioPacket).getData().toString());
        // Service.LOG.info("%d", ((RadioPacket) radioPacket).
        return true;
    }
    
}
