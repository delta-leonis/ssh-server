package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;

import javafx.scene.control.Label;
import javafx.scene.transform.Rotate;

public class RobotInfoContextMenu extends ContextMenuGO {
    
    private final Label _labelSpeed;
    private final Label _labelLocation;
                        
    public RobotInfoContextMenu(final Game game) {
        
        super(game, 1000, 500);
        
        this._labelSpeed = new Label();
        this._labelLocation = new Label();
    }
    
    @Override
    public void Destroy() {
        
        super.Destroy();
    }
    
    @Override
    public void Initialize() {
        
        super.Initialize();
        
        this._labelSpeed.setTranslateY(400);
        this._labelSpeed.setTranslateX(500);
        this._labelLocation.setTranslateX(500);
        
        this._labelLocation.setTranslateY(200);
        this._labelLocation.setRotationAxis(Rotate.Z_AXIS);
        this._labelLocation.setRotate(180);
        this._labelSpeed.setRotationAxis(Rotate.Z_AXIS);
        this._labelSpeed.setRotate(180);
        
        this._labelLocation.setScaleX(3.0);
        this._labelLocation.setScaleY(3.0);
        this._labelLocation.setScaleZ(3.0);
        this._labelSpeed.setScaleX(3.0);
        this._labelSpeed.setScaleY(3.0);
        this._labelSpeed.setScaleZ(3.0);
        
        this.GetGroup().getChildren().add(this._labelLocation);
        this.GetGroup().getChildren().add(this._labelSpeed);
    }
    
    public void SetLabelLocationText(final String text) {
        this._labelLocation.setText(text);
    }
    
    public void SetLabelSpeedText(final String text) {
        this._labelSpeed.setText(text);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        super.Update(timeDivNano);
    }
}
