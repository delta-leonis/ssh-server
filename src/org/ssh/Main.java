package org.ssh;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ssh.managers.manager.*;
import org.ssh.models.*;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.SendMethod;
import org.ssh.network.receive.detection.DetectionPipeline;
import org.ssh.network.receive.detection.consumers.DetectionModelConsumer;
import org.ssh.network.receive.geometry.GeometryPipeline;
import org.ssh.network.receive.geometry.consumers.GeometryModelConsumer;
import org.ssh.network.receive.wrapper.WrapperPipeline;
import org.ssh.network.receive.wrapper.consumers.WrapperConsumer;
import org.ssh.network.transmit.radio.RadioPipeline;
import org.ssh.network.transmit.senders.DebugSender;
import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.ui.components.widget.TestWidget;
import org.ssh.util.Logger;

import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * The Class Main.
 */
public class Main extends Application {
    private static final Logger LOG = Logger.getLogger("org.ssh");

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

        // Creating models
        Models.create(Game.class);
        Models.create(Goal.class, Allegiance.ALLY);
        Models.create(Goal.class, Allegiance.OPPONENT);
        Models.create(Team.class, Allegiance.ALLY);
        Models.create(Team.class, Allegiance.OPPONENT);
        Field field = Models.create(Field.class);

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
        // make another pipeline
        new RadioPipeline("controller");

        // make splitter from wrapper -> geometry / detection
        new WrapperConsumer().attachToCompatiblePipelines();
        new GeometryModelConsumer("oome geo").attachToCompatiblePipelines();
        new DetectionModelConsumer("oome decto").attachToCompatiblePipelines();

        Network.listenFor(WrapperPacket.class);
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
        Main.LOG.info("Starting software");

        // start the managers
        Services.start();
        Models.start();
        Pipelines.start();
        Network.start();
        Network.register(SendMethod.DEBUG, new DebugSender(Level.INFO));

        build();

        UI.start(primaryStage);

        /********************************/
        /* Below is just for testing!!! */
        /********************************/
        createWidgets();
    }
}