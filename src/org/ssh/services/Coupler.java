package org.ssh.services;

import org.ssh.managers.Services;
import org.ssh.models.enums.PacketPriority;

/**
 * The Class Coupler.
 *
 * A Coupler takes a {@link org.ssh.services.PipelinePacket} and returns a PipelinePacket
 * of the same genericType.
 *
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Coupler can work with.
 */
public abstract class Coupler<T extends PipelinePacket> extends Service<T> {
    
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
        // TODO: make sure the org.ssh.services.pipeline handles the genericType
        // get a list of all the org.ssh.pipelines
        Services.getPipelines(this.getType()).stream()
                // map them to the correct genericType
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
    public Coupler<T> attachToCompatiblePipelines(final PacketPriority couplerPriority) {
        // TODO: make sure the org.ssh.services.pipeline handles the genericType
        // get a list of all the org.ssh.pipelines
        Services.getPipelines(this.getType()).stream()
                // map them to the correct genericType
                .map(pipeline -> (Pipeline<T>) pipeline)
                // register with the org.ssh.services.pipeline
                .forEach(pipeline -> pipeline.registerCoupler(couplerPriority, this));
                
        return this;
    }
    
    /**
     * Process the PipelinePacket and return a PipelinePacket of the same genericType.
     *
     * @param pipelinePacket
     *            The old PipelinePacket
     * @return The new PipelinePacket
     */
    public abstract PipelinePacket process(PipelinePacket pipelinePacket);
    
}
