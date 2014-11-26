package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.Referee.SSL_Referee;
import robocup.model.World;
import robocup.model.enums.Color;
import robocup.model.enums.Command;
import robocup.model.enums.Stage;

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
		//System.out.println(message);
		// Update Referee
		world.getReferee().update(command, message.getCommandCounter(), message.getCommandCounter(), stage, message.getStageTimeLeft());
		//getStageTimeLeft
		
		/*
		switch(stage.name()) {
			case "NORMAL_FIRST_HALF":
			case "NORMAL_HALF_TIME":
			case "NORMAL_SECOND_HALF":
			case "EXTRA_TIME_BREAK":
			case "EXTRA_FIRST_HALF":
			case "EXTRA_HALF_TIME":
			case "EXTRA_SECOND_HALF":
			case "PENALTY_SHOOTOUT_BREAK":
				
				break;
		}*/
		
		//System.out.println("Stagename: " + stage.name());
		//System.out.println(message.getStageTimeLeft());

	  //  public int getStageTimeLeft() {
		
		
		// Update Teams
		world.getTeamByColor(Color.valueOf("BLUE")).update(message.getBlue().getName(), message.getBlue().getScore(),
				message.getBlue().getRedCards(), message.getBlue().getYellowCards(), message.getBlue().getTimeouts());
		world.getTeamByColor(Color.valueOf("YELLOW")).update(message.getYellow().getName(),
				message.getYellow().getScore(), message.getYellow().getRedCards(),
				message.getYellow().getYellowCards(), message.getYellow().getTimeouts());
		

		
		//System.out.println(" hoi," + world.getReferee().getl .getYellow().getGoalie() + " command: " + message.getYellow().getRedCards());
	}
}
