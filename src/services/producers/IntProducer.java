package services.producers;

import pipeline.packets.GeometryPacket;
import services.Producer;

/**
 * The Class IntProducer.
 */
public class IntProducer extends Producer<GeometryPacket> {

    /**
     * Instantiates a new int producer.
     *
     * @param name the name
     */
    public IntProducer (String name) {
        super(name);
        this.setCallable(() -> {
            return new GeometryPacket();
        });
    }
}