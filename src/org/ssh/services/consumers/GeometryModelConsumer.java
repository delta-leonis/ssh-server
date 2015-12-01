package org.ssh.services.consumers;

import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.services.service.Consumer;


public class GeometryModelConsumer extends Consumer<GeometryPacket> {

    public GeometryModelConsumer(String name) {
        super(name);
    }

    @Override
    public boolean consume(GeometryPacket pipelinePacket) {
        Models.<Field> get("field").ifPresent(field -> {
            field.update("field", pipelinePacket.read().getField());
            Consumer.LOG.info("Updated field with geometrydata.");
            });
        return true;
    }

}
