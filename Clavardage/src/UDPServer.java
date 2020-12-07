import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class UDPServer extends Thread {
	
	

	// UDP Broadcast fields
	
    private DatagramSocket socket;
    public boolean running;
    private byte[] buf = new byte[256];
    
    // Agent field
    
    private Agent agent;
    
    private String lastUsernameChecked;
    
    private boolean lastUsernameAvailability;
    
    private boolean usernameChecked;
    
    private List<InetAddress> listAllOwnLocalAddresses = null;
    
    private List<InetAddress> listAllOwnLocalAddresses() throws SocketException {
        List<InetAddress> addressList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces 
          = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream() 
              .map(a -> a.getAddress())
              .filter(Objects::nonNull)
              .forEach(addressList::add);
        }
        return addressList;
    }
    
    public UDPServer(Agent agent) throws SocketException {
    	if (Agent.debug) System.out.println("UDP Server created");
    	this.agent = agent;
    	socket = new DatagramSocket(Agent.broadcastPortNumber);
    	try {
    		listAllOwnLocalAddresses = listAllOwnLocalAddresses();
		} catch (SocketException e) {
			System.out.printf("Could not list all broadcast addresses ERROR\n");
			System.exit(-1);
		}
    	lastUsernameChecked = "";
    	lastUsernameAvailability = false;
    	usernameChecked=false;
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
    
    public void interrupt() {
    	if (Agent.debug) System.out.println("UDP Server interrupted");
    	this.running = false;
    	socket.close();
    }

    public void run(){
        this.running = true;

        try {while (this.running) {
            buf = new byte[256];
            DatagramPacket packet 
              = new DatagramPacket(buf, buf.length);
            
				socket.receive(packet);
			
            
            InetAddress address = packet.getAddress();
            if (listAllOwnLocalAddresses.contains(address)) continue; // Ignore own broadcast messages
            
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received 
              = new String(packet.getData(), 0, packet.getLength());
            received = received.replaceAll("\0\0", "");
            received = received.replaceAll("(\0)$", "");
            if (Agent.debug) System.out.printf("UDP Server - %s:%d said : %s.\n \n", address.toString(), port , received); 
                       
            // Message type : // A voir
            // "connect username"
            // "disconnect username"
            // "changeUsername oldUsername newUsername"
            // "checkUsernameAvailability username"
            // "tellUsernameAvailability username true/false"
            // "canAccess username" -> After receiving connect, send canAccess to this person
            
            // "updateDisconnectedUsers username address"
            
            String[] strip = received.split(" ");
            
            String action = strip[0];
            String username = strip[1];
            
            switch (action) {
            
            case "connect" :
            	this.agent.tellCanAccess(address);
            	this.agent.userConnect(username, address);
            	try {
					Socket sock = new Socket(address, Agent.defaultPortNumber);
	            	this.agent.newActiveUserSocket(sock);
				} catch (IOException e1) {
					System.out.printf("Could not create socket when trying to connect ERROR\n");
				}
            	try {Thread.sleep(500);} catch (Exception e) {} // Attente pour éviter d'envoyer une liste non mis à jour
            	this.agent.tellDisconnectedUsers(address);
            	break;
            case "disconnect" :
            	this.agent.userDisconnect(username, address);
            	break;
            case "changeUsername" :
            	String newUsername = strip[2];
            	this.agent.userChangeUsername(username, newUsername);
            	break;            
            case "checkUsernameAvailability" : // Quelqu'un demande la disponibilité d'un nom
            	this.agent.tellUsernameAvailibility(username, address);
            	break;
            case "tellUsernameAvailability" : // On a demandé la disponibilité d'un nom et on reçoit la réponse
            	System.out.println("Je suis là");
        		this.lastUsernameAvailability = Boolean.parseBoolean(strip[2]);
        		this.lastUsernameChecked = username;
        		setUsernameChecked(true);
            	break;
            case "canAccess" : 
            	this.agent.userConnect(username, address);
            	break;
            case "updateDisconnectedUsers" :
            	String disconnectedAddress = strip[2];
            	disconnectedAddress = disconnectedAddress.split("/")[1]; // Format d'adresse après .toString : \192.168.1.1
            	try {
            		InetAddress ipAddress = InetAddress.getByName(disconnectedAddress);
            		this.agent.updateDisconnectedUsers(username, ipAddress);
            		}
            	catch (UnknownHostException e) {
            		System.out.printf("Error UnknowHost : %s.\n", disconnectedAddress);
            		}
            	break;
            default :
            	System.out.printf("Error reading packet \n %s \n", received);
            }
			
            //System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
        }
        } catch (IOException e) {System.out.printf("Could not receive packet ERROR\n");}
    }
    
    public String getLastUsernameChecked() {return this.lastUsernameChecked;}
    public boolean getLastUsernameAvailability() {return this.lastUsernameAvailability;}
    public boolean wasUsernameChecked() {return this.usernameChecked;}
    public void setUsernameChecked(boolean bo) {this.usernameChecked = bo;}
}