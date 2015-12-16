package org.ssh.services.service;

import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;

import java.util.function.Function;

/**
 * The Class Coupler.
 *
 * A Coupler takes a {@link PipelinePacket} and returns a PipelinePacket of the same type.
 *
 * @param <P> A PipelinePacket this Coupler can work with.
 *
 * @author Rimon Oz
 */
public abstract class Coupler<P extends PipelinePacket<? extends Object>> extends Service<P> {

    /**
     * The transfer function representing the process the coupler symbolizes.
     */
    public Function<P, P> transferFunction;

    /**
     * Instantiates a new Coupler.
     *
     * @param name
     *            The name of the new Coupler.
     */
    public Coupler(final String name, final Function<P, P> transferFunction) {
        super(name);
        this.setTransferFunction(transferFunction);
    }

    /**
     * Sets the transfer function to the supplied function.
     * @param transferFunction The transfer function to be set.
     * @return                 The Coupler itself, to support method chaining.
     */
    public Coupler<P> setTransferFunction(final Function<P, P> transferFunction) {
        this.transferFunction = transferFunction;
        return this;
    }

    /**
     * Returns the transfer function.
     * @return The transfer function.
     */
    public Function<P, P> getTransferFunction () {
        return this.transferFunction;
    }
    
}
