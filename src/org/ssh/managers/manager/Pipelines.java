package org.ssh.managers.manager;

import org.ssh.expressions.languages.Pepe;
import org.ssh.managers.Manager;
import org.ssh.managers.controllers.PipelineController;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.service.Coupler;
import org.ssh.services.service.Producer;
import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.util.Logger;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class Pipelines
 * <p>
 * Pipelines is responsible for managing {@link Producer}, {@link Coupler}, and {@link Consumer}
 * relationships. Most datastreams within the application should be abstracted as pipelines.
 *
 * @author Rimon Oz
 */
@AvailableInLua
public final class Pipelines implements Manager<Pipeline<? extends PipelinePacket<?>>> {

    /**
     * The Pipelines manager has a controller that runs the place.
     */
    private static PipelineController controller = new PipelineController();

    /**
     * The instance.
     */
    private static final Object instance = new Object();
    /**
     * The pipeline expression parser. This is used to evaluate pipeline routes
     * and multi-pipe configurations
     */
    private static Pepe pepeEngine;

    // a logger for good measure
    private static final Logger LOG = Logger.getLogger();

    /**
     * Private constructor to hide the implicit public one.
     */
    private Pipelines() {
    }

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

        // instantiate pepe if it doesn't exist yet
        if (pepeEngine == null) {
            pepeEngine = new Pepe(name -> Services.<Coupler<PipelinePacket<?>>>get(name).get().getTransferFunction());
        }
    }

    /**
     * Gets a List of available {@link Consumer} compatible with the given {@link Pipeline}.
     *
     * @param <P>      The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>      The generic type Consumer compatible with the Pipeline.
     * @param pipeline The given pipeline.
     * @return The compatible Consumers.
     */
    public static <P extends PipelinePacket<?>, C extends Consumer<P>> List<C> getCompatibleConsumers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Consumer(s) for type: %s", pipeline.getType().toString());

        @SuppressWarnings("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Consumer.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C) service)
                .collect(Collectors.toList());

        Pipelines.LOG.info("%d Consumer(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());

        return collect;
    }

    /**
     * Gets a List of available {@link Coupler} compatible with the given {@link Pipeline}.
     *
     * @param <P>      The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>      The generic type Coupler compatible with the Pipeline.
     * @param pipeline The given pipeline.
     * @return The compatible Couplers.
     */
    public static <P extends PipelinePacket<?>, C extends Coupler<P>> List<C> getCompatibleCouplers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Coupler(s) for type: %s", pipeline.getType().toString());

        @SuppressWarnings("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Coupler.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C) service)
                .collect(Collectors.toList());

        Pipelines.LOG.info("%d Coupler(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());

        return collect;
    }

    /**
     * Gets a List of available {@link Producer} compatible with the given {@link Pipeline}.
     *
     * @param <P>      The generic type of {@link PipelinePacket} handled by the Producer.
     * @param <C>      The generic type Producer compatible with the Pipeline.
     * @param pipeline The given pipeline.
     * @return The compatible Producers.
     */
    public static <P extends PipelinePacket<?>, C extends Producer<P>> List<C> getCompatibleProducers(
            final Pipeline<P> pipeline) {
        Pipelines.LOG.info("Getting compatible Producers for genericType: %s", pipeline.getType().toString());

        @SuppressWarnings("unchecked")
        // get the list of services
        final List<C> collect = Services.getOfType(Producer.class).stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> (C) service)
                .collect(Collectors.toList());

        Pipelines.LOG.info("%d Producers found to be compatible with genericType %s",
                collect.size(),
                pipeline.getType().toString());

        return collect;
    }

    /**
     * Gets a list of {@link Pipeline} that operates on the supplied Type of
     * {@link org.ssh.pipelines.PipelinePacket}.
     *
     * @param <P>        The generic type of Pipeline requested by the user
     * @param packetType The Type with which the Pipelines need to be compatible.
     * @return The list of compatible Pipelines.
     */
    public static <P extends Pipeline<PipelinePacket<?>>> List<P> getOfDataType(final Type packetType) {
        Pipelines.LOG.fine("Getting compatible pipelines for type: %s", packetType.getTypeName());

        // get the list of pipelines
        @SuppressWarnings("unchecked")
        final List<P> collect = Pipelines.getAll().stream()
                // filter out the compatible ones by type
                .filter(pipeline -> pipeline.getType().getTypeName().equals(packetType.getTypeName()))
                // map them to the correct parameterized type and collect them in a list
                .map(pipeline -> (P) pipeline)
                .collect(Collectors.toList());

        Pipelines.LOG.fine("%d pipelines found to be compatible with type %s", collect.size(), packetType.toString());
        return collect;
    }

    /**
     * Finds a {@link Pipeline} with the given name in the Pipelines manager.
     *
     * @param name The name of the wanted Pipelines
     * @return The requested Pipeline
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> Optional<P> get(final String name) {
        Pipelines.LOG.fine("Getting a Pipeline named: %s", name);
        return Pipelines.controller.get(name);
    }

    /**
     * Gets all the Pipelines in the Pipelines manager.
     *
     * @return All the Pipelines
     * @see org.ssh.managers.ManagerController#getAll()
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> List<P> getAll() {
        return Pipelines.controller.getAll();
    }

    /**
     * Adds a {@link Pipeline} to the Pipelines manager.
     *
     * @param <P>      The generic type of Pipeline requested by the user
     * @param pipeline The Pipeline to be added.
     * @return true, if successful.
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> boolean add(final P pipeline) {
        Pipelines.LOG.info("Adding Pipeline: " + pipeline.getClass().getName());
        return Pipelines.controller.put(pipeline.getName(), pipeline);
    }

    /**
     * Wraps {@link #add}.
     *
     * @param <P>       The generic type of Pipeline requested by the user
     * @param pipelines The Services to be added
     * @return true if all succeeded, false otherwise
     */
    @SuppressWarnings("unchecked")
    public static <P extends Pipeline<? extends PipelinePacket<?>>> boolean addAll(final P... pipelines) {
        return Stream.of(pipelines).map(manageable -> Pipelines.controller.put(manageable.getName(), manageable))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Gets a list of Pipelines of the given type.
     *
     * @param <P>  The generic type of Pipeline requested by the user
     * @param type The type of the requested Pipelines
     * @return The list of Pipelines matching the supplied type
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> List<P> getOfType(final Class<?> type) {
        return Pipelines.controller.getOfType(type);
    }

    /**
     * Removes a {@link Pipeline} with the specified key from the list of Manageables.
     *
     * @param name The key belonging to the Manageable.
     * @param <P>  The type of Manageable requested by the user.
     * @return The removed Manageable.
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> P remove(final String name) {
        return Pipelines.controller.remove(name);
    }

    /**
     * Removes the supplied {@link Pipeline} from the list of Manageables if it is present in the list.
     *
     * @param pipeline The Manageable to be removed.
     * @param <P>      The type of Manageable requested by the user.
     * @return The removed Manageable.
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> P remove(final P pipeline) {
        return Pipelines.controller.remove(pipeline);
    }

    /**
     * Finds all the Pipelines whose true name matches the given pattern.
     * @param pattern   The pattern to match on.
     * @param <P>       The type of Pipeline requested by the user.
     * @return          The list of Pipelines matching the given pattern.
     */
    public static <P extends Pipeline<? extends PipelinePacket<?>>> List<P> find(final String pattern) {
        return Pipelines.controller.find(pattern);
    }

    /**
     * Generates the routes for a given pattern using PEPE. The routes are stored as lambda's generated
     * by currying the Coupler's transfer functions.
     * @param pattern The pattern which to generate routes from.
     * @return        The list of resulting lambda(s) representing a single branch in the route.
     */
    public static List<Function<PipelinePacket<?>, PipelinePacket<?>>> generateRoutes(String pattern) {
        return Pipelines.pepeEngine.evaluate(pattern);
    }
}
