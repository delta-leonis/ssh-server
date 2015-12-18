package org.ssh.models;

import javafx.geometry.Point3D;

/**
 * CameraSettings model class. This class is responsible for the camera settings.
 * 
 * @see Model
 *      
 * @author marklef2
 */
public class CameraSettings extends Model {
    
    /** The zoom of the camera. */
    private Double  zoom;
    /** The yaw of the camera. */
    private Double  yaw;
    /** The pitch of the camera. */
    private Double  pitch;
                    
    /** The location of the camera. */
    private Point3D location;
                    
    /**
     * Constructor. This instantiates the CameraSettings class.

     */
    public CameraSettings() {
        
        // Initialize super class
        super("camerasettings", "");
        
        // Setting default values
        zoom = yaw = pitch = 0.0;
        
        // Setting location
        location = new Point3D(0.0, 0.0, 0.0);
    }
    
    /**
     * Gets the zoom of the camera.
     * 
     * @return The zoom of the camera.
     */
    public double getZoom() {
        return this.zoom;
    }
    
    /**
     * Gets the yaw of the camera.
     * 
     * @return The zoom of the camera.
     */
    public double getYaw() {
        return this.yaw;
    }
    
    /**
     * Gets the pitch of the camera.
     * 
     * @return The pitch of the camera.
     */
    public double getPitch() {
        return this.pitch;
    }
    
    /**
     * Gets the {@link Point3D location} of the camera.
     * 
     * @return The {@link Point3D location} of the camera.
     */
    public Point3D getLocation() {
        return this.location;
    }
}
