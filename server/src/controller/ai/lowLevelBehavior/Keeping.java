package controller.ai.lowLevelBehavior;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import model.Point;
import model.Robot;
import output.ComInterface;

public class Keeping extends LowLevelBehavior {

	private GotoPosition goToPosition;
	private Rectangle2D goalArea;

	public Keeping(Robot robot, ComInterface output) {
		super(robot, output);
		goalArea = world.getField().getGoalArea(robot.getTeam().getSide());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calculate() {
		// if (!checkIfInGoalRange()) {
		double x = goalArea.getMinX();
		double y = goalArea.getMaxY();
		System.out.println(x + ":" + y);
		Point p = new Point(x, y);
		
		
//		if (goToPosition == null) {
			goToPosition = new GotoPosition(robot, output, p);
//		}
		goToPosition.calculate();
		
		// }
	}

	public boolean checkIfInGoalRange() {
		String side = robot.getTeam().getSide();
		Area robotArea = new Area(robot.getArea());
		if (robotArea.intersects(world.getField().getGoalArea(side)))
			return true;
		return false;
	}
}