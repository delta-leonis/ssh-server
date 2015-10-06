package services;

import pipeline.PipelinePacket;

/**
 * The Class Consumer.
 */
abstract public class Consumer extends Service {

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
