package model;

import java.util.ArrayList;

/**
 * The Class ModelController.
 */
public class ModelController {
    /**
     * Instantiates a new model controller.
     */
    public ModelController() {
    }

    /**
     * This method finds a model with the given name and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public Model get(String name) {
//        return (Model) this.listModels.stream().filter(listModel -> listModel.getName().equals(name)).findFirst().get();
    	return null;
    }

    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * @param modelName The (fuzzy) name of the model you want to find.
     * @return          The requested model.
     */
    public ArrayList<Model> getAll(String name) {
//        return (ArrayList<Model>) this.listModels.stream()
//                .filter(listModel -> listModel.getName().equals(name))
//                .map(listModel -> (Model) listModel)
//                .collect(Collectors.toList());
    	return null;
    }


}
