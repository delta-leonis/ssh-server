package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RefereeClient extends UDPClient {

	public RefereeClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Listens to UDP broadcastPackets and sends them to ProtoParser
	 * @throws IOException 
	 */
	public void startListening() throws IOException {
		ByteArrayInputStream input = null;
		while (true) {
			input = receive();
			if (input != null) {
				protoParser.parseReferee(input);
			}

		}
	}
}
