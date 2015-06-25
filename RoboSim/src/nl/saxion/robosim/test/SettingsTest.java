package nl.saxion.robosim.test;

import junit.framework.Assert;
import nl.saxion.robosim.model.Settings;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Damon on 18-6-2015.
 */
public class SettingsTest {

    @Test
    public void testWriteSettings() {
        Settings.destroy();
        Settings s = Settings.getInstance();
        String inputip = s.getIip();
        String outputip = s.getOip();
        String inputport = s.getIport();
        String outputport = s.getOport();
        String refip = s.getRefIp();
        String refport = s.getRefPort();
        s.setCommunicationSettings("1", "2", "3", "4");
        s.setRefSettings("5", "6");

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
            // get the property value and set the variables
            assertEquals("1", prop.getProperty("inputIp"));
            assertEquals("2", prop.getProperty("inputPort"));
            assertEquals("3", prop.getProperty("outputIp"));
            assertEquals("4", prop.getProperty("outputPort"));
            assertEquals("5", prop.getProperty("refereeIp"));
            assertEquals("6", prop.getProperty("refereePort"));

            s.setCommunicationSettings(inputip, inputport, outputip, outputport);
            s.setRefSettings(refip, refport);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void testCorruptPropertiesFile() {
        Settings.destroy();
        FileInputStream input = null;
        try {
            //erase file
            File f = new File("config.properties");
            PrintWriter writer = null;
            writer = new PrintWriter(f);
            writer.print("");

            Settings s = Settings.getInstance();
            writer.close();

            input = new FileInputStream("config.properties");
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            // get the property value and set the variables
            assertEquals("224.5.23.20", prop.getProperty("inputIp"));
            assertEquals("1337", prop.getProperty("inputPort"));
            assertEquals("224.5.23.2", prop.getProperty("outputIp"));
            assertEquals("10002", prop.getProperty("outputPort"));
            assertEquals("224.5.23.1", prop.getProperty("refereeIp"));
            assertEquals("10003", prop.getProperty("refereePort"));






        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Test
    public void testFileNotExist() {
        Settings.destroy();
        File temp = new File("config.properties");
        temp.delete();


        assertFalse(temp.exists());

        Settings.getInstance();

        File file = new File("config.properties");
        assertTrue(file.exists());


    }
}