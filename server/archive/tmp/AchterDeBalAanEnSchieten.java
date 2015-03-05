package nl.saxion.robocup.simtest.ai.behaviour;

import nl.saxion.robocup.simtest.Program;
import nl.saxion.robocup.simtest.ai.AIBehaviour;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionBall;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class AchterDeBalAanEnSchieten extends AIBehaviour {
	
	@Override
	protected void init() {
	}

	@Override
	public void processFrame(SSL_DetectionFrame frame) {
		if (frame.getBallsCount() > 0) {
			SSL_DetectionBall ball = frame.getBalls(0);
			for (SSL_DetectionRobot robot : ai.is_yellow_team ? frame.getRobotsYellowList() : frame.getRobotsBlueList()) {
				if(robot.getRobotId() != Program.robot_id)
					continue;
				if (!robot.hasOrientation())
					continue;

				int direction = 0;
				int directionSpeed = 0;
				int rotationSpeed = 0;
				boolean kicker = false;

				double diff_x = ball.getX() - robot.getX();
				double diff_y = ball.getY() - robot.getY();

				double dist_2 = Math.pow(diff_x, 2) + Math.pow(diff_y, 2);
				// double dist = Math.sqrt(dist_2);

				double angle = Math.atan2(diff_y, diff_x);
				double robo_angle = robot.getOrientation();
				double angle_diff = angle - robo_angle;

				if (Math.abs(angle_diff) > 0.03) {
					float force = (float) Math.min(Math.abs(angle_diff), 3);
					rotationSpeed = (int) ((angle_diff > 0 ? 1 * force : -1 * force) * 1000.0f);
				}

				//System.out.println(String.format("%f", dist_2));
				if (dist_2 > 100 * 100) {
					direction = (int) (angle_diff * 180 / Math.PI);

					if (dist_2 > 500 * 500) {
						directionSpeed = 2500;
					} else {
						directionSpeed = 2500;
						if (dist_2 < 110 * 110)
							kicker = true;
					}
				}

				simcom.send(0, robot.getRobotId(), direction, directionSpeed, 0, 0, rotationSpeed, kicker, false);
			}
		}
	}

}
