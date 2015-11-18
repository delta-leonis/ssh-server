package org.ssh;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.managers.manager.UI;
import org.ssh.models.enums.PacketPriority;
import org.ssh.pipelines.pipeline.GeometryPipeline;
import org.ssh.pipelines.pipeline.RadioPipeline;
import org.ssh.services.consumers.StringConsumer;
import org.ssh.services.couplers.ChangeCoupler;
import org.ssh.services.couplers.VerboseCoupler;
import org.ssh.services.producers.OftenProducer;
import org.ssh.services.producers.OnceProducer;

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
        
        // make a pipeline
        final GeometryPipeline mainPipeline = new GeometryPipeline("fieldbuilder");
        // make another pipeline
        final RadioPipeline radioPipeline   = new RadioPipeline("controller");
        
        // make some services
        final OnceProducer intService       = new OnceProducer("gratisintegers");
        final OftenProducer dingService     = new OftenProducer("dingetjes");
        final ChangeCoupler changeService   = new ChangeCoupler("meerdoubles");
        final StringConsumer stringService  = new StringConsumer("stringisbeter");
        final VerboseCoupler verboseCoupler = new VerboseCoupler("speaker");
        
        // add a few pipelines
        Pipelines.add(mainPipeline);
        Pipelines.add(radioPipeline);
        // add a few services
        Services.add(intService);
        Services.add(verboseCoupler);
        // let's add some other things
        Services.addAll(dingService, changeService, stringService);
        
        // attach them to the pipelines
        verboseCoupler.attachToCompatiblePipelines();
        changeService.attachToCompatiblePipelines(PacketPriority.LOWEST);
        stringService.attachToCompatiblePipelines();
        dingService.attachToCompatiblePipelines();
        intService.attachToCompatiblePipelines();
        
        // start production
        dingService.start();
    }
}