package org.ssh.network.receive.referee.consumers;

import org.ssh.managers.manager.Models;
import org.ssh.models.Game;
import org.ssh.models.Referee;
import org.ssh.models.Team;
import org.ssh.models.enums.TeamColor;
import org.ssh.pipelines.packets.RefereePacket;
import org.ssh.services.AbstractConsumer;
import protobuf.RefereeOuterClass;

/**
 * Consumes {@link RefereePacket RefereePackets} and uses them to update the {@link Referee} and {@link Team teams}
 * @author Thomas Hakkers
 */
public class RefereeModelConsumer extends AbstractConsumer<RefereePacket> {

    /** helperclass containing information about the game */
    private Game game;

    public RefereeModelConsumer(String name){
        super(name);
    }

    @Override
    public boolean consume(RefereePacket packet){
        // Read the packet
        RefereeOuterClass.Referee referee = packet.read();

        // Update the Referee
        Models.<Referee>get("referee").ifPresent(refereeModel -> {
                    refereeModel.update(
                            "commandCounter", referee.getCommandCounter(),
                            "lastPacketTimestamp", referee.getPacketTimestamp(),
                            "stageTimeLeft", referee.getStageTimeLeft());
                    refereeModel.updateCommand(referee.getCommand());
                    refereeModel.updateCurrentStage(referee.getStage());
                }
        );
        // Update the Teams
        // Check whether game is even available
        if(game != null){
            // Go through each team
            Models.<Team>getAll("team").stream()
                    .forEach(team -> {
                        // Check their allegiance and update the teams accordingly
                        if(team.getAllegiance() == game.getAllegiance(TeamColor.YELLOW)){
                            team.update(referee.getYellow());
                        }
                        else if(team.getAllegiance() == game.getAllegiance(TeamColor.BLUE)){
                            team.update(referee.getBlue());
                        }
                    });
        }
        else {
            LOG.fine("Could not find \"Game\"");
            // If game was null, see if there's a new game available.
            Models.<Game>get("game").ifPresent(game ->
                this.game = game);
            return false;
        }
        return true;
    }
}