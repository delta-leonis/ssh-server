package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Stopwatch;

import application.Models;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.Model;
import model.ModelFactory;
import model.Robot;
import model.Settings;
import util.Logger;

public class ModelExample {
	private static Logger logger = Logger.getLogger();
	
	public static void main(String[] args) {
		Models.start();

		logger.info(ModelFactory.create(Robot.class, 12, Color.BLACK).toString());
		
		//create a new robot (extends FieldObject extends Model)
		new Robot(12, Color.BLACK);
		new Robot(10, Color.BLACK);
		new Robot(3, Color.BLACK);
		Optional<Model> maybeCroissant = Models.get("Robot 3");
		System.out.println(Models.getAll().stream().count());
		Models.getAll().stream().forEach(model -> logger.info(model.toString()));
		
		if(!maybeCroissant.isPresent()){
			return;
		}
		Robot robot = (Robot)maybeCroissant.get();
		//Models.initializeAll();

		logger.info("Before change: ");
		logger.info(robot.toString());


		//we want to change a number of fields
		Map <String, Object> changes = new HashMap<String, Object>();
		//new robotid
		changes.put("robotId", Integer.valueOf(12));
		//new teamcolor
		changes.put("teamColor", Color.BLUE);
		//change the position in FieldObject
		changes.put("position", new Point2D(Math.random()* 4000, Math.random()* 4000));
		
		//update the Model with these new changes
		robot.update(changes);

		logger.info("after update: ");
		logger.info(Models.get("Robot 3").toString());

		Stopwatch timer = Stopwatch.createStarted();
		robot.saveAsDefault();
    	logger.info("Method took: " + timer.stop());
		robot.save();
	}
}
