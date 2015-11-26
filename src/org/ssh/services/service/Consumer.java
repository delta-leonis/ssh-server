package org.ssh.services.service;

import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;

/**
 * The Class Consumer.
 *
 * A Consumer takes a PipelinePacket and consumes it.
 *
 * @param <P>
 *            A PipelinePacket this Consumer can work with.
 *            
 * @author Rimon Oz
 */
public abstract class Consumer<P extends PipelinePacket<? extends Object>> extends Service<P> {
    
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
     * @param <C>
     *            The generic type of Consumer requested by the user.
     * @return The Consumer itself.
     */
    public <C extends Consumer<P>> C attachToCompatiblePipelines() {
        Pipelines.getOfDataType(this.getType()).stream()
                // register with the pipeline
                .forEach(pipeline -> pipeline.registerConsumer(this));
                
        return this.<C>getAsService();
    }
    
    /**
     * Consumes a PipelinePacket.
     *
     * @param pipelinePacket
     *            The PipelinePacket to be consumed.
     * @return true, if successful
     */
    public abstract boolean consume(P pipelinePacket);
}
