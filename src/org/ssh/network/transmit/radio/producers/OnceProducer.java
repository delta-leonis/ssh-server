package org.ssh.network.transmit.radio.producers;

import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractService;
import org.ssh.services.AbstractProducer;

import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * The Class OnceProducer.
 *
 * An example implementation of a Producer.
 *
 * @author Rimon Oz
 */
public class OnceProducer extends AbstractProducer<RadioPacket> {
    
    /**
     * Instantiates a new OnceProducer.
     *
     * @param name
     *            The name of the new Producer
     */
    public OnceProducer(final String name) {
        // set the name and priority
        super(name, ProducerType.SINGLE);
        // update the work function
        this.setCallable(() -> {
            AbstractService.LOG.finer("Produced a RadioPacket!");
            // create a new RadioPacket
            RadioProtocolCommand command = RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f).setVelocityX(4.0f)
            .setVelocityY(9293932.0f).build();
            // create a new packet
            return new RadioPacket(RadioProtocolWrapper.newBuilder().addCommand(command));
        });
    }
}