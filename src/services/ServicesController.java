package services;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pipeline.Pipeline;

/**
 * The Class ServicesController.
 */
public class ServicesController {

    /** The service pipeline list. */
    private final ObservableList<Pipeline<?>> servicePipelineList;

    /** The services list. */
    private final ObservableList<Service>  servicesList;

    /**
     * Instantiates a new services controller.
     */
    public ServicesController() {
        this.servicePipelineList = FXCollections.observableArrayList();
        this.servicesList        = FXCollections.observableArrayList();
    }

    /**
     * Adds the pipeline.
     *
     * @param pipeline the pipeline
     * @return true, if successful
     */
    public boolean addPipeline(Pipeline<?> pipeline) {
        return this.servicePipelineList.add(pipeline);
    }

    /**
     * Adds the pipelines.
     *
     * @param pipelines the pipelines
     * @return true, if successful
     */
    public boolean addPipelines(Pipeline<?>... pipelines) {
        return Stream.of(pipelines).map(pipeline -> this.addPipeline(pipeline))
            // collect all success values and reduce to true if all senders succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Adds the service.
     *
     * @param service the service
     * @return true, if successful
     */
    public boolean addService(Service service) {
        return this.servicesList.add(service.getAsService());
    }

    /**
     * Adds the services.
     *
     * @param services the services
     * @return true, if successful
     */
    public boolean addServices(Service... services) {
        return Stream.of(services).map(service -> this.addService(service))
            // collect all success values and reduce to true if all senders succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * This method finds a service with the given name and returns it as a Service.
     * @param name      The name of the service you want to find.
     * @return          The requested service.
     */
    public Service get(String name) {
        return (Service) this.servicesList.stream().filter(service -> service.getName().equals(name)).findFirst().get();
    }

    /**
     * Gets all the services.
     *
     * @return all services
     */
    public ObservableList<Service> getAll() {
        return this.servicesList;
    }

    /**
     * This method finds all services matching the name and returns them as an ArrayList<Service>
     * @param name      The (fuzzy) name of the service you want to find.
     * @return          The requested service.
     */
    public ArrayList<Service> getAll(String name) {
        return (ArrayList<Service>) this.servicesList.stream()
            .filter(service -> service.getName().equals(name))
            .collect(Collectors.toList());
    }

}