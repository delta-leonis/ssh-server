package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.ssh.managers.manager.Models;
import org.ssh.models.Model;
import org.ssh.models.Robot;
import org.ssh.models.Settings;
import org.ssh.models.enums.Allegiance;
import org.ssh.util.Logger;

public class ModelExample  {
    
    private static Logger logger = Logger.getLogger();
    
    public static void main(final String[] args) {
        // Start the Models controller
        Models.start();

        Models.<Settings> get("settings").ifPresent(set -> set.getProfilesPath());
        
        Optional<Model> oSettings = Models.get("settings");
        Settings settings = (Settings) oSettings.get();
        
        // without json
        Models.create(Robot.class, 3, Allegiance.ALLY);
        
        // with json
        Models.create(Robot.class, 12, "Foutief");
        
        // Retrieve a models
        final Optional<Model> oRobot = Models.get("robot A3");
        
        // check if found
        if (!oRobot.isPresent()) {
            ModelExample.logger.severe("robot model A3 not found");
            return;
        }
        
        // found it!
        final Robot robot = (Robot) oRobot.get();
        
        
        // we want to change a number of fields
        final Map<String, Object> changes = new HashMap<String, Object>();
        // new robotid
        changes.put("robotId", Integer.valueOf(12));
        // new teamcolor
        changes.put("allegiance", Allegiance.OPPONENT);
        // change the position in FieldObject
        changes.put("x", Math.random() * 4000.0f);
        changes.put("y", Math.random() * 4000.0f);
        
        // update the Model with these new changes
        robot.update(changes);
        
        ModelExample.logger.info("after update: ");
        ModelExample.logger.info(robot.toString());
        
        // manually update fields
        robot.update("x", 129.0f, "y", 12.0f, "allegiance", Allegiance.ALLY, "isSelected", true);

        // Alias field example
        robot.update("robot_id", 8282382);
        ModelExample.logger.info("after update: ");
        ModelExample.logger.info(robot.toString());

        // robot.saveAsDefault();
        // robot.save();
    }
}
