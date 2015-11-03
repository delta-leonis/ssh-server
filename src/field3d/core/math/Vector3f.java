/**************************************************************************************************
 * 
 *	Vector3f
 * 		This class is for our 3d vectors.
 * 
 **************************************************************************************************
 * 
 * 	TODO: javadoc
 * 	TODO: comment
 * 	TODO: cleanup
 * 
 **************************************************************************************************
 * @see GameObject
 * 
 * @author marklef2
 * @date 15-10-2015
 */
package field3d.core.math;


public class Vector3f {

    public float x;
    public float y;
    public float z;

   
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    
   
    public float lengthSquared() { return x * x + y * y + z * z; }
    public float length() { return (float) Math.sqrt(lengthSquared()); }

    
    public Vector3f normalize() {
        float length = length();
        return divide(length);
    }

   
    public Vector3f add(Vector3f other) {
        float x = this.x + other.x;
        float y = this.y + other.y;
        float z = this.z + other.z;
        return new Vector3f(x, y, z);
    }

    public Vector3f negate() {
        return scale(-1f);
    }

   
    public Vector3f subtract(Vector3f other) {
        return this.add(other.negate());
    }

    public Vector3f scale(float scalar) {
        float x = this.x * scalar;
        float y = this.y * scalar;
        float z = this.z * scalar;
        return new Vector3f(x, y, z);
    }

    
    public Vector3f divide(float scalar) {
    	
    	if (scalar == 0.0) {
    		
    		return scale(1.0f);
    	}
    	
        return scale(1f / scalar);
    }

   
    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    
    public Vector3f cross(Vector3f other) {
        float x = this.y * other.z - this.z * other.y;
        float y = this.z * other.x - this.x * other.z;
        float z = this.x * other.y - this.y * other.x;
        return new Vector3f(x, y, z);
    }

    public Vector3f lerp(Vector3f other, float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }
    
    public double GetRotationX() { 
    	
    	return Math.atan2(y, z);
    }
    public double GetRotationY() {
    	
    	return Math.atan2(z, x);
    }
    
    
    public float[] getFloatArray() { return new float[] { x, y, z }; }
    
    @Override
    public String toString() {
    	
    	return "Vector3f: (" + x + ", " + y + ", " + z + ")";
    }
}
