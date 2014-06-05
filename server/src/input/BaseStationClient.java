package input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class BaseStationClient implements Runnable {

	private final static int PACKETSIZE = 4 ;
	private DatagramSocket socket;
	
	public BaseStationClient(){

        // Construct the socket
        try {
			socket = new DatagramSocket( 10000 ) ;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println( "The server is ready..." ) ;
	}
	public void run(){
        // Create a packet
        DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
    	while(true){
    		// Receive a packet (blocking)
    		try {
    			socket.receive( packet ) ;
    			
    			// Print the packet
//    			System.out.println( packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()) ) ;
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