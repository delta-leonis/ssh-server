package nl.saxion.robosim.test;

/**
 * Created by Gebruiker on 22-5-2015.
 */
public class SSLFieldTest {

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
//    @Test
//    public void testModelAfter() {
//        byte[] data;
//        MessagesRobocupSslGeometry.SSL_GeometryFieldSize unparsedField;
//TODO create new test
//        try {
//            data = Files.readAllBytes(Paths.get("src\\nl\\saxion\\robosim\\test\\test_field.log"));
//            unparsedField = MessagesRobocupSslGeometry.SSL_GeometryFieldSize.parseFrom(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        SSL_Field SSLField = new SSL_Field(unparsedField);
//
//        assertEquals(10, SSLField.getLine_width());
//        assertEquals(6050, SSLField.getField_length());
//        assertEquals(4050, SSLField.getField_width());
//        assertEquals(250, SSLField.getBoundary_width());
//        assertEquals(425, SSLField.getReferee_width());
//        assertEquals(700, SSLField.getGoal_width());
//        assertEquals(180, SSLField.getGoal_depth());
//        assertEquals(20, SSLField.getGoal_wall_width());
//        assertEquals(500, SSLField.getCenter_circle_radius());
//        assertEquals(800, SSLField.getDefense_radius());
//        assertEquals(350, SSLField.getDefense_stretch());
//        assertEquals(200, SSLField.getFree_kick_from_defense_dist());
//        assertEquals(750, SSLField.getPenalty_spot_from_field_line_dist());
//        assertEquals(400, SSLField.getPenalty_line_from_spot_dist());
//    }


}
