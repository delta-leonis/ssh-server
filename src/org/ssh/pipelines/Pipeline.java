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
public abstract class Pipeline<P extends PipelinePacket<? extends Object>> extends Manageable {
    
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
        Pipeline.LOG.info("Packet of type %s added to pipeline %s ...",
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
        
        // process the packets by composing all couplers according to their priority
        // and feeding the packet as the argument
        final P resultPacket = Seq.foldLeft(
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
                
        Pipeline.LOG.info("Packet pushed through all %d couplers, now mapping to %d consumers on pipeline %s",
                couplers.size(),
                consumers.size(),
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
     *            The generic type of Consumer supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
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
    
    /**
     * Registers a {@link Coupler} with the Pipeline.
     *
     * @param <C>
     *           The generic type of Coupler supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param coupler
     *            the coupler
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Coupler<?>, S extends Pipeline<P>> S registerCoupler(final C coupler) {
        Pipeline.LOG.fine("Coupler named %s registered to pipeline %s.", coupler.getName(), this.getName());
        this.couplers.put((Coupler<P>) coupler, PacketPriority.MEDIUM);
        return (S) this;
    }
    
    /**
     * Registers a {@link Coupler} with the Pipeline with the given Priority.
     *
     * @param <C>
     *            The generic type of Coupler supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param packetPriority
     *            The priority with which the Coupler is to be registered.
     * @param coupler
     *            The coupler
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Coupler<?>, S extends Pipeline<P>> S registerCoupler(final PacketPriority packetPriority, final C coupler) {
        Pipeline.LOG.fine("Consumer named %s registered to pipeline %s with priority %s.",
                coupler.getName(),
                this.getName(),
                packetPriority.toString());
        this.couplers.put((Coupler<P>) coupler, packetPriority);
        return (S) this;
    }
    
    /**
     * Registers a list of {@link Coupler} with the Pipeline.
     *
     * @param <C>
     *            The generic type of Coupler supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param couplers
     *            the couplers
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Coupler<?>, S extends Pipeline<P>> S registerCouplers(final C... couplers) {
        Stream.of(couplers).forEach(coupler -> this.registerCoupler(coupler));
        return (S) this;
    }
    
    /**
     * Registers a list of {@link Coupler} with the Pipeline with the given Priority.
     *
     * @param <C>
     *            The generic type of Coupler supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param packetPriority
     *            The priority with which the Couplers are to be registered.
     * @param couplers
     *            the couplers
     * @return The Pipeline itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Coupler<?>, S extends Pipeline<P>> S registerCouplers(final PacketPriority packetPriority, final C... couplers) {
        Stream.of(couplers).map(coupler -> this.registerCoupler(packetPriority, coupler));
        return (S) this;
    }
}