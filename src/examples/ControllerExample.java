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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;

/**
 * Example to show how to create {@link ControllerHandler ControllerHandlers},
 * {@link ControllerLayout ControllerLayouts} and a {@link ControllerListener}.
 *
 * @author Jeroen de Jong
 * @author Thomas Hakkers
 * @see ControllerHandler
 * @see ControllerLayout
 * @see ControllerListener
 *      
 */
public class ControllerExample extends Application{
    
    // respective logger
    private static final Logger LOG = Logger.getLogger();
    
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
    public static Optional<Controller> findAvailableController() {
        return Stream.of(ControllerEnvironment.getDefaultEnvironment().getControllers())
                // filter controllers that are available
                .filter(controller -> ControllerExample.availableController(controller))
                // find the first in the list
                .findFirst();
    }
    
    private static boolean createDefaultLayout(ControllerLayout layout){
        // assign a bunch of buttons (note that the identifiernames are specific for Windows
        // since this is only a demo it shouldn't be much of a problem
        if(layout.getController().getType() == Type.GAMEPAD){
            layout.attach(layout.getComponent(Component.Identifier.Button._1), ButtonFunction.KICK);
            layout.attach(layout.getComponent(Component.Identifier.Button._0), ButtonFunction.CHIP);
            layout.attach(layout.getComponent(Component.Identifier.Button._3), ButtonFunction.DRIBBLE_TOGGLE);
            layout.attach(layout.getComponent(Component.Identifier.Axis.X), ButtonFunction.DIRECTION_X);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Y), ButtonFunction.DIRECTION_Y);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.DIRECTION_Y);
            layout.attach(layout.getComponent(Component.Identifier.Axis.RY), ButtonFunction.ORIENTATION_X);
            layout.attach(layout.getComponent(Component.Identifier.Axis.RX), ButtonFunction.ORIENTATION_Y);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.CHIP_STRENGTH);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.KICK_STRENGTH);
            layout.attach(layout.getComponent(Component.Identifier.Button._7), ButtonFunction.SELECT_NEXT_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Button._6), ButtonFunction.SELECT_PREV_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Button._8), ButtonFunction.STOP_ALL_ROBOTS);
            layout.attach(layout.getComponent(Component.Identifier.Axis.POV), ButtonFunction.DIRECTION_POV);
        }
        else if(layout.getController().getType() == Type.KEYBOARD){
            layout.attach(layout.getComponent(Component.Identifier.Key.F), ButtonFunction.KICK);
            layout.attach(layout.getComponent(Component.Identifier.Key.G), ButtonFunction.CHIP);
            layout.attach(layout.getComponent(Component.Identifier.Key.H), ButtonFunction.DRIBBLE_TOGGLE);
            layout.attach(layout.getComponent(Component.Identifier.Key.A), ButtonFunction.DIRECTION_LEFT);
            layout.attach(layout.getComponent(Component.Identifier.Key.D), ButtonFunction.DIRECTION_RIGHT);
            layout.attach(layout.getComponent(Component.Identifier.Key.W), ButtonFunction.DIRECTION_FORWARD);
            layout.attach(layout.getComponent(Component.Identifier.Key.S), ButtonFunction.DIRECTION_BACKWARD);
            layout.attach(layout.getComponent(Component.Identifier.Key.Q), ButtonFunction.ORIENTATION_WEST);
            layout.attach(layout.getComponent(Component.Identifier.Key.E), ButtonFunction.ORIENTATION_EAST);
            layout.attach(layout.getComponent(Component.Identifier.Key._2), ButtonFunction.SELECT_NEXT_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Key._1), ButtonFunction.SELECT_PREV_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Key.SPACE), ButtonFunction.STOP_ALL_ROBOTS);
        }
        else{
            return false;
        }
        layout.saveAsDefault();
        return true;
    }
    
    public static void main(final String[] args) throws InterruptedException {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        
        radioConsumer.attachToCompatiblePipelines();
//        // add a consumer voor radiopackets
//        Pipelines.get("communication pipeline").get().registerConsumer(radioConsumer);
        
        // create the service for the controller
        final ControllerListener listener = new ControllerListener(); 
        // add it to the servicehandler
        Services.add(listener);
        
        // create 3 controller handlers
        for (int i = 0; i < 5; i++) {
            // maybe find a controller that is available
            final Optional<Controller> controller = ControllerExample.findAvailableController();
            
            // check if we found one
            if (!controller.isPresent()) {
                ControllerExample.LOG.warning("No controller #%d present", i);
                break;
            }
            System.out.println("Controller: " + controller.get().getName());
            // create a layout for this specific controller
            final ControllerLayout layout = new ControllerLayout(controller.get());
            if(!Models.initialize(layout)){
                ControllerExample.createDefaultLayout(layout);
                System.out.println("Default! " + controller.get().getName());
            }
            
            System.out.println("Bindings Example: " + layout.bindings);
            
            listener.register(i, layout); // i = robotid
//            listener.unregister(0); // 0 = robotid
        }
        
        // Create a scene so that we can actually use the keyboard
        primaryStage.setScene(new Scene(new BorderPane(), 450, 450));
        primaryStage.show();
        
        new Thread(() -> {
            // process every controller
            while (true) {
                listener.processControllers();
                try {
                    Thread.sleep(100);
                }
                catch (Exception exception) {
                    ControllerExample.LOG.exception(exception);
                }
            }
        }).start();
    }
}
