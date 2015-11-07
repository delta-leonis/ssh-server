package org.ssh.services.producers;

import org.ssh.models.enums.ProducerType;
import org.ssh.services.Producer;
import org.ssh.services.Service;
import org.ssh.services.pipeline.packets.GeometryPacket;

/**
 * The Class OftenProducer.
 *
 * This is an example implementation of a scheduled Producer
 *
 * @author Rimon Oz
 */
public class OftenProducer extends Producer<GeometryPacket> {
    
    /**
     * Instantiates a new scheduled Producer
     *
     * @param name
     *            The name of the new Producer
     */
    public OftenProducer(final String name) {
        // set the name and priority
        super(name, ProducerType.SCHEDULED);
        // update the work function
        this.setCallable(() -> {
            Service.LOG.info("Produced a GeometryPacket!");
            // create a new packet
            return new GeometryPacket();
        });
    }
}