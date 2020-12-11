package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import datatypes.MyMap;
import userInterface.User;

public class NetworkManager {

	private Agent agent;
	
	private MyMap<User,UserSocket> mapSockets;
	private MyMap<User, InetAddress> mapAddresses;
		
	private Server connectionServer;
	private UDPServer udpServer;
	private UDPClient udpClient;

	
/*-----------------------Constructeurs-------------------------*/
	
	
	protected NetworkManager(Agent agent) throws IOException {
		this.agent=agent;
		
		this.udpClient = new UDPClient();
		this.udpServer = new UDPServer(this.agent);
		this.connectionServer = new Server(this.agent);

		this.mapSockets = new MyMap<User,UserSocket>();
		this.mapAddresses = new MyMap<User,InetAddress>();
	}
	
	
/*-----------------------Méthodes - Réseau-------------------------*/
	
	
	protected void tellUsernameAvailibility(String username, InetAddress address) {
		if (!this.agent.isFirstConnection) {
			boolean ok = this.agent.getUsernameManager().checkUsernameAvailability(username);
			// Envoie vrai si le nom est inconnu ...
			if (!(username.equals(this.agent.me.getUsername())) && !ok) {
				User user = this.agent.getUsernameManager().nameResolve(username);
				ok = user.getAddress().equals(address);
				// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
			}
			this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ ok, address);
		} else { 
			// A la première connexion : premier arrivé / premier servi
			// Si quelqu'un veut le même nom, on lui dit qu'il n'est pas disponible
			if (username.equals(this.agent.me.getUsername())) this.udpClient.sendBroadcast("tellUsernameAvailability "+username+" "+ false);
		}
			
	}
	
	protected void tellCanAccess(InetAddress address) {
		if (!this.agent.isFirstConnection) {
			this.udpClient.sendUDP("canAccess "+this.agent.me.getUsername(), address);
			// Une nouvelle personne veut qu'on se connecte à son serveur : on lui donne notre IP d'abord (pour faire le lien IP <-> Username)
		}
	}
	
	protected void tellDisconnectedUsers(InetAddress address) {
		if (!this.agent.isFirstConnection) {
			for (User disconnectedUser : this.agent.getUserStatusManager().getDisconnectedUsers()) {
				String disconnectedUsername = disconnectedUser.getUsername();
				String disconnectedAddress = disconnectedUser.getAddress().toString();
				this.udpClient.sendUDP("updateDisconnectedUsers "+ disconnectedUsername+" "+disconnectedAddress, address);
			}
		}
	}

	protected void newActiveUserSocket(Socket socket) {
		if (Agent.debug) System.out.printf("New socket connected\n");
	}
	
	protected Agent getAgent() {
		return this.agent;
	}
	
	protected void sendBroadcast(String broadcastMessage) {
		this.udpClient.sendBroadcast(broadcastMessage);
	}
	
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
	
	protected MyMap<User, InetAddress> getMapAddresses() {
		return this.mapAddresses;
	}
	
	protected User addressResolve(InetAddress address) {
		return this.mapAddresses.getUser(address);
	}
	
	protected boolean getLastUsernameAvailability() {
		return this.udpServer.getLastUsernameAvailability();
	}
	
	protected void removeSocket(User user) {
		this.mapSockets.remove(user);
	}
	
	protected void addAddress(User user, InetAddress address) {
		this.mapAddresses.putUser(user, address);
	}
	
	protected void stopAll() {
		this.udpServer.interrupt();
		this.connectionServer.interrupt();
		this.connectionServer=null; // supprime le Server et les Sockets
		this.udpServer=null; // supprime le Server et les Sockets
	}
	
	protected void startAll() throws IOException {
		this.udpServer = new UDPServer(this.agent);
		this.connectionServer = new Server(this.agent);
	}
}
