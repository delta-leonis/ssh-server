package org.ssh.field3d.core.math;

import org.ssh.field3d.core.gameobjects.GameObject;

/**
 *
 * Vector3f This class is for our 3d vectors.
 * 
 * @see GameObject
 *      
 * @author marklef2
 */
// TODO: Replace with DenseMatrix
public class Vector3f {
    
    public float x;
    public float y;
    public float z;
                 
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    
    public Vector3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f add(final Vector3f other) {
        final float x = this.x + other.x;
        final float y = this.y + other.y;
        final float z = this.z + other.z;
        return new Vector3f(x, y, z);
    }
    
    public Vector3f cross(final Vector3f other) {
        final float x = (this.y * other.z) - (this.z * other.y);
        final float y = (this.z * other.x) - (this.x * other.z);
        final float z = (this.x * other.y) - (this.y * other.x);
        return new Vector3f(x, y, z);
    }
    
    public Vector3f divide(final float scalar) {
        
        if (scalar == 0.0) {
            
            return this.scale(1.0f);
        }
        
        return this.scale(1f / scalar);
    }
    
    public float dot(final Vector3f other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }
    
    public float[] getFloatArray() {
        return new float[] { this.x, this.y, this.z };
    }
    
    public double GetRotationX() {
        
        return Math.atan2(this.y, this.z);
    }
    
    public double GetRotationY() {
        
        return Math.atan2(this.z, this.x);
    }
    
    public float length() {
        return (float) Math.sqrt(this.lengthSquared());
    }
    
    public float lengthSquared() {
        return (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
    }
    
    public Vector3f lerp(final Vector3f other, final float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }
    
    public Vector3f negate() {
        return this.scale(-1f);
    }
    
    public Vector3f normalize() {
        final float length = this.length();
        return this.divide(length);
    }
    
    public Vector3f scale(final float scalar) {
        final float x = this.x * scalar;
        final float y = this.y * scalar;
        final float z = this.z * scalar;
        return new Vector3f(x, y, z);
    }
    
    public Vector3f subtract(final Vector3f other) {
        return this.add(other.negate());
    }
    
    @Override
    public String toString() {
        
        return "Vector3f: (" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
