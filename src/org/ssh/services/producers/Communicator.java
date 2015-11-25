package org.ssh.services.producers;

import java.util.List;

import org.ssh.managers.manager.Pipelines;
import org.ssh.models.enums.ProducerType;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.packets.RadioPacket;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.senders.SenderInterface;
import org.ssh.services.service.Producer;
import org.ssh.ui.lua.console.AvailableInLua;

import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * Class that handles all the outgoing traffic, independent of what {@link SendMethod} is used.
 * <br />
 * Overloads different send methods to allow a wide variety of data throughput.
 *
 * TODO * update javadoc * update wiki
 *
 * @author Jeroen
 * @see SenderInterface
 */
@AvailableInLua
public class Communicator extends Producer<RadioPacket> {
    
    private final RadioPipeline commPipeline = (RadioPipeline) Pipelines.get("communication pipeline").get();
    
    public Communicator() {
        super("communicator", ProducerType.SINGLE);
    }
    
    /**
     * Builds a {@link RadioProtocolWrapper} from all commands supplied and tries to send through
     * {@link #send(com.google.protobuf.GeneratedMessage.Builder)}
     * 
     * @param commands
     *            Array with {@link RadioProtocolCommand} to send
     * @return success value
     */
    public boolean send(final List<Radio.RadioProtocolCommand.Builder> commands) {
        // create a wrapper-builder
        final RadioProtocolWrapper.Builder wrapperBuilder = Radio.RadioProtocolWrapper.newBuilder();
        // and add all commands to the wrapper-builder
        commands.stream().forEach(command -> wrapperBuilder.addCommand(command));
        return this.send(wrapperBuilder);
    }
    
    /**
     * Creates a {@link RadioPacket} and puts it in the {@link Pipeline Pipeline<RadioPacket>}
     * 
     * @param genericBuilder
     *            a RadioWrapper Builder<?>
     * @param sendMethod
     *            SendMethod that will be used to send message
     */
    public boolean send(final protobuf.Radio.RadioProtocolWrapper.Builder genericBuilder,
            final SendMethod... sendMethods) {
        return this.commPipeline.addPacket(new RadioPacket(genericBuilder.build(), sendMethods)).processPacket();
    }
    
    public boolean send(final RadioProtocolCommand.Builder command, final SendMethod... methods) {
        return this.send(RadioProtocolWrapper.newBuilder().addCommand(command), methods);
    }
}
