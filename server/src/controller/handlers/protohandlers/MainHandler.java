package controller.handlers.protohandlers;

import input.ProtoParser;
import input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryData;
import input.protobuf.Referee.SSL_Referee;
import model.World;

public class MainHandler implements Runnable {

	private ProtoParser protoParser;
	private GeometryHandler geometryHandler;
	private DetectionHandler detectionHandler;
	private RefereeHandler refereeHandler;

	public MainHandler(World world) {
		protoParser = ProtoParser.getInstance();
		geometryHandler = new GeometryHandler(world);
		detectionHandler = new DetectionHandler(world);
		refereeHandler = new RefereeHandler(world);
	}

	/**
	 * Processes all messages from SSL_Vision to specific messageHandlers.
	 * messageHandlers will process the messages and put them into the model.
	 */
	@Override
	public void run() {
		System.out.println("Thread MainHandler started");
		while (true) {
			//Method blocks until Object available
			Object message = protoParser.getHeadObject();

			if (message instanceof SSL_DetectionFrame) {
				detectionHandler.process((SSL_DetectionFrame) message);
			} else if (message instanceof SSL_GeometryData) {
				geometryHandler.process((SSL_GeometryData) message);
			} else if (message instanceof SSL_Referee) {
				refereeHandler.process((SSL_Referee) message);
			}
		}
	}
}
