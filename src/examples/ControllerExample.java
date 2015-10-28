package examples;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

import application.Services;
import controllers.ControllerHandler;
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

/**
 * Example to show how to create {@link ControllerHandler ControllerHandlers}, {@link ControllerLayout ControllerLayouts} and a {@link ControllerListener}.
 * 
 * @author Jeroen
 * @see ControllerHandler
 * @see ControllerLayout
 * @see ControllerListener
 *
 */
public class ControllerExample {
	//respective logger
	private static Logger logger = Logger.getLogger();

	public static void main(String[] args) throws InterruptedException {
		//register a new send method to monitor the output
		Communicator.register(SendMethod.DEBUG, new Debug(Level.INFO));

		//create the service for the controller
		ControllerListener listener = new ControllerListener(15); //15 = no. robots
		//add it to the servicehandler
		Services.addService(listener);
		
		//create 3 controller handlers
		for(int i = 0; i < 3; i++){
			//maybe find a controller that is available
			Optional<Controller> schrödingersController = findAvailableController("360");
	
			//check if we found one
			if(!schrödingersController.isPresent()) {
				logger.warning("No controller #%d present", i);
				break;
			}
	
			//create a layout for this specific controller
			ControllerLayout layout = new ControllerLayout(schrödingersController.get());
	
			//assign a bunch of buttons (note that the identifiernames are specific for Windows
			//since this is only a demo it shouldn't be much of a problem
			layout.attach(layout.getComponent(Component.Identifier.Button._1), ButtonFunction.KICK);
			layout.attach(layout.getComponent(Component.Identifier.Button._0), ButtonFunction.CHIP);
			layout.attach(layout.getComponent(Component.Identifier.Button._3), ButtonFunction.DRIBBLE);
			layout.attach(layout.getComponent(Component.Identifier.Axis.X), ButtonFunction.DIRECTION_X);
			layout.attach(layout.getComponent(Component.Identifier.Axis.Y), ButtonFunction.DIRECTION_Y);
			layout.attach(layout.getComponent(Component.Identifier.Axis.RY), ButtonFunction.ORIENTATION_X);
			layout.attach(layout.getComponent(Component.Identifier.Axis.RX), ButtonFunction.ORIENTATION_Y);
			layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.CHIPKICK_STRENGTH);
			layout.attach(layout.getComponent(Component.Identifier.Button._7), ButtonFunction.SELECT_NEXT_ROBOT);
			layout.attach(layout.getComponent(Component.Identifier.Button._6), ButtonFunction.SELECT_PREV_ROBOT);
			layout.attach(layout.getComponent(Component.Identifier.Button._8), ButtonFunction.STOP_ALL_ROBOTS);
			layout.attach(layout.getComponent(Component.Identifier.Axis.POV), ButtonFunction.DIRECTION_POV);
			
//			Stream.of(layout.getController().getComponents()).forEach(e -> System.out.println(e.getName() + ": " +e.getIdentifier().toString()));

			listener.register(i, layout); 	//i = robotid
			listener.unregister(0); 		//0 = robotid
		}

		//process every controller
		while(true){
			listener.processControllers();
			Thread.sleep(100);
		}
	}
	
	/**
	 * Find a controller that is not used in any ControllerHandler
	 * @param contains	part of the controllername
	 * @return maybe a controller
	 */
	public static Optional<Controller> findAvailableController(String contains){
		return Stream.of(ControllerEnvironment.getDefaultEnvironment().getControllers())
				// filter controllers that met the name conditions
				.filter(controller -> controller.getName().contains(contains))
				// filter controllers that are available
				.filter(controller -> availableController(controller))
				//find the first in the list
				.findFirst();
	}

	/**
	 * @param controller controller to check
	 * @return whether a controller is available
	 */
	private static boolean availableController(Controller controller) {
				//get the managing service
		return !((ControllerListener)Services.get("ControllerListener"))
				//check if it contains the controller
				.containsController(controller);
	}
}
