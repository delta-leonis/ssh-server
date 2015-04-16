package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryData;
import robocup.input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import robocup.model.World;
/**
 * Handler for {@link SSL_GeometryData} messages. 
 * updates the {@link Field} object from a {@link World} object
 */
public class GeometryHandler {

	private World world;

	/**
	 * Constructs handler for {@link SSL_GeometryData} messages
	 */
	public GeometryHandler() {
		this.world = World.getInstance();
	}

	/**
	 * Process a {@link SSL_GeometryData} message
	 * @param message to be processed
	 */
	public void process(SSL_GeometryData message) {
		processFieldSize(message);
		processCameraCalibration(message);
	}

	/**
	 * Updates {@link Field} information
	 * @param message with new information
	 */
	public void processFieldSize(SSL_GeometryData message) {
		SSL_GeometryFieldSize f = message.getField();
		world.getField().update(f.getLineWidth(), f.getFieldLength(), f.getFieldWidth(), f.getBoundaryWidth(),
				f.getRefereeWidth(), f.getGoalWallWidth(), f.getGoalDepth(), f.getGoalWallWidth(),
				f.getCenterCircleRadius(), f.getDefenseRadius(), f.getDefenseStretch(), f.getFreeKickFromDefenseDist(),
				f.getPenaltySpotFromFieldLineDist(), f.getPenaltyLineFromSpotDist());
	}

	/**
	 * Process camera calibration as notified by SSL_Vision
	 * @param message A {@link SSL_GeometryData} message used to update the {@link Field}
	 * @deprecated not processing anything
	 */
	public void processCameraCalibration(SSL_GeometryData message) {
		//TODO everything
	}
}
