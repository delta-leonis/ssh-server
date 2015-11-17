package org.ssh.services;

import org.ssh.managers.Pipelines;
import org.ssh.models.enums.PacketPriority;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;

/**
 * The Class Coupler.
 *
 * A Coupler takes a {@link PipelinePacket} and returns a PipelinePacket of the same genericType.
 *
 * @author Rimon Oz
 * @param
 *            <P>
 *            A PipelinePacket this Coupler can work with.
 */
public abstract class Coupler<P extends PipelinePacket> extends Service<P> {
    
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
    public Coupler<P> attachToCompatiblePipelines() {
        Pipelines.getOfDataType(this.getType()).stream()
                // register with the pipeline
                .forEach(pipeline -> ((Pipeline<P>)pipeline).registerCoupler(this));
                
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
    public Coupler<P> attachToCompatiblePipelines(final PacketPriority couplerPriority) {
        Pipelines.getOfDataType(this.getType()).stream()
                // register with the org.ssh.services.pipeline
                .forEach(pipeline -> ((Pipeline<P>)pipeline).registerCoupler(couplerPriority, this));
                
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
