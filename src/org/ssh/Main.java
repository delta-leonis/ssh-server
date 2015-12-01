package org.ssh;

import java.util.logging.Level;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.managers.manager.UI;
import org.ssh.models.enums.SendMethod;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.DetectionPipeline;
import org.ssh.pipelines.pipeline.GeometryPipeline;
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
        
        /** java fx start **/
        Application.launch(arg);
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        
        UI.start(primaryStage);
        
     	// @TODO create teams ofzeu
        // Allies (yellow) on the west side of the field
        @SuppressWarnings ("unused")
        Team teamAllies = (Team) Models.create(Team.class, Direction.WEST, TeamColor.YELLOW);
        // Opponents (blue) on the east side of the field
        @SuppressWarnings ("unused")
        Team teamOpponents = (Team) Models.create(Team.class, Direction.EAST, TeamColor.BLUE);
     
        
        /********************************/
        /* Below is just for testing!!! */
        /********************************/
        
        // make a pipeline
        final GeometryPipeline mainPipeline = new GeometryPipeline("fieldbuilder");
        // make another pipeline
        final RadioPipeline radioPipeline   = new RadioPipeline("controller");
        // Make a detection pipeline
        final Field3DDetectionPipeline detectionPipeline = new Field3DDetectionPipeline("detection");
        
        // make splitter from wrapper -> geometry / detection
        new WrapperConsumer().attachToCompatiblePipelines();
        new GeometryModelConsumer("oome geo").attachToCompatiblePipelines();
        new DetectionModelConsumer("oome decto").attachToCompatiblePipelines();
        Network.listenFor(WrapperPacket.class);
    }
}