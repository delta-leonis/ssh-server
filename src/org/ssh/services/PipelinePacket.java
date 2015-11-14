package org.ssh.services;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.ssh.util.Reflect;

import com.google.protobuf.MessageOrBuilder;

/**
 * The Class PipelinePacket.
 *
 * A PipelinePacket holds data and is processed by a {@link org.ssh.services.Pipeline}.
 *
 * @author Rimon Oz
 */
public abstract class PipelinePacket {
    
    /** The mutability setting. */
    private boolean isMutable;
    
    /**
     * Applies a lambda to the packet.
     *
     * @param <P>
     *            A PipelinePacket this lambda can work with.
     * @param function
     *            The lambda to execute on the PipelinePacket.
     * @return The resulting PipelinePacket.
     */
    @SuppressWarnings ("unchecked")
    public <P extends PipelinePacket> P apply(final Function<P, P> function) {
        return function.apply((P) this);
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
    public abstract Object read();
    
    /**
     * Save.
     *
     * @param <P>
     *            The type of the PipelinePacket
     * @param data
     *            The data to be put inside the packet.
     * @return The packet itself.
     */
    public abstract <P extends PipelinePacket> P save(MessageOrBuilder data);
    
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
     * @param <O> the generic type
     * @param clazz the clazz
     * @return the map
     */
    @SuppressWarnings ("unchecked")
    public <O extends Object> Map<String, O> toMap(final Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> Reflect.containsField(field.getName(), this.getClass()))
                .collect(Collectors.toMap(Field::getName, Unchecked.function(field -> (O) field.get(this))));
    }
}
