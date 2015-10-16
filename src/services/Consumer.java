package services;

import pipeline.PipelinePacket;

/**
 * The Class Consumer.
 * 
 * @author Rimon Oz
 */
abstract public class Consumer<T extends PipelinePacket> extends Service<T> {

    /**
     * Instantiates a new consumer.
     *
     * @param name the name
     */
    public Consumer(String name) {
        super(name);
    }

    /**
     * Consume.
     *
     * @param pipelinePacket the pipeline packet
     * @return true, if successful
     */
    public abstract boolean consume(PipelinePacket pipelinePacket);
}
