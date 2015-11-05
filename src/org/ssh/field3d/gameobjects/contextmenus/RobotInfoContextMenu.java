package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;

import javafx.scene.control.Label;
import javafx.scene.transform.Rotate;

public class RobotInfoContextMenu extends ContextMenuGO {
	
	private Label _labelSpeed;
	private Label _labelLocation;

	public RobotInfoContextMenu(Game game) {
		
		super(game, 1000, 500);
		
		_labelSpeed = new Label();
		_labelLocation = new Label();
	}
	
	@Override
	public void Initialize() { 
		
		super.Initialize();
		
		_labelSpeed.setTranslateY(400);
		_labelSpeed.setTranslateX(500);
		_labelLocation.setTranslateX(500);
		
		_labelLocation.setTranslateY(200);
		_labelLocation.setRotationAxis(Rotate.Z_AXIS);
		_labelLocation.setRotate(180);
		_labelSpeed.setRotationAxis(Rotate.Z_AXIS);
		_labelSpeed.setRotate(180);
		
		_labelLocation.setScaleX(3.0);
		_labelLocation.setScaleY(3.0);
		_labelLocation.setScaleZ(3.0);
		_labelSpeed.setScaleX(3.0);
		_labelSpeed.setScaleY(3.0);
		_labelSpeed.setScaleZ(3.0);
		
		GetGroup().getChildren().add(_labelLocation);
		GetGroup().getChildren().add(_labelSpeed);
	}
	
	@Override
	public void Update(long timeDivNano) {
		
		super.Update(timeDivNano);
	}
	
	@Override
	public void Destroy() {
		
		super.Destroy();
	}

	
	public void SetLabelLocationText(String text) { _labelLocation.setText(text); }
	public void SetLabelSpeedText(String text) { _labelSpeed.setText(text); }
}
