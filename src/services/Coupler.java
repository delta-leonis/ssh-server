package services;

import pipeline.PipelinePacket;

/**
 * The Class Coupler.
 * 
 * @author Rimon Oz
 */
abstract public class Coupler<T extends PipelinePacket> extends Service<T> {

    /**
     * Instantiates a new coupler.
     *
     * @param name the name
     */
    public Coupler(String name) {
        super(name);
    }

    /**
     * Process.
     *
     * @param pipelinePacket the pipeline packet
     * @return the pipeline packet
     */
    abstract public PipelinePacket process(PipelinePacket pipelinePacket);

}
