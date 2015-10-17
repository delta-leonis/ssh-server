package services.couplers;

import pipeline.PipelinePacket;
import pipeline.packets.RadioPacket;
import services.Coupler;
import services.Service;

/**
 * The Class VerboseCoupler.
 * 
 * An example implemtation of a Coupler.
 * 
 * @author Rimon Oz
 */
public class VerboseCoupler extends Coupler<RadioPacket> {

    /**
     * Instantiates a new VerboseCoupler.
     *
     * @param name The name of the new Coupler.
     */
    public VerboseCoupler(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see services.Coupler#process(pipeline.PipelinePacket)
     */
    @Override
    public PipelinePacket process(PipelinePacket radioPacket) {
        // modify the packet and return it
        return radioPacket.apply(content -> {
            // print the data
            Service.logger.info("The VerboseCoupler ate a packet that looked like: \n%s", ((RadioPacket) radioPacket).getData().toString());
            // and return it
            return content;
        });
    }
}
