/**
 * Initialize the program and start all threads
 */
package robocup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import robocup.controller.handlers.protohandlers.MainHandler;
import robocup.input.RefereeClient;
import robocup.input.SSLVisionClient;
import robocup.model.World;
import robocup.model.enums.TeamColor;

public class Main {

	public static final int TEST_ROBOT_ID = 1;
	public static final int KEEPER_ROBOT_ID = 3;

	public static final int POSSIBLE_IDS = 11;
	

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	/** The level used to log in logging.txt. Use LOGGER.warning() for debugging. */
	private static Level debugLevel = Level.WARNING;
	private static String teamConfig = "config/teams.properties";
	private static String protobufConfig = "config/config.properties";
	private static World world;

	public static void main(String[] args) {
		initLog();
		initLibraryPath();
		world = World.getInstance();
		LOGGER.info("Program started");
		initView();
		LOGGER.info("View initialized");
		initTeams();
		LOGGER.info("Teams initialized");
		initProtoBuffClients();
		LOGGER.info("Handlers initialized");
		initHandlers();
		LOGGER.info("Main initialized");
        new robocup.controller.ai.AiExecuter().start();
	}

	/**
	 * Sets the library path correct for gamepad use.
	 */
	public static void initLibraryPath() {
		String path = System.getProperty("java.library.path")  + File.pathSeparator + "/dist/";
	    System.setProperty("java.library.path", path);
	    Field sysPathsField;
		try {
			sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
			sysPathsField.setAccessible(true);
			sysPathsField.set(null, null);
			LOGGER.info("Library path for gamepad correctly set");
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			LOGGER.severe("Error in setting library path for gamepad: " + e);
		}
	}

	/**
	 * Create the root logger from this application.
	 * Disable the console logger and set the warning level.
	 */
	public static void initLog() {
		LOGGER.setUseParentHandlers(false);
		LOGGER.setLevel(debugLevel);
	}

	/**
	 * Set a fancy lookAndFeel and start the view GUI
	 */
	public static void initView() {
		world.setGUI(new robocup.view.GUI());

		world.getGUI().setExtendedState(world.getGUI().getExtendedState() | JFrame.MAXIMIZED_BOTH);
		world.getGUI().setVisible(true);
	}

	/**
	 * Creates teams from default values in configuration file.
	 */
	public static void initTeams() {
		final Properties configFile = new Properties();

		try {
			configFile.load(new FileInputStream(teamConfig));

			String ourTeamColor   = configFile.getProperty("ownTeamColor").toUpperCase();
			//there are only two colors, no need to retrieve the enemy color
			//String enemyTeamColor = configFile.getProperty("otherTeamColor").toUpperCase();
			String ourTeamName    = configFile.getProperty("ownTeam");
			String enemyTeamName  = configFile.getProperty("otherTeam");
			
			world.getReferee().getAlly().setName(ourTeamName);
			world.getReferee().getEnemy().setName(enemyTeamName);
			
			world.getReferee().setTeamColors(TeamColor.valueOf(ourTeamColor));
			
		} catch (IllegalArgumentException e) {
			LOGGER.severe("Please check the config file for type errors");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			LOGGER.severe(e.toString());
		}
		
		//World.getInstance().getGUI().update("widgetContainer");		//TURN OFF FOR TESTPATHPLANNERWIDGET & world JUNIT test
	}

	/**
	 * Initialize the client for data from SSLVISION
	 */
	public static void initProtoBuffClients() {
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream(protobufConfig));
			SSLVisionClient sslVisionClient = new SSLVisionClient(configFile.getProperty("sslVisionHost"),
					Integer.parseInt(configFile.getProperty("sslVisionPort")));
			Thread sslVisionThread = new Thread(sslVisionClient);
			sslVisionThread.start();

			RefereeClient refereeClient = new RefereeClient(configFile.getProperty("refereeHost"),
					Integer.parseInt(configFile.getProperty("refereePort")));
			Thread refereeThread = new Thread(refereeClient);
			refereeThread.start();
		} catch (IOException e) {
			LOGGER.severe(e.toString());
			System.exit(1);
		}
	}

	/**
	 * Create the handler threads for handling data from the SSLVISION client
	 */
	public static void initHandlers() {
		MainHandler handler = new MainHandler();
		Thread handlerThread = new Thread(handler);
		handlerThread.start();
	}
}