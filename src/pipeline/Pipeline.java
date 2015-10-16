package pipeline;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;

import com.google.common.reflect.TypeToken;

import application.Services;
import services.Consumer;
import services.Coupler;
import services.Producer;

/**
 * The Class Pipeline.
 *
 * @author Rimon Oz
 * @param <T> the generic type
 */
public class Pipeline<T extends PipelinePacket> {
    
    /** The producers */
    private final List<Producer<T>>    producers;

    /** The couplers. */
    private final Map<Coupler<T>, Priority> couplers;

    /** The consumers. */
    private final List<Consumer<T>>    consumers;

    /** The queue. */
    private final Queue<T>  queue = new ConcurrentLinkedQueue<T>();
    
    /** The reflected TypeToken (o¬‿¬o ) */
    public TypeToken<T> type = new TypeToken<T>(getClass()) {};

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
    public boolean addPacket(T pipelinePacket) {
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

        final PipelinePacket resultPacket = ((Seq<Coupler<T>>) this.couplers.entrySet().stream()
                .sorted((leftMap, rightMap) -> 
                	leftMap.getValue().ordinal() > rightMap.getValue().ordinal() ? -1 : 1)
                .map(entryMap -> entryMap.getKey()))
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
    private boolean registerProducer(Producer<T> producer) {
        return this.producers.add(producer);
    }

    /**
     * Register producers.
     *
     * @param producers the producers
     * @return true, if successful
     */
    public boolean registerProducers(Producer<T>... producers) {
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
    private boolean registerConsumer(Consumer<T> consumer) {
        return this.consumers.add(consumer);
    }

    /**
     * Register consumers.
     *
     * @param consumers the consumers
     * @return true, if successful
     */
    public boolean registerConsumers(Consumer<T>... consumers) {
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
    public boolean registerCoupler(Coupler<T> coupler) {
        return this.couplers.put(coupler, Priority.MEDIUM) != null;
    }

    /**
     * Register couplers.
     *
     * @param couplers the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(Coupler<T>... couplers) {
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
    public boolean registerCoupler(Priority priority, Coupler<T> coupler) {
        return this.couplers.put(coupler, priority) != null;
    }

    /**
     * Register couplers.
     *
     * @param couplers the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(Priority priority, Coupler<T>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(priority, coupler))
                // collect all success values and reduce to true if all senders succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
}