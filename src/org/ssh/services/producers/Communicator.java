package org.ssh.services.producers;

import java.util.ArrayList;

import org.ssh.managers.Services;
import org.ssh.models.enums.ProducerType;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.RadioPipeline;
import org.ssh.senders.SenderInterface;
import org.ssh.services.Pipeline;
import org.ssh.services.pipeline.Producer;
import org.ssh.services.pipeline.packets.RadioPacket;
import org.ssh.ui.lua.console.AvailableInLua;

import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * Class that handles all the outgoing traffic, independent of what {@link SendMethod} is used.<br />
 * Overloads different send methods to allow a wide variety of data throughput.
 * 
 * TODO
 *  * update javadoc
 *  * update wiki
 * 
 * @author Jeroen
 * @see SenderInterface
 */
@AvailableInLua
public class Communicator extends Producer<RadioPacket>{
	private RadioPipeline commPipeline = (RadioPipeline) Services.getPipeline("communication org.ssh.services.pipeline");
	
	public Communicator() {
		super("communicator", ProducerType.SINGLE);
	}
	
	
	/**
	 * Builds a {@link RadioProtocolWrapper} from all commands supplied  and tries to send through {@link #send(com.google.protobuf.GeneratedMessage.Builder)}
	 * @param commands	Array with {@link RadioProtocolCommand} to send
	 * @return success value
	 */
	public boolean send(ArrayList<Radio.RadioProtocolCommand.Builder> commands){
		//create a wrapper-builder
		RadioProtocolWrapper.Builder wrapperBuilder = Radio.RadioProtocolWrapper.newBuilder();
		//and add all commands to the wrapper-builder
		commands.stream().forEach(command -> wrapperBuilder.addCommand(command));
		return send(wrapperBuilder);
	}

	public boolean send(RadioProtocolCommand.Builder command, SendMethod... methods){
		return send(RadioProtocolWrapper.newBuilder().addCommand(command), methods);
	}

	/**
	 * Creates a {@link RadioPacket} and puts it in the {@link Pipeline Pipeline<RadioPacket>}
	 * 
	 * @param genericBuilder	a RadioWrapper Builder<?>
	 * @param sendMethod		SendMethod that will be used to send message
	 */
	public boolean send(protobuf.Radio.RadioProtocolWrapper.Builder genericBuilder, SendMethod... sendMethods){
		return commPipeline.addPacket(new RadioPacket(genericBuilder, sendMethods)).processPacket();
	}
}
