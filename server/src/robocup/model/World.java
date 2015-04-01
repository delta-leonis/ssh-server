package robocup.model;

import java.util.ArrayList;
import java.util.Observable;

import robocup.model.enums.Command;
import robocup.model.enums.FieldZone;
import robocup.model.enums.TeamColor;
import robocup.view.GUI;
/**
 * Model representation of the physical "world", including the field, 
 * all the robots (even non playing robots) and the ball
 */
public class World extends Observable {

	private static World instance;
	
	private Ball ball;
	private Referee referee;
	private Field field;
	
	private ArrayList<Robot> allyTeam;
	private ArrayList<Robot> enemyTeam;
	private ArrayList<Robot> robotList;
	
	private int robotRadius = Robot.DIAMETER/2;
	
	private int fieldHeight;
	private int fieldWidth;
	
	private static final int TOTAL_TEAM_SIZE = 11;
	private final int STOP_BALL_DISTANCE = 500; // in mm

	public GUI gui;
	
	/**
	 * Constructor for the {@link World}
	 * Can only be called as a singleton.
	 */
	private World() {
		fieldHeight = 4050;
		fieldWidth = 6050;
		
		ball = new Ball();
		ball.setPosition(new FieldPoint(400, 200)); // added starting point for ball
												// to remove nullpointer errors
		referee = new Referee();
		field = new Field(fieldHeight, fieldWidth);
		
		// initialize all robots
		allyTeam = new ArrayList<Robot>();
		enemyTeam = new ArrayList<Robot>();;
		
		for(int i = 0; i < TOTAL_TEAM_SIZE; i++) 
		{	
			allyTeam.add(new Ally(i, false, 150));
		}
		for(int i = 0; i < TOTAL_TEAM_SIZE; i++) 
		{	
			enemyTeam.add(new Enemy(i, false, 150));
		} 
		referee.initAllyTeam(allyTeam);
		referee.initEnemyTeam(enemyTeam);
		
		robotList = new ArrayList<Robot>();
		robotList.addAll(allyTeam);	
		robotList.addAll(enemyTeam);
		
	}
		
	/**
	 * @return Singleton for the {@link World}
	 */
	public static World getInstance() {
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}
	
	/**
	 * Set used GUI
	 * @param current GUI object
	 */
	public void setGUI(GUI _gui){
		gui = _gui;
	}

	/**
	 * mainly used to push updates
	 * 
	 * @return the gui object
	 */
	public GUI getGUI(){
		return gui;
	}

	/**
	 * 
	 * @param message
	 */
	public void HandlerFinished(String message) {
		setChanged();
		notifyObservers(message + "HandlerFinished");
	}

	/**
	 * @return the {@link Ball} that is currently in the field.
	 */
	public Ball getBall() {
		return ball;
	}

	/**
	 * @return the referee
	 */
	public Referee getReferee() {
		return referee;
	}

	/**
	 * @return the {@link Field} that is used for this game.
	 */
	public Field getField() {
		return field;
	}
	
	/**
	 * Returns the {@link Team} with the given color.
	 * this method is an adapter from the old model, so its better to use referee getTeambyColor
	 * @param color the color of the {@link Team}
	 * @return the {@link Team} with the given color. Returns null if there is no {@link Team} with the given color.
	 */
	public Team getTeamByColor(TeamColor color) {
		return referee.getTeamByColor(color);
	}

	/**
	 * gets our team, this is a method from the old model, it bypasses the referee
	 * @return the ally {@link Team} in the current match.
	 * @deprecated Get it from Referee
	 */
	public Team getAlly() {
		return referee.getAlly();
	}

	/**
	 * gets the enemy team, this is a method from the old model, it bypasses the referee
	 * @return the enemy {@link Team} in the current match.
	 * @deprecated Get it from Referee
	 */
	public Team getEnemy() {
		return referee.getEnemy();
	}

	/**
	 * 
	 * @return all the robots in the current match
	 */
	public ArrayList<Robot> getAllRobots() {
		return robotList;
	}
	
	/**
	 * @return all robots currently on the playing field
	 */
	public ArrayList<Robot> getAllRobotsOnSight(){
		ArrayList<Robot> onsight = new ArrayList<Robot>();
		for( Robot robot : robotList){
			if(robot.isOnSight())
				onsight.add(robot);
		}
		return onsight;
	}
	
	/**
	 * Sets the {@link Field} of the current match. 
	 * The {@link Field} contains all variables regarding the {@link Field}. (Think of field width, goal length etc.)
	 * @param field the {@link Field} to set for the current match.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "World \r\n[ball=" + ball + "\r\nreferee=" + referee + "\r\n"
				+ field + "]";
	}

	/**
	 * Calculate if ally team is closer to the ball
	 * @return true when the ally team is closer
	 */
	public boolean allyHasBall() {
		ArrayList<Robot> allies = getReferee().getAlly().getRobots();
		ArrayList<Robot> enemies = getReferee().getEnemy().getRobots();

		double distanceAlly = getTeamDistanceToBall(allies);
		double distanceEnemy = getTeamDistanceToBall(enemies);

		return distanceAlly <= distanceEnemy;
	}

	/**
	 * Get the distance from the closest robot in one team to the ball
	 * @param robots the team of robots
	 * @return the distance of the closest robot
	 */
	private double getTeamDistanceToBall(ArrayList<Robot> robots) {
		if (ball == null)
			return Integer.MAX_VALUE;

		double minDistance = -1.0;

		for (Robot r : robots) {
			if (minDistance == -1.0)
				minDistance = r.getPosition().getDeltaDistance(ball.getPosition());
			else {
				double distance = r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance)
					minDistance = distance;
			}
		}

		return minDistance;
	}
	
	/**
	 * method that returns a robot by checking vertex points of the given fieldzone
	 * the closest robot will be returned
	 *
	 * this method is not yet finished, as robots "within" the zone should get priority, as should the center of the zone be more important
	 * @param fieldZone
	 * @return
	 */
	public Robot getClosestRobotToZoneWithoutRole(FieldZone fieldZone) {
		Ally foundAlly = null;
		double closestDistance = Double.MAX_VALUE;
		
		for (Robot itRobot: allyTeam) {
			Ally itAlly = (Ally) itRobot;
			
			if (itAlly.getRole() == null) {
				double itDistance = field.getZone(fieldZone).getClosestVertex(itAlly.getPosition()).getDeltaDistance(itAlly.getPosition());
				if (itDistance < closestDistance)
				{
					closestDistance = itDistance;
					foundAlly = itAlly;
				}
			}
		}
		return foundAlly;
	}

	/**
	 * Helper function for referee commands, checks last command issued
	 * when the robot may move because of the "keep 50 cm clearance" rule, pathfinding must find a way from the ball
	 * @param robotID
	 * @return bool indicating if movement is allowed
	 */
	public boolean robotMayMove(int robotID) {
		// Halt = all robots stop
		if (referee.getCommand() == Command.HALT) {
			return false;
		}

		// Stop = keep 50cm from ball
		if (referee.getCommand() == Command.STOP) {
			// if the distance to ball is less then 50cm, is so return false
			if (referee.getAlly().getRobotByID(robotID).getPosition().getDeltaDistance(ball.getPosition()) < STOP_BALL_DISTANCE) {
				return false;
			}

			// Goal = Should be treated the same as STOP
		} else if (referee.getCommand() == Command.GOAL_YELLOW || referee.getCommand() == Command.GOAL_BLUE) {
			return false;
		}
		return true;
	}
	
	/**
	 * Method that retrieves all allies that are present within a zone
	 * @param argPoly the polygon in which the method looks for ally's, a point list with absolute locations
	 * @return an list with all the ally robots 
	 */
	public ArrayList<Ally> getAllyRobotsInArea(FieldPoint[] argPoly) {
		ArrayList<Ally> foundAllies = new ArrayList<Ally>();
		for (Robot ally : allyTeam) {
			if (areaContainsCircle(ally.getPosition(), argPoly, robotRadius))
			{
				foundAllies.add((Ally)ally);
			}
		}
		return foundAllies;
	}

	/**
	 * Method that retrieves all enemies that are present within a zone
	 * @param argPoly the polygon in which the method looks for enemies, a point list with absolute locations
	 * @return an list with all the enemy robots 
	 */
	public ArrayList<Enemy> getEnemyRobotsInArea(FieldPoint[] argPoly) {
		ArrayList<Enemy> foundEnemies = new ArrayList<Enemy>();
		for (Robot enemy : enemyTeam) {
			if (areaContainsCircle(enemy.getPosition(), argPoly, robotRadius))
			{
				foundEnemies.add((Enemy)enemy);
			}
		}
		return foundEnemies;
	}
	
	/**
	 * Method that retrieves all robots that are present within a zone
     * @param argPoly the polygon in which the method looks for robots, a point list with absolute locations
	 * @return an list with all the robots 
	 */
	public ArrayList<Robot> getAllRobotsInArea(FieldPoint[] argPoly) {
		ArrayList<Robot> foundRobots = new ArrayList<Robot>();
		for (Robot robot : robotList) {
			if (areaContainsCircle(robot.getPosition(), argPoly, robotRadius))
			{
				foundRobots.add(robot);
			}
		}
		return foundRobots;
	}
	
	/**
	 * Method that retrieves all allies that are present within a zone
     * @param fieldZones array with all the zones where to look for robots
	 * @return an list with all the found robots 
	 */
	public ArrayList<Ally> getAllyRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Ally> foundAllies = new ArrayList<Ally>();
		
		for (FieldZone fieldzone : fieldZones) {
			for (Robot ally : allyTeam) {
				if (field.getZone(fieldzone).contains(ally.getPosition()))
				{
					foundAllies.add((Ally)ally);
				};
			}
		}
		return foundAllies;
	}

	/**
	 * Method that retrieves all enemies that are present within a zone
     * @param fieldZones array with all the zones where to look for robots
	 * @return an list with all the found robots 
	 */
	public ArrayList<Enemy> getEnemyRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Enemy> foundEnemies = new ArrayList<Enemy>();
		
		for (FieldZone fieldzone : fieldZones) {
			for (Robot enemy : enemyTeam) {
				if (field.getZone(fieldzone).contains(enemy.getPosition()))
				{
					foundEnemies.add((Enemy)enemy);
				};
			}
		}
		return foundEnemies;
	}
	
	/**
	 * Method that locates an object and returns the field where the object is (or null if not found)
	 * @param fieldObject the object to locate
	 * @return FieldZone where the given object is located 
	 */
	public FieldZone locateFieldObject(FieldObject fieldObject) {
		for (FieldZone fieldzone : FieldZone.values()) {
			if (field.getZone(fieldzone).contains(fieldObject.getPosition())) {
				return fieldzone;
			}
		}
		return null;
	}
	
	/**
	 * Method that retrieves all robots that are present within given zones
	 * @param fieldZones a list with FieldZones which have to be searched for robots
	 * @return list containing all the found robots in the field zones
	 */
	public ArrayList<Robot> getAllRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Robot> foundRobots = new ArrayList<Robot>();
		
		for (FieldZone fieldzone : fieldZones) {
			for (Robot robot : robotList) {
				if (field.getZone(fieldzone).contains(robot.getPosition()))
				{
					foundRobots.add(robot);
				};
			}
		}
		return foundRobots;
	}
	
	/**
	 * method that calculates if a circle falls in or touches a polygon
	 * accepts 
	 *@param argPoint the center of the circle
	 *@param areaPoly the point array with corner locations of the polygon
	 *@param radius the radius of the circle
	 */
    public boolean areaContainsCircle(FieldPoint argPoint, FieldPoint[] areaPoly, double radius) {
    	boolean result = false;
    	
    	for (int edges = 0; edges < areaPoly.length-1; edges++) {
    		if (pointToLineDistance(
    				new FieldPoint(areaPoly[edges].getX()+(fieldWidth/2), areaPoly[edges].getY() + (fieldHeight/2)), 
    				new FieldPoint(areaPoly[edges + 1].getX()+(fieldWidth/2), areaPoly[edges + 1].getY()+(fieldHeight/2)), 
    				new FieldPoint(argPoint.getX()+(fieldWidth/2), argPoint.getY()+(fieldHeight/2)) ) < radius) {
    			result = true;
            }
    	}
    	
    	result = result || pointInPolygon(argPoint, areaPoly);
    	return result;
    }
    
    /**
     * a method that calculates the distance of the right-angle between a point and a line
     * @param a the start point of the line
     * @param b the end point of the line
     * @param p the point to check the distance from the line with
     */
    public double pointToLineDistance(FieldPoint A, FieldPoint B, FieldPoint P) {
    	double normalLength = Math.sqrt((B.getX() - A.getX()) * (B.getX() - A.getX()) + (B.getY() - A.getY()) * (B.getY() - A.getY()));
    	return Math.abs((P.getX() - A.getX()) * (B.getY() - A.getY()) - (P.getY() - A.getY()) * (B.getX() - A.getX())) / normalLength;
    }
    
    /**
     * a method that checks if a point falls within a polygon
     * @param argPoint the point
     * @param point[] the array with the locations of the polygon
     */
    public boolean pointInPolygon(FieldPoint argPoint, FieldPoint[] areaPoly) {
        boolean result = false;
        for (int i = 0, j = areaPoly.length - 1; i < areaPoly.length; j = i++) {        	
          if ((areaPoly[i].getY() > argPoint.getY()) != (areaPoly[j].getY() > argPoint.getY()) &&
              (argPoint.getX() < (areaPoly[j].getX() - areaPoly[i].getX()) * (argPoint.getY() - areaPoly[i].getY()) / (areaPoly[j].getY() - areaPoly[i].getY()) + areaPoly[i].getX())) {
        	  result = !result;
           }
        }
        return result;
     }

	/**
	 * Returns the color of your own team.
	 * Suggestion: Rename to getAllyTeamColor()
	 * 
 	 * @Deprecated use Referee.getOwnTeamColor();
	 * @return
	 */
	@Deprecated
	public TeamColor getOwnTeamColor() {
		return referee.getOwnTeamColor();
	}
}
