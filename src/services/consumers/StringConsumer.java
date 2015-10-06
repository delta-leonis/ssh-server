package services.consumers;

import application.Models;
import pipeline.PipelinePacket;
import services.Consumer;

/**
 * The Class StringConsumer.
 */
public class StringConsumer extends Consumer {

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
        Models.get("holystringlist").addData(pipelinePacket.toString());
        return true;
    }

}
