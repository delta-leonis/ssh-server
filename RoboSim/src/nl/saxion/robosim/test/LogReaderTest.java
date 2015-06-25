package nl.saxion.robosim.test;

import nl.saxion.robosim.exception.InvalidLogFileException;
import nl.saxion.robosim.model.LogReader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by Fieldhof on 17-6-2015.
 */
public class LogReaderTest {

    @Test
    public void testLogReader() {

        try {
            new LogReader("src\\nl\\saxion\\robosim\\test\\log\\test_complete.log");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test (expected = InvalidLogFileException.class)
    public void testNoBeginText() throws IOException{
        new LogReader("src\\nl\\saxion\\robosim\\test\\log\\test_no_text.log");
    }

    @Test (expected = InvalidLogFileException.class)
    public void testNoVersion() throws IOException {
        new LogReader("src\\nl\\saxion\\robosim\\test\\log\\test_no_version.log");
    }

    @Test (expected = InvalidLogFileException.class)
    public void testEmptyLog() throws IOException {
        new LogReader("src\\nl\\saxion\\robosim\\test\\log\\test_empty.log");
    }

    @Test (expected = InvalidLogFileException.class)
    public void testWrongLog() throws IOException {
        new LogReader("src\\nl\\saxion\\robosim\\test\\log\\test_random.log");
    }

}
