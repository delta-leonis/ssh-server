package robocup.controller.ai.lowLevelBehavior;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class FuckRobot extends LowLevelBehavior {

	protected Point ballPosition;
	protected int distanceToOponent;
	protected Point oponentPosition;
	protected Point defenderPosition;
	
	/**
	 * Create a defender (stand between "target" enemy and ball)
	 * @param robot
	 * @param output
	 * @param distanceToOponent defense radius size, 40?? ideal in most situations
	 * @param goToKick if true, move to ball and kick it away
	 * @param ballPosition current position of the ball
	 * @param defenderPosition current position of the defender (this)
	 * @param oponentPosition center position of the oponent
	 */
	public FuckRobot(Robot robot, ComInterface output, int distanceToOponent, boolean goToKick, Point ballPosition,
			Point defenderPosition, Point oponentPosition) {
		super(robot, output);
		
		this.oponentPosition = oponentPosition;
		this.ballPosition = ballPosition;
		this.distanceToOponent = distanceToOponent;
		this.defenderPosition = defenderPosition;
		go = new GotoPosition(robot, output, defenderPosition, oponentPosition, 400);
	}
	
	
	public void update(int distanceToOponent, boolean goToKick, Point ballPosition, Point defenderPosition, Point oponentPosition) {
		this.distanceToOponent = distanceToOponent;
		//this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.defenderPosition = defenderPosition;
		this.oponentPosition = oponentPosition;
	}
	
	@Override
	public void calculate() {
		
		if(timeOutCheck()) {
			
		} else {
			
			Point newDestination = getNewDestination();
			
			if(newDestination != null) {
				/*if(goToKick)
					go.setGoal(ballPosition);//GotoPosition(keeperPosition, ballPosition, ballPosition)
				else
					go.setGoal(newDestination);//GotoPosition(keeperPosition, newDestination, ballPosition)
			 	*/
				go.setGoal(newDestination);
				//go.setTarget(newDestination);
				go.setTarget(ballPosition);
				go.calculate();
			}
			
			
		}

	}

	private Point getNewDestination() {
		Point newDestination = null;
		
		if(ballPosition != null) {
			
			int angle = Math.abs(oponentPosition.getAngle(ballPosition));
			int realAngle = angle > 90 ? 180 - angle : angle;
			
			//System.out.println(" Angle:" + angle + " Reala: " + realAngle + " disto" + distanceToOponent);
			double dx = Math.sin(Math.toRadians(realAngle)) * distanceToOponent;
			double dy = Math.sqrt(distanceToOponent * distanceToOponent - dx * dx);
			
			int oponentX = (int) oponentPosition.getX();
			int destX = 0;
			//System.out.println("balX" + ballPosition.getX() + " opX:" + oponentPosition.getX());
			if(ballPosition.getX() < 0) {
				if(ballPosition.getX() < oponentPosition.getX()) {
					destX = (int) (oponentX - dx);
				} else {
					destX = (int) (oponentX + dx);
				}
			} else {
				if(ballPosition.getX() > oponentPosition.getX()) {
					destX = (int) (oponentX + dx);
				} else {
					destX = (int) (oponentX - dx);
				}
			}
			//int destX = (int) (ballPosition.getX() > oponentPosition.getX() ? oponentX - dx : oponentX + dx);
			
			int oponentY = (int) oponentPosition.getY();
			int destY = 0 ;
			
			if(ballPosition.getY() < 0) {
				if(ballPosition.getY() < oponentPosition.getY()) {
					destY = (int) (oponentY - dy);
				} else {
					destY = (int) (oponentY + dy);
				}
			} else {
				if(ballPosition.getY() > oponentPosition.getY()) {
					destY = (int) (oponentY + dy);
				} else {
					destY = (int) (oponentY - dy);
				}
			}
			
			
			newDestination = new Point(destX, destY);
			//System.out.println(" dx:" + dx + " dy: " + dy);
			//System.out.println(newDestination);
			//System.out.println(ballPosition + " " +newDestination);
		}

		return newDestination;
	}
	
}
