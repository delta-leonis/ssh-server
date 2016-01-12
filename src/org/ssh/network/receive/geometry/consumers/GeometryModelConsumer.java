package org.ssh.network.receive.geometry.consumers;

import org.ssh.field3d.FieldGame;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Field;
import org.ssh.models.Game;
import org.ssh.models.Goal;
import org.ssh.models.enums.Direction;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.services.AbstractConsumer;
import org.ssh.ui.windows.MainWindow;
import protobuf.Geometry.GeometryFieldSize;

/**
 * Class that consumes the parsed {@link GeometryPacket}s and updates the {@link Field} and all
 * {@link Goal}s. Also calls {@link FieldGame#updateGeometry()}
 * 
 * @author Jeroen de Jong
 *        
 */
public class GeometryModelConsumer extends AbstractConsumer<GeometryPacket> {
    
    /** Reference to FieldGame in GUI, used for updating */
    private FieldGame fieldGame;

    private Game game;
    
    /**
     * creates a new modelconsumer for {@link GeometryPacket}s.
     * 
     * @param name
     *            name of this consumer
     */
    public GeometryModelConsumer(String name) {
        super(name);
    }
    
    @Override
    public boolean consume(GeometryPacket pipelinePacket) {
        if(fieldGame == null)
            // Getting reference to the main window
            UI.<MainWindow> get("main").ifPresent(main ->
                fieldGame = main.getFieldGame());
        
        if(fieldGame == null)
            return false;

        if(game == null)
            // Getting reference to the main window
            Models.<Game> get("game").ifPresent(gameModel ->
                    this.game = gameModel);

        if(game == null)
            return false;

        Models.<Field> get("field").ifPresent(field -> {
            // read received data
            GeometryFieldSize fieldData = pipelinePacket.read().getField();
            
            // Check if there is new data
            if (!field.getFieldSize().equals(fieldData)) {
                // Update field
                field.update("field", fieldData);
                
                Models.<Goal>getAll("goal").forEach(goal -> {
                    // get position modifier (+1 or -1 according to field position
                    int modifier = game.getSide(goal.getAllegiance()).equals(Direction.EAST) ? 1 : -1;
                    
                    // update goal
                    goal.update("x",
                            Float.valueOf((field.getFieldLength() + fieldData.getGoalDepth()) / 2 * modifier),
                            "goalDepth",
                            fieldData.getGoalDepth(),
                            "goalWidth",
                            fieldData.getGoalWidth());
                });
            }
        });
        
        // Update geometry
        fieldGame.updateGeometry();
        return true;
    }
    
}
