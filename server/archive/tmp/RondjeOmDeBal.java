package nl.saxion.robocup.simtest.ai.behaviour;

import nl.saxion.robocup.simtest.Program;
import nl.saxion.robocup.simtest.ai.AIBehaviour;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionBall;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class RondjeOmDeBal extends AIBehaviour {
	
	@Override
	protected void init() {
	}

	@Override
	@SuppressWarnings("unused")
	public void processFrame(SSL_DetectionFrame frame) {
		if (frame.getBallsCount() > 0) {
			SSL_DetectionBall ball = frame.getBalls(0);
			
			for (SSL_DetectionRobot robot : ai.is_yellow_team ? frame.getRobotsYellowList() : frame
					.getRobotsBlueList()) {
				if (robot.getRobotId() != Program.robot_id)
					continue;
				if (!robot.hasOrientation())
					continue;
				
			}
			
		}
	}

}
