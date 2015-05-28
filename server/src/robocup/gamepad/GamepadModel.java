package robocup.gamepad;

import robocup.model.Robot;
import net.java.games.input.Controller;

public class GamepadModel {

	private GamepadThread gamepadThread;

	private Controller gamepad;
	private Robot robot;

	private boolean dribble;
	private int kickChip;
	private int direction;
	private int speed;
	private int orientation;

	public GamepadModel() {
		gamepadThread = new GamepadThread(this, true);
		gamepadThread.findController();
	}

	public boolean isDribble() {
		return dribble;
	}

	public void setDribble(boolean dribble) {
		this.dribble = dribble;
	}

	public int getKickChip() {
		return kickChip;
	}

	public void setKickChip(int kickChip) {
		this.kickChip = kickChip;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Controller getGamepad() {
		return gamepad;
	}

	public void setGamepad(Controller gamepad) {
		this.gamepad = gamepad;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public GamepadThread getGamepadThread() {
		return gamepadThread;
	}

	public void setGamepadThread(GamepadThread gamepadThread) {
		this.gamepadThread = gamepadThread;
	}

}
