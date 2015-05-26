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
			if(protolog.getState() == LogState.PAUSE)
				delay(1);
			else if(protolog.getState() == LogState.PLAY){
				data = new ByteArrayInputStream(protolog.getData(protolog.getCursor()));
			}else
				data = receive();

			if (data != null) {

				if(protolog.getState() == LogState.RECORDING){
					byte[] byteArray = read(data);
					protolog.add(byteArray);
					data = new ByteArrayInputStream(byteArray);
				}
				
				protoParser.parseVision(data);
			}
		}
	}
	
	private void delay(long time){
		try {	
			Thread.sleep(time);
		} catch (InterruptedException e) {	}
	}
	
	public byte[] read(ByteArrayInputStream bais) throws IOException {
	     byte[] array = new byte[bais.available()];
	     bais.read(array);
	     return array;
	}
}
