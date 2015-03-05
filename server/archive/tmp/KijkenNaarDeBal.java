package nl.saxion.robocup.simtest.ai.behaviour;

import nl.saxion.robocup.simtest.ai.AIBehaviour;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionBall;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class KijkenNaarDeBal extends AIBehaviour {
	
	@Override
	protected void init() {
	}

	@Override
	public void processFrame(SSL_DetectionFrame frame) {
		if (frame.getBallsCount() > 0) {
			SSL_DetectionBall ball = frame.getBalls(0);
			for (SSL_DetectionRobot robot : ai.is_yellow_team ? frame.getRobotsYellowList() : frame.getRobotsBlueList()) {
				if (!robot.hasOrientation())
					continue;

				int direction = 0;
				int directionSpeed = 0;
				int rotationSpeed = 0;
				boolean kicker = false;

				double diff_x = ball.getX() - robot.getX();
				double diff_y = ball.getY() - robot.getY();

				double angle = Math.atan2(diff_y, diff_x);
				double robo_angle = robot.getOrientation();
				double angle_diff = angle - robo_angle;
				
				if (Math.abs(angle_diff) > 0.03) {
					float force = (float) Math.min(Math.abs(angle_diff), 3);
					rotationSpeed = (int) ((angle_diff > 0 ? 1 * force : -1 * force) * 1000.0f);
				}

				simcom.send(0, robot.getRobotId(), direction, directionSpeed, 0, 0, rotationSpeed, kicker, false);
			}
		}

	}

}
