package org.ssh.services;

import org.ssh.pipelines.AbstractPipelinePacket;

import java.util.function.Function;

/**
 * The Class AbstractTranslator.
 *
 * A Translator takes a {@link AbstractPipelinePacket} and translates it to a PipelinePacket of a different type.
 *
 * @param <P> The type of PipelinePacket this Translator uses as input.
 * @param <Q> The type of PipelinePacket this Translator uses as output.
 *
 * @author Rimon Oz
 */
public abstract class AbstractTranslator<P extends AbstractPipelinePacket<?>, Q extends AbstractPipelinePacket<?>> extends AbstractService<P> {

    /**
     * The transfer function representing the process the coupler symbolizes.
     */
    public Function<P, Q> translationFunction;

    /**
     * Instantiates a new Translator.
     *
     * @param name
     *            The name of the new Translator.
     */
    public AbstractTranslator(final String name, final Function<P, Q> translationFunction) {
        super(name);
        this.setTranslationFunction(translationFunction);
    }

    /**
     * Sets the transfer function to the supplied function.
     * @param translationFunction The translation function to be set.
     * @return                    The Translator itself, to support method chaining.
     */
    public AbstractTranslator<P, Q> setTranslationFunction(final Function<P, Q> translationFunction) {
        this.translationFunction = translationFunction;
        return this;
    }

    /**
     * Returns the transfer function.
     * @return The transfer function.
     */
    public Function<P, Q> getTranslationFunction() {
        return this.translationFunction;
    }

}
