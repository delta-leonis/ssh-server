package org.ssh.services;

import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.AbstractPipelinePacket;

/**
 * The Class AbstractConsumer.
 *
 * A Consumer takes a PipelinePacket and consumes it.
 *
 * @param <P> A PipelinePacket this AbstractConsumer can work with.
 *            
 * @author Rimon Oz
 */
public abstract class AbstractConsumer<P extends AbstractPipelinePacket<? extends Object>> extends AbstractService<P> {
    
    /**
     * Instantiates a new Consumer.
     *
     * @param name
     *            The name of the new Consumer
     */
    public AbstractConsumer(final String name) {
        super(name);
    }
    
    /**
     * Attachs to all compatible Pipelines.
     *
     * @param <C>
     *            The generic type of AbstractConsumer requested by the user.
     * @return The AbstractConsumer itself.
     */
    public <C extends AbstractConsumer<P>> C attachToCompatiblePipelines() {
        long noPipes = Pipelines.getOfDataType(this.getType()).stream()
                // register with the pipeline
                .map(pipeline -> ((AbstractPipeline<P>) pipeline).registerConsumer(this))
                .count();
        AbstractConsumer.LOG.info("Attached %s to %d pipes.", getClass().getSimpleName(), noPipes);
                
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
