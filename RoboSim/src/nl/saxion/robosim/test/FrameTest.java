package nl.saxion.robosim.test;

import nl.saxion.robosim.model.LogReader;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Created by DaanBrandt on 22-5-2015.
 */
public class FrameTest {

    @BeforeClass
    public static  void init() {
    }


    // test de staat van het eerste frame in de lijst van frames in het  model.
    @Test
    public  void testModelAfter(){
        new LogReader("2013-06-29-133738_odens_mrl.log");
        Model model = Model.getInstance();
        SSL_DetectionFrame frame = model.getFrames().getFirst();
        assertFalse(frame.getRobotsBlueList().isEmpty());
        assertFalse(frame.getRobotsYellowList().isEmpty());
        assertFalse(frame.getBallsList() == null);


    }
}
