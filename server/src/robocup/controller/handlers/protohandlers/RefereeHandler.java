package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.Referee.SSL_Referee;
import robocup.model.World;
import robocup.model.enums.Command;
import robocup.model.enums.Stage;
import robocup.model.enums.TeamColor;

/**
 * Handles {@link SSL_Referee} messages,<br>
 * processes messages for both Enemy as Ally {@link Team teams}. Other messages are pushed to the {@link robocup.model.Referee Referee} found in {@link World} object.
 */
public class RefereeHandler {

	private World world;

	/**
	 * construct Referee for a specific {@link World}
	 * @param world	world this RefereeHandler will affect
	 */
	public RefereeHandler(World world) {
		this.world = world;
	}

	/**
	 * Processes a referee frame
	 */
	public void process(SSL_Referee message) {
		Stage stage = Stage.valueOf(message.getStage().toString());
		Command command = Command.valueOf(message.getCommand().toString());
		// System.out.println(message);
		// Update Referee
		world.getReferee().update(command, message.getCommandCounter(), message.getCommandCounter(), stage,
				message.getStageTimeLeft());
		// getStageTimeLeft

		// Update Teams
		world.getTeamByColor(TeamColor.BLUE).update(message.getBlue().getName(), message.getBlue().getScore(),
				message.getBlue().getRedCards(), message.getBlue().getYellowCards(), message.getBlue().getTimeouts(),
				message.getBlue().getGoalie());
		world.getTeamByColor(TeamColor.YELLOW).update(message.getYellow().getName(),
				message.getYellow().getScore(), message.getYellow().getRedCards(),
				message.getYellow().getYellowCards(), message.getYellow().getTimeouts(),
				message.getYellow().getGoalie());

		World.getInstance().getGUI().update("sectionContainer");
	}
}
