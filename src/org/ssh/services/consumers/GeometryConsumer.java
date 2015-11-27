package org.ssh.services.consumers;

import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.services.service.Consumer;


public class GeometryConsumer extends Consumer<GeometryPacket> {

    public GeometryConsumer(String name) {
        super(name);
    }

    @Override
    public boolean consume(GeometryPacket pipelinePacket) {
        Models.<Field> get("field").ifPresent(field -> 
                {
                    field.update("field", pipelinePacket.read().getField());
                    GeometryConsumer.LOG.info("updated %s.", field.getFullName());
                }
                );
        return true;
    }
    

}
