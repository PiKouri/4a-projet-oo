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

public class BroadcastClient {
	
    private static DatagramSocket socket = null;
    
    private static int broadcastPortNumber = 4445;
    
    private static List<InetAddress> listBroadcastAddresses = null;
    
    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
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

    public static void main(String[] args) throws IOException {
    	//broadcast("Hello test", InetAddress.getByName("255.255.255.255"));
    	listBroadcastAddresses = listAllBroadcastAddresses();
    	for (InetAddress address : listBroadcastAddresses) {
     		broadcast("Hello test", address);
    		broadcast("Test 2", address);
    		broadcast("end", address);
    		System.out.printf("Address : %s \n", address.toString()); 
    	}
    }

    public static void broadcast(
      String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet 
          = new DatagramPacket(buffer, buffer.length, address, BroadcastClient.broadcastPortNumber);
        socket.send(packet);
        socket.close();
    }
}