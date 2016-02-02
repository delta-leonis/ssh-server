package org.ssh;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import net.java.games.input.Controller;
import org.ssh.controllers.ControllerLayout;
import org.ssh.controllers.ControllerListener;
import org.ssh.controllers.ControllerSettings;
import org.ssh.managers.manager.*;
import org.ssh.models.*;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.models.enums.SendMethod;
import org.ssh.network.receive.detection.DetectionPipeline;
import org.ssh.network.receive.detection.consumers.DetectionModelConsumer;
import org.ssh.network.receive.geometry.GeometryPipeline;
import org.ssh.network.receive.geometry.consumers.GeometryModelConsumer;
import org.ssh.network.receive.referee.RefereePipeline;
import org.ssh.network.receive.referee.consumers.RefereeModelConsumer;
import org.ssh.network.receive.wrapper.WrapperPipeline;
import org.ssh.network.receive.wrapper.consumers.WrapperConsumer;
import org.ssh.network.transmit.radio.couplers.VerboseCoupler;
import org.ssh.network.transmit.senders.DebugSender;
import org.ssh.network.transmit.senders.LegacyUDPSender;
import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.ui.components.widget.TestWidget;
import org.ssh.util.Logger;
import protobuf.Detection;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * The Class Main.
 */
public class Main extends Application {
    private static final Logger LOG = Logger.getLogger("org.ssh");
    // Ip address of the basestation
    private static final String ADDRESS = "224.5.23.20";

    /**
     * The main method.
     *
     * @param arg Command line arguments
     */
    public static void main(final String[] arg) {
        /** java fx start **/
        Application.launch(arg);
    }

    /**
     * Create essential {@link AbstractModel models}, {@link AbstractPipeline pipelines} and {@link org.ssh.services.AbstractService services}
     */
    private void build() {

        // Create the settings for the Controller.
        Models.create(ControllerSettings.class);

        // Creating models
        Models.create(Game.class);
        Models.create(Goal.class, Allegiance.ALLY);
        Models.create(Goal.class, Allegiance.OPPONENT);
        Models.create(Team.class, Allegiance.ALLY);
        Models.create(Team.class, Allegiance.OPPONENT);
        Models.create(Referee.class);
        Models.create(Field.class);

        // Create some robots
        IntStream.range(0, 8).forEach(id -> {
            Models.create(Robot.class, id, Allegiance.OPPONENT);
            Models.create(Robot.class, id, Allegiance.ALLY);
        });

        new WrapperPipeline("Wrappahrs");
        // make a pipeline
        new GeometryPipeline("fieldbuilder");
        // Create new detection pipeline
        new DetectionPipeline("detection");
        // Create a new Referee pipeline
        new RefereePipeline("referee");

        // make splitter from wrapper -> geometry / detection
        new WrapperConsumer().attachToCompatiblePipelines();
        new GeometryModelConsumer("oome geo").attachToCompatiblePipelines();
        new DetectionModelConsumer("oome decto").attachToCompatiblePipelines();
        new RefereeModelConsumer("oome ronald").attachToCompatiblePipelines();
    }

    /**
     * Test method
     */
    private static void createWidgets() {
        for (int i = 0; i < 25; i++)
            UI.addWidget(new TestWidget("widget " + i));
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        //Main.LOG.setUseParentHandlers(false);
        Main.LOG.info("Starting software");

        // start the managers
        Services.start();
        Models.start();
        Pipelines.start();
        new VerboseCoupler();
        Network.start();

        build();

        UI.start(primaryStage);

        /********************************/
        /* Below is just for testing!!! */
        /********************************/
        createWidgets();

        Network.register(SendMethod.UDP, new LegacyUDPSender(ADDRESS, 1337));
        Network.register(SendMethod.DEBUG, new DebugSender(Level.INFO));
        Network.listenFor(WrapperPacket.class);
    }

    /**
     * Start listening for new usb controllers
     */
    private void startControllers(){
        // create the service for the controller
        ControllerListener listener = new ControllerListener();

        Services.scheduleTask("USB watcher", () -> {
            System.out.println("Searching for new USB controllers");
            final Optional<Controller> controller = listener.findAvailableController("360");

            // check if we found one
            if (!controller.isPresent())
                return;
            // create a layout for this specific controller
            final ControllerLayout layout = new ControllerLayout(controller.get());
            if (!Models.initialize(layout))
                ControllerLayout.createDefaultLayout(layout);

            if(listener.findAvailableRobotid().isPresent()) {
                listener.register(listener.findAvailableRobotid().getAsInt(), layout); // i = robotid
                if (!Services.get("ControllerExample poller").isPresent())
                    Services.scheduleTask("ControllerExample poller", listener::processControllers, 20000);
            }
        }, 5000000);
    }


}