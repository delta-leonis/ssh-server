package robocup.controller.handlers;

import robocup.model.Referee;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Stage;
import robocup.model.enums.Command;
import robocup.model.enums.TeamColor;

public class RefereeInputDispatcher {
	private Referee ref;
	
	public RefereeInputDispatcher() {
		ref = World.getInstance().getReferee();
	}
	
	public void assignStage(Stage argStage, int argStageTimeLeft) {
		switch (argStage) {
		case NORMAL_FIRST_HALF_PRE: 
			// all robots in standard start positions
			break;
		case NORMAL_FIRST_HALF: 
			// all robots standard behaviour when listening to commands
			break;
		case NORMAL_HALF_TIME:
			// all robots go to the side
			break;
		case NORMAL_SECOND_HALF_PRE: 
			// all robots in standard start position (except maybe a strategy change)
			break;
		case NORMAL_SECOND_HALF: 
			// all robots standard behaviour when listening to commands
			break;
		case EXTRA_TIME_BREAK: 
			// all robots go to the side
			break;
		case EXTRA_FIRST_HALF_PRE: 
			// all robots in standard start position (except maybe a strategy change)
			break;
		case EXTRA_FIRST_HALF: 
			// all robots standard behaviour when listening to commands
			break;
		case EXTRA_HALF_TIME: 
			// all robots go to the side
			break;
		case EXTRA_SECOND_HALF_PRE: 
			// all robots in standard start position (except maybe a strategy change)
			break;
		case EXTRA_SECOND_HALF: 
			// all robots standard behaviour when listening to commands
			break;
		case PENALTY_SHOOTOUT_BREAK: 
			// select best kicker
			break;
		case PENALTY_SHOOTOUT: 
			// penalty behaviour
			break;
		case POST_GAME: 
			// go to side
			break;
		}
		ref.setStage(argStage);
		ref.setStagetimeLeft(argStageTimeLeft);
	}
	
	public void assignCommand(Command argCommand, int argCommandCounter) {
		if (ref.getCommandCounter() < argCommandCounter) {
			/* execution depends on stage */
			switch (argCommand) {
			case HALT:
				executeCommandHalt();
				break;
				
			case STOP: 
				executeCommandStop();
				break;

			case NORMAL_START: 
				executeCommandNormalStart();
				break;
				
			case FORCE_START: 
				executeCommandForceStart();
				break;
				
			case PREPARE_KICKOFF_YELLOW: 
				executeCommandPrepareKickoff(TeamColor.YELLOW);
				break;
				
			case PREPARE_KICKOFF_BLUE: 
				executeCommandPrepareKickoff(TeamColor.BLUE);
				break;
				
			case PREPARE_PENALTY_YELLOW: 
				executeCommandPreparePenalty(TeamColor.YELLOW);
				break;
				
			case PREPARE_PENALTY_BLUE: 
				executeCommandPreparePenalty(TeamColor.BLUE);
				break;
				
			case DIRECT_FREE_YELLOW: 
				executeCommandPrepareDirectFree(TeamColor.YELLOW);
				break;
				
			case DIRECT_FREE_BLUE: 
				executeCommandPrepareDirectFree(TeamColor.BLUE);
				break;
				
			case INDIRECT_FREE_YELLOW: 
				executeCommandPrepareIndirectFree(TeamColor.YELLOW);
				break;
				
			case INDIRECT_FREE_BLUE: 
				executeCommandPrepareIndirectFree(TeamColor.BLUE);
				break;
				
			case TIMEOUT_YELLOW: 
				executeCommandTimeout(TeamColor.YELLOW);
				break;
				
			case TIMEOUT_BLUE: 
				executeCommandTimeout(TeamColor.BLUE);
				break;
				
			case GOAL_YELLOW: 
				executeCommandGoal(TeamColor.YELLOW);
				break;
				
			case GOAL_BLUE:
				executeCommandGoal(TeamColor.BLUE);
				break;
			}

			// change the current command
			ref.setCommand(argCommand);
			// update the command counter, since the command has changed
			ref.setCommandCounter(argCommandCounter);
		}
	}
	
	public void updateTeam(TeamColor argTeam, String argTeamName, int argScore, int argYellowCards, int argRedCards, int argTimeOuts, int argGoalie) {
		Team team = ref.getTeamByColor(argTeam);
		team.setName(argTeamName);
		team.setScore(argScore);
		team.setYellowCards(argYellowCards);
		team.setRedCards(argRedCards);
		team.setTimeoutsLeft(argTimeOuts);
		team.setGoalie(argGoalie);
	}	
	
	private void executeCommandHalt() {
		
	}
	
	private void executeCommandStop() {
		
	}
	
	private void executeCommandNormalStart() {
		
	}
	
	private void executeCommandForceStart() {
		
	}
	
	private void executeCommandPrepareKickoff(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}
	}
		
	private void executeCommandPreparePenalty(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}		
	}

	private void executeCommandPrepareDirectFree(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}
	}

	private void executeCommandPrepareIndirectFree(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}
	}
	
	private void executeCommandTimeout(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}
	}

	private void executeCommandGoal(TeamColor argTeam) {
		if (ref.getAlly().isColor(argTeam)) {
			
		} else {
			
		}
	}
}