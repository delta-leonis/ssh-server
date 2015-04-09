package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.Referee.SSL_Referee;
import robocup.model.World;
import robocup.model.enums.Command;
import robocup.model.enums.Stage;
import robocup.model.enums.TeamColor;

public class RefereeHandler {

	private World world;

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
		world.getTeamByColor(TeamColor.valueOf("BLUE")).update(message.getBlue().getName(), message.getBlue().getScore(),
				message.getBlue().getRedCards(), message.getBlue().getYellowCards(), message.getBlue().getTimeouts(),
				message.getBlue().getGoalie());
		world.getTeamByColor(TeamColor.valueOf("YELLOW")).update(message.getYellow().getName(),
				message.getYellow().getScore(), message.getYellow().getRedCards(),
				message.getYellow().getYellowCards(), message.getYellow().getTimeouts(),
				message.getYellow().getGoalie());

		World.getInstance().getGUI().update("sectionContainer");
	}
}
