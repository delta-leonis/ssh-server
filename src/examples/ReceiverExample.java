package examples;

import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.pipeline.RadioPipeline;

// import org.ssh.services.producers.UDPReceiver;

public class ReceiverExample {
    
    public static void main(String[] args) {
        // make models available
        Models.start();
        // make services available
        Services.start();
        RadioPipeline pipa = new RadioPipeline("communication pipeline");
        Pipelines.add(pipa);
        
        // UDPReceiver<RadioPacket> receiver = new UDPReceiver<RadioPacket>("host", 31337);
        // Services.addService(receiver);
        
    }
    
}
