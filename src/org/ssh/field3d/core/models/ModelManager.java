/**
 *
 * Model manager class
 *
 * @author marklef2
 * @date 29-9-2015
 */
package org.ssh.field3d.core.models;

import java.io.IOException;
import java.util.HashMap;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.scene.shape.MeshView;

public class ModelManager {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Private variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private HashMap<String, MeshView> _models;
    
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
    public ModelManager() {
        
        // Creating new hash map
        this._models = new HashMap<String, MeshView>();
    }
    
    /**
     * 
     * Get org.ssh.models method, tries to get a loaded org.ssh.models
     * 
     * @param modelName
     *            The name of the org.ssh.models
     * @return The org.ssh.models's triangle mesh
     */
    public MeshView GetModel(final String modelName) {
        
        MeshView rtn = null;
        
        // If org.ssh.models is loaded
        if (this._models.containsKey(modelName)) {
            
            // Getting org.ssh.models
            rtn = this._models.get(modelName);
        }
        
        return rtn;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Getters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public HashMap<String, MeshView> getModels() {
        return this._models;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Public methods
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * Load org.ssh.models method, loads a .obj org.ssh.models
     * 
     * @param file
     *            The file location
     * @param modelName
     *            The org.ssh.models name
     * @return True if correctly loaded, false if not loaded
     * @throws IOException
     */
    public boolean LoadModel(final String file, final String modelName) throws IOException {
        
        if (!this._models.containsKey(modelName)) {
            
            final ObjModelImporter objModelImporter = new ObjModelImporter();
            objModelImporter.read(file);
            
            // TODO: Add to hash map
            this._models.put(modelName, objModelImporter.getImport()[0]);
            
            return true;
        }
        
        return false;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Setters
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setModels(final HashMap<String, MeshView> models) {
        this._models = models;
    }
}
