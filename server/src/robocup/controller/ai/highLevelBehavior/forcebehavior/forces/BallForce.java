package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Ball;
import robocup.model.Point;

public class BallForce extends Force {

	public BallForce(Ball ball, int power, int scope) {
		super(ball.getPosition(), power, scope);
	}

	public boolean affectsPoint(Point position) {
		
		return false;
	}
}
