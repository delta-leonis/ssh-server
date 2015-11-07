package org.ssh.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.ssh.models.enums.PacketPriority;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class Pipeline.
 *
 * A Pipeline processes data using {@link org.ssh.services.Producer},
 * {@link org.ssh.services.Coupler}, and {@link org.ssh.services.Consumer}.
 *
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Pipeline can work with.
 */
public abstract class Pipeline<T extends PipelinePacket> {
                                                   
    /** The name of the Pipeline. */
    private final String                    name;
                                            
    /** The Couplers registered to this Pipeline. */
    private final Map<Coupler<T>, PacketPriority> couplers;
                                            
    /** The Consumers registered to this Pipeline. */
    private final List<Consumer<T>>         consumers;
                                            
    /** The queue of PipelinePackets. */
    private final Queue<T>                  queue  = new ConcurrentLinkedQueue<T>();
                                                   
    /** The reflected TypeToken (o¬‿¬o ). */
    /* This is how we defeat Generics */
    @SuppressWarnings ("serial")
    public TypeToken<T>                     genericType   = new TypeToken<T>(this.getClass()) {
    };
   
    // a logger for good measure
    private static final Logger             LOG = Logger.getLogger();
   
    /**
     * Instantiates a new Pipeline.
     *
     * @param name
     *            The name of the new Pipeline.
     */
    public Pipeline(final String name) {
        // set attributes
        this.name = name;
        this.couplers = new HashMap<Coupler<T>, PacketPriority>();
        this.consumers = new ArrayList<Consumer<T>>();

        Pipeline.LOG.info("New org.ssh.services.pipeline created with name %s", name);
    }
    
    /**
     * Adds a {@link org.ssh.services.PipelinePacket} to the Pipeline.
     *
     * @param pipelinePacket
     *            The packet to be added to the Pipeline.
     * @return true, if successful
     */
    public Pipeline<T> addPacket(final T pipelinePacket) {
        // add the packet
        this.queue.add(pipelinePacket);
        Pipeline.LOG.fine("Packet of genericType %s added to org.ssh.services.pipeline %s ...",
                pipelinePacket.getClass().toString(),
                this.getName());
                
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
     * Gets the genericType of PipelinePackets this Pipeline operates on.
     *
     * @return The genericType of packets this Pipeline operates on.
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

        Pipeline.LOG.fine("Starting to process packet on org.ssh.services.pipeline %s", this.getName());
        
        // get the packet
        final PipelinePacket pipelinePacket = this.queue.poll();
        
        // process the packets by composing all couplers according to their priority
        // and feeding the packet as the argument
        final PipelinePacket resultPacket = Seq.foldLeft(
                // get the couplers with their priorities
                this.couplers.entrySet().stream()
                        // sort them by their priority
                        .sorted((leftMap, rightMap) -> leftMap.getValue().ordinal() > rightMap.getValue().ordinal() ? -1
                                : 1)
                        // extract the couplers
                        .map(entryMap -> entryMap.getKey()),
                // the argument to the composed function
                pipelinePacket,
                // the composition itself
                (packet, coupler) -> coupler.process(packet));
                
        Pipeline.LOG.fine(
                "Packet pushed through all couplers, now mapping to consumers on org.ssh.services.pipeline %s",
                this.getName());
                
        return this.consumers.stream().map(consumer -> consumer.consume(resultPacket))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a {@link org.ssh.services.Consumer} with the Pipeline.
     *
     * @param consumer
     *            The Consumer to be registered with the Pipeline.
     * @return true, if successful
     */
    public <P extends PipelinePacket, C extends Consumer<P>> boolean registerConsumer(final C consumer) {
        Pipeline.LOG.fine("Consumer named %s registered to org.ssh.services.pipeline %s.",
                consumer.getName(),
                this.getName());
        return this.consumers.add((Consumer<T>) consumer);
    }
    
    /**
     * Register a list of {@link org.ssh.services.Consumer} with the Pipeline.
     *
     * @param consumer
     *            The Consumers to be registered with the Pipeline.
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public boolean registerConsumers(final Consumer<T>... consumers) {
        return Stream.of(consumers).map(consumer -> this.registerConsumer(consumer))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a {@link org.ssh.services.Coupler} with the Pipeline.
     *
     * @param consumer
     *            The Coupler to be registered with the Pipeline.
     * @return true, if successful
     */
    public <P extends PipelinePacket, C extends Coupler<P>> boolean registerCoupler(final C coupler) {
        Pipeline.LOG.fine("Coupler named %s registered to org.ssh.services.pipeline %s.",
                coupler.getName(),
                this.getName());
        return this.couplers.put((Coupler<T>) coupler, PacketPriority.MEDIUM) != null;
    }
    
    /**
     * Registers a {@link org.ssh.services.Coupler} with the Pipeline with the given
     * Priority.
     *
     * @param packetPriority
     *            The priority with which the Coupler is to be registered.
     * @param consumer
     *            The Consumers to be registered with the Pipeline.
     * @return true, if successful
     */
    public boolean registerCoupler(final PacketPriority packetPriority, final Coupler<T> coupler) {
        Pipeline.LOG.fine("Consumer named %s registered to org.ssh.services.pipeline %s with priority %s.",
                coupler.getName(),
                this.getName(),
                packetPriority.toString());
        return this.couplers.put(coupler, packetPriority) != null;
    }
    
    /**
     * Registers a list of {@link org.ssh.services.Coupler} with the Pipeline.
     *
     * @param consumer
     *            The Couplers to be registered with the Pipeline.
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public boolean registerCouplers(final Coupler<T>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(coupler))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a list of {@link org.ssh.services.Coupler} with the Pipeline with the
     * given Priority.
     *
     * @param packetPriority
     *            The priority with which the Couplers are to be registered.
     * @param consumer
     *            The Consumers to be registered with the Pipeline.
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public boolean registerCouplers(final PacketPriority packetPriority, final Coupler<T>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(packetPriority, coupler))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
}