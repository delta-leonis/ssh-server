package robocup.model;

import java.awt.Graphics2D;

/**
 * Enemy instance of {@link Robot}<br>
 * Used for differentiating teams without lookups and interference of {@link World}
 */
public class Enemy extends Robot implements Drawable {
	/**
	 * @param robotID	reference ID for the robot
	 * @param height	height of the robot
	 */
	public Enemy(int robotID, double height) {
		super(robotID, height);
	}

	@Override
	public String toString() {
		return "Enemy" + super.toString();
	}
	
	@Override
	public void paint(Graphics2D g, FieldPoint origin) {
		g.setColor(World.getInstance().getReferee().getEnemyTeamColor().toColor());
		super.paint(g, origin);
	}
}
