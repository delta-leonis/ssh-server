package org.ssh.services;

import com.google.common.reflect.TypeToken;
import org.ssh.pipelines.AbstractPipelinePacket;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * The Class AbstractTranslator.
 * <p>
 * A Translator takes a {@link AbstractPipelinePacket} and translates it to a PipelinePacket of a different type.
 *
 * @param <P> The type of PipelinePacket this Translator uses as input.
 * @param <Q> The type of PipelinePacket this Translator uses as output.
 * @author Rimon Oz
 */
public abstract class AbstractTranslator<P extends AbstractPipelinePacket<?>, Q extends AbstractPipelinePacket<?>> extends AbstractService<P> {

    /**
     * The transfer function representing the process the coupler symbolizes.
     */
    private Function<P, Q> translationFunction;

    /**
     * The reflected TypeToken (o¬‿¬o ).
     */
    @SuppressWarnings("serial")
    private TypeToken<Q> outputType = new TypeToken<Q>(this.getClass()) {
    };

    /**
     * Instantiates a new Translator.
     *
     * @param name The name of the new Translator.
     */
    public AbstractTranslator(final String name, final Function<P, Q> translationFunction) {
        super(name);
        this.setTranslationFunction(translationFunction);
    }

    /**
     * Sets the transfer function to the supplied function.
     *
     * @param translationFunction The translation function to be set.
     * @return The Translator itself, to support method chaining.
     */
    public AbstractTranslator<P, Q> setTranslationFunction(final Function<P, Q> translationFunction) {
        this.translationFunction = translationFunction;
        return this;
    }

    /**
     * Returns the transfer function.
     *
     * @return The transfer function.
     */
    public Function<P, Q> getTranslationFunction() {
        return this.translationFunction;
    }

    /**
     * Translates a packet from type P to type Q.
     *
     * @param inputPacket The packet of type P.
     * @return The packet of type Q.
     */
    public Q translate(final P inputPacket) {
        return this.getTranslationFunction().apply(inputPacket);
    }

    /**
     * Returns the output type (Q) of this translator.
     *
     * @return The type of output packet produced by this translator.
     */
    public Type getOutputType() {
        return outputType.getType();
    }
}
