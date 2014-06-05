package input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Only override startListening()
 * @author Erik Hubers, Gerbrand Bosch
 *
 */
public class SSLVisionClient extends UDPClient {

	public SSLVisionClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Listens to UDP broadcastPackets and sends them to ProtoParser
	 * @throws IOException 
	 */
	public void startListening() throws IOException {
		System.out.println("SSLVisionClient starts listening");
		ByteArrayInputStream data = null;
		while (true) {
				data = receive();
			if (data != null) {
				protoParser.parseVision(data);
			}
		}
	}
}
