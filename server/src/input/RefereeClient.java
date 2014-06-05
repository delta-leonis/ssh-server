package input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 
 * @author Erik Hubers, Gerbrand Bosch
 *
 */
public class RefereeClient extends UDPClient{

	public RefereeClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Listens to UDP broadcastPackets and sends them to ProtoParser
	 * @throws IOException 
	 */
	public void startListening() throws IOException {
		System.out.println("referee client starts listening");
		ByteArrayInputStream input = null;
		while (true) {
			input = receive();
			if (input != null) {
				protoParser.parseReferee(input);
			}

		}
	}
}
