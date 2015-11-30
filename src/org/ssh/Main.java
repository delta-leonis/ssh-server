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
import org.ssh.services.consumers.GeometryConsumer;
import org.ssh.services.consumers.WrapperConsumer;
import org.ssh.services.producers.UDPReceiver;

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
        
        /********************************/
        /* Below is just for testing!!! */
        /********************************/
        
        // make a pipelines
        new WrapperPipeline("wrapper pipeline");
        new DetectionPipeline("detection pipeline");
        new GeometryPipeline("geometry pipeline");
        
        // make splitter from wrapper -> geometry / detection
        new WrapperConsumer().attachToCompatiblePipelines();
        new GeometryConsumer("oome geo").attachToCompatiblePipelines();
        Network.listenFor(WrapperPacket.class);
    }
}