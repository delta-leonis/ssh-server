package output;

import java.util.ArrayList;

import application.Services;
import model.enums.ProducerType;
import model.enums.SendMethod;
import pipeline.Pipeline;
import pipeline.packets.RadioPacket;
import pipelines.RadioPipeline;
import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;
import services.Producer;
import ui.lua.console.AvailableInLua;

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
	private RadioPipeline commPipeline = (RadioPipeline) Services.getPipeline("communication pipeline");
	
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
