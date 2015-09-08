package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import robocup.model.ProtoLog;
import robocup.model.World;
import robocup.model.enums.LogState;

/**
 * Only override startListening()
 */
public class SSLVisionClient extends UDPClient {
	private ProtoLog protolog = World.getInstance().getProtoLog();
	public SSLVisionClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Listens to UDP broadcastPackets and sends them to ProtoParser
	 * @throws IOException 
	 */
	public void startListening() throws IOException {
		ByteArrayInputStream data = null;

		while (true) {
			if(protolog.getState() == LogState.PAUSE){
				delay(1);
				continue;
			} else if(protolog.getState() == LogState.PLAY){
				data = new ByteArrayInputStream(protolog.getData(protolog.getCursor()));
			}else
				data = receive();

			if (data != null) {

				if(protolog.getState() == LogState.RECORDING){
					//It's copying data to a byte[]
					//and making a new ByteArrayInputStream because data gets emptied when read 
					byte[] byteArray = read(data);
					protolog.add(byteArray);
					data = new ByteArrayInputStream(byteArray);
				}
				
				//pass data to a ProtoParser
				protoParser.parseVision(data);
			}
		}
	}
	
	/**
	 * Wait for a given amount of time
	 * @param time
	 */
	private void delay(long time){
		try {	
			Thread.sleep(time);
		} catch (InterruptedException e) {	
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a stream to a byte[]
	 * @param stream	stream to read from
	 * @return	byte[] containing bytes from stream
	 * @throws IOException
	 */
	public byte[] read(ByteArrayInputStream stream) throws IOException {
	     byte[] array = new byte[stream.available()];
	     stream.read(array);
	     return array;
	}
}
