package examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import org.ssh.controllers.ControllerHandler;
import org.ssh.controllers.ControllerLayout;
import org.ssh.controllers.ControllerListener;
import org.ssh.controllers.ControllerSettings;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.ButtonFunction;
import org.ssh.models.enums.SendMethod;
import org.ssh.network.transmit.radio.RadioPipeline;
import org.ssh.network.transmit.radio.couplers.VerboseCoupler;
import org.ssh.network.transmit.senders.LegacyUDPSender;
import org.ssh.util.Logger;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Example to show how to create {@link ControllerHandler ControllerHandlers},
 * {@link ControllerLayout ControllerLayouts} and a {@link ControllerListener}.
 *
 * @author Jeroen de Jong
 * @author Thomas Hakkers
 * @see ControllerHandler
 * @see ControllerLayout
 * @see ControllerListener
 */
public class ControllerExample extends Application {

    // respective logger
    private static final Logger LOG = Logger.getLogger();
    // Ip address of the basestation
    private static final String ADDRESS = "224.5.23.20";

    public static void main(final String[] args) throws InterruptedException {
        Application.launch();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Logger.getLogger("org.ssh").setUseParentHandlers(false);
        Logger.getLogger("org.ssh.network").setUseParentHandlers(true);
        // make models available
        Models.start();
        // Create the settings for the Controller.
        Models.create(ControllerSettings.class);
        // Make the Network available
        Network.start();
        // make services available
        Services.start();
        Pipelines.start();
        new VerboseCoupler();
        // create a comminucation pipeline
        new RadioPipeline("communication pipeline").setRoute("verbosecoupler");

        Network.register(SendMethod.UDP, new LegacyUDPSender(ADDRESS, 1337));

        // create the service for the controller
        final ControllerListener listener = new ControllerListener();
        // add it to the servicehandler
        Services.add(listener);

        // create 3 controller handlers
        for (int i = 0; i < 3; i++) {
            // maybe find a controller that is available
            final Optional<Controller> controller = listener.findAvailableController("360");

            // check if we found one
            if (!controller.isPresent()) {
                ControllerExample.LOG.warning("No controller #%d present", i);
                break;
            }
            // create a layout for this specific controller
            final ControllerLayout layout = new ControllerLayout(controller.get());
            if (!Models.initialize(layout)) {
                ControllerLayout.createDefaultLayout(layout);
            }

            listener.register(i, layout); // i = robotid
            //            listener.unregister(0); // 0 = robotid
        }

        // Create a scene so that we can actually use the keyboard
        primaryStage.setScene(new Scene(new BorderPane(), 450, 450));
        primaryStage.show();

        Services.submitTask("ControllerExample poller", () -> {
            // process every controller
            while (true) {
                listener.processControllers();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }
        });
    }
}
