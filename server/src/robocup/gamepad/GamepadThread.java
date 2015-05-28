package robocup.gamepad;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Gamepad {

	Controller controller;

	public Gamepad() {
		findController();
	}
	
	private void findController() {
		// get the controller
		Controller[] controllersList = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (int i = 0; i < controllersList.length; i++)
			if (controllersList[i].getName().equals("XBOX 360 For Windows (Controller)"))
				controller = controllersList[i];

		// get the components of the controller
		if (controller == null)
			return;
		Component[] components = controller.getComponents();
		for (int j = 0; j < components.length; j++) {
			System.out.println(components[j].getName());
		}
	}
}
