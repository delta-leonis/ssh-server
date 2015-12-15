package org.ssh.pipelines;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.Manageable;
import org.ssh.managers.manager.Pipelines;
import org.ssh.models.enums.PacketPriority;
import org.ssh.services.service.Consumer;
import org.ssh.services.service.Coupler;
import org.ssh.services.service.Producer;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class Pipeline.
 *
 * A Pipeline processes data using {@link Producer}, {@link Coupler}, and {@link Consumer}.
 *
 * @param <P>
 *            A PipelinePacket this Pipeline can work with.
 *
 * @author Rimon Oz
 */
public abstract class Pipeline<P extends PipelinePacket<?>> extends Manageable {

    /** The Consumers registered to this Pipeline. */
    private final List<Consumer<P>>               consumers;

    /** The queue of PipelinePackets. */
    private final Queue<P>                        queue       = new ConcurrentLinkedQueue<P>();

    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<P>                           genericType = new TypeToken<P>(this.getClass()) {
    };

    /**
     * The routes which the Pipeline symbolizes.
     */
    private List<Function<PipelinePacket<?>, PipelinePacket<?>>> routes;

    // a logger for good measure
    private static final Logger                   LOG         = Logger.getLogger();

    /**
     * Instantiates a new Pipeline.
     *
     * @param name
     *            The name of the new Pipeline.
     */
    public Pipeline(final String name) {
        super(name);
        // set attributes
        this.consumers = new ArrayList<>();
        this.routes    = new ArrayList<>();

        Pipelines.add(this);
        Pipeline.LOG.info("New pipeline created with name %s", name);
    }

    /**
     * Sets the route(s) symbolized by the pipeline.
     * @param pattern The route expressed as a PEPE pattern.
     * @return        The Pipeline itself, to support method chaining.
     */
    public Pipeline setRoute(String pattern) {
        this.routes = Pipelines.generateRoutes(pattern);
        return this;
    }

    /**
     * Adds a {@link PipelinePacket} to the Pipeline.
     *
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param pipelinePacket
     *            The packet to be added to the Pipeline.
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <S extends Pipeline<P>> S addPacket(final P pipelinePacket) {
        // add the packet
        this.queue.add(pipelinePacket);
        Pipeline.LOG.fine("Packet of type %s added to pipeline %s ...",
                pipelinePacket.getClass().toString(),
                this.getName());

        return (S) this;
    }

    /**
     * Gets the type of PipelinePackets this Pipeline operates on.
     *
     * @return The type of packets this Pipeline operates on.
     */
    public Type getType() {
        return this.genericType.getType();
    }

    /**
     * Processes the packet sitting at the head of queue.
     *
     * @return true, if successful
     */
    public boolean processPacket() {
        // check to see if there's a packet available
        if (this.queue.peek() == null) {
            return false;
        }

        Pipeline.LOG.fine("Starting to process packet on pipeline %s", this.getName());

        // get the packet
        final P pipelinePacket = this.queue.poll();

        return this.routes.stream()
                .map(route -> route.apply(pipelinePacket))
                .map(resultPacket -> this.consumers.stream()
                        .map(consumer -> consumer.consume((P)resultPacket)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .reduce(true, (accumulator, success) -> accumulator && success);

    }

    /**
     * Registers a {@link Consumer} with the Pipeline.
     *
     * @param <C>
     *            The generic type of Consumer supplied by the user.
     * @param <S>
     *            The generic tyinfope of Pipeline requested by the user.
     * @param consumer
     *            The Consumer to be registered with the Pipeline.
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Consumer<?>, S extends Pipeline<P>> S registerConsumer(final C consumer) {
        Pipeline.LOG.fine("Consumer named %s registered to pipeline %s.", consumer.getName(), this.getName());
        this.consumers.add((Consumer<P>) consumer);
        return (S) this;
    }

    /**
     * Register a list of {@link Consumer} registered with the Pipeline.
     *
     * @param <C>
     *            The generic type of Consumer supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param consumers
     *            the consumers
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Consumer<?>, S extends Pipeline<P>> S registerConsumers(final C... consumers) {
        Stream.of(consumers).forEach(consumer -> this.registerConsumer(consumer));
        return (S) this;
    }

}