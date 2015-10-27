package model;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The Class ModelController.
 */
public class ModelController {
	private ArrayList<Model> models = new ArrayList<Model>();
	
    /**
     * Instantiates a new model controller.
     */
    public ModelController() {
    }
    
    public void add(Model model){
    	models.add(model);
    }

    /**
     * This method finds a model with the given name and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public Optional<Model> get(String name) {
    	return models.stream().filter(model -> model.getFullName().trim().equals(name.trim())).findFirst();
    }

    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * @param modelName The (fuzzy) name of the model you want to find.
     * @return          The requested model.
     */
    public ArrayList<Model> getAll(String name) {
    	return (ArrayList<Model>) models.stream().filter(model -> model.getFullName().equals(name))
                .collect(Collectors.toList());
    }
    
    public ArrayList<Model> getAll(){
    	return models;
    }


}
