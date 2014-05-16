package controller.handlers.protohandlers;

import input.protobuf.Referee.SSL_Referee;
import model.World;
import model.enums.Color;
import model.enums.Command;
import model.enums.Stage;

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

		// Update Referee
		world.getReferee().update(command, message.getCommandCounter(), message.getCommandCounter(), stage);

		// Update Teams
		world.getTeamByColor(Color.valueOf("BLUE")).update(message.getBlue().getName(), message.getBlue().getScore(),
				message.getBlue().getRedCards(), message.getBlue().getYellowCards(), message.getBlue().getTimeouts());
		world.getTeamByColor(Color.valueOf("BLUE")).update(message.getYellow().getName(),
				message.getYellow().getScore(), message.getYellow().getRedCards(),
				message.getYellow().getYellowCards(), message.getYellow().getTimeouts());
	}
}
