package org.ssh.pipelines.translators;

import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.pipelines.packets.StrategyPacket;
import org.ssh.services.AbstractTranslator;

/**
 * The Class StrategyToRadioTranslator
 *
 * @author Rimon Oz
 */
public class StrategyToRadioTranslator extends AbstractTranslator<StrategyPacket, RadioPacket> {
    /**
     * Instantiates a new Translator.
     *
     * @param name                The name of the new Translator.
     * @param translationFunction
     */
    public StrategyToRadioTranslator(String name) {
        super(name, (StrategyPacket packet) -> new RadioPacket(null));
    }
}
