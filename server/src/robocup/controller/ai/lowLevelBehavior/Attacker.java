package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;

public class Attacker extends LowLevelBehavior {

	private FieldPoint ballPosition;
	private int chipKick;
	private double shootDirection;

	/**
	 * Create an attacker.
	 * The attacker will try to get into a shooting position for the given shoot direction.
	 * @param robot the attacker {@link Robot} in the model.
	 * @param output Used to send data to the Robot
	 */
	public Attacker(Robot robot) {
		super(robot);
		shootDirection = 0.0;
		chipKick = 0;
		ballPosition = null;

		this.role = RobotMode.ATTACKER;
		go = new GotoPosition(robot, robot.getPosition(), ballPosition);
		go.setStartupSpeedVelocity(400);
		go.setMaxVelocity(1500);
		go.setDistanceToSlowDown(500);
		go.setMaxRotationSpeed(1000);
		go.setStartupSpeedRotation(100);
//		go.setAvoidEastGoal(true);
//		go.setAvoidWestGoal(true);
		
	}

	/**
	 * Update values
	 * @param shootDirection direction where the attacker needs to shoot, relative to the field. Values between -180 and 180. 0 degrees facing east. 90 degrees facing north.
	 * @param chipKick kick and chip strength in percentages. max kick = -100% , max chip = 100% . if chipKick = 0, do nothing
	 * @param ballPosition the position of the ball
	 */
	public void update(double shootDirection, int chipKick, FieldPoint ballPosition) {
		this.ballPosition = ballPosition;
		this.chipKick = chipKick;
		this.shootDirection = shootDirection;
	}

	@Override
	public void calculate() {
		if (robot.getPosition() != null && ballPosition != null) {
			FieldPoint newDestination = ballPosition;

			// find a shooting position
			newDestination = getShootingPosition(shootDirection, ballPosition, 100);
//
//			avoidBall = (Math.abs(robot.getOrientation() - robot.getPosition().getAngle(ballPosition)) > 5.0
//					|| robot.getPosition().getDeltaDistance(ballPosition) > 300); //HAD JE COMM + Robot.DIAMETER/2ENTAAR 
//			
//
			if(robot.getPosition().getDeltaDistance(ballPosition) < 500){
				go.setKick(chipKick);
			}


			changeDestination(newDestination);
		}
	}

	/**
	 * Change the destination of the robot
	 * @param newDestination the new destination
	 */
	private void changeDestination(FieldPoint newDestination) {
		go.setTarget(ballPosition);
		if(chipKick == 0){
			go.setDestination(newDestination);
			go.setMaxRotationSpeed(1000);
//			go.setMaxRotationSpeed(1400);
			go.setForcedSpeed(0);
			go.calculateTurnAroundTarget(250);
		}
		else{
			double overshootBallX = ballPosition.getX() + Math.cos(Math.toRadians(robot.getPosition().getAngle(ballPosition))) * 80;
			double overshootBallY = ballPosition.getY() + Math.sin(Math.toRadians(robot.getPosition().getAngle(ballPosition))) * 80;
			
			go.setDestination(new FieldPoint(overshootBallX, overshootBallY));
			go.setStartupSpeedVelocity(400);

			go.setForcedSpeed(1500); // 2000
//			go.goForwardUntilKick(3000);
			go.setMaxRotationSpeed(300);
			go.calculate(false, true);
		}
	}
}
