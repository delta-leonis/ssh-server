package pipeline;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;

import application.Services;
import services.Consumer;
import services.Coupler;
import services.Producer;

/**
 * The Class Pipeline.
 *
 * @param <T> the generic type
 */
public class Pipeline<T> {
    
    /** The producers */
    private final ArrayList<Producer>    producers;

    /** The couplers. */
    private final Map<Coupler, Priority> couplers;

    /** The consumers. */
    private final ArrayList<Consumer>    consumers;

    /** The queue. */
    private final Queue<PipelinePacket>  queue = new ConcurrentLinkedQueue<PipelinePacket>();

    /**
     * Instantiates a new pipeline.
     */
    public Pipeline() {
        this.producers = Services.getCompatibleProducers(this);
        this.couplers  = Services.getCompatibleCouplers(this)
                .stream()
                .collect(Collectors.toMap(
                        coupler -> coupler,
                        coupler -> Priority.MEDIUM));

        this.consumers = Services.getCompatibleConsumers(this);
    }

    /**
     * Adds a packet to the pipeline.
     *
     * @param pipelinePacket The new packet to be added.
     * @return true, if successful
     */
    public boolean addPacket(PipelinePacket pipelinePacket) {
        return this.queue.add(pipelinePacket);
    }

    /**
     * Process packet.
     *
     * @return true, if successful
     */
    public boolean processPacket() {
        if (this.queue.peek() == null) {
            return false;
        }

        final PipelinePacket pipelinePacket = this.queue.poll();

        final PipelinePacket resultPacket = ((Seq<Coupler>) this.couplers.entrySet().stream()
                .map(entryMap -> entryMap.getKey()))
                // sort highest priority (URGENT) from left to right
                //.sorted()
                // apply the function from left to right
                .foldLeft(pipelinePacket, (packet, coupler) -> coupler.process(packet));

        return this.consumers.stream().map(consumer -> consumer.consume(resultPacket))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a producer with the pipeline.
     *
     * @param producer the producer
     * @return true, if successful
     */
    private boolean registerProducer(Producer producer) {
        return this.producers.add(producer);
    }

    /**
     * Register producers.
     *
     * @param producers the producers
     * @return true, if successful
     */
    public boolean registerProducers(Producer... producers) {
        return Stream.of(producers).map(producer -> this.registerProducer(producer))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a consumer with the pipeline.
     *
     * @param consumer the consumer
     * @return true, if successful
     */
    private boolean registerConsumer(Consumer consumer) {
        return this.consumers.add(consumer);
    }

    /**
     * Register consumers.
     *
     * @param consumers the consumers
     * @return true, if successful
     */
    public boolean registerConsumers(Consumer... consumers) {
        return Stream.of(consumers).map(consumer -> this.registerConsumer(consumer))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a coupler with the pipeline.
     *
     * @param coupler the coupler
     * @return true, if successful
     */
    public boolean registerCoupler(Coupler coupler) {
        return this.couplers.put(coupler, Priority.MEDIUM) != null;
    }

    /**
     * Register couplers.
     *
     * @param couplers the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(Coupler... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(coupler))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a coupler with the pipeline with a given priority.
     *
     * @param coupler the coupler
     * @return true, if successful
     */
    public boolean registerCoupler(Priority priority, Coupler coupler) {
        return this.couplers.put(coupler, priority) != null;
    }

    /**
     * Register couplers.
     *
     * @param couplers the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(Priority priority, Coupler... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(priority, coupler))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
}