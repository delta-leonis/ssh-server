package output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.protobuf.Message;

import model.enums.SendMethod;
import pipeline.PipelinePacket;
import pipeline.packets.RadioPacket;
import services.Consumer;
import util.Logger;

public class RadioPacketConsumer extends Consumer<RadioPacket>{

	/**
	 * Maps a {@link SenderInterface} to a {@link SendMethod} for easy management
	 */
	private Map<SendMethod, SenderInterface> senders = new HashMap<SendMethod , SenderInterface>();
	/**
	 * Current selected sendMethods. First to register will be automaticaly added
	 */
	private List<SendMethod> sendMethods = new ArrayList<SendMethod>();
	
	//respective logger
	private Logger LOG = Logger.getLogger();

	public RadioPacketConsumer() {
		super("RadioPacketConsumer");
	}
	
	/**
	 * Register a new handler for a given SendMethod. Note that only one {@link SenderInterface} per {@link SendMethod} is allowed. New handlers will overwrite older ones.<br />
	 * The first registered {@link SendMethod} will be set as default sendmethod.
	 *   
	 * @param key			sendMethod to use 
	 * @param communicator	communicator that implements given sendmethod
	 */
	public void register(SendMethod key, SenderInterface communicator){
		//give a notification that existing keys will be overriden
		if(senders.containsKey(key)){
			LOG.info("Sendmethod %s has a communicator, and will be overwritten.\n", key);

			//try to unregister the key
			unregister(key);
		}

		senders.put(key, communicator);
		LOG.info("registered hook for %s.", key);

		//set a default communicator when we have communicators, but no send method
		if(senders.size() == 1 
				&& sendMethods.isEmpty())
			addDefault(key);
	}
	
	/**
	 * Unregisters a hook to a {@link SendMethod}
	 * 
	 * @param sendmethod	method to unhook
	 * @return succes value
	 */
	public boolean unregister(SendMethod sendmethod){
		//check if the key exists
		if(!senders.containsKey(sendmethod)){
			LOG.warning("Could not unregister %s, as it has no hook", sendmethod);
			return false;
		}

		//remove sender from list and call unregister
		if(!senders.remove(sendmethod).unregister()){
			LOG.warning("Could not unregister %s.", sendmethod);
			return false;
		}

		//unhooking and unregistering were a success!
		LOG.info("Unregistered %s.", sendmethod);
		return true;
	}
	
	/**
	 * Define the SendMethod to send messages, as used in {@link #send(Message)}<br />
	 * A handler for the {@link SendMethod} should be {@link #register(SendMethod, SenderInterface) registered} before setting new send method
	 * 
	 * @param newSendMethod
	 * @return succes value
	 */
	public boolean addDefault(SendMethod... newSendMethods){
		return Stream.of(newSendMethods)
				.map(method -> addDefault(method))
				.reduce(true, (accumulator, result) -> accumulator && result);
	}
	
	public boolean addDefault(SendMethod method){
		//making sure sendMethod has a registered handler
		if(!senders.containsKey(method)){
			LOG.warning("SendMethod (%s) has no registered handler.\n", method);
			return false;
		}

		// 
		if(sendMethods.contains(method)){
			LOG.info("%s allready is a default sendMethod");
			return false;
		}
		
		sendMethods.add(method);
		LOG.info("SendMethod %s has been set as a default sendmethod.\n", method);
		return true;
	}
	
	public boolean removeDefault(SendMethod method){ 
		if(!sendMethods.contains(method)){
			LOG.info("%s isn't a default sendMethod");
			return false;
		}
		
		return sendMethods.remove(method);
	}
	
	private boolean send(Message genericMessage, SendMethod... sendMethods){
		//check if sendMethod is set
		if(sendMethods == null || sendMethods.length == 0){
			LOG.severe("Sendmethod has not been set.");
			return false;
		}

		// get all the senders that have been specified by the user and have implemented send-methods
		Map<Boolean, List<SendMethod>>filteredMethods = Stream.of(sendMethods).collect(Collectors.partitioningBy(sender -> senders.containsKey(sender)));

		// print a warning for each sendMethod that was specified but had no implemented send method
		filteredMethods.get(false).forEach(sendmethod -> LOG.warning("Sendmethod (%s) has no registered handler.\n", sendmethod));

		// return success value
		return filteredMethods.get(true).stream()
			// send messages parallel
			.parallel()
			// send the message
			.map(sendmethod -> senders.get(sendmethod).send(genericMessage))
			// collect all success values and reduce to true if all senders succeeded; false otherwise 
			.reduce(true, (accumulator, success) -> accumulator && success) 
			&& filteredMethods.get(false).isEmpty();
	}

	@Override
	public boolean consume(PipelinePacket pipelinePacket) {
		// cast to the right packet-type
		RadioPacket packet = (RadioPacket)pipelinePacket;
		// get default sendmethods
		SendMethod[] methods = packet.getSendMethods();
		// replace them by specified sendmethods if neccecary 
		if(packet.getSendMethods() == null || packet.getSendMethods().length == 0)
			methods = this.sendMethods.toArray(methods);

		LOG.info("Trying to send a consumed packet");
		// send the packet 
		return send(packet.getMessage(), methods);
	}
	
}
