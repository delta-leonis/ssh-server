package nl.saxion.robosim.test;

import com.google.protobuf.InvalidProtocolBufferException;
import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionFrame;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by DaanBrandt on 22-5-2015.
 */
public class FrameTest {

    @BeforeClass
    public static  void init() {
    }


    // test de staat van het eerste frame in de lijst van frames in het  model.
    @Test
    public  void testFrame(){

        SSL_DetectionFrame frame;

        try {
            byte[] data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_frame.log"));
            frame = SSL_DetectionFrame.parseFrom(data);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }

        assertNotNull(frame);

        assertEquals(1155470, frame.getFrameNumber());
        assertEquals(1.372505858434287E9, frame.getTCapture(), 0.0001);
        assertEquals(1.372505858437304E9, frame.getTSent(), 0.0001);
        assertEquals(0, frame.getCameraId());
        assertEquals(1, frame.getBallsCount());
        assertEquals(6, frame.getRobotsBlueCount());
    }

    @Test (expected = InvalidProtocolBufferException.class)
    public void testWrongLog() throws IOException{
        byte [] data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_random.log"));
        SSL_DetectionFrame.parseFrom(data);
    }

    @Test (expected = InvalidProtocolBufferException.class)
    public void testEmptyLog() throws IOException {
        byte [] data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_empty.log"));
        SSL_DetectionFrame.parseFrom(data);
    }
}
