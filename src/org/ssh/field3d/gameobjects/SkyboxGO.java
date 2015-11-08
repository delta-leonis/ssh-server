package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

/**
 * SkyboxGO class. This class is the class used for creating a 3d skybox. It creates an enormous
 * {@link Sphere} and create a {@link PhongMaterial} from a file texture file. Face Culling is
 * disabled so the texture is rendered on the inside to.
 * 
 * @see GameObject
 *      
 * @author marklef2
 *         
 */
public class SkyboxGO extends GameObject {
    
    private static final Logger LOG                 = Logger.getLogger("SkyboxGO");
    private static final String SKYBOX_TEXTURE_FILE = "./assets/textures/skybox.jpg";
                                                    
    private final Sphere        model;
    private final PhongMaterial skyMaterial;
      
    
    /**
     * 
     * Constructor
     * 
     * @param game
     *            The {@link GameObject}'s {@link Game}.
     */
    public SkyboxGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Define file input stream as null
        FileInputStream fs = null;
        this.model = new Sphere(300000);
        this.skyMaterial = new PhongMaterial(Color.WHITE);
        
        // Try to load texture
        try {
            
            fs = new FileInputStream("./assets/textures/skybox.jpg");
            
        }
        catch (final FileNotFoundException fileNotFoundException) {
            
            // TODO: Log exception
            // Log error
            LOG.info("Could not load: " + SKYBOX_TEXTURE_FILE);
            LOG.finer(fileNotFoundException.getStackTrace().toString());
            
            return;
        }
        
        // Setting diffuse color map
        this.skyMaterial.setDiffuseMap(new Image(fs));
        
        // Setting model material
        this.model.setMaterial(this.skyMaterial);
        // Setting face culling to none
        this.model.setCullFace(CullFace.NONE);
        
        // Rotate 180 degrees around x-axis
        this.model.setRotationAxis(Rotate.X_AXIS);
        this.model.setRotate(180);
        
        // Close file
        try {
            
            fs.close();
        } catch (IOException ioException) {
            
            // TODO: Log exception
            LOG.finer(ioException.getStackTrace().toString());
        }
    }
    
    /**
     * Initialize method. Adds models to the world.
     */
    @Override
    public void Initialize() {
        
        // Add models to world group
        Platform.runLater(() -> this.GetGame().getWorldGroup().getChildren().add(this.model));
    }
    
    /**
     * Update method.
     */
    @Override
    public void Update(final long timeDivNano) {
    }
    
    /**
     * Destroy method. Removes model from world. 
     */
    @Override
    public void Destroy() {
        
        // Check if model is in the world group
        if (this.GetGame().getWorldGroup().getChildren().contains(this.model)) {
            
            // Remove from world
            this.GetGame().getWorldGroup().getChildren().remove(this.model);
        }
    }
    
   
}
