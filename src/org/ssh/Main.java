package org.ssh;

import java.util.logging.Level;
import java.util.stream.IntStream;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.managers.manager.UI;
import org.ssh.models.*;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.SendMethod;
import org.ssh.network.receive.detection.DetectionPipeline;
import org.ssh.network.receive.geometry.GeometryPipeline;
import org.ssh.network.receive.wrapper.WrapperPipeline;
import org.ssh.network.transmit.radio.RadioPipeline;
import org.ssh.network.transmit.radio.producers.OftenProducer;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.network.transmit.senders.DebugSender;
import org.ssh.network.receive.detection.consumers.DetectionModelConsumer;
import org.ssh.network.receive.geometry.consumers.GeometryModelConsumer;
import org.ssh.network.receive.wrapper.consumers.WrapperConsumer;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ssh.ui.components.overlay.LoggerConsole;
import org.ssh.util.Logger;
import org.ssh.util.LoggerMemoryHandler;
import org.ssh.ui.components.widget.TestWidget;

/**
 * The Class Main.
 */
public class Main extends Application {
    /**
     * {@link LoggerMemoryHandler} for handling the logging and managing what {@link Level} of
     * logging is displayed.
     */
    private LoggerMemoryHandler loggerHandler;

    private Logger LOG = Logger.getLogger("org.ssh");

    /**
     * The main method.
     *
     * @param arg
     *            Command line arguments
     */
    static public void main(final String[] arg) {
        /** java fx start **/
        Application.launch(arg);
    }

    private static void build() {

        // Creating models
        Models.create(Game.class);
        Models.create(Goal.class, Allegiance.ALLY);
        Models.create(Goal.class, Allegiance.OPPONENT);
        Models.create(Team.class, Allegiance.ALLY);
        Models.create(Team.class, Allegiance.OPPONENT);
        Field field = Models.create(Field.class);

        // Getting field dimensions
        float fieldWidth = field.getFieldWidth();
        float fieldLength = field.getFieldLength();


        // Create some robots
        IntStream.range(0, 8).forEach(id ->{
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

    /** Test method */
    private static void createWidgets() {
        UI.addWidget(new TestWidget("widget 0"));
        UI.addWidget(new TestWidget("widget 1"));
        UI.addWidget(new TestWidget("widget 2"));
        UI.addWidget(new TestWidget("widget 3"));
        UI.addWidget(new TestWidget("widget 4"));
        UI.addWidget(new TestWidget("widget 5"));
        UI.addWidget(new TestWidget("widget 6"));
        UI.addWidget(new TestWidget("widget 7"));
        UI.addWidget(new TestWidget("widget 8"));
        UI.addWidget(new TestWidget("widget 9"));
        UI.addWidget(new TestWidget("widget 10"));
        UI.addWidget(new TestWidget("widget 11"));
        UI.addWidget(new TestWidget("widget 12"));
        UI.addWidget(new TestWidget("widget 13"));
        UI.addWidget(new TestWidget("widget 14"));
        UI.addWidget(new TestWidget("widget 15"));
        UI.addWidget(new TestWidget("widget 16"));
        UI.addWidget(new TestWidget("widget 17"));
        UI.addWidget(new TestWidget("widget 18"));
        UI.addWidget(new TestWidget("widget 19"));
        UI.addWidget(new TestWidget("widget 20"));
        UI.addWidget(new TestWidget("widget 21"));
        UI.addWidget(new TestWidget("widget 22"));
        UI.addWidget(new TestWidget("widget 23"));
        UI.addWidget(new TestWidget("widget 24"));
        UI.addWidget(new TestWidget("widget 25"));
    }
    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
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