package nl.saxion.robocup.simtest.ai.behaviour;

import nl.saxion.robocup.simtest.Program;
import nl.saxion.robocup.simtest.ai.AIBehaviour;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class SchietOpBal extends AIBehaviour {

	@Override
	protected void init() {
	}

	@Override
	protected void spacebar() {
		super.spacebar();
		ai.switchBehaviour(GaAchterDeBalStaan.class);
	}

	@Override
	protected void processFrame(SSL_DetectionFrame frame) {
		for (SSL_DetectionRobot robot : ai.is_yellow_team ? frame.getRobotsYellowList() : frame
				.getRobotsBlueList()) {
			if (robot.getRobotId() != Program.robot_id)
				continue;
			if(!robot.hasOrientation())
				continue;
			
			simcom.send(0, robot.getRobotId(), 0, 1000, 0, 0, 0, true, false);
			
		}
	}

}
