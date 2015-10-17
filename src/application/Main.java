package application;

import javafx.application.Application;
import javafx.stage.Stage;
import pipeline.Priority;
import pipelines.GeometryPipeline;
import pipelines.RadioPipeline;
import services.consumers.StringConsumer;
import services.couplers.ChangeCoupler;
import services.couplers.VerboseCoupler;
import services.producers.OftenProducer;
import services.producers.OnceProducer;

/**
 * The Class Main.
 */
public class Main extends Application {

    /**
     * The main method.
     *
     * @param arg Command line arguments
     */
    static public void main (String[] arg) {
        // start the application modules
        Services.start();
        Models.start();
        /** java fx start **/
        Application.launch(arg);

    }

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        UI.start(primaryStage);

        /********************************/
        /* Below is just for testing!!! */
        /********************************/

        // make a pipeline for stuff
        final GeometryPipeline mainPipeline = new GeometryPipeline("fieldbuilder");

        // make another pipeline
        final RadioPipeline  radioPipeline = new RadioPipeline("controller");
        // make some services
        final OnceProducer    intService    = new OnceProducer("gratisintegers");
        final OftenProducer    dingService   = new OftenProducer("dingetjes");
        final ChangeCoupler  changeService = new ChangeCoupler("meerdoubles");
        final StringConsumer stringService = new StringConsumer("stringisbeter");
        final VerboseCoupler verboseCoupler = new VerboseCoupler("speaker");

        
        // add a pipeline to the services store
        Services.addPipeline(mainPipeline);
        Services.addPipeline(radioPipeline);
//        // add the consumer to the services store
        Services.addService(intService);
        Services.addService(verboseCoupler);
//        // oh, and let's add some other things to the model store
        Services.addServices(dingService, changeService, stringService);
        
        verboseCoupler.attachToCompatiblePipelines();
        changeService.attachToCompatiblePipelines(Priority.LOWEST);
        stringService.attachToCompatiblePipelines();
        dingService.attachToCompatiblePipelines();
        intService.attachToCompatiblePipelines();
        
        //dingService.start();
        intService.start();
        
        
        // let's find one of the models we added and get the data from it
        //Models.get("dingenlijst").getData();
        // and let's stop the consumer
//        Services.get("stringisbeter").stop();
    }
}