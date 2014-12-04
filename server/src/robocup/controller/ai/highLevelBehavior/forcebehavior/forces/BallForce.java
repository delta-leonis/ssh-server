package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Ball;
import robocup.model.Point;

public class BallForce extends Force {

	private Ball ball;

	public BallForce(Ball ball, int power, int scope) {
		super(ball.getPosition(), power, scope);
		this.ball = ball;
	}

	public boolean affectsPoint(Point position) {
		return super.affectsPoint(position);
	}

	public int getDirection(Point position) {
		return power > 0 ? position.getAngle(ball.getPosition()) : ball.getPosition().getAngle(position);
	}
}
