package pipeline;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;

import com.google.common.reflect.TypeToken;

import services.Consumer;
import services.Coupler;
import util.Logger;

/**
 * The Class Pipeline.
 * 
 * A Pipeline processes data using {@link services.Producer}, {@link services.Coupler}, and {@link services.Consumer}.
 *
 * @author Rimon Oz
 * @param <T> A PipelinePacket this Pipeline can work with.
 */
abstract public class Pipeline<T extends PipelinePacket> {

    /**  The name of the Pipeline. */
    private final String name;

    /** The Couplers registered to this Pipeline. */
    private final Map<Coupler<T>, Priority> couplers;

    /** The Consumers registered to this Pipeline. */
    private final List<Consumer<T>> consumers;

    /** The queue of PipelinePackets. */
    private final Queue<T> queue = new ConcurrentLinkedQueue<T>();

    /**  The reflected TypeToken (o¬‿¬o ). */
    /*   This is how we defeat Generics    */
    @SuppressWarnings("serial")
    public TypeToken<T> type = new TypeToken<T>(this.getClass()) {};

    // a logger for good measure
    private static final Logger logger = Logger.getLogger();

    /**
     * Instantiates a new Pipeline.
     *
     * @param name The name of the new Pipeline.
     */
    public Pipeline(String name) {
        // set attributes
        this.name      = name;
        this.couplers  = new HashMap<Coupler<T>, Priority>();
        this.consumers = new ArrayList<Consumer<T>>();
        
        Pipeline.logger.info("New pipeline created with name %s", name);
    }

    /**
     * Adds a {@link pipeline.PipelinePacket} to the Pipeline.
     *
     * @param pipelinePacket The packet to be added to the Pipeline.
     * @return               true, if successful
     */
    public Pipeline<T> addPacket(T pipelinePacket) {
        // add the packet
        this.queue.add(pipelinePacket);
        Pipeline.logger.info("Packet of type %s added to pipeline %s ...", pipelinePacket.getClass().toString(), this.getName());
        
        return this;
    }

    /**
     * Gets the name of the Pipeline.
     *
     * @return The name of this Pipeline.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the type of PipelinePackets this Pipeline operates on.
     *
     * @return The type of packets this Pipeline operates on.
     */
    public Type getType() {
        return this.type.getType();
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
        
        Pipeline.logger.info("Starting to process packet on pipeline %s", this.getName());

        // get the packet
        final PipelinePacket pipelinePacket = this.queue.poll();

        // process the packets by composing all couplers according to their priority
        // and feeding the packet as the argument
        final PipelinePacket resultPacket = Seq.foldLeft(
            // get the couplers with their priorities
            this.couplers.entrySet().stream()
                // sort them by their priority
                .sorted((leftMap, rightMap) 
                    -> leftMap.getValue().ordinal() > rightMap.getValue().ordinal() ? -1 : 1)
                // extract the couplers
                .map(entryMap -> entryMap.getKey()),
            // the argument to the composed function
            pipelinePacket, 
            // the composition itself
            (packet, coupler) -> coupler.process(packet)
        );

        Pipeline.logger.info("Packet pushed through all couplers, now mapping to consumers on pipeline %s", this.getName());

        return this.consumers.stream().map(consumer -> consumer.consume(resultPacket))
            // collect all success values and reduce to true if all senders
            // succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a {@link services.Consumer} with the Pipeline.
     *
     * @param consumer The Consumer to be registered with the Pipeline.
     * @return         true, if successful
     */
    public boolean registerConsumer(Consumer<T> consumer) {
        Pipeline.logger.info("Consumer named %s registered to pipeline %s.", consumer.getName(), this.getName());
        return this.consumers.add(consumer);
    }

    /**
     * Register a list of {@link services.Consumer} with the Pipeline.
     *
     * @param consumer The Consumers to be registered with the Pipeline.
     * @return         true, if successful
     */    
    @SuppressWarnings("unchecked")
    public boolean registerConsumers(Consumer<T>... consumers) {
        return Stream.of(consumers).map(consumer -> this.registerConsumer(consumer))
            // collect all success values and reduce to true if all senders
            // succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a {@link services.Coupler} with the Pipeline.
     *
     * @param consumer The Coupler to be registered with the Pipeline.
     * @return         true, if successful
     */
    public boolean registerCoupler(Coupler<T> coupler) {
        Pipeline.logger.info("Coupler named %s registered to pipeline %s.", coupler.getName(), this.getName());
        return this.couplers.put(coupler, Priority.MEDIUM) != null;
    }

    /**
     * Registers a {@link services.Coupler} with the Pipeline with the given Priority.
     *
     * @param priority The priority with which the Coupler is to be registered.
     * @param consumer The Consumers to be registered with the Pipeline.
     * @return         true, if successful
     */
    public boolean registerCoupler(Priority priority, Coupler<T> coupler) {
        Pipeline.logger.info("Consumer named %s registered to pipeline %s with priority %s.", coupler.getName(), this.getName(), priority.toString());
        return this.couplers.put(coupler, priority) != null;
    }

    /**
     * Registers a list of {@link services.Coupler} with the Pipeline.
     *
     * @param consumer The Couplers to be registered with the Pipeline.
     * @return         true, if successful
     */
    @SuppressWarnings("unchecked")
    public boolean registerCouplers(Coupler<T>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(coupler))
            // collect all success values and reduce to true if all senders
            // succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Registers a list of {@link services.Coupler} with the Pipeline with the given Priority.
     *
     * @param priority The priority with which the Couplers are to be registered.
     * @param consumer The Consumers to be registered with the Pipeline.
     * @return         true, if successful
     */
    @SuppressWarnings("unchecked")
    public boolean registerCouplers(Priority priority, Coupler<T>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(priority, coupler))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
}