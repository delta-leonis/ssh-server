package robocup.gamepad;

import java.util.logging.Logger;

import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GamepadThread extends Thread {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private GamepadModel gamepadModel;

	private Controller gamepad;

	long dribbleButtonTime;
	long chipButtonTime;
	long kickButtonTime;

	private Component dribbleButton;
	private Component chipButton;
	private Component kickButton;
	private Component directionX;
	private Component directionY;
	private Component orientationX;
	private Component orientationY;
	private Component forceTrigger;

	private boolean useCamera;
	private boolean stop;

	public GamepadThread(GamepadModel gamepadModel, boolean useCamera) {
		this.gamepadModel = gamepadModel;
		this.useCamera = useCamera;
		dribbleButtonTime = System.currentTimeMillis();
		chipButtonTime = System.currentTimeMillis();
		kickButtonTime = System.currentTimeMillis();
		stop = false;
	}

	public void findController() {
		Controller[] controllersList = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (int i = 0; i < controllersList.length; i++)
			if (controllersList[i].getName().toLowerCase().contains("xbox 360")) {
				gamepadModel.setGamepad(controllersList[i]);
				gamepad = controllersList[i];
			}
	}

	private void initComponents() {
		if (gamepad == null) {
			LOGGER.warning("No controller to find components");
			System.out.println("kut");
			return;
		}
		Component[] components = gamepad.getComponents();
		for (int j = 0; j < components.length; j++) {
			switch (components[j].getIdentifier().getName()) {
			case "0":
				dribbleButton = components[j];
				break;
			case "1":
				chipButton = components[j];
				break;
			case "3":
				kickButton = components[j];
				break;
			case "x":
				directionX = components[j];
				break;
			case "y":
				directionY = components[j];
				break;
			case "rx":
				orientationX = components[j];
				break;
			case "ry":
				orientationY = components[j];
				break;
			case "z":
				forceTrigger = components[j];
				break;
			default:
				break;
			}
		}
	}

	private boolean calculateDribble() {
		boolean dribble = gamepadModel.isDribble();
        if(dribbleButton.getPollData() != 0.0f && System.currentTimeMillis() - dribbleButtonTime > 1000) {
            dribble = !dribble;
            dribbleButtonTime = System.currentTimeMillis();
        }
		return dribble;
	}

	private int calculateKickChip() {
		int chipKick = (int) (Math.round(Math.abs(forceTrigger.getPollData()*100)));
		if(kickButton.getPollData() != 0.0f && System.currentTimeMillis() - kickButtonTime > 1000) {
			kickButtonTime = System.currentTimeMillis();
		}
		if(chipButton.getPollData() != 0.0f && System.currentTimeMillis() - chipButtonTime > 1000) {
			chipKick = chipKick * -1;
			chipButtonTime = System.currentTimeMillis();
		}
		return chipKick;
	}

	private int calculateDirection() {
		int stickDirection = (int) Math.toDegrees(Math.atan2(directionY.getPollData(), directionX.getPollData()))*-1;
		if(stickDirection < 0)
			stickDirection = 360 + stickDirection;
		double robotAngle = gamepadModel.getRobot().getOrientation();
		double direction = robotAngle - stickDirection;
		if (direction > 180)
			direction -= 360;
		if (direction < -180)
			direction += 360;
		return (int) direction;
	}

	/**
	 * Calculates the radius of the left analog stick on the gamepad. This indicates the speed for the
	 * selected robot. Values are between (0 and 100)*30.
	 * @return 
	 */
	private int calculateSpeed() {
		int x = Math.round(directionX.getPollData()*100);
		int y = Math.round(directionY.getPollData()*100);
		int speed = (int) Math.sqrt(x*x+y*y)*30;
		return speed > 5 ? speed : 0;
	}

	/**
	 * Calculates the orientation of the right analog stick in degrees. The values are as used to be in the unity
	 * circle.
	 * @return {@link int} The orientation the orientation the gamepad gives via the right analog stick in degrees.
	 */
	private int calculateOrientation() {
		int orientation = (int) Math.toDegrees(Math.atan2(orientationY.getPollData(), orientationX.getPollData()))*-1;
		if(orientation < 0) {
			orientation = 360 + orientation;
		}
		return orientation;
	}

	/**
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * Precondition: -180 <= rotation <= 180
	 * @param rotation The rotation we want to make
	 * @param speed The speed of the {@link Robot}
	 * @return the speed at which the {@link Robot} should turn.
	 */
	private double getRotationSpeed(double rotation, double speed) {
		double DISTANCE_ROTATIONSPEED_COEFFICIENT = 12;
		int MAX_ROTATION_SPEED = 1000;
		int START_UP_SPEED = 100;
		double circumference = (Robot.DIAMETER * Math.PI);
		int MAX_VELOCITY =3000;
		double rotationPercent = rotation / 360;

		double rotationDistance = circumference * rotationPercent;
		rotationDistance *= DISTANCE_ROTATIONSPEED_COEFFICIENT;
		rotationDistance *= 1 - speed/(MAX_VELOCITY + 500);
		if(Math.abs(rotationDistance) > MAX_ROTATION_SPEED){
			if(rotationDistance < 0){
				rotationDistance = -MAX_ROTATION_SPEED;
			}
			else{
				rotationDistance = MAX_ROTATION_SPEED;
			}
		}
		if(rotationDistance < 0){
			return rotationDistance - START_UP_SPEED;
		}
		else{
			return rotationDistance + START_UP_SPEED;
		}
	}

	/**
	 * Initializes the controller and its components by calling {@link GamepadThread#findController()}
	 * and {@link GamepadThread#initComponents()}. Then stays in an loop that reads the gamepad and
	 * controls a robot by using the {@link ComInterface#send(int, int, int, int, int, int, boolean)} until
	 * {@link GamepadThread#stop} is true.
	 */
	@Override
	public void run() {
		findController();
		initComponents();
		gamepadModel.setRobot(World.getInstance().getGUI().getSelectedRobot());
		while (!stop) {
			gamepad.poll();

			int messageType = 1;
			int robotID = gamepadModel.getRobot().getRobotId();
			int direction = calculateDirection();
			int directionSpeed = calculateSpeed();
			int orientation = calculateOrientation();
			int rotationSpeed = (int) getRotationSpeed(orientation, directionSpeed);
			int shootKicker = calculateKickChip();
			boolean dribble = calculateDribble();
			System.out.println("messageType: " + messageType + 
							 "\nRobotId:     " + robotID +
							 "\nDirection:   " + direction +
							 "\nDirSpeed:    " + directionSpeed +
							 "\nOrientation: " + orientation +
							 "\nRotSpeed:    " + rotationSpeed +
							 "\nChipKick:    " + shootKicker +
							 "\nDribble:     " + dribble + "\n\n");
			ComInterface.getInstance().send(messageType, robotID, direction, directionSpeed, rotationSpeed, shootKicker, dribble);
			
			gamepadModel.setDirection(direction);
			gamepadModel.setSpeed(directionSpeed);
			gamepadModel.setOrientation(orientation);
			gamepadModel.setKickChip(shootKicker);
			gamepadModel.setDribble(dribble);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 'Beunhaas'-method to stop the {@link GamepadThread} because the old {@link Thread#stop()} is deprecated.
	 * @param stop Boolean that is true if the {@link GamepadThread} should stop.
	 */
	public void stop(boolean stop) {
		this.stop = stop;
	}
}
