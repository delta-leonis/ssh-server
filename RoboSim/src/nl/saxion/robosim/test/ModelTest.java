package nl.saxion.robosim.test;

import static org.junit.Assert.assertNotNull;
import nl.saxion.robosim.model.LogReader;
import nl.saxion.robosim.model.Model;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by DaanBrandt on 22-5-2015.
 * Deze class Test het Model voor en nadat er een log is ingelezen.
 */
public class ModelTest {

    @BeforeClass
    public static void init() {
    }


    @Test
    public void testModelAfter() {
        new LogReader("2013-06-29-133738_odens_mrl.log");
        Model model = Model.getInstance();
        assertNotNull(model.getSSLField()); // kijkt of er een veld uit de log is gelezen
        assertNotNull(model.getLastFrame()); // kijkt of het volgende frame aanwezig is
        assertNotNull(model.getLastReferee()); // kijkt of de volgende referee gevuld is
//        assertNotNull(model.getByteField());// kijkt of de bytearray van het veld wel gevuld is;
    }


}
