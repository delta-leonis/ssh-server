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
 * @author marklef2
 *         
 * @see GameObject
 */

public class SkyboxGO extends GameObject {
    
    /** The logger */
    private static final Logger LOG                 = Logger.getLogger("SkyboxGO");
                                                    
    /** The texture file for the skybox */
    private static final String SKYBOX_TEXTURE_FILE = "./assets/textures/skybox.jpg";
                                                    
    /** The sphere size of the skybox */
    private static final double SPHERE_SIZE         = 300000.0;
                                                    
    /** The model. */
    private final Sphere        model;
                                
    /** The skybox material. */
    private final PhongMaterial skyMaterial;
                                
    /**
     * Constructor. This instantiates a new SkyboxGO object.
     *
     * @param game
     *            The {@link Game} of the {@link GameObject}.
     */
    public SkyboxGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Define file input stream as null
        FileInputStream fs = null;
        
        // Create new sphere
        this.model = new Sphere(SPHERE_SIZE);
        // Create new material
        this.skyMaterial = new PhongMaterial(Color.WHITE);
        
        // Try to load texture
        try {
            
            // Open texture file
            fs = new FileInputStream("./assets/textures/skybox.jpg");
            // Setting diffuse color map
            this.skyMaterial.setDiffuseMap(new Image(fs));
        }
        catch (final FileNotFoundException fileNotFoundException) {
            
            // Log error
            LOG.info("Could not load: " + SKYBOX_TEXTURE_FILE);
            LOG.exception(fileNotFoundException);
        }

        // Setting model material
        this.model.setMaterial(this.skyMaterial);
        // Setting face culling to none
        this.model.setCullFace(CullFace.NONE);
        
        // Rotate 180 degrees around x-axis
        this.model.setRotationAxis(Rotate.X_AXIS);
        this.model.setRotate(180);
        
        // Close file
        try {
            if (fs != null)
                fs.close();
        }
        catch (IOException ioException) {
            
            // Log error
            LOG.exception(ioException);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Execute on UI thread; add models to world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(this.model));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        // Check if model is in the world group
        if (this.getGame().getWorldGroup().getChildren().contains(this.model)) {
            
            // Remove from world
            this.getGame().getWorldGroup().getChildren().remove(this.model);
        }
    }
}
