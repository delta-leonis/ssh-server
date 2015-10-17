package services.producers;

import model.enums.ProducerType;
import pipeline.packets.GeometryPacket;
import services.Producer;
import services.Service;

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
     * @param name The name of the new Producer
     */
    public OftenProducer(String name) {
        // set the name and priority
        super(name, ProducerType.SCHEDULED);
        // update the work function
        this.setCallable(() -> {
            Service.logger.info("Produced a GeometryPacket!");
            // create a new packet
            return new GeometryPacket();
        });
    }
}