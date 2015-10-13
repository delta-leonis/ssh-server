package application;

import examples.PipelinePacketExample;
import javafx.application.Application;
import javafx.stage.Stage;
import pipeline.Pipeline;
import services.consumers.StringConsumer;
import services.couplers.DoubleCoupler;
import services.producers.IntProducer;

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
        final Pipeline<PipelinePacketExample> mainPipeline = new Pipeline<PipelinePacketExample>();
        // make some services
        final IntProducer    intService    = new IntProducer("gratisintegers");
        final DoubleCoupler  doubleService = new DoubleCoupler("meerdoubles");
        final StringConsumer stringService = new StringConsumer("stringisbeter");

        // add a pipeline to the services store
        Services.addPipeline(mainPipeline);
        // add the consumer to the services store
        Services.addService(intService);
        // oh, and let's add some other things to the model store
        Services.addServices(doubleService, stringService);


        // let's find one of the models we added and get the data from it
        //Models.get("dingenlijst").getData();
        // and let's stop the consumer
        Services.get("stringisbeter").stop();
    }
}