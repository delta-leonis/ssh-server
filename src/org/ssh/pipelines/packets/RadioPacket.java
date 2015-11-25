package org.ssh.pipelines.packets;

import java.io.ByteArrayInputStream;

import org.ssh.models.enums.SendMethod;

import protobuf.Radio.RadioProtocolWrapper;
import protobuf.Radio.RadioProtocolWrapper.Builder;

/**
 * The RadioPacket class.
 *
 * @author Rimon Oz
 * @author Jeroen de Jong
 * 
 * @see {@link org.ssh.managers.manager.Pipelines Pipelines}
 * @see {@link org.ssh.pipelines.PipelinePacket PipelinePackets}
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
     * @param builder
     *            the message.
     * @param methods
     *            specify senders different than default.
     */
    public RadioPacket(final RadioProtocolWrapper message, final SendMethod... methods) {
        super(message);
        this.sendMethods = methods;
    }
    

    public RadioPacket(ByteArrayInputStream byteStream) {
        super(byteStream);
        this.sendMethods = null;
    }
    
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
     * @return sendmethods that should be used instead of default sendmethod, empty when default
     *         sendmehtods are preferred.
     */
    public SendMethod[] getSendMethods() {
        return (SendMethod[]) sendMethods.clone();
    }
    
}