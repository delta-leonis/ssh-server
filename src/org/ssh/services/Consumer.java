package org.ssh.services;

import org.ssh.managers.Services;

/**
 * The Class Consumer.
 *
 * A Consumer takes a PipelinePacket and consumes it.
 *
 * @author Rimon Oz
 * @param
 *            <P>
 *            A PipelinePacket this Consumer can work with.
 */
public abstract class Consumer<P extends PipelinePacket> extends Service<P> {
    
    /**
     * Instantiates a new Consumer.
     *
     * @param name
     *            The name of the new Consumer
     */
    public Consumer(final String name) {
        super(name);
    }
    
    /**
     * Attachs to all compatible Pipelines.
     *
     * @return The consumer itself.
     */
    @SuppressWarnings ("unchecked")
    public Consumer<P> attachToCompatiblePipelines() {
        Services.getPipelines(this.getType()).stream()
                // map them to the correct type
                .map(pipeline -> (Pipeline<P>) pipeline)
                // register with the pipeline
                .forEach(pipeline -> pipeline.registerConsumer(this));
                
        return this;
    }
    
    /**
     * Consumes a PipelinePacket.
     *
     * @param pipelinePacket
     *            The PipelinePacket to be consumed.
     * @return true, if successful
     */
    public abstract boolean consume(PipelinePacket pipelinePacket);
}
