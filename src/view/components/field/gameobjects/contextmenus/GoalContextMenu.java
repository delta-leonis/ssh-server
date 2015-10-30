package view.components.field.gameobjects.contextmenus;

import view.components.field.core.game.Game;

import javafx.scene.control.Button;


public class GoalContextMenu extends ContextMenuGO {
	
	private Button _buttonSelectTeam;
	
	public GoalContextMenu(Game game, double width, double height) {
		
		// Initialize super class
		super(game, width, height);
		
		_buttonSelectTeam = new Button("Switch to this side!");	
		
		
		
		GetControlsGroup().getChildren().add(_buttonSelectTeam);
	}
	
	
	@Override
	public void Initialize() {
		
		// Execute super Initialize method
		super.Initialize();
	}
	
	@Override
	public void Update(long timeDivNano) {
		
		// Execute super Update method
		super.Update(timeDivNano);
	}
	
	@Override
	public void Destroy() {
		
		// Execute super Destroy method
		super.Destroy();
	}

}
