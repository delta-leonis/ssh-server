package robocup.model;

public class Enemy extends Robot {
	public Enemy(int robotID, boolean isKeeper, float height, Team team) {
		super(robotID, isKeeper, height, team);
	}

	@Override
	public String toString() {
		return "Enemy" + super.toString();
	}
}
