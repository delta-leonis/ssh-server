package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import application.Models;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.FieldObject;
import model.Model;
import model.Robot;
import util.Logger;

public class ModelExample {
	private static Logger logger = Logger.getLogger();

	public static void main(String[] args) {
		// Start the Models controller
		Models.start();

		// create a couple of models
		Models.create(FieldObject.class, "test", "object");

		// without json
		Models.create(Robot.class, 3, Color.YELLOW);

		// with json
		Models.create(Robot.class, 12, Color.YELLOW);

		// Retrieve a model
		Optional<Model> oRobot = Models.get("robot Y3");

		// check if found
		if (!oRobot.isPresent()) {
			logger.severe("robot model Y3 not found");
			return;
		}

		// found it!
		Robot robot = (Robot) oRobot.get();

		// we want to change a number of fields
		Map<String, Object> changes = new HashMap<String, Object>();
		// new robotid
		changes.put("robotId", Integer.valueOf(12));
		// new teamcolor
		changes.put("teamColor", Color.BLUE);
		// change the position in FieldObject
		changes.put("position", new Point2D(Math.random() * 4000, Math.random() * 4000));

		// update the Model with these new changes
		robot.update(changes);

		logger.info("after update: ");
		logger.info(robot.toString());

		// manually update fields
		robot.update("position", new Point2D(23123,33333), "teamColor", Color.PURPLE);

		logger.info("after update: ");
		logger.info(robot.toString());

		robot.saveAsDefault();
		robot.save();
	}
}
