package org.ssh.services;

import org.ssh.pipelines.AbstractPipelinePacket;

import java.util.function.Function;

/**
 * The Class AbstractCoupler.
 *
 * A Coupler takes a {@link AbstractPipelinePacket} and returns a PipelinePacket of the same type.
 *
 * @param <P> A PipelinePacket this Coupler can work with.
 *
 * @author Rimon Oz
 */
public abstract class AbstractCoupler<P extends AbstractPipelinePacket<?>> extends AbstractService<P> {

    /**
     * The transfer function representing the process the coupler symbolizes.
     */
    private Function<P, P> transferFunction;

    /**
     * Instantiates a new Coupler.
     *
     * @param name
     *            The name of the new Coupler.
     */
    public AbstractCoupler(final String name, final Function<P, P> transferFunction) {
        super(name);
        this.setTransferFunction(transferFunction);
    }

    /**
     * Sets the transfer function to the supplied function.
     * @param transferFunction The transfer function to be set.
     * @return                 The Coupler itself, to support method chaining.
     */
    public AbstractCoupler<P> setTransferFunction(final Function<P, P> transferFunction) {
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
