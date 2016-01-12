package org.ssh.pipelines.translators;

import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.services.AbstractTranslator;
import protobuf.Radio;

/**
 * The Class DetectionToStrategyTranslator
 * @author Rimon Oz
 */
public class DetectionToStrategyTranslator extends AbstractTranslator<DetectionPacket, RadioPacket> {
    /**
     * Instantiates a new Translator.
     *
     * @param name                The name of the new Translator.
     */
    public DetectionToStrategyTranslator(String name) {
        super(name, (DetectionPacket packet) ->
            new RadioPacket(Radio.RadioProtocolWrapper.newBuilder()));
    }
}
