package org.ssh.pipelines;

import com.google.common.reflect.TypeToken;
import org.jooq.lambda.Unchecked;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class PipelinePacket.
 *
 * A PipelinePacket holds data and is processed by a {@link AbstractPipeline}.
 *
 * @author Rimon Oz
 */
public abstract class AbstractPipelinePacket<O extends Object> {
    
    /** The mutability setting. */
    private boolean isMutable;
    
    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<O>           genericType = new TypeToken<O>(this.getClass()) { };
    
    /**
     * Gets the type of {@link AbstractPipelinePacket} on which this Service operates.
     *
     * @return The type of PipelinePacket on which this Service operates.
     */
    public Type getType() {
        return this.genericType.getType();
    }
    
    
    // a logger for good measure
    protected final static Logger                   LOG         = Logger.getLogger();
    
    /**
     * The data contained by this package.
     */
    private O data;
    
    /**
     * Applies a lambda to the packet.
     *
     * @param function
     *            The lambda to execute on the PipelinePacket.
     * @return The resulting PipelinePacket.
     */
    public O apply(final Function<O, O> function) {
        return function.apply(this.read());
    }
    
    /**
     * Gets the mutability of the packet.
     *
     * @return The mutability of the packet.
     */
    public boolean getMutability() {
        return this.isMutable;
    }
    
    /**
     * Checks if the packet is mutable.
     *
     * @return true, if is mutable
     */
    public boolean isMutable() {
        return this.isMutable;
    }
    
    /**
     * Returns the data inside the packet.
     *
     * @return The data inside the packet.
     */
    public O read() {
        return data;
    }
    
    /**
     * Saves the data to the packet.
     *
     * @param <I>
     *            The type of data in the PipelinePacket
     * @param data
     *            The data to be put inside the packet.
     * @return The PipelinePacket itself.
     */
    @SuppressWarnings ("unchecked")
    public <I> AbstractPipelinePacket<O> save(I data) {
        this.data = (O) data;
        return this;
    }
    
    /**
     * Sets the mutability of the packet.
     *
     * @param mutability
     *            The mutability (true or false)
     */
    public void setMutability(final boolean mutability) {
        this.isMutable = mutability;
    }
    
    /**
     * Saves the data in the packet as a Map<String, O extends Object>.
     *
     * @param <F>
     *            The generic type of field contained by the data in the packet.
     * @param clazz
     *            The class from which to extract the fields.
     * @return The data in the packet as a Map.
     */
    @SuppressWarnings ("unchecked")
    public <F> Map<String, F> toMap(final Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> Reflect.hasField(field.getName(), this.getClass()))
                .collect(Collectors.toMap(Field::getName, Unchecked.function(field -> (F) field.get(this))));
    }
}
