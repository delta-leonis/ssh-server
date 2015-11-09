package org.ssh.field3d.core.math;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * The Xform class. This class is used for the 3d rotations and translations.
 *
 * @author Mark Lefering
 */
public class Xform extends Group {
    
    /**
     * The Enum RotateOrder.
     */
    public enum RotateOrder {
        
        /** The x,y,z. */
        XYZ,
        /** The x,z,y. */
        XZY,
        /** The y,x,z. */
        YXZ,
        /** The y,z,x. */
        YZX,
        /** The z,x,y. */
        ZXY,
        /** The z,y,x. */
        ZYX
    }
    
    /** The translation. */
    public Translate translate    = new Translate();
                                  
    /** The pivot translations. */
    public Translate pivot        = new Translate();
                                  
    /** The inverse pivot translation. */
    public Translate inversePivot = new Translate();
                                  
    /** The rotation around the x-axis. */
    public Rotate    rotationX    = new Rotate();
                                  
    /** The rotation around the y-axis. */
    public Rotate    rotationY    = new Rotate();
                                  
    /** The rotation around the z-axis. */
    public Rotate    rotationZ    = new Rotate();
                                  
    /** The scale. */
    public Scale     scale        = new Scale();
                                  
    /**
     * Instantiates a new Xform.
     */
    public Xform() {
        
        // Initialize super class
        super();
        
        // Setting rotation axes
        this.rotationX.setAxis(Rotate.X_AXIS);
        this.rotationY.setAxis(Rotate.Y_AXIS);
        this.rotationZ.setAxis(Rotate.Z_AXIS);
        
        // Adding transformations
        this.getTransforms().addAll(this.translate, this.rotationZ, this.rotationY, this.rotationX, this.scale);
    }
    
    /**
     * Instantiates a new xform.
     *
     * @param rotateOrder
     *            The rotation order.
     */
    public Xform(final RotateOrder rotateOrder) {
        
        // Initialize super class
        super();
        
        // Choose order of rotation
        switch (rotateOrder) {
            case XYZ:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationZ,
                        this.rotationY,
                        this.rotationX,
                        this.scale,
                        this.inversePivot);
                break;
                
            case XZY:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationY,
                        this.rotationZ,
                        this.rotationX,
                        this.scale,
                        this.inversePivot);
                break;
                
            case YXZ:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationZ,
                        this.rotationX,
                        this.rotationY,
                        this.scale,
                        this.inversePivot);
                break;
                
            case YZX:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationX,
                        this.rotationZ,
                        this.rotationY,
                        this.scale,
                        this.inversePivot);
                break;
                
            case ZXY:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationY,
                        this.rotationX,
                        this.rotationZ,
                        this.scale,
                        this.inversePivot);
                break;
            case ZYX:
                this.getTransforms().addAll(this.translate,
                        this.pivot,
                        this.rotationX,
                        this.rotationY,
                        this.rotationZ,
                        this.scale,
                        this.inversePivot);
                break;
        }
    }
    
    /**
     * reset method. This method resets all the variables used by the class.
     */
    public void reset() {
        this.translate.setX(0.0);
        this.translate.setY(0.0);
        this.translate.setZ(0.0);
        this.rotationX.setAngle(0.0);
        this.rotationY.setAngle(0.0);
        this.rotationZ.setAngle(0.0);
        this.scale.setX(1.0);
        this.scale.setY(1.0);
        this.scale.setZ(1.0);
        this.pivot.setX(0.0);
        this.pivot.setY(0.0);
        this.pivot.setZ(0.0);
        this.inversePivot.setX(0.0);
        this.inversePivot.setY(0.0);
        this.inversePivot.setZ(0.0);
    }
    
    /**
     * resetTSP method. This method only resets the translations.
     */
    public void resetTSP() {
        this.translate.setX(0.0);
        this.translate.setY(0.0);
        this.translate.setZ(0.0);
        this.scale.setX(1.0);
        this.scale.setY(1.0);
        this.scale.setZ(1.0);
        this.pivot.setX(0.0);
        this.pivot.setY(0.0);
        this.pivot.setZ(0.0);
        this.inversePivot.setX(0.0);
        this.inversePivot.setY(0.0);
        this.inversePivot.setZ(0.0);
    }
    
    /**
     * Sets the pivot of the transformation.
     *
     * @param x
     *            The x-coordinate.
     * @param y
     *            The y-coordinate.
     * @param z
     *            The z-coordinate.
     */
    public void setPivot(final double x, final double y, final double z) {
        
        // Setting pivot
        this.pivot.setX(x);
        this.pivot.setY(y);
        this.pivot.setZ(z);
        // Setting inverse pivot
        this.inversePivot.setX(-x);
        this.inversePivot.setY(-y);
        this.inversePivot.setZ(-z);
    }
    
    /**
     * Sets the rotation of the entire transformation.
     *
     * @param x
     *            The angle to rotate around x-axis.
     * @param y
     *            The angle to rotate around y-axis.
     * @param z
     *            The angle to rotate around z-axis.
     */
    public void setRotate(final double x, final double y, final double z) {
        
        // Rotate around x-axis
        this.rotationX.setAngle(x);
        // Rotate around y-axis
        this.rotationY.setAngle(y);
        // Rotate around z-axis
        this.rotationZ.setAngle(z);
    }
    
    /**
     * Sets the rotation angle around the x-axis.
     *
     * @param x
     *            The new rotation angle around the x-axis.
     */
    public void setRotateX(final double x) {
        
        // Set the rotation around the x-axis
        this.rotationX.setAngle(x);
    }
    
    /**
     * Sets the rotation angle around the y-axis.
     *
     * @param y
     *            The new rotation angle around the y-axis.
     */
    public void setRotateY(final double y) {
        
        // Set the rotation around the y-axis
        this.rotationY.setAngle(y);
    }
    
    /**
     * Sets the rotation angle around the z-axis.
     *
     * @param z
     *            The new rotation angle around the z-axis.
     */
    public void setRotateZ(final double z) {
        
        // Set the rotation around the z-axis
        this.rotationZ.setAngle(z);
    }
    
    /**
     * Sets the scale of the transformation.
     *
     * @param scaleFactor
     *            The new scale factor.
     */
    public void setScale(final double scaleFactor) {
        
        // Scale all axes
        this.scale.setX(scaleFactor);
        this.scale.setY(scaleFactor);
        this.scale.setZ(scaleFactor);
    }
    
    /**
     * Sets the scale of the transformation.
     *
     * @param x
     *            The new scale of the x-axis.
     * @param y
     *            The new scale of the y-axis.
     * @param z
     *            The new scale of the z-axis.
     */
    public void setScale(final double x, final double y, final double z) {
        
        // Set scale of the x-axis
        this.scale.setX(x);
        // Set scale of the y-axis
        this.scale.setY(y);
        //
        this.scale.setZ(z);
    }
    
    /**
     * Sets the scale of the x-axis.
     *
     * @param x
     *            The new scale of the x-axis.
     */
    public void setSx(final double x) {
        
        // Set the scale of the x-axis
        this.scale.setX(x);
    }
    
    /**
     * Sets the scale of the y-axis.
     *
     * @param y
     *            The new scale of the y-axis.
     */
    public void setSy(final double y) {
        
        // Set the scale of the y-axis
        this.scale.setY(y);
    }
    
    /**
     * Sets the scale of the z-axis.
     *
     * @param z
     *            The new scale of the z-axis.
     */
    public void setSz(final double z) {
        
        // Set the scale of the z-axis
        this.scale.setZ(z);
    }
    
    /**
     * Sets the translation on the x-axis & y-axis.
     *
     * @param x
     *            The new translation on the x-axis.
     * @param y
     *            The new translation on the y-axis.
     */
    public void setTranslate(final double x, final double y) {
        
        // Set the translation on the x-axis
        this.translate.setX(x);
        // Set the translation on the y-axis
        this.translate.setY(y);
    }
    
    /**
     * Sets the translation on the x-axis, y-axis & z-axis.
     *
     * @param x
     *            The new translation on the x-axis.
     * @param y
     *            The new translation on the y-axis.
     * @param z
     *            The new translation on the z-axis.
     */
    public void setTranslate(final double x, final double y, final double z) {
        
        // Set the translation on the x-axis
        this.translate.setX(x);
        // Set the translation on the y-axis
        this.translate.setY(y);
        // Set the translation on the z-axis
        this.translate.setZ(z);
    }
    
    /**
     * Sets the new translation on the x-axis.
     *
     * @param x
     *            The new translation on the x-axis.
     */
    public void setTx(final double x) {
        
        // Set translation on x-axis
        this.translate.setX(x);
    }
    
    /**
     * Sets the new translation on the y-axis.
     *
     * @param y
     *            The new translation on the y-axis.
     */
    public void setTy(final double y) {
        
        // Set translation on y-axis
        this.translate.setY(y);
    }
    
    /**
     * Sets the new translation on the z-axis.
     *
     * @param z
     *            The new translation on the z-axis.
     */
    public void setTz(final double z) {
        
        // Set translation on y-axis
        this.translate.setZ(z);
    }
}