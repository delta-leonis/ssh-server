package output;

import java.util.logging.Level;

import com.google.protobuf.Message;

import util.Logger;

/**
 * SendInterface used for debugging. This sender will log the contents of {@link Message} to {@link Logger}
 * 
 * @author Jeroen
 *
 */
public class Debug implements SenderInterface {
	//respective logger
	private Logger logger = Logger.getLogger();
	
	/**
	 * Create a debug-sender
	 * 
	 * @param loggerLevel	level to log to
	 */
	public Debug(Level loggerLevel){
		logger.setLevel(loggerLevel);
	}

	@Override
	public boolean send(Message genericMessage) {
		logger.log(logger.getLevel(), genericMessage.toString());
		return true;
	}

	@Override
	public boolean unregister() {
		return true;
	}

}
