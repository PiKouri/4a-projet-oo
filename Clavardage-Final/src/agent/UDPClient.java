package agent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class UDPClient {

/*-----------------------Attributs privés-------------------------*/
	
	/**DatagramSocket used to send UDP messages*/
    private DatagramSocket socket = null;
        
    /**List of all the available broadcast addresses*/
    private List<InetAddress> listBroadcastAddresses = null;
    
    
/*-----------------------Méthodes - Emission UDP-------------------------*/
  
    
    /**
     * This method lists all the available broadcast addresses      * 
     * @return List of all the available broadcast addresses
     */
    private List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces 
          = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream() 
              .map(a -> a.getBroadcast())
              .filter(Objects::nonNull)
              .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    /**
     * This method sends a broadcast message on all available broadcast addresses
     *  
     * @param message Message that we want to send in broadcast
     */
    public void sendBroadcast(String message) {
    	//broadcast("Hello test", InetAddress.getByName("255.255.255.255"));
    	try {
			listBroadcastAddresses = listAllBroadcastAddresses();
		} catch (SocketException e) {
        	Agent.errorMessage("ERROR when trying to list all broadcast addresses\n", e);
		}
    	if (Agent.debug) System.out.printf("Broadcast Client - Message : %s\n", message);
    	for (InetAddress address : listBroadcastAddresses) {
     		try {
				broadcast(message, address);
			} catch (IOException e) {
	        	Agent.errorMessage("ERROR when trying to send a broadcast message\n", e);
			}
    		//System.out.printf("Broadcast Client - Address : %s\n", address.toString()); 
    	}
    }

    /**
     * This method sends a broadcast message to a specific broadcast address
     *  
     * @param broadcastMessage Message that we want to send in broadcast
     * @param address Broadcast address to which we want to send the message
     */
    private void broadcast(String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet 
          = new DatagramPacket(buffer, buffer.length, address, Agent.broadcastPortNumber);
        socket.send(packet);
        socket.close();
    }
    
    /**
     * This method sends a UDP message to a specific address
     *  
     * @param udpMessage Message that we want to send in broadcast
     * @param address Address to which we want to send the message
     */
    public void sendUDP(String udpMessage, InetAddress address){
    	try {
        	if (Agent.debug) System.out.printf("UDP Client - Message to %s : %s\n", address.toString() , udpMessage);
		  	socket = new DatagramSocket();
		  	byte[] buffer = udpMessage.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, Agent.broadcastPortNumber);
			socket.send(packet);
			socket.close();
    	} catch (Exception e) {
        	Agent.errorMessage("ERROR when trying to send an UDP message", e);
    	}
  }
}