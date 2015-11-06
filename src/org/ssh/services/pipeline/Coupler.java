package org.ssh.services.pipeline;

import org.ssh.managers.Services;
import org.ssh.services.Pipeline;
import org.ssh.services.Service;

/**
 * The Class Coupler.
 *
 * A Coupler takes a {@link org.ssh.services.pipeline.PipelinePacket} and returns a PipelinePacket
 * of the same type.
 *
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Coupler can work with.
 */
abstract public class Coupler<T extends PipelinePacket> extends Service<T> {
    
    /**
     * Instantiates a new Coupler.
     *
     * @param name
     *            The name of the new Coupler.
     */
    public Coupler(final String name) {
        super(name);
    }
    
    /**
     * Attaches to all compatible Pipelines.
     *
     * @return The Coupler itself.
     */
    @SuppressWarnings ("unchecked")
    public Coupler<T> attachToCompatiblePipelines() {
        // TODO: make sure the org.ssh.services.pipeline handles the type
        // get a list of all the org.ssh.pipelines
        Services.getPipelines(this.getDataType()).stream()
                // map them to the correct type
                .map(pipeline -> (Pipeline<T>) pipeline)
                // register with the org.ssh.services.pipeline
                .forEach(pipeline -> pipeline.registerCoupler(this));
                
        return this;
    }
    
    /**
     * Attaches to all compatible Pipelines with the given Priority.
     *
     * @param couplerPriority
     *            The Priority with which the Coupler is to be registered.
     * @return The Coupler itself.
     */
    @SuppressWarnings ("unchecked")
    public Coupler<T> attachToCompatiblePipelines(final Priority couplerPriority) {
        // TODO: make sure the org.ssh.services.pipeline handles the type
        // get a list of all the org.ssh.pipelines
        Services.getPipelines(this.getDataType()).stream()
                // map them to the correct type
                .map(pipeline -> (Pipeline<T>) pipeline)
                // register with the org.ssh.services.pipeline
                .forEach(pipeline -> pipeline.registerCoupler(couplerPriority, this));
                
        return this;
    }
    
    /**
     * Process the PipelinePacket and return a PipelinePacket of the same type.
     *
     * @param pipelinePacket
     *            The old PipelinePacket
     * @return The new PipelinePacket
     */
    abstract public PipelinePacket process(PipelinePacket pipelinePacket);
    
}
