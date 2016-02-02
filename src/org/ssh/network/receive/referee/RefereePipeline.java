package org.ssh.network.receive.referee;

import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.RefereePacket;

/**
 * Pipeline for the {@link RefereePacket RefereePacket}
 *
 * @author Thomas Hakkers
 */
public class RefereePipeline extends AbstractPipeline<RefereePacket> {

    public RefereePipeline(String name) {
        super(name);
    }

}

