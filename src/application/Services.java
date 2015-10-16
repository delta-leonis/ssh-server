package application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import util.Logger;
import pipeline.Pipeline;
import pipeline.PipelinePacket;
import services.Consumer;
import services.Coupler;
import services.Producer;
import services.Service;
import services.ServicesController;

/**
 * The Class Services.
 * 
 * @author Rimon Oz
 */
public final class Services {
    /**
     * The services store has a controller that runs the store.
     */
    private static ServicesController servicesController = new ServicesController();

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    //private static Logger logger = Logger.getLogger();

    /**
     * Adds a pipeline.
     *
     * @param pipeline A new pipeline.
     * @return true, if successful
     */
    public static boolean addPipeline(Pipeline<?> pipeline) {
        //Services.logger.info("Adding pipeline: " + pipeline.getClass().getName());
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
    public static <T extends Service<?>> boolean addService(T service) {
        //Services.logger.info("Adding Service: " + service.getClass().getName());
        return Services.servicesController.addService(service);
    }

    /**
     * This method is a wrapper for {@link #addService}.
     *
     * @param <T> A generic type extending Service.
     * @param services A list of services that will be added to the services store.
     */
    @SafeVarargs
    public static <S extends PipelinePacket, T extends Service<S>> void addServices(T... services) {
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
    public static Service<?> get(String name) {
        return Services.servicesController.get(name);
    }
    
    /**
     * Gets all the services.
     *
     * @return all services
     */
    public ObservableList<Service<?>> getAll() {
        return Services.servicesController.getAll();
    }

    /**
     * This method finds all services matching the name and returns them as an ArrayList<Service>
     * @param name      The (fuzzy) name of the service you want to find.
     * @return          The requested service.
     */
    public ArrayList<Service<?>> getAll(String name) {
        return Services.servicesController.getAll(name);
    }

    /**
     * Gets the consumers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible consumers.
     */
    public static <T extends PipelinePacket> List<Consumer<T>> getCompatibleConsumers(Pipeline<T> pipeline) {
    	@SuppressWarnings("unchecked")
		List<Consumer<T>> collect = (List<Consumer<T>>) Services.servicesController.getAll().stream()
                .filter(service -> service.type.equals(pipeline.type))
                .map(service -> service.type.getType().getClass().cast(service))
                .collect(Collectors.toList());
		return collect;
    }

    /**
     * Gets the couplers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible couplers.
     */
    public static <T extends PipelinePacket> List<Coupler<T>> getCompatibleCouplers(Pipeline<T> pipeline) {
	    @SuppressWarnings("unchecked")
		List<Coupler<T>> collect = (List<Coupler<T>>) Services.servicesController.getAll().stream()
                .filter(service -> service.type.equals(pipeline.type))
                .map(service -> service.type.getType().getClass().cast(service))
                .collect(Collectors.toList());
		return collect;
    }

    /**
     * Gets the producers compatible with the given pipeline.
     *
     * @param pipeline The given pipeline.
     * @return         The compatible producers.
     */
	public static <T extends PipelinePacket> List<Producer<T>> getCompatibleProducers(Pipeline<T> pipeline) {
	    @SuppressWarnings("unchecked")
		List<Producer<T>> collect = (List<Producer<T>>) Services.servicesController.getAll().stream()
                .filter(service -> service.type.equals(pipeline.type))
                .map(service -> service.type.getType().getClass().cast(service))
                .collect(Collectors.toList());
		return collect;
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
     * This method periodically schedules a Runnable to be called with the given delay between
     * termination of the previous execution and start of the next execution. 
     * @param task   The Runnable to be executed periodically
     * @param delay  Time between termination of previous execution and start of next execution in ms
     * @return       A ScheduledFuture that can be used to cancel the periodic execution
     */
    public ScheduledFuture<?> scheduleTask(String taskName, Runnable task, long delay) {
    	return Services.servicesController.scheduleTask(taskName, task, delay);
    }

    /**
     * Start.
     */
    public static void start() {
        //Services.logger.info("Starting Services...");
        Services.servicesController = new ServicesController();
    }

}