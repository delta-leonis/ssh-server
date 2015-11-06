package org.ssh.services.pipeline;

import org.ssh.managers.Services;
import org.ssh.services.Pipeline;
import org.ssh.services.Service;

/**
 * The Class Consumer.
 *
 * A Consumer takes a PipelinePacket and consumes it.
 *
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Consumer can work with.
 */
public abstract class Consumer<T extends PipelinePacket> extends Service<T> {
    
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
    public Consumer<T> attachToCompatiblePipelines() {
        // TODO: make sure org.ssh.pipelines can handle same type
        // get all the org.ssh.services
        Services.getPipelines(this.getDataType()).stream()
                // map them to the correct type
                .map(pipeline -> (Pipeline<T>) pipeline)
                // register with the org.ssh.services.pipeline
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
