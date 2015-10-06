package services.couplers;

import pipeline.PipelinePacket;
import services.Coupler;

/**
 * The Class DoubleCoupler.
 */
public class DoubleCoupler extends Coupler {

    /**
     * Instantiates a new double coupler.
     *
     * @param name the name
     */
    public DoubleCoupler(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see services.Coupler#process(pipeline.PipelinePacket)
     */
    @Override
    public PipelinePacket process(PipelinePacket potentiallyNotDouble) {
        return potentiallyNotDouble.apply((content) -> {
            final double doubleValue = Double.parseDouble(content.read().toString());
            content.save(doubleValue);
            return content;
        });
    }


}
