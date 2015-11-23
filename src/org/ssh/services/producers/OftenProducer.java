package org.ssh.services.producers;

import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Producer;

import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * The Class OftenProducer.
 *
 * This is an example implementation of a scheduled Producer
 *
 * @author Rimon Oz
 */
public class OftenProducer extends Producer<RadioPacket> {
    
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
            Service.LOG.info("Produced a RadioPacket!");
            RadioProtocolCommand command = RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f).setVelocityX(4.0f)
            .setVelocityY(9293932.0f).build();
            // create a new packet
            return new RadioPacket(RadioProtocolWrapper.newBuilder().addCommand(command));
        });
    }
}