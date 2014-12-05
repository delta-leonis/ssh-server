package robocup.model;

public class Enemy extends Robot {
	public Enemy(int robotID, boolean isKeeper, float height, double diameter, Team team) {
		super(robotID, isKeeper, height, diameter, team);
	}

	@Override
	public String toString() {
		return "Enemy" + super.toString();
	}
}
