package output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.protobuf.Message;

import model.enums.SendMethod;
import protobuf.Radio;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

/**
 * Class that handles all the outgoing traffic, independent of what {@link SendMethod} is used.<br />
 * Overloads different send methods to allow a wide variety of data throughput.
 * 
 * @author Jeroen
 * @see SenderInterface
 */
public class Communicator {

	/**
	 * Maps a {@link SenderInterface} to a {@link SendMethod} for easy management
	 */
	private static Map<SendMethod, SenderInterface> senders = new HashMap<SendMethod , SenderInterface>();
	/**
	 * Current sendMethod. will be declared upon first register of a hook
	 */
	public static SendMethod sendMethod;
	/**
	 * 	Logger for this specific class
	 */
	private static Logger logger = Logger.getLogger(Communicator.class.toString());
	
	/**
	 * Register a new handler for a given SendMethod. Note that only one {@link SenderInterface} per {@link SendMethod} is allowed. New handlers will overwrite older ones.<br />
	 * The first registered {@link SendMethod} will be set as default sendmethod.
	 *   
	 * @param key			sendMethod to use 
	 * @param communicator	communicator that implements given sendmethod
	 */
	public static void register(SendMethod key, SenderInterface communicator){
		//give a notification that existing keys will be overriden
		if(senders.containsKey(key)){
			logger.info(String.format("Sendmethod %s has a communicator, and will be overwritten.\n", key));

			//try to unregister the key
			unregister(key);
		}

		senders.put(key, communicator);
		logger.info(String.format("registered hook for %s.", key));

		//set a default communicator when we have communicators, but no send method
		if(senders.size() == 1 
				&& sendMethod == null)
			setSendMethod(key);
	}
	
	/**
	 * Unregisters a hook to a {@link SendMethod}
	 * 
	 * @param sendmethod	method to unhook
	 * @return succes value
	 */
	public static boolean unregister(SendMethod sendmethod){
		//check if the key exists
		if(!senders.containsKey(sendmethod)){
			logger.warning(String.format("Could not unregister %s, as it has no hook", sendmethod));
			return false;
		}

		//remove sender from list and call unregister
		if(!senders.remove(sendmethod).unregister()){
			logger.warning(String.format("Could not unregister %s.", sendmethod));
			return false;
		}

		//unhooking and unregistering were a success!
		logger.info(String.format("Unregistered %s.", sendmethod));
		return true;
	}
	
	/**
	 * Define the SendMethod to send messages, as used in {@link #send(Message)}<br />
	 * A handler for the {@link SendMethod} should be {@link #register(SendMethod, SenderInterface) registered} before setting new send method
	 * 
	 * @param newSendMethod
	 * @return succes value
	 */
	public static boolean setSendMethod(SendMethod newSendMethod){
		//making sure sendMethod has a registered handler
		if(!senders.containsKey(newSendMethod)){
			logger.warning(String.format("SendMethod (%s) has no registered handler.\n", newSendMethod));
			return false;
		}

		sendMethod = newSendMethod;
		logger.info(String.format("SendMethod %s has been set as default sendmethod.\n", sendMethod));
		return true;
	}
	
	/**
	 * Builds a {@link RadioProtocolWrapper} from all commands supplied  and tries to send through {@link #send(com.google.protobuf.GeneratedMessage.Builder)}
	 * @param commands	Array with {@link RadioProtocolCommand} to send
	 * @return success value
	 */
	public static boolean send(ArrayList<Radio.RadioProtocolCommand> commands){
		//create a wrapper-builder
		RadioProtocolWrapper.Builder wrapperBuilder = Radio.RadioProtocolWrapper.newBuilder();
		//and add all commands to the wrapper-builder
		commands.stream().forEach(command -> wrapperBuilder.addCommand(command));
		return send(wrapperBuilder);
	}

	/**
	 * Wraps a {@link RadioProtocolCommand} in a {@link RadioProtocolWrapper} and tries to send through {@link #send(com.google.protobuf.GeneratedMessage.Builder)}
	 * 
	 * @param command
	 * @return success value
	 */
	public static boolean send(RadioProtocolCommand.Builder command){
		//add command to new wrapper-builder
		return send(RadioProtocolWrapper.newBuilder().addCommand(command));
	}

	/**
	 * Builds a given Builder to a Message and tries to send it through {@link #send(Message))} with {@link #sendMethod} that has been set
	 * 
	 * @param genericBuilder	a protobuf Builder<?>
	 * @return	succes value
	 */
	public static boolean send(com.google.protobuf.GeneratedMessage.Builder<?> genericBuilder){
		return send(genericBuilder.build(), sendMethod);
	}
	/**
	 * Builds a given {@link Builder} to a {@link Message} and tries to send it through {@link #send(Message))} 
	 * 
	 * @param genericBuilder	a protobuf Builder<?>
	 * @param sendMethod		SendMethod that will be used to send message
	 * @return	succes value
	 */
	public static boolean send(com.google.protobuf.GeneratedMessage.Builder<?> genericBuilder, SendMethod... sendMethods){
		return send(genericBuilder.build(), sendMethods);
	}

	/**
	 * Tries to send a Message<?> through given sendMethod
	 * 
	 * @param genericMessage	protobuf Message<?> to send
	 * @param sendMethod		SendMethod that will be used to send message
	 * @return succes value
	 */
	public static boolean send(Message genericMessage, SendMethod... sendMethods){
		//check if sendMethod is set
		if(sendMethods == null || sendMethods.length == 0){
			logger.severe("Sendmethod has not been set.");
			return false;
		}

		// get all the senders that have been specified by the user and have implemented send-methods
		Map<Boolean, List<SendMethod>>filteredMethods = Stream.of(sendMethods).collect(Collectors.partitioningBy(sender -> senders.containsKey(sender)));

		// print a warning for each sendMethod that was specified but had no implemented send method
		filteredMethods.get(false).forEach(sendmethod -> logger.warning(String.format("Sendmethod (%s) has no registered handler.\n", sendmethod)));

		// return success value
		return filteredMethods.get(true).stream()
			// send messages parallel
			.parallel()
			// send the message
			.map(sendmethod -> senders.get(sendmethod).send(genericMessage))
			// collect all success values and reduce to true if all senders succeeded; false otherwise 
			.reduce(true, (accumulator, success) -> accumulator && success) 
			&& filteredMethods.get(false).size() <= 0;
	}
	
}