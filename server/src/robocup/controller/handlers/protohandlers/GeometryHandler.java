package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryData;
import robocup.input.protobuf.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import robocup.model.World;

public class GeometryHandler {

	private World world;

	public GeometryHandler(World world) {
		this.world = world;
	}

	public void process(SSL_GeometryData message) {
		processFieldSize(message);
		processCameraCalibration(message);
	}

	public void processFieldSize(SSL_GeometryData message) {
		SSL_GeometryFieldSize f = message.getField();
		world.getField().update(f.getLineWidth(), f.getFieldLength(), f.getFieldWidth(), f.getBoundaryWidth(),
				f.getRefereeWidth(), f.getGoalWallWidth(), f.getGoalDepth(), f.getGoalWallWidth(),
				f.getCenterCircleRadius(), f.getDefenseRadius(), f.getDefenseStretch(), f.getFreeKickFromDefenseDist(),
				f.getPenaltySpotFromFieldLineDist(), f.getPenaltyLineFromSpotDist());
	}

	public void processCameraCalibration(SSL_GeometryData message) {

	}
}
