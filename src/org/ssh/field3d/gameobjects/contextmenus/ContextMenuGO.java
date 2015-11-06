/**************************************************************************************************
 *
 * ContextMenuGO This class is for our 3d context menus.
 *
 **************************************************************************************************
 *
 * TODO: change size according to zoom of camera TODO: javadoc TODO: comment TODO: cleanup
 *
 **************************************************************************************************
 * @see GameObject
 *      
 * @author marklef2
 * @date 15-10-2015
 */
package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.math.Xform;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ContextMenuGO extends GameObject {
    
    private final Rectangle _rectangle;
    private final Xform     _xform;
    private final Xform     _xform2;
    private final Xform     _xform3;
    private final Group     _contextMenuGroup, _controlsGroup;
    private final double    _width, _height;
    private Vector3f        _loc;
                            
    public ContextMenuGO(final Game game, final double width, final double height) {
        
        super(game);
        
        this._xform = new Xform();
        this._xform2 = new Xform();
        this._xform3 = new Xform();
        this._contextMenuGroup = new Group();
        this._controlsGroup = new Group();
        
        this._loc = new Vector3f();
        
        this._width = width;
        this._height = height;
        
        this._rectangle = new Rectangle(width, height);
        this._rectangle.setFill(new Color(0.0, 0.0, 1.0, 0.75));
        
        this._xform.getChildren().add(this._xform2);
        this._xform2.getChildren().add(this._xform3);
        this._xform3.getChildren().add(this._contextMenuGroup);
        this._contextMenuGroup.getChildren().add(this._controlsGroup);
        this._controlsGroup.getChildren().add(this._rectangle);
        this._rectangle.setTranslateZ(10);
        
        this._xform3.rx.setAngle(180);
        this._xform3.ry.setAngle(180);
        
        this._contextMenuGroup.setVisible(false);
        this._contextMenuGroup.setTranslateX(-width / 2.0);
        this._contextMenuGroup.setTranslateY(-height / 2.0);
    }
    
    @Override
    public void Destroy() {
    }
    
    protected Group GetControlsGroup() {
        return this._controlsGroup;
    }
    
    protected Group GetGroup() {
        return this._contextMenuGroup;
    }
    
    public double GetHeight() {
        return this._height;
    }
    
    public double GetWidth() {
        return this._width;
    }
    
    public void Hide() {
        this._contextMenuGroup.setVisible(false);
    }
    
    @Override
    public void Initialize() {
        
        // Add context menu to world
        this.GetGame().GetWorldGroup().getChildren().add(this._xform);
    }
    
    public void Rotate(final double x, final double y, final double z) {
        
        this._xform2.setRotate(x, y, z);
    }
    
    public void Show() {
        this._contextMenuGroup.setVisible(true);
    }
    
    public void Translate(final double x, final double y, final double z) {
        
        this._xform.setTranslate(x, y, z);
        this._loc = new Vector3f((float) x, (float) y, (float) z);
    }
    
    @Override
    public void Update(final long timeDivNano) {
        
        // Calculate scale of the context menu
        double scale = 1 - ((this.GetGame().GetMouseInputHandler().GetScrollWheelYValue() + 1000.0) / 2000.0);
        scale += 0.14; // Add minimal scale
        
        // Rotate towards camera
        this.Rotate(this.GetGame().GetThirdPersonCamera().GetRotateX(),
                this.GetGame().GetThirdPersonCamera().GetRotateY(),
                0.0);
        // Translate to location
        this._xform.setTranslate(this._loc.x, this._loc.y + (scale * 5 * (this._height / 2.0)), this._loc.z);
        // Scale
        this._xform3.setScale(scale * 5, scale * 5, scale * 5);
    }
}
