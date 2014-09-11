package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

//Robot receives ball location and target position
//robot drives to ball and starts dribbler.
//robot drives to target location with the ball.
public class GotoPositionWithBall extends LowLevelBehavior {

	private Point newPosition = null;
	private Point ballPosition = null;

	public GotoPositionWithBall(Robot robot, ComInterface output, Point targetPosition, Point BallPosition) {
		super(robot, output);
		this.newPosition = targetPosition;
		this.ballPosition = BallPosition;
		// TODO Auto-generated constructor stub
	}

	// public GotoPositionWithBall(Robot robot, ComInterface output, FieldObject
	// target) {
	// super(robot, output);
	// this.newPosition = target.getPosition();
	// }

	public Point getTarget() {
		return newPosition;
	}

	public void setTarget(Point p) {
		this.newPosition = p;
	}

	// zolang de ball niet binnen een bepaalde afstand van de robot ligt moet
	// hij naar de bal rijden.
	// zodra de ball in de buurt van de robot ligt moet hij dribbler aanzetten
	// en deze 'oppakken'
	// met de ball moet de robot naar de aangegeven plek rijden.

	@Override
	public void calculate() {
		if (timeOutCheck()) {

		} else if (ballPosition == null) {
			robot.setOnSight(true);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
			return;
		} else {
			if (newPosition.getX() - ballPosition.getX() > 10 && newPosition.getY() - ballPosition.getY() > 10) {
				robot.setOnSight(true);
				int direction = 0;
				int travelDistance = 0;
				int newRotation = rotationToDest(ballPosition);
				int speed = 0;
				int rotationSpeed = 0;

				if (Math.abs(newRotation) > 140) {
					rotationSpeed = 350;
				} else if (Math.abs(newRotation) > 70) {
					rotationSpeed = 280;
				} else if (Math.abs(newRotation) > 30) {
					travelDistance = getDistanceToTarget(ballPosition);
					speed = getSpeed(travelDistance, newRotation);
					if (speed == 0)
						rotationSpeed = 400;
					else
						rotationSpeed = 300;
				} else if (Math.abs(newRotation) > 10) {
					travelDistance = getDistanceToTarget(ballPosition);
					speed = getSpeed(travelDistance, newRotation);
					if (speed == 0)
						rotationSpeed = 200;
					else
						rotationSpeed = 150;
				} else {
					travelDistance = getDistanceToTarget(ballPosition);
					speed = getSpeed(travelDistance, newRotation);
				}

				if (newRotation < 0)
					rotationSpeed *= -1;
				if (travelDistance < 300 && Math.abs(newRotation) > 10)
					speed = 0;
				output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0,
						false);
			}

			else {

				robot.setOnSight(true);
				int direction = 0;
				int travelDistance = 0;
				int newRotation = rotationToDest(newPosition);
				int speed = 0;
				int rotationSpeed = 0;

				if (Math.abs(newRotation) > 140) {
					rotationSpeed = 350;
				} else if (Math.abs(newRotation) > 70) {
					rotationSpeed = 280;
				} else if (Math.abs(newRotation) > 30) {
					travelDistance = getDistanceToTarget(newPosition);
					speed = getSpeed(travelDistance, newRotation);
					if (speed == 0)
						rotationSpeed = 400;
					else
						rotationSpeed = 300;
				} else if (Math.abs(newRotation) > 10) {
					travelDistance = getDistanceToTarget(newPosition);
					speed = getSpeed(travelDistance, newRotation);
					if (speed == 0)
						rotationSpeed = 200;
					else
						rotationSpeed = 150;
				} else {
					travelDistance = getDistanceToTarget(newPosition);
					speed = getSpeed(travelDistance, newRotation);
				}

				if (newRotation < 0)
					rotationSpeed *= -1;
				if (travelDistance < 300 && Math.abs(newRotation) > 10)
					speed = 0;
				output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0,
						true);
			}
		}
	}

	// public int getDistanceToTarget() {
	// double dx = (robot.getPosition().getX() - newPosition.getX());
	// double dy = (robot.getPosition().getY() - newPosition.getY());
	// return (int) Math.sqrt(dx * dx + dy * dy);
	// }

	public int getDistanceToTarget(Point target) {
		double dx = (robot.getPosition().getX() - target.getX());
		double dy = (robot.getPosition().getY() - target.getY());
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public int getSpeed(int distance, int rotationToDest) {
		int speed = 7000;
		if ((int) (distance / 1.6) < speed) {
			speed = (int) (distance / 1.0);
		}
		if (Math.abs(rotationToDest) > 10) {
			speed = 900;
			if ((int) (distance / 2) < speed)
				speed = (int) (distance / 2);
		}
		// if(Math.abs(rotationToDest) >= 1 ){
		// speed -= (int)((float)speed / ((float)90 /
		// (float)Math.abs(rotationToDest)));
		// }
		if (speed < 400)
			speed = 400;
		// if(Math.abs(rotationToDest) > 30){
		// speed = 0;
		// }
		// if(Math.abs(rotationToDest) > 10 && distance < 300)
		// speed = 0;

		//
		// if( Math.abs(rotationToDest) > 10 ){
		// // speed = 400;
		// speed /= (Math.abs(rotationToDest)/10);
		// if( (int)(distance/2) < speed)
		// speed = (int)(distance/2);
		// }

		return speed;
	}

}
