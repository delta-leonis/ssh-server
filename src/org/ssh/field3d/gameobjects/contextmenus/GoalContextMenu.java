package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;

import javafx.scene.control.Button;

/**
 * GoalContextMenu class. This class represents the context menu for the goals.
 * 
 * @author Mark Lefering
 */
// TODO: fxml
public class GoalContextMenu extends ContextMenuGO {
    
    /** The button for selecting a team. */
    private final Button buttonSelectTeam;
    
    /**
     * Constructor.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}
     * @param width
     *            The width of the context menu.
     * @param height
     *            The height of the context menu.
     */
    public GoalContextMenu(final Game game, final double width, final double height) {
        
        // Initialize super class
        super(game, width, height);
        
        // Creating new button
        this.buttonSelectTeam = new Button("Switch to this side!");
        
        // Adding button to the controls group
        this.getControlsGroup().getChildren().add(this.buttonSelectTeam);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Execute super Destroy method
        super.onDestroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Execute super Initialize method
        super.onInitialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
        
        // Execute super Update method
        super.onUpdate(timeDivNano);
    }
    
}
