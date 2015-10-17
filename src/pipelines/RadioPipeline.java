package pipelines;

import pipeline.Pipeline;
import pipeline.packets.RadioPacket;

/**
 * The Class RadioPipeline.
 *
 * @author Rimon Oz
 */
public class RadioPipeline extends Pipeline<RadioPacket> {

    /**
     * Instantiates a new radio pipeline.
     *
     * @param name the name
     */
    public RadioPipeline(String name) {
        super(name);
        // TODO setup pipeline
    }
}
