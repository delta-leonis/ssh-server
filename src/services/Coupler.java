package services;

import application.Services;
import pipeline.Pipeline;
import pipeline.PipelinePacket;
import pipeline.Priority;

/**
 * The Class Coupler.
 * 
 * A Coupler takes a {@link pipeline.PipelinePacket} and returns a PipelinePacket of the same type.
 *
 * @author Rimon Oz
 * @param <T> A PipelinePacket this Coupler can work with.
 */
abstract public class Coupler<T extends PipelinePacket> extends Service<T> {

    /**
     * Instantiates a new Coupler.
     *
     * @param name The name of the new Coupler.
     */
    public Coupler(String name) {
        super(name);
    }

    /**
     * Attaches to all compatible Pipelines.
     *
     * @return The Coupler itself.
     */
    @SuppressWarnings("unchecked")
    public Coupler<T> attachToCompatiblePipelines() {
        // TODO: make sure the pipeline handles the type 
        // get a list of all the pipelines
        Services.getPipelines(this.getDataType()).stream()
            // map them to the correct type
            .map(pipeline -> (Pipeline<T>) pipeline)
            // register with the pipeline
            .forEach(pipeline -> pipeline.registerCoupler(this));
        
        return this;
    }

    /**
     * Attaches to all compatible Pipelines with the given Priority.
     *
     * @param couplerPriority The Priority with which the Coupler is to be registered.
     * @return                The Coupler itself.
     */
    @SuppressWarnings("unchecked")
    public Coupler<T> attachToCompatiblePipelines(Priority couplerPriority) {
        // TODO: make sure the pipeline handles the type 
        // get a list of all the pipelines
        Services.getPipelines(this.getDataType()).stream()
            // map them to the correct type
            .map(pipeline -> (Pipeline<T>) pipeline)
            // register with the pipeline
            .forEach(pipeline -> pipeline.registerCoupler(couplerPriority, this));
        
        return this;
    }

    /**
     * Process the PipelinePacket and return a PipelinePacket of the same type.
     *
     * @param pipelinePacket The old PipelinePacket
     * @return               The new PipelinePacket
     */
    abstract public PipelinePacket process(PipelinePacket pipelinePacket);

}
