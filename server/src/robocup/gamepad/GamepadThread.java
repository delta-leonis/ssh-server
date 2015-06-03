package robocup.gamepad;

import java.util.logging.Logger;

import robocup.Main;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
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
	private Component selectButton;

	private boolean useCamera;
	private boolean stop;
	private long selectButtonTime;

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
			if (controllersList[i].getName().toLowerCase().contains("360")) {
				gamepadModel.setGamepad(controllersList[i]);
				gamepad = controllersList[i];
			}
	}

	private void initComponents() {
		if (gamepad == null) {
			LOGGER.warning("No controller to find components");
			return;
		}
		Component[] components = gamepad.getComponents();
		for (int j = 0; j < components.length; j++) {
			switch (components[j].getIdentifier().getName()) {
			case "0":
			case "A":
				kickButton = components[j];
				break;
			case "B":
			case "1":
				chipButton = components[j];
				break;
			case "3":
			case "Y":
				dribbleButton = components[j];
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
			case "rz":
				forceTrigger = components[j];
				break;
			case "Select":
				selectButton = components[j];
				break;
			default:
				System.out.println("unassigned button: " + components[j].getIdentifier().getName());
				break;
			}
		}
	}

	private boolean calculateDribble() {
		boolean dribble = gamepadModel.isDribble();
        if(dribbleButton.getPollData() != 0.0f && System.currentTimeMillis() - dribbleButtonTime > 1000) {
            dribble = !dribble;
            gamepadModel.setDribble(dribble);
            dribbleButtonTime = System.currentTimeMillis();
        }
		return dribble;
	}

	private int calculateKickChip() {
		int chipKick = 0;
		if(kickButton.getPollData() > 0.1f && System.currentTimeMillis() - kickButtonTime > 250) {
			chipKick = (int) (Math.round(Math.abs(forceTrigger.getPollData()*100)));
			kickButtonTime = System.currentTimeMillis();
		}
		if(chipButton.getPollData() > 0.1f && System.currentTimeMillis() - chipButtonTime > 250) {
			chipKick = (int) (Math.round(Math.abs(forceTrigger.getPollData()*100))) * -1;
			chipButtonTime = System.currentTimeMillis();
		}
		return chipKick;
	}
	
	private void selectNextRobot(){
		if(selectButton.getPollData() > 0.1f && System.currentTimeMillis() - selectButtonTime > 250){
			World.getInstance().getGUI().selectNextRobot();
			selectButtonTime = System.currentTimeMillis();
		}
	}

	/**
	 * Calculates the radius of the left analog stick on the gamepad. This indicates the speed for the
	 * selected robot. Values are between (0 and 100)*20.
	 * @return 
	 */
	private int calculateSpeed() {
		int x = Math.round(directionX.getPollData()*100);
		int y = Math.round(directionY.getPollData()*100);
		int speed = (int) Math.sqrt(x*x+y*y)*20;
		return speed > 5 ? speed : 0;
	}

	private FieldPoint calculateDestination(Robot robot) {
		double dx = directionX.getPollData() * 100;
		double dy = directionY.getPollData() * -100;
		if(Math.abs(dx) < 10 && Math.abs(dy) < 10) {
			return null;
		} else {
			if (robot.getPosition() != null) {
				double x = robot.getPosition().getX() + dx * 2;
				double y = robot.getPosition().getY() + dy * 2;
				return new FieldPoint(x, y);
			}
		}
		return null;
	}

	private FieldPoint calculateTarget(Robot robot) {
		double dx = orientationX.getPollData() * 100;
		double dy = orientationY.getPollData() * -100;
		if(Math.abs(dx) < 10 && Math.abs(dy) < 10) {
			return null;
		} else {
			if (robot.getPosition() != null) {
				double x = robot.getPosition().getX() + dx * 2;
				double y = robot.getPosition().getY() + dy * 2;
				return new FieldPoint(x, y);
			}
		}
		return null;
	}

	/**
	 * Initializes the controller and its components by calling {@link GamepadThread#findController()}
	 * and {@link GamepadThread#initCom
INFO: Failed to open device (/dev/input/event5): Failed to open device /dev/input/event5 (13)ponents()}. Then stays in a loop that reads the gamepad and
	 * controls a robot by using {@link GotoPosition#calculateWithoutPathPlanner(int)} until
	 * {@link GamepadThread#stop} is true.
	 */
	@Override
	public void run() {
		findController();
		initComponents();
		gamepadModel.setRobot(World.getInstance().getGUI().getSelectedRobot());
		while (!stop) {
			gamepad.poll();

			Robot robot = gamepadModel.getRobot();
			FieldPoint destination = calculateDestination(robot);
			FieldPoint target = calculateTarget(robot);
			int speed = calculateSpeed();
//			selectNextRobot();
			GotoPosition goPos = new GotoPosition(robot, destination, target);
			goPos.calculateWithoutPathPlanner(speed, calculateKickChip(), calculateDribble());

			try {
				Thread.sleep(1000/60);
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
