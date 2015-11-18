package org.ssh.pipelines;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

/**
 * The Class PipelinePacket.
 *
 * A PipelinePacket holds data and is processed by a {@link Pipeline}.
 *
 * @author Rimon Oz
 */
public abstract class PipelinePacket<O extends Object> {
    
    /** The mutability setting. */
    private boolean isMutable;
    
    // a logger for good measure
    public static final Logger                   LOG         = Logger.getLogger();
    
    
    /**
     * The data contained by this package.
     */
    private O data;
    
    /**
     * Applies a lambda to the packet.
     *
     * @param <P>
     *            A PipelinePacket this lambda can work with.
     * @param function
     *            The lambda to execute on the PipelinePacket.
     * @return The resulting PipelinePacket.
     */
    public <P extends PipelinePacket<O>> O apply(final Function<O, O> function) {
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
     * @param <P>
     *            The type of the PipelinePacket
     * @param data
     *            The data to be put inside the packet.
     * @return The packet itself.
     */
    @SuppressWarnings ("unchecked")
    public <I extends Object> PipelinePacket<O> save(I data) {
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
     *            the clazz
     * @return the map
     */
    @SuppressWarnings ("unchecked")
    public <F extends Object> Map<String, F> toMap(final Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> Reflect.containsField(field.getName(), this.getClass()))
                .collect(Collectors.toMap(Field::getName, Unchecked.function(field -> (F) field.get(this))));
    }
}
