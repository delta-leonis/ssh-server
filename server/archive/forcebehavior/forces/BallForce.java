package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Ball;
import robocup.model.FieldPoint;

public class BallForce extends Force {

	private Ball ball;

	/**
	 * Create a BallForce, pulling towards the ball
	 * @param ball the ball
	 * @param power the power
	 * @param scope the scope
	 */
	public BallForce(Ball ball, int power, int scope) {
		super(ball.getPosition(), power, scope);
		this.ball = ball;
	}

	/**
	 * Calculate if a point is affected by this force
	 * 	true when the distance of the ball to the point is within the scope
	 */
	public boolean affectsPoint(FieldPoint position) {
		return super.affectsPoint(position);
	}

	/**
	 * If the power is positive use the angle from the position to the ball (pulling towards the ball)
	 * If the power is negative use the angle from the ball to the position (pushing away from the ball)
	 */
	public int getDirection(FieldPoint position) {
		return (int) (power > 0 ? position.getAngle(ball.getPosition()) : ball.getPosition().getAngle(position));
	}
}
