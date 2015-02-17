/**
 * Initialize the program and start all threads
 * 
 * @author Gerbrand Bosch
 */
package robocup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.UnsupportedLookAndFeelException;

import robocup.controller.handlers.protohandlers.MainHandler;
import robocup.input.BaseStationClient;
import robocup.input.RefereeClient;
import robocup.input.SSLVisionClient;
import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.Field;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Color;

public class Main {

	public static final int TEST_ROBOT_ID = 1;
	public static final int KEEPER_ROBOT_ID = 3;
	public static final int TEST_FUCK_ROBOT_ID = 4;

	public static final int TEAM_SIZE = 8;

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private static Level debugLevel = Level.INFO;
	private static String fieldConfigName = "config/field.properties";
	private static String teamConfig = "config/teams.properties";
	private static String protobufConfig = "config/config.properties";
	private static String aiConfig = "config/config.properties";

	public static void main(String[] args) {
		initLog();
		LOGGER.info("Program started");
		initView();
		LOGGER.info("View initialized");
		initBasestationClient();
		LOGGER.info("BasestationClient initialized");
		initField();
		LOGGER.info("Field initialized");
		initTeams();
		LOGGER.info("Teams initialized");
		initProtoBuffClients();
		LOGGER.info("Handlers initialized");
		initHandlers();
		LOGGER.info("AI initialized");
		initAi();
		LOGGER.info("Console disabled");
		// console();
	}

	/**
	 * Create the root logger from this application.
	 * Disable the console logger and set the warning level.
	 * 
	 * @author Gerbrand Bosch
	 */
	public static void initLog() {
		try {
			LOGGER.setUseParentHandlers(false);
			FileHandler fileTxt = new FileHandler("Logging.txt");
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			LOGGER.addHandler(fileTxt);
			LOGGER.setLevel(debugLevel);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			System.err.println("LOGGER could not start");
			LOGGER.setUseParentHandlers(true);
		}
	}

	/**
	 * Set a fancy lookAndFeel and start the view GUI
	 * 
	 * @author Gerbrand Bosch
	 */
	public static void initView() {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.warning("view lookAndFeel can't started");
		}
		//new robocup.view.GUI(World.getInstance()).setVisible(true);
		new robocup.migView.GUI(World.getInstance()).setVisible(true);
	}

	/**
	 * Initialize the field using fieldConfigName
	 * 
	 * @author Gerbrand Bosch
	 */
	public static void initField() {
		Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream(fieldConfigName));
			World.getInstance().setField(
					new Field(	Integer.parseInt(configFile.getProperty("length")),
								Integer.parseInt(configFile.getProperty("width")), 
								Integer.parseInt(configFile.getProperty("lineWidth")), 
								Integer.parseInt(configFile.getProperty("boundaryWidth")), 
								Integer.parseInt(configFile.getProperty("refereeWidth")), 
								Integer.parseInt(configFile.getProperty("centerCircleRadius")), 
								Integer.parseInt(configFile.getProperty("defenceRadius")),
								Integer.parseInt(configFile.getProperty("defenceStretch")),
								Integer.parseInt(configFile.getProperty("freeKickFromDefenceDistance")),
								Integer.parseInt(configFile.getProperty("penaltySpotFromFieldLineDistance")), 
								Integer.parseInt(configFile.getProperty("penaltyLineFromSpotDistance")), 
								Integer.parseInt(configFile.getProperty("goalWidth")),
								Integer.parseInt(configFile.getProperty("goalDepth")), 
								Integer.parseInt(configFile.getProperty("goalWallWidth")), 
								Integer.parseInt(configFile.getProperty("goalHeight")), 
								Integer.parseInt(configFile.getProperty("cameraOverlapZoneWidth"))));
		} catch (IOException e) {
			LOGGER.severe("Field config cannot be read, please make sure the fieldConfig file exist and is correctly set");
			System.exit(1);
		}
	}

	/**
	 * Creates teams from default values in configuration file.
	 * 
	 * @author Gerbrand Bosch
	 */
	public static void initTeams() {
		World w = World.getInstance();
		final Properties configFile = new Properties();
		Color ownTeamColor = null;
		Color otherTeamColor = null;
		try {
			configFile.load(new FileInputStream(teamConfig));

			ownTeamColor = Color.valueOf(configFile.getProperty("ownTeamColor").toUpperCase());
			otherTeamColor = Color.valueOf(configFile.getProperty("otherTeamColor").toUpperCase());

			w.setAlly(new Team(configFile.getProperty("ownTeam"), ownTeamColor));
			for(int i=0; i < TEAM_SIZE; i++)
				w.getAlly().addRobot(new Ally(i, false, 150));
			
			
			w.setEnemy(new Team(configFile.getProperty("otherTeam"), otherTeamColor));

			for(int i=0; i < TEAM_SIZE; i++)
				w.getEnemy().addRobot(new Enemy(i, false, 150));
			w.setOwnTeamColor(ownTeamColor);

		} catch (IllegalArgumentException e) {
			LOGGER.severe("Please check the config file for type errors");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			LOGGER.severe(e.toString());
		}
	}

	/**
	 * Initialize the client for data from SSLVISION
	 * 
	 * @author Gerbrand Bosch
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
	 * Create a client for the base station for receiving data from the robots.
	 * 
	 * @author Gerbrand Bosch
	 */
	public static void initBasestationClient() {
		new Thread(new BaseStationClient()).start();
	}

	/**
	 * Create the handler threads for handling data from the SSLVISION client
	 */
	public static void initHandlers() {
		MainHandler handler = new MainHandler(World.getInstance());
		Thread handlerThread = new Thread(handler);
		handlerThread.start();
	}

	/**
	 * Read data from the console
	 * 
	 * @author Gerbrand Bosch
	 * @deprecated Unused function
	 */
	public static void console() {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		World w = World.getInstance();
		while (true) {
			String s = null;
			try {
				s = bufferRead.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (s.toLowerCase().equals("tostring"))
				System.out.println(World.getInstance().toString());
			if (s.toLowerCase().equals("ownteam"))
				System.out.println(w.getTeamByColor(w.getOwnTeamColor()).getRobots().toString());
			if (s.toLowerCase().equals("stop"))
				World.getInstance().getReferee().setStart(false);
			if (s.toLowerCase().equals("start"))
				World.getInstance().getReferee().setStart(true);
		}
	}

	/**
	 * Initialize the intelligence
	 * 
	 * @author Gerbrand Bosch
	 * @deprecated Unused function
	 */
	public static void initAi() {
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream(aiConfig));
			new robocup.controller.ai.Main();
		} catch (IOException e) {
			LOGGER.severe(e.toString());
			System.exit(1);
		}
	}
}
