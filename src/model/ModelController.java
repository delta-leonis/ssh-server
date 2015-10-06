package model;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ui.ListModel;

/**
 * The Class ModelController.
 */
public class ModelController {

    /** The list models. */
    // ye olde datastore
    private final ObservableList<ListModel<?>> listModels;

    /**
     * Instantiates a new model controller.
     */
    public ModelController() {
        this.listModels = FXCollections.observableArrayList();
    }

    /**
     * Adds a list model.
     *
     * @param listModel the list model
     */
    public void addListModel(ListModel<?> listModel) {
        this.listModels.add(listModel);

    }

    /**
     * This method finds a model with the given name and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public Model get(String name) {
        return (Model) this.listModels.stream().filter(listModel -> listModel.getName().equals(name)).limit(1);
    }

    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * @param modelName The (fuzzy) name of the model you want to find.
     * @return          The requested model.
     */
    public ArrayList<Model> getAll(String name) {
        return (ArrayList<Model>) this.listModels.stream()
                .filter(listModel -> listModel.getName().equals(name))
                .map(listModel -> (Model) listModel)
                .collect(Collectors.toList());
    }

    /**
     * Gets all the list data.
     *
     * @return all list data
     */
    public ObservableList<?> getAllListData() {
        // create an observablelist of following result:
        return FXCollections.observableList(
                // stream the models
                this.listModels.stream()
                // map each ListModel to a stream of its own data
                .flatMap(model -> model.getData().stream())
                // collect the data into a single list
                .collect(Collectors.toList()));
    }

}
