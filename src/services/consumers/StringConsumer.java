package services.consumers;

import pipeline.PipelinePacket;
import pipeline.packets.GeometryPacket;
import services.Consumer;

/**
 * The Class StringConsumer.
 */
public class StringConsumer extends Consumer<GeometryPacket> {

    /**
     * Instantiates a new string consumer.
     *
     * @param name the name
     */
    public StringConsumer(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see services.Consumer#consume(pipeline.PipelinePacket)
     */
    @Override
    public boolean consume(PipelinePacket pipelinePacket) {
    	System.out.println(pipelinePacket.toString());
        return true;
    }

}
