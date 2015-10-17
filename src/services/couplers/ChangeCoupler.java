package services.couplers;

import pipeline.PipelinePacket;
import pipeline.packets.RadioPacket;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolCommand.Builder;
import services.Coupler;
import services.Service;

/**
 * The Class ChangeCoupler.
 * 
 * An example implemtation of a Coupler
 * 
 * @author Rimon Oz
 */
public class ChangeCoupler extends Coupler<RadioPacket> {

    /**
     * Instantiates a new ChangeCoupler.
     *
     * @param name The name of the new Coupler
     */
    public ChangeCoupler(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see services.Coupler#process(pipeline.PipelinePacket)
     */
    @Override
    public PipelinePacket process(PipelinePacket pipelinePacket) {
        // modify the packet and return it
        return pipelinePacket.apply(content -> {
            Service.logger.info("Change Coupler is updating some values ....");
            // get the data
            final RadioProtocolCommand.Builder changeling = (Builder) ((RadioPacket) content).getData();
            // update it
            changeling.setVelocityY(300f);
            // return it
            return new RadioPacket(changeling);
        });
    }
}
