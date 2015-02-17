package robocup.model;

public class Enemy extends Robot {
	public Enemy(int robotID, boolean isKeeper, float height, double diameter) {
		super(robotID, isKeeper, height, diameter);
	}

	@Override
	public String toString() {
		return "Enemy" + super.toString();
	}
}
