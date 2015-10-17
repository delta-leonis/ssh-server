package services.producers;

import model.enums.ProducerType;
import pipeline.packets.RadioPacket;
import protobuf.Radio.RadioProtocolCommand;
import services.Producer;
import services.Service;

/**
 * The Class OnceProducer.
 * 
 * An example implementation of a Producer.
 * 
 * @author Rimon Oz
 */
public class OnceProducer extends Producer<RadioPacket> {

    /**
     * Instantiates a new OnceProducer.
     *
     * @param name The name of the new Producer
     */
    public OnceProducer(String name) {
        // set the name and priority
        super(name, ProducerType.SINGLE);
        // update the work function
        this.setCallable(() -> {
            Service.logger.finer("Produced a RadioPacket!");
            // create a new RadioPacket
            return new RadioPacket(RadioProtocolCommand.newBuilder().setRobotId(4).setVelocityR(0.2f).setVelocityX(4.0f).setVelocityY(9293932.0f));
        });
    }
}