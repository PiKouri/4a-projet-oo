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
	 * @throws SocketException 
	 */
	protected UDPServer(Agent agent) throws SocketException {
		Agent.printAndLog(String.format("UDP Server created\n"));
		this.agent = agent;
		try {
			this.socket = new DatagramSocket(Agent.broadcastPortNumber);
		} catch (SocketException e1) {
			this.interrupt();
			throw e1;
		}
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
			if (listAllOwnLocalAddresses.contains(NetworkManager.addressToString(address)))
				continue;  

			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			String received 
			= new String(packet.getData(), 0, packet.getLength());
			received = received.replaceAll("\0\0", "");
			received = received.replaceAll("(\0)$", "");
			Agent.printAndLog(String.format("UDP Server - %s:%d said : %s.\n", address.toString(), port , received)); 

			String[] strip = received.split(" ");

			String action = strip[0];
			String username ="";
			if (strip.length>1) username = strip[1];
			
			boolean isPresenceServer = this.agent.getNetworkManager().getPresenceServerBroadcastAddresses().contains(
					NetworkManager.addressToString(address));

			// Ignore presenceServer if could'nt connect at beginning
			/*if((this.agent.getNetworkManager().presenceServer==null) && (isPresenceServer)) {
				Agent.printAndLog("UDP Server - message ignored because"
						+ " it is from Presence Server and we could not connect at beginning\n");
				continue;
			}*/

			// If we are extern we check only Presence Server's messages
			// We check all messages if we are Intern User
			if (this.agent.isExtern==1 && !isPresenceServer) {
				Agent.printAndLog(String.format("UDP Server - message ignored (%s) because"
						+ " it is not from Presence Server and we are extern user\n",NetworkManager.addressToString(address)));
				continue;
			}
			switch (action) {
			case "connect" :
				if (!this.agent.isFirstConnection) {
					int isExtern = Integer.valueOf(strip[2]);
					this.agent.getNetworkManager().tellCanAccess(address);
					this.agent.getUserStatusManager().userConnect(username, address, isExtern);
					try {
						Socket sock = new Socket(address, Agent.defaultPortNumber);
						this.agent.getNetworkManager().newActiveUserSocket(sock);
					} catch (IOException e) {
			        	Agent.errorMessage(
			        			"ERROR Could not create socket when trying to connect\n", e);
					}
					this.agent.getNetworkManager().tellDisconnectedUsers(address);
				}
				break;
			case "disconnect" :
				this.agent.getUserStatusManager().userDisconnect(username);
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
				this.agent.getUserStatusManager().userConnect(username, address, 0);
				// If we receive canAccess : the user is in the same subnetwork
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
			case "presenceServerShutdown" :
				Agent.printAndLog("Presence Server Shutdown : considering intern now\n");
				//this.agent.getNetworkManager().presenceServer=null;
				this.agent.getUserStatusManager().putAllExternUsersDisconnected();
				break;
			case "presenceServerNotifyConnection":
				if (!this.agent.isFirstConnection) {
					// Presence server sends : presenceServerNotifyConnection username isExtern address
					int isExtern = Integer.valueOf(strip[2]);
					// Condition = 
					// Presence server is sending extern user info
					// OR Presence server is sending user info and we are extern user
					boolean condition = (isExtern!=0 || this.agent.isExtern!=0);
					// If the address of new user is in our own local address we change status
					if (!condition) continue;
					else {
						address=InetAddress.getByName(strip[3]);
						this.agent.getNetworkManager().newExternUserConnected(address, username);
					}
				}
				break;
			default :
				System.out.printf("ERROR reading packet \n %s \n", received);
			}}
			//System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
		}catch (IOException e) {
			Agent.printAndLog("UDPServer - IOException\n");
		}
	}

	/**
	 * This method return the availability of the last username that was checked by the agent
	 * 
	 * @return True if the last username that was checked by the agent is available
	 */
	protected boolean getLastUsernameAvailability() {return this.lastUsernameAvailability;}

}