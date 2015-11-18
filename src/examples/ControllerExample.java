package examples;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.ssh.controllers.ControllerHandler;
import org.ssh.controllers.ControllerLayout;
import org.ssh.controllers.ControllerListener;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.ButtonFunction;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.senders.DebugSender;
import org.ssh.services.consumers.RadioPacketConsumer;
import org.ssh.services.producers.Communicator;
import org.ssh.util.Logger;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Example to show how to create {@link ControllerHandler ControllerHandlers},
 * {@link ControllerLayout ControllerLayouts} and a {@link ControllerListener}.
 *
 * @author Jeroen
 * @see ControllerHandler
 * @see ControllerLayout
 * @see ControllerListener
 *      
 */
public class ControllerExample {
    
    // respective logger
    private static Logger logger = Logger.getLogger();
    
    /**
     * @param controller
     *            controller to check
     * @return whether a controller is available
     */
    private static boolean availableController(final Controller controller) {
        // get the managing service
        return !((ControllerListener) Services.get("ControllerListener").get())
                // check if it contains the controller
                .containsController(controller);
    }
    
    /**
     * Find a controller that is not used in any ControllerHandler
     * 
     * @param contains
     *            part of the controllername
     * @return maybe a controller
     */
    public static Optional<Controller> findAvailableController(final String contains) {
        return Stream.of(ControllerEnvironment.getDefaultEnvironment().getControllers())
                // filter controllers that met the name conditions
                .filter(controller -> controller.getName().contains(contains))
                // filter controllers that are available
                .filter(controller -> ControllerExample.availableController(controller))
                // find the first in the list
                .findFirst();
    }
    
    public static void main(final String[] args) throws InterruptedException {
        // make models available
        Models.start();
        // make services available
        Services.start();
        // create a comminucation pipeline
        Pipelines.add(new RadioPipeline("communication pipeline"));
        // create communicator (producer for RadioPackets)
        Services.add(new Communicator());
        
        final RadioPacketConsumer radioConsumer = new RadioPacketConsumer();
        radioConsumer.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
        
        // add a consumer voor radiopackets
        Pipelines.get("communication pipeline").get().registerConsumer(radioConsumer);
        
        // create the service for the controller
        final ControllerListener listener = new ControllerListener(15); // 15 = no. robots
        // add it to the servicehandler
        Services.add(listener);
        
        // create 3 controller handlers
        for (int i = 0; i < 3; i++) {
            // maybe find a controller that is available
            final Optional<Controller> schrödingersController = ControllerExample.findAvailableController("360");
            
            // check if we found one
            if (!schrödingersController.isPresent()) {
                ControllerExample.logger.warning("No controller #%d present", i);
                break;
            }
            
            // create a layout for this specific controller
            final ControllerLayout layout = new ControllerLayout(schrödingersController.get());
            
            // assign a bunch of buttons (note that the identifiernames are specific for Windows
            // since this is only a demo it shouldn't be much of a problem
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
            
            // Stream.of(layout.getController().getComponents()).forEach(e ->
            // System.out.println(e.getName() + ": " +e.getIdentifier().toString()));
            
            listener.register(i, layout); // i = robotid
            listener.unregister(0); // 0 = robotid
        }
        
        // process every controller
        while (true) {
            listener.processControllers();
            Thread.sleep(100);
        }
    }
}
