package robocup.model;

public class Enemy extends Robot {
	public Enemy(int robotID, boolean isKeeper, float height) {
		super(robotID, isKeeper, height);
	}

	@Override
	public String toString() {
		return "Enemy" + super.toString();
	}
}
