package controller.ai.lowLevelBehavior;

import output.ComInterface;
import model.Point;
import model.Robot;
import model.World;

public abstract class LowLevelBehavior {
	protected World world;
	protected Robot robot;
	protected ComInterface output;
	
	public LowLevelBehavior(Robot robot, ComInterface output){
		this.world = World.getInstance();
		this.robot = robot;
		this.output = output;
	}
	public abstract void calculate();
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	public int rotationToDest(Point newPoint){
		//angle vector between old and new
		double dy = newPoint.getY() - robot.getPosition().getY();
		double dx = newPoint.getX() - robot.getPosition().getX();
		double newRad = Math.atan2(dy, dx);
		int rot = (int)(Math.toDegrees(newRad) - robot.getOrientation());
		if( rot > 180 )
			rot -= 360;
		if (rot <= -180 )
			rot += 360;
		return  rot;
	}
}