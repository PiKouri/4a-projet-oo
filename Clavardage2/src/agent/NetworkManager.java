package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import datatypes.MyMap;
import userInterface.User;

public class NetworkManager {

	/**The agent that created this NetworkManager*/
	private Agent agent;
	
	/**MyMap that associates a User to a UserSocket*/
	private MyMap<User,UserSocket> mapSockets;
	/**MyMap that associates a User to a InetAddress*/
	private MyMap<User, InetAddress> mapAddresses;
		
	/**TCPServer that will manage incoming TCP connections*/
	private TCPServer connectionServer;
	/**UDPServer that will manage incoming UDP connections*/
	private UDPServer udpServer;
	/**UDPClient used to send UDP messages (also broadcast)*/
	private UDPClient udpClient;
	
/*
 * Message type :
 * "connect username" -> Pour notifier notre arrivée et que les autres utilisateurs se connectent
 * "disconnect username" -> Pour notifier notre départ
 * "changeUsername oldUsername newUsername" -> Pour notifier le changement de notre username
 * "checkUsernameAvailability username" -> Requête demandant la disponibilité de l'username
 * "tellUsernameAvailability username true/false" -> Réponse notifiant la disponibilité(ou non) de l'username
 * "canAccess username" -> Après avoir reçu un "connect", renvoie "canAccess" pour notifier notre adresse IP au nouvel arrivant
 * "updateDisconnectedUsers username address" -> Après avoir reçu un "connect", met à jour les utilisateurs déconnectés du nouvel arrivant
*/

/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class NetworkManager
     * <p>This constructor create the UDPClient, the UDPServer 
     * and the Server (incoming connections) and it will start
     * both servers
     * 
     * @param agent The agent that created this NetworkManager
     */
	protected NetworkManager(Agent agent) throws IOException {
		this.agent=agent;
		
		this.udpClient = new UDPClient();
		this.udpServer = new UDPServer(this.agent);
		this.connectionServer = new TCPServer(this.agent);

		this.mapSockets = new MyMap<User,UserSocket>();
		this.mapAddresses = new MyMap<User,InetAddress>();
	}
	
	
/*-----------------------Méthodes - Réseau-------------------------*/
	
	/**
     * This method sends a broadcast message on all available broadcast addresses
     *  
     * @param broadcastMessage Message that we want to send in broadcast
     */
	protected void sendBroadcast(String broadcastMessage) {
		this.udpClient.sendBroadcast(broadcastMessage);
	}
	
	/**
     * This method sends a UDP message to a specific address
     *  
     * @param udpMessage Message that we want to send in broadcast
     * @param address Address to which we want to send the message
     */
	protected void sendUDP(String udpMessage, InetAddress address) {
		this.udpClient.sendUDP(udpMessage,address);
	}
	
	/**
     * This method sends a UDP message after a request of username availability
     * <p>UDP Message template : "tellUsernameAvailability username true/false"
     *  
     * @param username Username of which we want to verify the availability
     * @param address Address that requested the verification
     */
	protected void tellUsernameAvailability(String username, InetAddress address) {
		if (!this.agent.isFirstConnection) { // Cas Général
			// Vérifie la disponibilité du nom (dans notre table local)
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
	
	/**
     * This method sends a UDP message (to give our IP Address) after a request of connection
     * <p>UDP Message template : "canAccess username"
     *  
     * @param address Address that requested the connection
     */
	protected void tellCanAccess(InetAddress address) {
		if (!this.agent.isFirstConnection) {
			this.udpClient.sendUDP("canAccess "+this.agent.me.getUsername(), address);
			// Une nouvelle personne veut qu'on se connecte à son serveur : on lui donne notre IP d'abord (pour faire le lien IP <-> Username)
		}
	}
	
	/**
     * This method sends a UDP message (to update the disconnected users' list) after a request of connection
     * <p>UDP Message template : "tellDisconnectedUsers username address"
     *  
     * @param address Address that requested the connection
     */
	protected void tellDisconnectedUsers(InetAddress address) {
		if (!this.agent.isFirstConnection) {
			for (User disconnectedUser : this.agent.getUserStatusManager().getDisconnectedUsers()) {
				String disconnectedUsername = disconnectedUser.getUsername();
				String disconnectedAddress = disconnectedUser.getAddress().toString();
				this.udpClient.sendUDP("updateDisconnectedUsers "+ disconnectedUsername+" "+disconnectedAddress, address);
			}
		}
	}

	/**
     * This method adds a new UserSocket to the local list after a new connection was established
     *  
     * @param socket Socket from which we create the UserSocket
     */
	protected void newActiveUserSocket(Socket socket) {
		// Le thread attend que l'on reçoive l'adresse de l'utilisateur avant de pouvoir créer le UserSocket
		if (!this.agent.isFirstConnection) {
			final class MyThread extends Thread {
				private Agent agent; private NetworkManager nm; private Socket socket;
				public MyThread(Agent agent,NetworkManager nm, Socket socket) {this.agent=agent;this.nm=nm;this.socket=socket;}
				public void run() {
					User user = null;
					synchronized(this.nm) {
						while (user==null) {
							//if (Agent.debug) System.out.printf("Could not get User from address : %s -> Waiting for canAccess\n",socket.getInetAddress());
							try {this.nm.wait(Agent.timeout);} catch (InterruptedException e) {}
							user = this.nm.mapAddresses.getUser(socket.getInetAddress());
						}
					}
					if (Agent.debug) System.out.printf("New socket connected: "+user.getUsername()+"\n");
					this.nm.mapSockets.putUser(user, new UserSocket(user, this.agent, socket));
				}
			}
			(new MyThread(this.agent,this,socket)).start();
		}
	}
		
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
		
	/**
     * This method returns the MyMap of known addresses associated to users
     * 
     * @return MyMap of known addresses associated to users
     */
	protected MyMap<User, InetAddress> getMapAddresses() { // A voir pour enlever
		return this.mapAddresses;
	}
	
	/**
     * This method returns the user associated to a specific IP address
     *
     * @param address Address that we want to resolve
     * 
     * @return The user associated to the IP address
     */
	protected User addressResolve(InetAddress address) {
		return this.mapAddresses.getUser(address);
	}
	
	/**
     * This method returns the user associated to a specific UserSocket
     *
     * @param usocket UserSocket that we want to resolve
     * 
     * @return The user associated to the user socket
     */
	protected User socketResolve(UserSocket uSocket) {
		return this.mapSockets.getUser(uSocket);
	}
	
	/**
     * This method returns the UserSocket associated to this user
     *
     * @param user 
     */
	protected UserSocket getSocket(User user) {
		return this.mapSockets.getValue(user);
	}
	
	/**
     * This method returns the IP Address associated to this user
     *
     * @param user 
     */
	protected InetAddress getAddress(User user) {
		return this.mapAddresses.getValue(user);
	}
	
	/**
     * This method returns the availability of the username that was checked
     * 
     * @return True if the last checked username is available
     */
	protected boolean getLastUsernameAvailability() {
		return this.udpServer.getLastUsernameAvailability();
	}
	
	/**
     * This method removes the UserSocket associated to a specific user
     *
     * @param user User that we want to remove 
     */
	protected void removeSocket(User user) {
		this.mapSockets.getValue(user).interrupt();
		this.mapSockets.remove(user);
	}
	
	/**
     * This method adds an association of a user and a IP address
     *
     * @param user User that we want to add
     * @param address Address that we want to add
     */
	protected void addAddress(User user, InetAddress address) {
		this.mapAddresses.putUser(user, address);
	}
	
	/**
     * This methods stops and deletes the different servers (UDP and TCP)
     */
	protected void stopAll() {
		this.udpServer.interrupt();
		this.connectionServer.interrupt();
		this.connectionServer=null; // supprime le TCPServer et les Sockets associés
		this.udpServer=null; // supprime l'UDPServer et les Sockets associés
		for (User u : this.agent.getUserStatusManager().getActiveUsers())
			this.removeSocket(u);; // interrompt tous les UserSockets
		this.mapSockets.clear();
	}
	
	/**
     * This method initializes and starts the differents servers (UDP and TCP)
     */
	protected void startAll() throws IOException {
		this.udpServer = new UDPServer(this.agent);
		this.connectionServer = new TCPServer(this.agent);
	}
	
	/**
     * This method prints both Addresses and UserSocket maps
     */
	protected void printAll() {
		this.mapAddresses.printAll();
		this.mapSockets.printAll();
	}
}
