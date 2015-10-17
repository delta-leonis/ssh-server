package services.consumers;

import pipeline.PipelinePacket;
import pipeline.packets.RadioPacket;
import services.Consumer;
import services.Service;

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
     * @param name The name of the StringConsumer
     */
    public StringConsumer(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see services.Consumer#consume(pipeline.PipelinePacket)
     */
    @Override
    public boolean consume(PipelinePacket radioPacket) {
        Service.logger.info("The StringConsumer ate a packet that looked like: \n%s",
            ((RadioPacket) radioPacket).getData().toString());
        return true;
    }

}
