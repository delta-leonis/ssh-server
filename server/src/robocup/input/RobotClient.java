package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import robocup.controller.handlers.RobotInputHandler;
import robocup.model.World;

public class RobotClient extends UDPClient {
	private RobotInputHandler handler;

	public RobotClient(String host, int port) {
		super(host, port);
		handler = new RobotInputHandler(World.getInstance());
	}

	public void startListening() throws IOException {
		ByteArrayInputStream input = null;
		while (true) {
			input = receive();
			if (input != null) {

				handler.process(input.read(), input.read(), input.read());
				// XXX:input = null;
			}

		}
	}
}
