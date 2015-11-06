/**
 *
 * Transformation class, handles 3d transformation
 *
 * @author marklef2
 * @date 29-9-2015
 */
package org.ssh.field3d.core.transformation;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class Transform extends Group {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Translate _position;
    private Translate _pivot;
    private Translate _invertedPivot;
    private Scale     _scale;
    private Rotate    _rotateX, _rotateY, _rotateZ;
                      
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Constructors
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Constructor
     * 
     */
    public Transform() {
        
        // Creating new objects
        this._position = new Translate();
        this._pivot = new Translate();
        this._scale = new Scale();
        
        this._rotateX = new Rotate();
        this._rotateY = new Rotate();
        this._rotateZ = new Rotate();
        
        // Setting rotation axis
        this._rotateX.setAxis(Rotate.X_AXIS);
        this._rotateY.setAxis(Rotate.Y_AXIS);
        this._rotateZ.setAxis(Rotate.Z_AXIS);
        
        // Setting inverted position
        this._invertedPivot = this._pivot.createInverse();
        
        // Clear transforms
        this.getTransforms().clear();
        // Setting transforms
        this.getTransforms().addAll(this._position, this._rotateZ, this._rotateY, this._rotateX);
    }
    
    /*
     * public void update() {
     * 
     * // Clear transforms getTransforms().clear(); // Setting transforms
     * getTransforms().addAll(_position, _pivot, _rotateZ, _rotateY, _rotateX, _scale,
     * _invertedPivot); }
     */
    
    public Translate getInvertedPivot() {
        return this._invertedPivot;
    }
    
    public Translate getPivot() {
        return this._pivot;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public Translate getPosition() {
        return this._position;
    }
    
    public Rotate getRotateX() {
        return this._rotateX;
    }
    
    public Rotate getRotateY() {
        return this._rotateY;
    }
    
    public Rotate getRotateZ() {
        return this._rotateZ;
    }
    
    public Scale getScale() {
        return this._scale;
    }
    
    public void setInvertedPivot(final Point3D invertedPivot) {
        
        // Setting inverted pivot
        this._invertedPivot.setX(invertedPivot.getX());
        this._invertedPivot.setY(invertedPivot.getY());
        this._invertedPivot.setZ(invertedPivot.getZ());
        
        // Setting pivot
        this._pivot.setX(invertedPivot.getX());
        this._pivot.setY(invertedPivot.getY());
        this._pivot.setZ(invertedPivot.getZ());
    }
    
    /////////////////////////////////////////////
    //
    // Inverted pivot
    //
    /////////////////////////////////////////////
    public void setInvertedPivot(final Translate invertedPivot) {
        
        // Setting inverted pivot
        this._invertedPivot = invertedPivot;
        
        // Setting pivot
        this._pivot = this._invertedPivot.createInverse();
    }
    
    public void setPivot(final Point3D pivot) {
        
        // Setting pivot
        this._pivot.setX(pivot.getX());
        this._pivot.setY(pivot.getY());
        this._pivot.setZ(pivot.getZ());
        
        // Setting inverted pivot
        this._invertedPivot.setX(-pivot.getX());
        this._invertedPivot.setY(-pivot.getY());
        this._invertedPivot.setZ(-pivot.getZ());
    }
    
    /////////////////////////////////////////////
    //
    // Pivot
    //
    /////////////////////////////////////////////
    public void setPivot(final Translate pivot) {
        
        // Setting pivot
        this._pivot = pivot;
        
        // Setting inverted pivot
        this._invertedPivot = this._pivot.createInverse();
    }
    
    public void setPosition(final Point3D position) {
        
        // Setting position
        this._position.setX(position.getX());
        this._position.setY(position.getY());
        this._position.setZ(position.getZ());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setPosition(final Translate position) {
        
        // Setting position
        this._position = position;
    }
    
    public void setRotateX(final float angleX) {
        this._rotateX.setAngle(angleX);
    }
    
    /////////////////////////////////////////////
    //
    // X-Rotation
    //
    /////////////////////////////////////////////
    public void setRotateX(final Rotate rotateX) {
        this._rotateX = rotateX;
    }
    
    public void setRotateY(final float angleY) {
        this._rotateY.setAngle(angleY);
    }
    
    /////////////////////////////////////////////
    //
    // Y-Rotation
    //
    /////////////////////////////////////////////
    public void setRotateY(final Rotate rotateY) {
        this._rotateY = rotateY;
    }
    
    public void setRotateZ(final float angleZ) {
        this._rotateZ.setAngle(angleZ);
    }
    
    /////////////////////////////////////////////
    //
    // Z-Rotation
    //
    /////////////////////////////////////////////
    public void setRotateZ(final Rotate rotateZ) {
        this._rotateZ = rotateZ;
    }
    
    public void setScale(final float scale) {
        
        this._scale.setX(scale);
        this._scale.setY(scale);
        this._scale.setZ(scale);
    }
    
    public void setScale(final Point3D scale) {
        
        this._scale.setX(scale.getX());
        this._scale.setY(scale.getY());
        this._scale.setZ(scale.getZ());
    }
    
    /////////////////////////////////////////////
    //
    // Scale
    //
    /////////////////////////////////////////////
    public void setScale(final Scale scale) {
        this._scale = scale;
    }
    
    @Override
    public String toString() {
        
        // TODO: complete method
        String rtn = "";
        
        rtn += this.getClass().getName() + ": Position; ";
        
        return rtn;
    }
}
