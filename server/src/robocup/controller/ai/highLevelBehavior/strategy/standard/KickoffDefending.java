package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class KickoffDefending extends Strategy {
	public KickoffDefending () {
		super();
		roles.add(RobotMode.KEEPER);
		// Center robot coverer
		roles.add(RobotMode.COVERER);
		// Top side field coverer
		roles.add(RobotMode.COVERER);
		// Bottom side field coverer
		roles.add(RobotMode.COVERER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT);
			zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_MIDDLE);
		} else {
			zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_MIDDLE);
		}
	}
}
