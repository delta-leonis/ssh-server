package org.ssh.ui.components2;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import org.ssh.managers.manager.Models;
import org.ssh.models.Game;
import org.ssh.models.Robot;
import org.ssh.models.enums.Allegiance;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.components.RobotStatus;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Jeroen de Jong
 * @date 12/23/2015
 */
public class TopContent extends UIComponent2<GridPane> {

    @FXML
    private FlowPane robotStatusContainer;

    public TopContent() {
        super("topElementswrapper", "topsection/topcontent.fxml");

        fillRobotContainer();
    }

    private void fillRobotContainer() {
        Optional<Game> game = Models.<Game> get("game");

        if(!game.isPresent())
            return;

        // Get all robot-models
        game.get()
                .getRobots(Allegiance.ALLY).stream()
                .sorted((r1, r2) ->
                    Integer.compare(r1.getRobotId(),
                                    r2.getRobotId()))
                .forEach(robot -> {

            // A new robotstatus is made and added to the robotstatuscontainer
            final RobotStatus robotStatus = new RobotStatus(robot);
            this.robotStatusContainer.getChildren().add(robotStatus);

            // fix resizing
            robotStatus.getRobotstatusRoot().prefHeightProperty()
                    .bind(Bindings.divide(robotStatusContainer.heightProperty(), 2.05));
            robotStatus.getRobotstatusRoot().prefWidthProperty()
                    .bind(Bindings.divide(robotStatusContainer.heightProperty(), 2.75));


        });
    }

}
