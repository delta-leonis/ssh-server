package pipeline;

import java.util.function.Function;

import com.google.protobuf.MessageOrBuilder;

/**
 * The Class PipelinePacket.
 * 
 * @author Rimon Oz
 */
abstract public class PipelinePacket {

    /** The is mutable. */
    private boolean   isMutable;

    /**
     * Apply.
     *
     * @param function the function
     * @return the pipeline packet
     */
    public <T extends PipelinePacket> T apply(Function<T, T> function) {
        return function.apply((T) this);
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
     * Read.
     *
     * @return the object
     */
    abstract public MessageOrBuilder read();

    /**
     * Save.
     *
     * @param data the data
     * @return the object
     */
    abstract public <T extends PipelinePacket> T save(MessageOrBuilder data);

    /**
     * Sets the mutability.
     *
     * @param mutability the new state
     */
    public void setMutability(boolean mutability) {
        this.isMutable = mutability;
    }
    
    /**
     * Gets the mutability.
     *
     * @return mutability the new state
     */
    public boolean getMutability() {
        return this.isMutable;
    }
    

}
