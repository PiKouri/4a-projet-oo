import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.SocketException;

public class UDPServer extends Thread {
	
	

	// UDP Broadcast fields
	
    private DatagramSocket socket;
    public boolean running;
    private byte[] buf = new byte[256];
    
    // Agent field
    
    private Agent agent;
    
    public String lastUsernameChecked;
    
    public boolean lastUsernameAvailablity;
    

    public UDPServer(Agent agent) throws SocketException {
    	this.agent = agent;
    	socket = new DatagramSocket(Agent.broadcastPortNumber);
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
    	
        this.running = true;

        while (this.running) {
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
            // "connect username"
            // "disconnect username"
            // "changeUsername oldUsername newUsername"
            // "checkUsernameAvailablity username"
            // "tellUsernameAvailablity username true/false"
            
            String[] strip = received.split(" ");
            
            String action = strip[0];
            String username = strip[1];
            
            switch (action) {
            
            case "connect" :
            	this.agent.userConnect(username);
            	
            	try {
					Socket sock = new Socket(address, Agent.defaultPortNumber);
	            	this.agent.newActiveUserSocket(sock);
				} catch (IOException e1) {
					System.out.printf("Could not create socket when trying to connect ERROR\n");
					System.exit(-1);
				}          	
            	break;
            case "disconnect" :
            	this.agent.userDisconnect(username);
            	break;
            case "changeUsername" :
            	String newUsername = strip[2];
            	this.agent.userChangeUsername(username, newUsername);
            	break;            
            case "checkUsernameAvailablity" : // Quelqu'un demande la disponibilité d'un nom
            	this.agent.tellUsernameAvailibility(username);
            	break;
            case "tellUsernameAvailablity" : // On a demandé la disponibilité d'un nom et on reçoit la réponse
            	if (username != this.lastUsernameChecked) {
            		this.lastUsernameChecked = username;
            		this.lastUsernameAvailablity = Boolean.parseBoolean(strip[2]);
            	}
            	break;
            default :
            	System.out.printf("Error reading packet \n %s \n", received);
				System.exit(-1);
            }
            
			try {
				Socket newSocket = new Socket(address, Agent.defaultPortNumber);
				agent.newActiveUserSocket(newSocket);     
			} catch (IOException e) {
				System.out.printf("Could not create socket ERROR\n");
				System.exit(-1);
			}
			
			// Send "end" from Agent to Server to stop running ? pas sur
			
            if (received.equals("end")) {
                this.running = false;
                continue;
            }
            //System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
        }
        socket.close();
    }
}