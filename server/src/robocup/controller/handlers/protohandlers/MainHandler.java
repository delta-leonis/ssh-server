package robocup.controller.handlers.protohandlers;

import robocup.input.ProtoParser;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import robocup.input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryData;
import robocup.input.protobuf.Referee.SSL_Referee;
import robocup.model.World;
/**
 * Handler that gets all generic protobuf messages from the {@link ProtoParser}
 * these messages will be send to the individual handlers.<br>
 * <br>
 * See 	{@link ProtoParser}, <br>
 *	   	{@link GeometryHandler},<br>
 *		{@link DetectionHandler},<br>
 *		{@link RefereeHandler}<br>
 */
public class MainHandler implements Runnable {

	private ProtoParser protoParser;
	private GeometryHandler geometryHandler;
	private DetectionHandler detectionHandler;
	private RefereeHandler refereeHandler;

	/**
	 * Constructs all specific handlers for {@link SSL_DetectionFrame}, {@link SSL_GeometryData} and {@link SSL_Referee} messages 
	 * @param world the {@link World} that will be affected
	 */
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
		while (true) {
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
