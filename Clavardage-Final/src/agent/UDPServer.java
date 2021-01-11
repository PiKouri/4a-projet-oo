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
	private List<String> listAllOwnLocalAddresses = null;


	/*-----------------------Méthodes - Gestion des connexions UDP entrantes-------------------------*/


	/**
	 * Constructor for the class UDPServer
	 * <p> This class manages the incoming UDP messages
	 * 
	 * @param agent The agent that created this UDPServer
	 */
	protected UDPServer(Agent agent) throws SocketException {
		Agent.printAndLog(String.format("UDP Server created\n"));
		this.agent = agent;
		this.socket = new DatagramSocket(Agent.broadcastPortNumber);
		try {
			listAllOwnLocalAddresses = listAllOwnLocalAddresses();
		} catch (SocketException e) {
        	Agent.errorMessage("ERROR when trying to list own local addresses\n", e);
		}
		this.lastUsernameAvailability = false;
		this.start();
	}

	/**
	 * This method lists all own local addresses
	 * 
	 * @return List of all own local addresses
	 * */
	private List<String> listAllOwnLocalAddresses() throws SocketException {
		List<String> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces 
		= NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
		    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
		    while (addresses.hasMoreElements())
		    {
		        addressList.add(NetworkManager.addressToString((InetAddress) addresses.nextElement()));
		    }
		}
		return addressList;
	}

	@Override
	public void interrupt() {
		Agent.printAndLog(String.format("UDP Server interrupted\n"));
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
			if (listAllOwnLocalAddresses.contains(NetworkManager.addressToString(address))) continue;  

			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			String received 
			= new String(packet.getData(), 0, packet.getLength());
			received = received.replaceAll("\0\0", "");
			received = received.replaceAll("(\0)$", "");
			Agent.printAndLog(String.format("UDP Server - %s:%d said : %s.\n", address.toString(), port , received)); 

			String[] strip = received.split(" ");

			String action = strip[0];
			String username = strip[1];

			switch (action) {

			case "connect" :
				if (!this.agent.isFirstConnection) {
					int externId = Integer.valueOf(strip[2]);
					this.agent.getNetworkManager().tellCanAccess(address);
					this.agent.getUserStatusManager().userConnect(username, address, externId);
					try {
						Socket sock = new Socket(address, Agent.defaultPortNumber);
						this.agent.getNetworkManager().newActiveUserSocket(sock, externId);
					} catch (IOException e) {
			        	Agent.errorMessage(
			        			"ERROR Could not create socket when trying to connect\n", e);
					}
					this.agent.getNetworkManager().tellDisconnectedUsers(address);
				}
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
				int externId = Integer.valueOf(strip[2]);
				this.agent.getUserStatusManager().userConnect(username, address, externId);
				break;
			case "updateDisconnectedUsers" :
				String disconnectedAddress = strip[2];
				int externId2 = Integer.valueOf(strip[3]);
				try {
					InetAddress ipAddress = InetAddress.getByName(disconnectedAddress);
					this.agent.getUserStatusManager().updateDisconnectedUsers(username, ipAddress,externId2);
				}
				catch (UnknownHostException e) {
		        	Agent.errorMessage(
							String.format("ERROR UnknowHost : %s.\n", disconnectedAddress), e);
				}
				break;
			default :
				System.out.printf("ERROR reading packet \n %s \n", received);
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