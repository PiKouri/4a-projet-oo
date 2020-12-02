import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.SocketAddress;
import java.net.SocketException;


public class UDPServer extends Thread {
	
	private static int broadcastPortNumber = 4445;
			
	
    private static DatagramSocket socket;
    private static boolean running;
    private static byte[] buf = new byte[256];

    public UDPServer() throws SocketException {
    	socket = new DatagramSocket(UDPServer.broadcastPortNumber);
    }
    
    /*private static InetAddress getOutboundAddress(SocketAddress remoteAddress) throws SocketException {
        DatagramSocket sock = new DatagramSocket();
        // connect is needed to bind the socket and retrieve the local address
        // later (it would return 0.0.0.0 otherwise)
        sock.connect(remoteAddress);

        final InetAddress localAddress = sock.getLocalAddress();

        sock.disconnect();
        sock.close();
        sock = null;

        return localAddress;
    }*/

    public static void main(String[] args) throws IOException {
    	try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        running = true;

        while (running) {
            buf = new byte[256];
            DatagramPacket packet 
              = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received 
              = new String(packet.getData(), 0, packet.getLength());
            received = received.replaceAll("\0\0", "");
            received = received.replaceAll("(\0)$", "");
            System.out.printf("%s:%d said : %s.\n \n", address.toString(), port , received); 
            if (received.equals("end")) {
                running = false;
                continue;
            }
            //System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
        }
        socket.close();
    }
}