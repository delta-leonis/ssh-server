package org.ssh.pipelines;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.ssh.managers.Manageable;
import org.ssh.managers.Pipelines;
import org.ssh.models.enums.PacketPriority;
import org.ssh.services.Consumer;
import org.ssh.services.Coupler;
import org.ssh.services.Producer;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class Pipeline.
 *
 * A Pipeline processes data using {@link Producer}, {@link Coupler}, and {@link Consumer}.
 *
 * @param
 *            <P>
 *            A PipelinePacket this Pipeline can work with.
 *            
 * @author Rimon Oz
 */
public abstract class Pipeline<P extends PipelinePacket> extends Manageable {
                                                  
    /** The Couplers registered to this Pipeline. */
    private final Map<Coupler<P>, PacketPriority> couplers;
                                                  
    /** The Consumers registered to this Pipeline. */
    private final List<Consumer<P>>               consumers;
                                                  
    /** The queue of PipelinePackets. */
    private final Queue<P>                        queue       = new ConcurrentLinkedQueue<P>();
                                                              
    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<P>                           genericType = new TypeToken<P>(this.getClass()) {
                                                              };
                                                              
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
        this.couplers = new HashMap<Coupler<P>, PacketPriority>();
        this.consumers = new ArrayList<Consumer<P>>();
        
        Pipelines.add(this);
        Pipeline.LOG.info("New pipeline created with name %s", name);
    }
    
    /**
     * Adds a {@link PipelinePacket} to the Pipeline.
     *
     * @param pipelinePacket
     *            The packet to be added to the Pipeline.
     * @return true, if successful
     */
    public Pipeline<P> addPacket(final P pipelinePacket) {
        // add the packet
        this.queue.add(pipelinePacket);
        Pipeline.LOG.fine("Packet of genericType %s added to opipeline %s ...",
                pipelinePacket.getClass().toString(),
                this.getName());
                
        return this;
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
                
        Pipeline.LOG.fine("Packet pushed through all couplers, now mapping to consumers on pipeline %s",
                this.getName());
                
        return this.consumers.stream().map(consumer -> consumer.consume(resultPacket))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a {@link Consumer} with the Pipeline.
     *
     * @param <C>
     *            the generic type of Consumer
     * @param consumer
     *            The Consumer to be registered with the Pipeline.
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public <C extends Consumer<?>> boolean registerConsumer(final C consumer) {
        Pipeline.LOG.fine("Consumer named %s registered to pipeline %s.", consumer.getName(), this.getName());
        return this.consumers.add((Consumer<P>) consumer);
    }
    
    /**
     * Register a list of {@link Consumer} registered with the Pipeline.
     *
     * @param consumers
     *            the consumers
     * @return true, if successful
     */
    public boolean registerConsumers(final Consumer<?>... consumers) {
        return Stream.of(consumers).map(consumer -> this.registerConsumer(consumer))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a {@link Coupler} with the Pipeline.
     *
     * @param <C>
     *            the generic type of Coupler
     * @param coupler
     *            the coupler
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public <C extends Coupler<?>> boolean registerCoupler(final C coupler) {
        Pipeline.LOG.fine("Coupler named %s registered to pipeline %s.", coupler.getName(), this.getName());
        return this.couplers.put((Coupler<P>) coupler, PacketPriority.MEDIUM) != null;
    }
    
    /**
     * Registers a {@link Coupler} with the Pipeline with the given Priority.
     *
     * @param packetPriority
     *            The priority with which the Coupler is to be registered.
     * @param coupler
     *            The coupler
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public boolean registerCoupler(final PacketPriority packetPriority, final Coupler<?> coupler) {
        Pipeline.LOG.fine("Consumer named %s registered to pipeline %s with priority %s.",
                coupler.getName(),
                this.getName(),
                packetPriority.toString());
        return this.couplers.put((Coupler<P>) coupler, packetPriority) != null;
    }
    
    /**
     * Registers a list of {@link Coupler} with the Pipeline.
     *
     * @param couplers
     *            the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(final Coupler<?>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(coupler))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Registers a list of {@link Coupler} with the Pipeline with the given Priority.
     *
     * @param packetPriority
     *            The priority with which the Couplers are to be registered.
     * @param couplers
     *            the couplers
     * @return true, if successful
     */
    public boolean registerCouplers(final PacketPriority packetPriority, final Coupler<?>... couplers) {
        return Stream.of(couplers).map(coupler -> this.registerCoupler(packetPriority, coupler))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
}