package org.ssh.services.consumers;

import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.services.Service;
import org.ssh.services.service.Consumer;

/**
 * Class that updates the field-model with the result of the GeometryPacket. 
 * 
 * @author Jeroen de Jong
 *
 */
public class GeometryConsumer extends Consumer<GeometryPacket> {
    
    /**
     * Instanciate new consumer 
     * 
     * @param name name of this consumer
     */
    public GeometryConsumer(String name) {
        super(name);
    }
    
    /**
     * Retreives {@link Field} and updates the fieldSize
     */
    @Override
    public boolean consume(GeometryPacket pipelinePacket) {
        Models.<Field> get("field").ifPresent(field -> {
            field.update("field", pipelinePacket.read().getField());
            Service.LOG.info("updated %s.", field.getFullName());
        });
        return true;
    }
    
}
