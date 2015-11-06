package org.ssh.field3d.core.math;

/*
 * The MIT License (MIT)
 *
 * Copyright © 2015, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION Vector2fOF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * This class represents a (x,y,z)-Vector. GLSL equivalent to vec3.
 *
 * @author Heiko Brumme
 */
public class Vector2f {
    
    public float x;
    public float y;
                 
    /**
     * Creates a default 3-tuple vector with all values set to 0.
     */
    public Vector2f() {
        this.x = 0f;
        this.y = 0f;
    }
    
    /**
     * Creates a 3-tuple vector with specified values.
     *
     * @param x
     *            x value
     * @param y
     *            y value
     * @param z
     *            z value
     */
    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds this vector to another vector.
     *
     * @param other
     *            The other vector
     * @return Sum of this + other
     */
    public Vector2f add(final Vector2f other) {
        final float x = this.x + other.x;
        final float y = this.y + other.y;
        
        return new Vector2f(x, y);
    }
    
    /**
     * Divides a vector by a scalar.
     *
     * @param scalar
     *            Scalar to multiply
     * @return Scalar quotient of this / scalar
     */
    public Vector2f divide(final float scalar) {
        return this.scale(1f / scalar);
    }
    
    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param other
     *            The other vector
     * @return Dot product of this * other
     */
    public float dot(final Vector2f other) {
        return (this.x * other.x) + (this.y * other.y);
    }
    
    /**
     * Calculates the length of the vector.
     *
     * @return Length of this vector
     */
    public float length() {
        return (float) Math.sqrt(this.lengthSquared());
    }
    
    /**
     * Calculates the squared length of the vector.
     *
     * @return Squared length of this vector
     */
    public float lengthSquared() {
        return (this.x * this.x) + (this.y * this.y);
    }
    
    /**
     * Calculates a linear interpolation between this vector with another vector.
     *
     * @param other
     *            The other vector
     * @param alpha
     *            The alpha value, must be between 0.0 and 1.0
     * @return Linear interpolated vector
     */
    public Vector2f lerp(final Vector2f other, final float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }
    
    /**
     * Negates this vector.
     *
     * @return Negated vector
     */
    public Vector2f negate() {
        return this.scale(-1f);
    }
    
    /**
     * Normalizes the vector.
     *
     * @return Normalized vector
     */
    public Vector2f normalize() {
        final float length = this.length();
        return this.divide(length);
    }
    
    /**
     * Multiplies a vector by a scalar.
     *
     * @param scalar
     *            Scalar to multiply
     * @return Scalar product of this * scalar
     */
    public Vector2f scale(final float scalar) {
        final float x = this.x * scalar;
        final float y = this.y * scalar;
        
        return new Vector2f(x, y);
    }
    
    /**
     * Subtracts this vector from another vector.
     *
     * @param other
     *            The other vector
     * @return Difference of this - other
     */
    public Vector2f subtract(final Vector2f other) {
        return this.add(other.negate());
    }
}
