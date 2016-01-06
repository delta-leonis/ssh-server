package org.ssh.pipelines.packets;

import org.ssh.models.enums.SendMethod;

import org.ssh.pipelines.AbstractPipelinePacket;
import protobuf.Radio.RadioProtocolWrapper;
import protobuf.Radio.RadioProtocolWrapper.Builder;

/**
 * The RadioPacket class.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 * 
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link AbstractPipelinePacket PipelinePackets}
 * @see {@link org.ssh.pipelines.packets.ProtoPacket ProtoPacket}
 */
public class RadioPacket extends ProtoPacket<RadioProtocolWrapper> {
    
    /**
     * Overridden sendMethods, leave empty for default sendmethods.
     */
    private final SendMethod[] sendMethods;
    
    /**
     * Instantiates a new radio packet.
     * 
     * @param packetBuilder
     *            initial data.
     */
    public RadioPacket(Builder packetBuilder) {
        super(packetBuilder.build());
        this.sendMethods = new SendMethod[0];
    }
    
    /**
     * Instantiates a new radio packet.
     *
     * @param builder
     *            the message.
     * @param methods
     *            specify transmit different than default.
     */
    public RadioPacket(final RadioProtocolWrapper message, final SendMethod... methods) {
        super(message);
        this.sendMethods = methods;
    }
    
    /**
     * @return sendmethods that should be used instead of default sendmethod, empty when default
     *         sendmehtods are preferred.
     */
    public SendMethod[] getSendMethods() {
        return sendMethods.clone();
    }
    
}