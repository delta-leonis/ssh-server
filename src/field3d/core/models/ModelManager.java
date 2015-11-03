/**
 * 
 * Model manager class
 * 
 * @author marklef2
 * @date 29-9-2015
 */
package field3d.core.models;

import java.io.IOException;
import java.util.HashMap;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.shape.MeshView;


public class ModelManager {
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////	
	private HashMap<String, MeshView> _models;
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Constructor
	 * 
	 */
	public ModelManager() {
		
		// Creating new hash map
		_models = new HashMap<String, MeshView>();
	}

	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Public methods
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Load model method, loads a .obj model
	 * 
	 * @param file The file location
	 * @param modelName The model name
	 * @return True if correctly loaded, false if not loaded
	 * @throws IOException 
	 */
	public boolean LoadModel(String file, String modelName) throws IOException { 
		
		if (!_models.containsKey(modelName)) {
			
			ObjModelImporter objModelImporter = new ObjModelImporter();
			objModelImporter.read(file);
			
			// TODO: Add to hash map
			_models.put(modelName, objModelImporter.getImport()[0]);
			
			return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * 
	 * Get model method, tries to get a loaded model
	 * 
	 * @param modelName The name of the model
	 * @return The model's triangle mesh
	 */
	public MeshView GetModel(String modelName) { 
		
		MeshView rtn = null;
		
		// If model is loaded
		if (_models.containsKey(modelName)) {
			
			// Getting model
			rtn = _models.get(modelName);
		}
		
		return rtn;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public HashMap<String, MeshView> getModels() { return _models; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void setModels(HashMap<String, MeshView> models) { _models = models; }
}
