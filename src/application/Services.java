package application;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import pipeline.Pipeline;
import services.Consumer;
import services.Coupler;
import services.Producer;
import services.Service;
import services.ServicesController;

/**
 * The Class Services.
 */
public final class Services {
    /**
     * The services store has a controller that runs the store.
     */
    private static ServicesController servicesController;

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    private static Logger logger = Logger.getLogger(Services.class.toString());

    /**
     * Adds a pipeline.
     *
     * @param pipeline A new pipeline.
     * @return true, if successful
     */
    public static boolean addPipeline(Pipeline<?> pipeline) {
        Services.logger.info("Adding pipeline: " + pipeline.getClass().getName());
        return Services.servicesController.addPipeline(pipeline);
    }

    /**
     * This method is a wrapper for {@link #addPipeline}.
     *
     * @param pipelines the pipelines
     */
    @SafeVarargs
    public static void addPipelines(Pipeline<?>... pipelines) {
        Stream.of(pipelines)
        //            .parallel()
        .forEach(Services::addPipeline);
    }

    /**
     * Adds the service.
     *
     * @param <T> A generic type extending Service.
     * @param service The service to be added.
     * @return true, if successful
     */
    public static <T extends Service> boolean addService(T service) {
        Services.logger.info("Adding Service: " + service.getClass().getName());
        return Services.servicesController.addService(service);
    }

    /**
     * This method is a wrapper for {@link #addService}.
     *
     * @param <T> A generic type extending Service.
     * @param services A list of services that will be added to the services store.
     */
    @SafeVarargs
    public static <T extends Service> void addServices(T... services) {
        Stream.of(services)
        //            .parallel()
        .forEach(Services::addService);
    }

    /**
     * Finds a Service with the given name.
     *
     * @param string The name.
     * @return       The wanted service.
     */
    public static Service get(String name) {
        return Services.servicesController.get(name);
    }
    
    /**
     * Gets all the services.
     *
     * @return all services
     */
    public ObservableList<Service> getAll() {
        return Services.servicesController.getAll();
    }

    /**
     * This method finds all services matching the name and returns them as an ArrayList<Service>
     * @param name      The (fuzzy) name of the service you want to find.
     * @return          The requested service.
     */
    public ArrayList<Service> getAll(String name) {
        return Services.servicesController.getAll(name);
    }

    /**
     * Gets the consumers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible consumers.
     */
    public static ArrayList<Consumer> getCompatibleConsumers(Pipeline<?> pipeline) {
        return (ArrayList<Consumer>) Services.servicesController.getAll().stream()
                .filter(service -> service instanceof Consumer)
                .map(consumer -> (Consumer) consumer)
                .collect(Collectors.toList());
    }

    /**
     * Gets the couplers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible couplers.
     */
    public static ArrayList<Coupler> getCompatibleCouplers(Pipeline<?> pipeline) {
        return (ArrayList<Coupler>) Services.servicesController.getAll().stream()
                .filter(service -> service instanceof Coupler)
                .map(coupler -> (Coupler) coupler)
                .collect(Collectors.toList());
    }

    /**
     * Gets the producers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible producers.
     */
    public static ArrayList<Producer> getCompatibleProducers(Pipeline<?> pipeline) {
        return (ArrayList<Producer>) Services.servicesController.getAll().stream()
                .filter(service -> service instanceof Producer)
                .map(producer -> (Producer) producer)
                .collect(Collectors.toList());
    }

    /**
     * Gets the single instance of Services.
     *
     * @return single instance of Services
     */
    public static Object getInstance() {
        return Services.instance;
    }

    /**
     * Start.
     */
    public static void start() {
        Services.logger.info("Starting Services...");
        Services.servicesController = new ServicesController();

    }

}