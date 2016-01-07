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
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.*;
import org.ssh.senders.DebugSender;
import org.ssh.services.Service;
import org.ssh.services.consumers.DetectionModelConsumer;
import org.ssh.services.consumers.GeometryModelConsumer;
import org.ssh.services.consumers.WrapperConsumer;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ssh.ui.components.overlay.LoggerConsole;
import org.ssh.util.Logger;
import org.ssh.util.LoggerMemoryHandler;

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
                    Models.create(Robot.class, id, Allegiance.OPPONENT)
                            .update("x", fieldLength / 2 - id * 200f - 180f, "y", -fieldWidth / 2 + 180f);
                    Models.create(Robot.class, id, Allegiance.ALLY)
                            .update("x", id * 200f - fieldLength / 2 + 180f, "y", fieldWidth / 2 - 180f);

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
    }
}