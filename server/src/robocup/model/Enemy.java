package robocup.model;
/**
 * Enemy instance of {@link Robot}<br>
 * Used for differentiating teams without lookups and interference of {@link World}
 */
public class Enemy extends Robot {
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
}
