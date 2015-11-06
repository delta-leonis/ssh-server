package org.ssh;

import org.ssh.managers.Models;
import org.ssh.managers.Services;
import org.ssh.managers.UI;
import org.ssh.pipelines.GeometryPipeline;
import org.ssh.pipelines.RadioPipeline;
import org.ssh.services.consumers.StringConsumer;
import org.ssh.services.couplers.ChangeCoupler;
import org.ssh.services.couplers.VerboseCoupler;
import org.ssh.services.pipeline.Priority;
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
        // start the org.ssh.managers modules
        Services.start();
        Models.start();
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
        
        // make a org.ssh.services.pipeline for stuff
        final GeometryPipeline mainPipeline = new GeometryPipeline("fieldbuilder");
        
        // make another org.ssh.services.pipeline
        final RadioPipeline radioPipeline = new RadioPipeline("controller");
        // make some org.ssh.services
        final OnceProducer intService = new OnceProducer("gratisintegers");
        final OftenProducer dingService = new OftenProducer("dingetjes");
        final ChangeCoupler changeService = new ChangeCoupler("meerdoubles");
        final StringConsumer stringService = new StringConsumer("stringisbeter");
        final VerboseCoupler verboseCoupler = new VerboseCoupler("speaker");
        
        // add a org.ssh.services.pipeline to the org.ssh.services store
        Services.addPipeline(mainPipeline);
        Services.addPipeline(radioPipeline);
        // // add the consumer to the org.ssh.services store
        Services.addService(intService);
        Services.addService(verboseCoupler);
        // // oh, and let's add some other things to the org.ssh.models store
        Services.addServices(dingService, changeService, stringService);

        verboseCoupler.attachToCompatiblePipelines();
        changeService.attachToCompatiblePipelines(Priority.LOWEST);
        stringService.attachToCompatiblePipelines();
        dingService.attachToCompatiblePipelines();
        intService.attachToCompatiblePipelines();

        // dingService.start();
        intService.start();
        
        // let's find one of the models we added and get the data from it
        // Models.get("dingenlijst").getData();
        // and let's stop the consumer
        // Services.get("stringisbeter").stop();
    }
}