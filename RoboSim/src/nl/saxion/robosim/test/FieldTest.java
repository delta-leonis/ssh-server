package nl.saxion.robosim.test;

import com.google.protobuf.InvalidProtocolBufferException;
import nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryData;
import nl.saxion.robosim.model.protobuf.SslGeometry.SSL_GeometryFieldSize;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Gebruiker on 22-5-2015.
 */
public class FieldTest {

    /**
     * Test that checks if a the field is correctly read
     * The parsed file looks like this:
     * line_width: 10
     * field_length: 6050
     * field_width: 4050
     * boundary_width: 250
     * referee_width: 425
     * goal_width: 700
     * goal_depth: 180
     * goal_wall_width: 20
     * center_circle_radius: 500
     * defense_radius: 800
     * defense_stretch: 350
     * free_kick_from_defense_dist: 200
     * penalty_spot_from_field_line_dist: 750
     * penalty_line_from_spot_dist: 400
     */
    @Test
    public void testField() {
        byte[] data;
        SSL_GeometryData geometryData;
        try {
            data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_field.log"));
            geometryData = SSL_GeometryData.parseFrom(data);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }

        SSL_GeometryFieldSize field = geometryData.getField();

        assertEquals(10, field.getLineWidth());
        assertEquals(6050, field.getFieldLength());
        assertEquals(4050, field.getFieldWidth());
        assertEquals(250, field.getBoundaryWidth());
        assertEquals(425, field.getRefereeWidth());
        assertEquals(700, field.getGoalWidth());
        assertEquals(180, field.getGoalDepth());
        assertEquals(20, field.getGoalWallWidth());
        assertEquals(500, field.getCenterCircleRadius());
        assertEquals(800, field.getDefenseRadius());
        assertEquals(350, field.getDefenseStretch());
        assertEquals(200, field.getFreeKickFromDefenseDist());
        assertEquals(750, field.getPenaltySpotFromFieldLineDist());
        assertEquals(400, field.getPenaltyLineFromSpotDist());
    }

    @Test (expected = InvalidProtocolBufferException.class)
    public void testWrongLog() throws IOException{
        byte [] data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_random.log"));
        SSL_GeometryData.parseFrom(data);
    }

    @Test (expected = InvalidProtocolBufferException.class)
    public void testEmptyLog() throws IOException {
        byte [] data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\log\\test_empty.log"));
        SSL_GeometryData.parseFrom(data);
    }

}
