package org.ssh;

import java.util.logging.Level;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.managers.manager.UI;
import org.ssh.models.Field;
import org.ssh.models.Goal;
import org.ssh.models.Team;
import org.ssh.models.enums.Direction;
import org.ssh.models.enums.SendMethod;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.DetectionPipeline;
import org.ssh.pipelines.pipeline.GeometryPipeline;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.pipelines.pipeline.WrapperPipeline;
import org.ssh.senders.DebugSender;
import org.ssh.services.consumers.DetectionModelConsumer;
import org.ssh.services.consumers.GeometryModelConsumer;
import org.ssh.services.consumers.WrapperConsumer;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Class Main.
 */
public class Main extends Application {

    
    /**
     * The main method.
     *
     * @param arg
     *            Command line arguments
     */
    static public void main(final String[] arg) {
        
        // start the managers
        Services.start();
        Models.start();
        Pipelines.start();
        Network.start();
        Network.register(SendMethod.DEBUG, new DebugSender(Level.INFO));

        build();
        
        /** java fx start **/
        Application.launch(arg);

    }
    
    private static void build(){
        // Allies (yellow) on the west side of the field
        Models.create(Team.class, TeamColor.YELLOW);
        // Opponents (blue) on the east side of the field
        Models.create(Team.class, TeamColor.BLUE);
        Models.create(Goal.class, Direction.EAST);
        Models.create(Goal.class, Direction.WEST);
        Models.create(Field.class);
        
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

        UI.start(primaryStage);

        // Disable logger
        //LogManager.getLogManager().reset();
        
        /********************************/
        /* Below is just for testing!!! */
        /********************************/
    }
}