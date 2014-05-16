package init;

import input.RefereeClient;
import input.SSLVisionClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import output.ComInterface;
import output.RobotCom;
import model.Field;
import model.Team;
import model.World;
import model.enums.Color;
import controller.handlers.protohandlers.MainHandler;

public class Main {
	public static void main(String[] args) {
		System.out.println("Start Program");
		@SuppressWarnings("unused")
		World w = World.getInstance();
		initField();
		initTeams();
		initProtoBuffClients();
		initHandlers();
		initAi();
		console();
	}

	public static void initField() {
		Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/field.properties"));
			World.getInstance().setField(
					new Field(Integer.parseInt(configFile.getProperty("length")), Integer.parseInt(configFile
							.getProperty("width")), Integer.parseInt(configFile.getProperty("lineWidth")), Integer
							.parseInt(configFile.getProperty("boundaryWidth")), Integer.parseInt(configFile
							.getProperty("refereeWidth")), Integer.parseInt(configFile
							.getProperty("centerCircleRadius")), Integer.parseInt(configFile
							.getProperty("defenceRadius")), Integer.parseInt(configFile.getProperty("defenceStretch")),
							Integer.parseInt(configFile.getProperty("freeKickFromDefenceDistance")), Integer
									.parseInt(configFile.getProperty("penaltySpotFromFieldLineDistance")), Integer
									.parseInt(configFile.getProperty("penaltyLineFromSpotDistance")), Integer
									.parseInt(configFile.getProperty("goalWidth")), Integer.parseInt(configFile
									.getProperty("goalDepth")), Integer.parseInt(configFile
									.getProperty("goalWallWidth")), Integer.parseInt(configFile
									.getProperty("goalHeight")), 500));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates teams from default values in config file.
	 */
	public static void initTeams() {
		World w = World.getInstance();
		final Properties configFile = new Properties();
		Color ownTeamColor = null;
		Color otherTeamColor = null;
		String ownTeamSide = "";
		String otherTeamSide = "";
		
		try {
			configFile.load(new FileInputStream("config/teams.properties"));

			ownTeamColor = Color.valueOf(configFile.getProperty("ownTeamColor").toUpperCase());
			otherTeamColor = Color.valueOf(configFile.getProperty("otherTeamColor").toUpperCase());
			ownTeamSide = configFile.getProperty("ownTeamSide").toLowerCase();
			otherTeamSide = configFile.getProperty("otherTeamSide").toLowerCase();
			
			w.setAlly(new Team(configFile.getProperty("ownTeam"), ownTeamColor, ownTeamSide));
			w.setEnemy(new Team(configFile.getProperty("otherTeam"), otherTeamColor, otherTeamSide));
			w.setOwnTeamCollor(ownTeamColor);

		} catch (IllegalArgumentException e) {
			System.err.println("Please check the config file for type errors");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initProtoBuffClients() {
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/config.properties"));
			SSLVisionClient sslVisionClient = new SSLVisionClient(configFile.getProperty("sslVisionHost"),
					Integer.parseInt(configFile.getProperty("sslVisionPort")));
			Thread sslVisionThread = new Thread(sslVisionClient);
			sslVisionThread.start();

			RefereeClient refereeClient = new RefereeClient(configFile.getProperty("refereeHost"),
					Integer.parseInt(configFile.getProperty("refereePort")));
			Thread refereeThread = new Thread(refereeClient);
			refereeThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void initHandlers() {
		MainHandler handler = new MainHandler(World.getInstance());
		Thread handlerThread = new Thread(handler);
		handlerThread.start();
	}

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
		}
	}

	public static void initAi(){
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/config.properties"));
			ComInterface output = new RobotCom(configFile.getProperty("outputAddress"), Integer.parseInt(configFile.getProperty("ownTeamOutputPort")));
			controller.ai.Main main = new controller.ai.Main(output);
					
//					configFile.getProperty("outputAddress"),
//					Integer.parseInt(configFile.getProperty("ownTeamOutputPort")));
			new Thread(main).start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}