package services.producers;

import pipeline.packets.GeometryPacket;
import services.Producer;
import util.Logger;

/**
 * The Class IntProducer.
 */
public class IntProducer extends Producer<GeometryPacket> {

    // a logger for good measure
    private static final Logger logger = Logger.getLogger();
    
    /**
     * Instantiates a new int producer.
     *
     * @param name the name
     */
    public IntProducer (String name) {
        super(name);
        this.setCallable(() -> {
        	System.out.println("Produced a GeometryPacket!");
        	util.Logger.getLogger().finer("Produced a GeometryPacket!");
            return new GeometryPacket();
        });
    }
}