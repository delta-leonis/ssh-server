package examples;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

import controllers.ControllerLayout;
import controllers.ControllerListener;
import model.enums.ButtonFunction;
import model.enums.SendMethod;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import output.Communicator;
import output.Debug;
import util.Logger;

public class ControllerExample {
	private static ControllerLayout layout;
	private static Logger logger = Logger.getLogger();

	public static void main(String[] args) throws InterruptedException {
		Communicator.register(SendMethod.HOMING_PIGEON, new Debug(Level.INFO));
		
		Optional<Controller> schrödingersController = findController("360");

		if(!schrödingersController.isPresent()) {
			logger.warning("No controller present");
			return;
		}

		layout = new ControllerLayout(schrödingersController.get());

		layout.attach(getComponent("1") .get(), ButtonFunction.KICK);
		layout.attach(getComponent("0") .get(), ButtonFunction.CHIP);
		layout.attach(getComponent("3") .get(), ButtonFunction.DRIBBLE);
		layout.attach(getComponent("x") .get(), ButtonFunction.DIRECTION_X);
		layout.attach(getComponent("y") .get(), ButtonFunction.DIRECTION_Y);
		layout.attach(getComponent("rx").get(), ButtonFunction.ORIENTATION_X);
		layout.attach(getComponent("ry").get(), ButtonFunction.ORIENTATION_Y);
		layout.attach(getComponent("z") .get(), ButtonFunction.CHIPKICK_STRENGTH);
		layout.attach(getComponent("7") .get(), ButtonFunction.SELECT_NEXT_ROBOT);
		layout.attach(getComponent("6") .get(), ButtonFunction.SELECT_PREV_ROBOT);
		layout.attach(getComponent("8") .get(), ButtonFunction.STOP_ALL_ROBOTS);

		ControllerListener listener = new ControllerListener(15); //15 = no. robots
		
		listener.register(0, layout); 	//0 = robotid
		
		while(true){
			listener.processControllers();
			Thread.sleep(100);
		}
	}
	
	public static Optional<Component> getComponent(String identifier){
		return Stream.of(layout.getController().getComponents())
				.filter(component -> component.getIdentifier().getName().equals(identifier))
				.findFirst();
	}
	
	public static Optional<Controller> findController(String contains) {
		return Stream.of(ControllerEnvironment.getDefaultEnvironment().getControllers())
				.filter(controller -> controller.getName().contains(contains))
				.findFirst();
	}
}
