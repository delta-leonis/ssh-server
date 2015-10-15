package output;

import java.util.logging.Level;

import com.google.protobuf.Message;

import util.Logger;

public class Debug implements SenderInterface {
	private Logger logger = Logger.getLogger();
	
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
