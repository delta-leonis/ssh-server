package nl.saxion.robocup.simtest.ai.behaviour;

import nl.saxion.robocup.simtest.Program;
import nl.saxion.robocup.simtest.ai.AIBehaviour;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionBall;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionFrame;
import nl.saxion.robocup.simtest.proto.MessagesRobocupSslDetection.SSL_DetectionRobot;

import org.lwjgl.util.vector.Vector2f;

public class GaAchterDeBalStaan extends AIBehaviour {
	Vector2f shootTarget = new Vector2f();
	Vector2f ballPosition = new Vector2f();
	Vector2f robotPosition = new Vector2f();
	Vector2f robotTarget = new Vector2f();
	Vector2f robotBallDistance = new Vector2f();

	@Override
	protected void init() {
		float line = geometry.getField().getFieldLength() / 2;
		if (!ai.is_yellow_team)
			line *= -1;
		shootTarget.setX(line);
	}

	@Override
	protected void spacebar() {
		super.spacebar();
		ai.switchBehaviour(SchietOpBal.class);
	}

	@Override
	public void processFrame(SSL_DetectionFrame frame) {
		if (frame.getBallsCount() > 0) {
			SSL_DetectionBall ball = frame.getBalls(0);

			boolean ballChanged = false;

			if (Math.abs(ball.getX() - ballPosition.getX()) > 0.001
					|| Math.abs(ball.getY() - ballPosition.getY()) > 0.001) {
				ballChanged = true;
			}
			ballPosition.set(ball.getX(), ball.getY());

			Vector2f diff_vect = new Vector2f();
			Vector2f.sub(shootTarget, ballPosition, diff_vect);

			double target_ball_angle_rad = Math.atan2(diff_vect.getY(), diff_vect.getX());
			double target_ball_angle_deg = Math.toDegrees(target_ball_angle_rad);

			double target_x = Math.cos(target_ball_angle_rad) * 250.0;
			double target_y = Math.sin(target_ball_angle_rad) * 250.0;

			robotBallDistance.set((float) -target_x, (float) -target_y);

			Vector2f.add(ballPosition, robotBallDistance, robotTarget);

			if (ballChanged) {
				// System.out.println(String.format("Math:\n\t%f, %f\n\t%f, %f\n\t%f, %f\n\t%f, %f",
				// shootTarget.getX(), shootTarget.getY(),
				// ball.getX(), ball.getY(),
				// robotBallDistance.getX(), robotBallDistance.getY(),
				// robotTarget.getX(), robotTarget.getY()));
			}

			for (SSL_DetectionRobot robot : ai.is_yellow_team ? frame.getRobotsYellowList() : frame
					.getRobotsBlueList()) {
				if (robot.getRobotId() != Program.robot_id)
					continue;
				if (!robot.hasOrientation())
					continue;

				robotPosition.set(robot.getX(), robot.getY());

				Vector2f robotMovement = new Vector2f();
				Vector2f unit0 = new Vector2f(1, 0);

				Vector2f.sub(robotTarget, robotPosition, robotMovement);

				float driving_angle_rad = Vector2f.angle(robotMovement, unit0);
				if (unit0.getY() < robotMovement.getY())
					driving_angle_rad *= -1;

				float driving_angle_deg = (float) Math.toDegrees(driving_angle_rad);
				float robot_angle_deg = (float) Math.toDegrees(robot.getOrientation());

				int drivingDirection = 0;
				int directionSpeed = 0;
				int rotationSpeed = 0;
				boolean kicker = false;

				drivingDirection = 360 - (int) (driving_angle_deg + robot_angle_deg);
				float abs_len = Math.abs(robotMovement.length());
				directionSpeed = (int) (abs_len * 2);
				if (directionSpeed > 3000)
					directionSpeed = 3000;
				if (directionSpeed < 20)
					directionSpeed = 0;

				double angle_diff = (target_ball_angle_deg - robot_angle_deg);
				while (angle_diff > 180) {
					angle_diff -= 360;
				}
				while (angle_diff < -180) {
					angle_diff += 360;
				}
				if (Math.abs(angle_diff) > 0.1) {
					float force = (float) Math.min(Math.abs(angle_diff) / 180, 1);
					rotationSpeed = (int) ((angle_diff > 0 ? 1 * force : -1 * force) * 1000.0f);
				}

				// System.out.printf("Robot:\n\t%f, %f\n\t%d; %d, %d\n\t%f, %f\n\t%f, %f\n",
				// robot.getX(), robot.getY(),
				// drivingDirection, directionSpeed, rotationSpeed,
				// driving_angle_deg, robot_angle_deg,
				// target_ball_angle_deg, angle_diff);

				// directionSpeed = 0;
				// rotationSpeed = 0;

				simcom.send(0, robot.getRobotId(), drivingDirection, directionSpeed, 0, 0, rotationSpeed, kicker, false);
			}
		}
	}

}
