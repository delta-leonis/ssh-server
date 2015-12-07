package org.ssh.managers.manager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.Manageable;
import org.ssh.managers.Manager;
import org.ssh.managers.ManagerController;
import org.ssh.managers.controllers.PipelineController;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;
import org.ssh.services.service.Coupler;
import org.ssh.services.service.Producer;
import org.ssh.util.Logger;

/**
 * The Class Pipelines
 * 
 * Pipelines is responsible for managing {@link Producer}, {@link Coupler}, and {@link Consumer}
 * relationships. Most datastreams within the application should be abstracted as pipelines.
 * 
 * @author Rimon Oz
 */
public final class Pipelines implements Manager<Pipeline<? extends PipelinePacket<? extends Object>>> {
    
    /**
     * The Pipelines manager has a controller that runs the place.
     */
    private static PipelineController controller = new PipelineController();
                                                 
    /** The instance. */
    private static final Object       instance   = new Object();
                                                 
    // a logger for good measure
    private static final Logger       LOG        = Logger.getLogger();

    /**
     * Private constructor to hide the implicit public one.
     */
    private Pipelines() { }
    
    /**
     * Gets the Singleton instance of Pipelines.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Pipelines.instance;
    }
    
    /**
     * Starts the Pipelines manager.
     */
    public static void start() {
        Pipelines.LOG.info("Starting Pipelines...");
        Pipelines.controller = new PipelineController();
    }
    
    /**
     * Gets a List of available {@link Consumer} compatible with the given {@link Pipeline}.
     *
     * @param <P>
     *            The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>
     *            The generic type Consumer compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Consumers.
     */
    public static <P extends PipelinePacket<? extends Object>, C extends Consumer<P>> List<C> getCompatibleConsumers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Consumer(s) for type: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Consumer.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C) service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Pipelines.LOG.info("%d Consumer(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link Coupler} compatible with the given {@link Pipeline}.
     *
     * @param <P>
     *            The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>
     *            The generic type Coupler compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Couplers.
     */
    public static <P extends PipelinePacket<? extends Object>, C extends Coupler<P>> List<C> getCompatibleCouplers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Coupler(s) for type: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Coupler.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C)service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Pipelines.LOG.info("%d Coupler(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link Producer} compatible with the given {@link Pipeline}.
     *
     * @param <P>
     *            The generic type of {@link PipelinePacket} handled by the Producer.
     * @param <C>
     *            The generic type Producer compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Producers.
     */
    public static <P extends PipelinePacket<? extends Object>, C extends Producer<P>> List<C> getCompatibleProducers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Producers for genericType: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Producer.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C) service.getType().getClass().cast(service)).collect(Collectors.toList());

        Pipelines.LOG.info("%d Producers found to be compatible with genericType %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a list of {@link Pipeline} that operates on the supplied Type of
     * {@link org.ssh.pipelines.PipelinePacket}.
     *
     * @param <P>
     *            The generic type of Pipeline requested by the user
     * @param packetType
     *            The Type with which the Pipelines need to be compatible.
     * @return The list of compatible Pipelines.
     */
    public static <P extends Pipeline<PipelinePacket<? extends Object>>> List<P> getOfDataType(final Type packetType) {
        Pipelines.LOG.fine("Getting compatible pipelines for type: %s", packetType.getTypeName());
        
        // get the list of pipelines
        @SuppressWarnings ("unchecked")
        final List<P> collect = Pipelines.getAll().stream()
                // filter out the compatible ones by type
                .filter(pipeline -> pipeline.getType().getTypeName().equals(packetType.getTypeName()))
                .map(pipeline -> (P) pipeline)
                .collect(Collectors.toList());
                
        Pipelines.LOG.fine("%d pipelines found to be compatible with type %s", collect.size(), packetType.toString());
        return collect;
    }
    
    /**
     * Finds a {@link Pipeline} with the given name in the Pipelines manager.
     *
     * @param name
     *            The name of the wanted Pipelines
     * @return The requested Pipeline
     */
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> Optional<P> get(final String name) {
        Pipelines.LOG.fine("Getting a Pipeline named: %s", name);
        return Pipelines.controller.get(name);
    }
    
    /**
     * Gets all the Pipelines in the Pipelines manager.
     *
     * @return All the Pipelines
     * @see org.ssh.managers.ManagerController#getAll()
     */
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> List<P> getAll() {
        return Pipelines.controller.getAll();
    }
    
    /**
     * Finds all Pipelines matching the name and returns them as a List.
     *
     * @param name
     *            The name of the Pipeline you want to find.
     * @return The requested Pipeline.
     * @see ManagerController#getAll(String)
     */
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> List<P> getAll(final String name) {
        return Pipelines.controller.getAll(name);
    }
    
    /**
     * Adds a {@link Pipeline} to the Pipelines manager.
     *
     * @param <P>
     *            The generic type of Pipeline requested by the user
     * @param service
     *            The Pipeline to be added.
     * @return true, if successful.
     * @see ManagerController#add(Manageable)
     */
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> boolean add(final P service) {
        Pipelines.LOG.info("Adding Pipeline: " + service.getClass().getName());
        return Pipelines.controller.add(service);
    }
    
    /**
     * Wraps {@link #add}.
     *
     * @param <P>
     *            The generic type of Pipeline requested by the user
     * @param pipelines
     *            The Services to be added
     * @return true if all succeeded, false otherwise
     */
    @SuppressWarnings ("unchecked")
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> boolean addAll(final P... pipelines) {
        return Stream.of(pipelines).map(manageable -> Pipelines.controller.add(manageable))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Gets a list of Pipelines of the given type.
     *
     * @param <P>
     *            The generic type of Pipeline requested by the user
     * @param type
     *            The type of the requested Pipelines
     * @return The list of Pipelines matching the supplied type
     */
    public static <P extends Pipeline<? extends PipelinePacket<? extends Object>>> List<P> getOfType(final Class<?> type) {
        return Pipelines.controller.getOfType(type);
    }
}
