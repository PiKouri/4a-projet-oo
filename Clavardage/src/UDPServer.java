import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.SocketException;


public class UDPServer extends Thread {
	
	private int broadcastPortNumber = 4445;
	private int defaultPortNumber = 1234;

	// UDP Broadcast fields
	
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    
    // Agent field
    
    private Agent agent;

    public UDPServer(Agent agent) throws SocketException {
    	this.agent = agent;
    	socket = new DatagramSocket(broadcastPortNumber);
    }
    
    // Get destination ip (local) of the packet
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

    public void run(){
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
            try {
				socket.receive(packet);
			} catch (IOException e) {
				System.out.printf("Could not receive packet ERROR\n");
				System.exit(-1);
			}
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received 
              = new String(packet.getData(), 0, packet.getLength());
            received = received.replaceAll("\0\0", "");
            received = received.replaceAll("(\0)$", "");
            System.out.printf("%s:%d said : %s.\n \n", address.toString(), port , received); 
            
            // Message type : // A voir
            // "username"
            
            agent.userConnect(new User(received));
            
			try {
				Socket newSocket = new Socket(address, defaultPortNumber);
				agent.newActiveUserSocket(newSocket);     
			} catch (IOException e) {
				System.out.printf("Could not create socket ERROR\n");
				System.exit(-1);
			}
			
			// Send "end" from Agent to Server to stop running
			
            if (received.equals("end")) {
                running = false;
                continue;
            }
            //System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
        }
        socket.close();
    }
}