package org.ssh.pipelines;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.AbstractManageable;
import org.ssh.managers.manager.Pipelines;
import org.ssh.services.AbstractConsumer;
import org.ssh.services.AbstractProducer;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class AbstractPipeline.
 *
 * A Pipeline processes data using {@link AbstractProducer}, {@link Coupler}, and {@link AbstractConsumer}.
 *
 * @param <P>
 *            A PipelinePacket this Pipeline can work with.
 *
 * @author Rimon Oz
 */
public abstract class AbstractPipeline<P extends AbstractPipelinePacket<?>> extends AbstractManageable {

    /** The Consumers registered to this Pipeline. */
    private final List<AbstractConsumer<P>>               consumers;

    /** The queue of PipelinePackets. */
    private final Queue<P>                        queue       = new ConcurrentLinkedQueue<P>();

    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<P>                           genericType = new TypeToken<P>(this.getClass()) {
    };

    /**
     * The routes which the Pipeline symbolizes.
     */
    private List<Function<AbstractPipelinePacket<?>, AbstractPipelinePacket<?>>> routes;

    // a logger for good measure
    private static final Logger                   LOG         = Logger.getLogger();

    /**
     * Instantiates a new Pipeline.
     *
     * @param name
     *            The name of the new Pipeline.
     */
    public AbstractPipeline(final String name) {
        super(name);
        // set attributes
        this.consumers = new ArrayList<>();
        this.routes    = new ArrayList<>();

        Pipelines.add(this);
        AbstractPipeline.LOG.info("New pipeline created with name %s", name);
    }

    /**
     * Sets the route(s) symbolized by the pipeline.
     * @param pattern The route expressed as a PEPE pattern.
     * @return        The Pipeline itself, to support method chaining.
     */
    public AbstractPipeline setRoute(String pattern) {
        this.routes = Pipelines.generateRoutes(pattern);
        return this;
    }

    /**
     * Adds a {@link AbstractPipelinePacket} to the Pipeline.
     *
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param pipelinePacket
     *            The packet to be added to the Pipeline.
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <S extends AbstractPipeline<P>> S addPacket(final P pipelinePacket) {
        // add the packet
        this.queue.add(pipelinePacket);
        AbstractPipeline.LOG.fine("Packet of type %s added to pipeline %s ...",
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
    @SuppressWarnings("unchecked")
    public boolean processPacket() {
        // check to see if there's a packet available
        if (this.queue.peek() == null) {
            return false;
        }

        AbstractPipeline.LOG.fine("Starting to process packet on pipeline %s", this.getName());

        // get the packet
        final P pipelinePacket = this.queue.poll();

        // get the results
        List<P> resultPackets = (List<P>) this.routes.stream()
                .map(route -> route.apply(pipelinePacket))
                .collect(Collectors.toList());

        // if there were no couplers then the result is the original packet
        resultPackets = resultPackets.isEmpty() ? Collections.singletonList(pipelinePacket) : resultPackets;

        // map the results on the consumers
        return resultPackets.stream().map(resultPacket -> this.consumers.stream()
                        .map(consumer -> consumer.consume((P)resultPacket)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                // return true if everything succeeded, false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);

    }

    /**
     * Registers a {@link AbstractConsumer} with the Pipeline.
     *
     * @param <C>
     *            The generic type of Consumer supplied by the user.
     * @param <S>
     *            The generic type of of Pipeline requested by the user.
     * @param consumer
     *            The Consumer to be registered with the Pipeline.
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends AbstractConsumer<?>, S extends AbstractPipeline<P>> S registerConsumer(final C consumer) {
        AbstractPipeline.LOG.fine("Consumer named %s registered to pipeline %s.", consumer.getName(), this.getName());
        this.consumers.add((AbstractConsumer<P>) consumer);
        return (S) this;
    }

    /**
     * Register a list of {@link AbstractConsumer} registered with the Pipeline.
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
    public <C extends AbstractConsumer<?>, S extends AbstractPipeline<P>> S registerConsumers(final C... consumers) {
        Stream.of(consumers).forEach(consumer -> this.registerConsumer(consumer));
        return (S) this;
    }

}