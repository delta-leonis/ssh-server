package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;

import javafx.scene.control.Button;

public class GoalContextMenu extends ContextMenuGO {
    
    private final Button _buttonSelectTeam;
    
    public GoalContextMenu(final Game game, final double width, final double height) {
        
        // Initialize super class
        super(game, width, height);
        
        this._buttonSelectTeam = new Button("Switch to this side!");
        
        this.GetControlsGroup().getChildren().add(this._buttonSelectTeam);
    }
    
    @Override
    public void Destroy() {
        
        // Execute super Destroy method
        super.Destroy();
    }
    
    @Override
    public void Initialize() {
        
        // Execute super Initialize method
        super.Initialize();
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        // Execute super Update method
        super.Update(timeDivNano);
    }
    
}
