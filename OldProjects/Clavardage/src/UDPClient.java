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
	
    private DatagramSocket socket = null;
    
    private int broadcastPortNumber = 4445;
    
    private List<InetAddress> listBroadcastAddresses = null;
    
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

    public void sendBroadcast(String message) {
    	//broadcast("Hello test", InetAddress.getByName("255.255.255.255"));
    	try {
			listBroadcastAddresses = listAllBroadcastAddresses();
		} catch (SocketException e) {
			System.out.printf("Could not list all broadcast addresses ERROR\n");
			System.exit(-1);
		}
    	if (Agent.debug) System.out.printf("Broadcast Client - Message : %s\n", message);
    	for (InetAddress address : listBroadcastAddresses) {
     		try {
				broadcast(message, address);
			} catch (IOException e) {
				System.out.printf("Could not send broadcast message ERROR\n");
				System.exit(-1);
			}
    		//System.out.printf("Broadcast Client - Address : %s\n", address.toString()); 
    	}
    }

    private void broadcast(
      String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet 
          = new DatagramPacket(buffer, buffer.length, address, broadcastPortNumber);
        socket.send(packet);
        socket.close();
    }
    
    public void sendUDP(String udpMessage, InetAddress address){
    	try {
        	if (Agent.debug) System.out.printf("UDP Client - Message to %s : %s\n", address.toString() , udpMessage);
		  	socket = new DatagramSocket();
		  	byte[] buffer = udpMessage.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, broadcastPortNumber);
			socket.send(packet);
			socket.close();
    	} catch (Exception e) {
    		System.out.printf("Could not send UDP message ERROR\n");
			System.exit(-1);
    	}
  }
}