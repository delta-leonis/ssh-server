package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * {@link UDPClient} that gets all referee packages for {@link ProtoParser}
 */
public class RefereeClient extends UDPClient {

	/**
	 * Constructs new client for Referee packages
	 * @param host	multicast host ip
	 * @param port	multicast port
	 */
	public RefereeClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Listens to UDP broadcastPackets and sends them to {@link ProtoParser}
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
