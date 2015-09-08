package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.input.protobuf.Referee.SSL_Referee.Command;
import robocup.model.FieldPoint;
import robocup.model.Referee;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;
import robocup.model.enums.TeamColor;

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
		go.setStartupSpeedVelocity(550);
		go.setMaxVelocity(1500);
		go.setDistanceToSlowDown(500);
		go.setMaxRotationSpeed(1300);
		go.setStartupSpeedRotation(180);
		Referee referee = World.getInstance().getReferee();
		if(	referee.getPreviousCommand().equals(Command.PREPARE_PENALTY_YELLOW) && referee.getAllyTeamColor().equals(TeamColor.YELLOW)
				||
			referee.getPreviousCommand().equals(Command.PREPARE_PENALTY_BLUE) && referee.getAllyTeamColor().equals(TeamColor.BLUE)){
			go.setAvoidEastGoal(false);
			go.setAvoidWestGoal(false);
		}
		else{
			go.setAvoidEastGoal(true);
			go.setAvoidWestGoal(true);
		}
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
			if (robot.getPosition().getDeltaDistance(ballPosition) < 500 && isValidOrientation()) {
				go.setKick(chipKick);
			}
			else{
				go.setKick(0);
			}

			changeDestination(newDestination);
		}
	}

	private boolean isValidOrientation() {
		double orientation = robot.getOrientation();
		// correct orientation so you can compare it
		orientation = orientation < 0 ? orientation + 360 : orientation;

		double correctedShootDirection = shootDirection < 0 ? shootDirection + 360 : shootDirection;

		double ballToRobot = robot.getPosition().getAngle(ballPosition);
		ballToRobot = ballToRobot < 0 ? ballToRobot + 360 : ballToRobot;
				
		boolean orientationOkay = Math.abs(orientation - correctedShootDirection) < 2000 / robot.getPosition().getDeltaDistance(ballPosition);

		boolean positionOkay = Math.abs(correctedShootDirection - ballToRobot) < 3000 / robot.getPosition().getDeltaDistance(ballPosition);

		return orientationOkay && positionOkay;
	}

	/**
	 * Change the destination of the robot
	 * @param newDestination the new destination
	 */
	private void changeDestination(FieldPoint newDestination) {
		go.setTarget(ballPosition);
		if (go.getChipKick() == 0) {
			go.setDestination(newDestination);
			go.setMaxRotationSpeed(1300);
			go.setForcedSpeed(0);
			go.calculateTurnAroundTarget(300);
			go.setGoStraightForward(false);
		}
		else{
			double overshootBallX = ballPosition.getX() + Math.cos(Math.toRadians(robot.getPosition().getAngle(ballPosition))) * 80;
			double overshootBallY = ballPosition.getY() + Math.sin(Math.toRadians(robot.getPosition().getAngle(ballPosition))) * 80;
			
			go.setDestination(new FieldPoint(overshootBallX, overshootBallY));

			go.setForcedSpeed(2000); // 2000
//			go.goForwardUntilKick(3000);
			go.setMaxRotationSpeed(300);
			go.setGoStraightForward(true);
			go.calculate(0, true);
		}
	}
}
