package nl.saxion.robosim.test;

import nl.saxion.robosim.model.protobuf.SslReferee.SSL_Referee;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Gebruiker on 22-5-2015.
 */
public class RefereeTest {

    /**
     * Test for Referee
     * This is what the file looks like
     * packet_timestamp: 1372513037704641
     * stage: NORMAL_FIRST_HALF
     * stage_time_left: 599887758
     * command: NORMAL_START
     * command_counter: 3
     * command_timestamp: 1372513037636896
     */
    @Test
    public void testReferee() {
        byte[] data;
        SSL_Referee referee;

        try {
            data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\test_referee.log"));
            referee = SSL_Referee.parseFrom(data);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }



        assertEquals("NORMAL_FIRST_HALF", referee.getStage());
        assertEquals(599887758, referee.getStageTimeLeft());
        assertEquals("NORMAL_START", referee.getCommand());
        assertEquals(3, referee.getCommandCounter());
        assertEquals(Long.parseLong("1372513037636896"), referee.getCommandTimestamp());
    }

}
