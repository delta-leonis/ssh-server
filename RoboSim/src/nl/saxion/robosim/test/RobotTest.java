package nl.saxion.robosim.test;

import nl.saxion.robosim.model.protobuf.SslDetection.SSL_DetectionRobot;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Fieldhof on 27-5-2015.
 */
public class RobotTest {

    public SSL_DetectionRobot makeRobot() {
        byte[] data;

        try {
            data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\test_createrobot.log"));
            return SSL_DetectionRobot.parseFrom(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Test for creation of the robot
     * The log looks like this:
     * confidence: 0.9520438
     * robot_id: 2
     * x: -151.43614
     * y: 1787.3628
     * orientation: -0.5076895
     * pixel_x: 690.9697
     * pixel_y: 23.454546
     * height: 145.0
     */
    @Test
    public void testRobotCreation() {
        SSL_DetectionRobot robot = makeRobot();
        assertNotNull(robot);

        assertEquals(0.9520438, robot.getConfidence(), 1e-4);
        assertEquals(2, robot.getRobotId());
        assertEquals(-151.43614, robot.getX(), 1e-4);
        assertEquals(1787.3628, robot.getY(), 1e-4);
        assertEquals(-0.5076895, robot.getOrientation(), 1e-4);
        assertEquals(690.9697, robot.getPixelX(), 1e-4);
        assertEquals(23.454546, robot.getPixelY(), 1e-4);
        assertEquals(145.0, robot.getHeight(), 1e-4);
    }
}
