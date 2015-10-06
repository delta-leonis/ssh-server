package pipeline;

import java.util.function.Function;

/**
 * The Class PipelinePacket.
 */
abstract public class PipelinePacket {

    /** The is mutable. */
    private boolean   isMutable;

    /** The data. */
    protected Object  data;

    /**
     * Apply.
     *
     * @param function the function
     * @return the pipeline packet
     */
    public PipelinePacket apply(Function<PipelinePacket, PipelinePacket> function) {
        return function.apply(this);
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
    abstract public Object read();

    /**
     * Save.
     *
     * @param data the data
     * @return the object
     */
    abstract public Object save(Object data);

    /**
     * Sets the mutability.
     *
     * @param mutability the new state
     */
    public void setMutable(boolean mutability) {
        this.isMutable = mutability;
    }

}
