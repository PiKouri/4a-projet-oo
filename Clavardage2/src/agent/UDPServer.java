package agent;
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

	/*-----------------------Attributs - UDP broadcast-------------------------*/

	/**DatagramSocket associated to this UDPServer*/
	private DatagramSocket socket;
	/**True if the UDPServer is running*/
	protected boolean running;
	/**Reception buffer*/
	private byte[] buf = new byte[256];

	/*-----------------------Attributs - Agent-------------------------*/

	/**The agent that created this UDPServer*/
	private Agent agent;

	/**Availability of the last username that was checked by the agent*/
	private boolean lastUsernameAvailability;
	/**List of all own local addresses*/
	private List<InetAddress> listAllOwnLocalAddresses = null;


	/*-----------------------Méthodes - Gestion des connexions UDP entrantes-------------------------*/


	/**
	 * Constructor for the class UDPServer
	 * <p> This class manages the incoming UDP messages
	 * 
	 * @param agent The agent that created this UDPServer
	 */
	protected UDPServer(Agent agent) throws SocketException {
		if (Agent.debug) System.out.println("UDP Server created");
		this.agent = agent;
		this.socket = new DatagramSocket(Agent.broadcastPortNumber);
		try {
			listAllOwnLocalAddresses = listAllOwnLocalAddresses();
		} catch (SocketException e) {
			System.out.printf("Could not list all broadcast addresses ERROR\n");
			System.exit(-1);
		}
		this.lastUsernameAvailability = false;
		this.start();
	}

	/**
	 * This method lists all own local addresses
	 * 
	 * @return List of all own local addresses
	 * */
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

	@Override
	public void interrupt() {
		if (Agent.debug) System.out.println("UDP Server interrupted");
		this.running = false;
		socket.close();
	}

	@Override
	public void run(){
		this.running = true;

		try {while (this.running) {
			buf = new byte[256];
			DatagramPacket packet 
			= new DatagramPacket(buf, buf.length);

			socket.receive(packet);


			InetAddress address = packet.getAddress();
			// On ignore nos propres messages broadcast
			if (listAllOwnLocalAddresses.contains(address)) continue;  

			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			String received 
			= new String(packet.getData(), 0, packet.getLength());
			received = received.replaceAll("\0\0", "");
			received = received.replaceAll("(\0)$", "");
			if (Agent.debug) System.out.printf("UDP Server - %s:%d said : %s.\n \n", address.toString(), port , received); 

			String[] strip = received.split(" ");

			String action = strip[0];
			String username = strip[1];

			switch (action) {

			case "connect" :
				this.agent.getNetworkManager().tellCanAccess(address);
				this.agent.getUserStatusManager().userConnect(username, address);
				// Attente pour éviter d'envoyer une liste non mis à jour
				// +Attente de la mise à jour de sa liste de sockets
				try {
					Socket sock = new Socket(address, Agent.defaultPortNumber);
					this.agent.getNetworkManager().newActiveUserSocket(sock);
				} catch (IOException e1) {
					System.out.printf("Could not create socket when trying to connect ERROR\n");
				}
				//try {Thread.sleep(500);} catch (Exception e) {} 
				this.agent.getNetworkManager().tellDisconnectedUsers(address);
				break;
			case "disconnect" :
				this.agent.getUserStatusManager().userDisconnect(username, address);
				break;
			case "changeUsername" :
				String newUsername = strip[2];
				this.agent.getUsernameManager().userChangeUsername(username, newUsername);
				break;            
			case "checkUsernameAvailability" : 
				// Quelqu'un demande la disponibilité d'un nom
				this.agent.getNetworkManager().tellUsernameAvailability(username, address);
				break;
			case "tellUsernameAvailability" : 
				// On a demandé la disponibilité d'un nom et on reçoit la réponse
				this.lastUsernameAvailability = Boolean.parseBoolean(strip[2]);
				synchronized(this.agent.getUsernameManager()) {this.agent.getUsernameManager().notifyAll();}
				break;
			case "canAccess" : 
				this.agent.getUserStatusManager().userConnect(username, address);
				break;
			case "updateDisconnectedUsers" :
				String disconnectedAddress = strip[2];
				// Format d'adresse après .toString : \192.168.1.1
				disconnectedAddress = disconnectedAddress.split("/")[1]; 
				try {
					InetAddress ipAddress = InetAddress.getByName(disconnectedAddress);
					this.agent.getUserStatusManager().updateDisconnectedUsers(username, ipAddress);
				}
				catch (UnknownHostException e) {
					System.out.printf("Error UnknowHost : %s.\n", disconnectedAddress);
				}
				break;
			default :
				System.out.printf("Error reading packet \n %s \n", received);
			}
			//System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
		}} catch (IOException e) {}
	}

	/**
	 * This method return the availability of the last username that was checked by the agent
	 * 
	 * @return True if the last username that was checked by the agent is available
	 */
	protected boolean getLastUsernameAvailability() {return this.lastUsernameAvailability;}
}