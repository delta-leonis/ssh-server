package robocup.input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.World;

public class BaseStationClient implements Runnable {

	private final static int PACKETSIZE = 16 ;
	private DatagramSocket socket;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public BaseStationClient(){

        // Construct the socket
        try {
			socket = new DatagramSocket( 10000 ) ;
		} catch (SocketException e) {
			LOGGER.warning(e.toString());
		}

	}
	public void run(){
        // Create a packet
        DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
    	while(true){
    		// Receive a packet (blocking)
    		try {
    			socket.receive( packet ) ;
    			
    			// Print the packet
    			String input = new String(packet.getData());
    			if(input.startsWith("V:")){
    				String floatString = input.substring(2).trim();
    				if(World.getInstance().getAlly().getRobotByID(11)!=null)
    					World.getInstance().getAlly().getRobotByID(11).setBatteryStatus(Float.parseFloat(floatString));
    			} else
    				logToCSV(new String(packet.getData()));
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
 
    	}
	}
	
	public void logToCSV(String logData) {
		BufferedWriter writer = null;
		FileWriter fw;
		try {
			File file = new File("log/log.log");
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			writer.write(logData);
			writer.newLine();
			writer.flush();
			writer.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}