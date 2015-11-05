package org.ssh.services.pipeline;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.ssh.services.Pipeline;
import org.ssh.util.Reflect;

import com.google.protobuf.MessageOrBuilder;

/**
 * The Class PipelinePacket.
 * 
 * A PipelinePacket holds data and is processed by a {@link org.ssh.services.Pipeline}.
 *
 * @author Rimon Oz
 */
abstract public class PipelinePacket {

    /** The mutability setting. */
    private boolean isMutable;

    /**
     * Applies a lambda to the packet.
     *
     * @param <T>      A PipelinePacket this lambda can work with.
     * @param function The lambda to execute on the PipelinePacket.
     * @return         The resulting PipelinePacket.
     */
    @SuppressWarnings("unchecked")
    public <T extends PipelinePacket> T apply(Function<T, T> function) {
        return function.apply((T) this);
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
    abstract public Object read();

    /**
     * Save.
     *
     * @param <T>  The type of the PipelinePacket
     * @param data The data to be put inside the packet.
     * @return     The packet itself.
     */
    abstract public <T extends PipelinePacket> T save(MessageOrBuilder data);

    /**
     * Sets the mutability of the packet.
     *
     * @param mutability The mutability (true or false)
     */
    public void setMutability(boolean mutability) {
        this.isMutable = mutability;
    }

    	
    public Map<String, ?> toMap(Class<?> clazz){
    	return Stream.of(clazz.getDeclaredFields())
			    	.filter(field -> Reflect.containsField(field.getName(), this.getClass()))
			    	.collect(Collectors.toMap(Field::getName, Unchecked.function(field -> field.get(this))));
    }
}
