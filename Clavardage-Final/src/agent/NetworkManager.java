package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

	/**The agent that created this NetworkManager*/
	private Agent agent;
	
	/**MyMap that associates a username to a UserSocket*/
	private Map<String,UserSocket> mapSockets;
		
	/**TCPServer that will manage incoming TCP connections*/
	private TCPServer connectionServer;
	/**UDPServer that will manage incoming UDP connections*/
	private UDPServer udpServer;
	/**UDPClient used to send UDP messages (also broadcast)*/
	private UDPClient udpClient;
	
/*
 * Message type :
 * "connect username externId" -> Pour notifier notre arrivée et que les autres utilisateurs se connectent
 * "disconnect username externId" -> Pour notifier notre départ
 * "changeUsername oldUsername newUsername" -> Pour notifier le changement de notre username
 * "checkUsernameAvailability username" -> Requête demandant la disponibilité de l'username
 * "tellUsernameAvailability username true/false" -> Réponse notifiant la disponibilité(ou non) de l'username
 * "canAccess username externId" -> Après avoir reçu un "connect", renvoie "canAccess" pour notifier notre adresse IP au nouvel arrivant
 * "updateDisconnectedUsers username address externId" -> Après avoir reçu un "connect", met à jour les utilisateurs déconnectés du nouvel arrivant
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

		this.mapSockets = new HashMap<>();
		
		this.checkInternOrExtern();
	}
	
	
/*-----------------------Méthodes - Réseau-------------------------*/
	
	/**
     * This method checks if the PresenceServer is on the same broadcast network or if we are a extern user
     *  
     */
	private void checkInternOrExtern() {
		// TODO Auto-generated method stub
		
	}

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
			if (!(username.equals(this.agent.getUsername())) && !ok) {
				ok = this.getAddress(username).equals(address);
			// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
			}
			this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ ok, address);
		} else { 
			// A la première connexion : premier arrivé / premier servi
			// Si quelqu'un veut le même nom, on lui dit qu'il n'est pas disponible
			if (username.equals(this.agent.tempName)) this.udpClient.sendBroadcast("tellUsernameAvailability "+username+" "+ false);
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
			this.udpClient.sendUDP("canAccess "+this.agent.getUsername()+" "+this.agent.extern_id, address);
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
			for (String username : this.agent.getUserStatusManager().getDisconnectedUsers()) {
				String disconnectedAddress = NetworkManager.addressToString(
						this.agent.getNetworkManager().getAddress(username));
				int externId = this.agent.getNetworkManager().getExternId(username);
				this.udpClient.sendUDP("updateDisconnectedUsers "+ username+" "+disconnectedAddress+" "+externId, address);
			}
		}
	}

	/**
     * This method adds a new UserSocket to the local list after a new connection was established
     *  
     * @param socket Socket from which we create the UserSocket
     * @param externId Extern id of the user we add (0 if intern user)
     */
	protected void newActiveUserSocket(Socket socket, int externId) {
		// Le thread attend que l'on reçoive l'adresse de l'utilisateur avant de pouvoir créer le UserSocket
		if (!this.agent.isFirstConnection) {
			final class MyThread extends Thread {
				private NetworkManager nm; private Socket socket;
				public MyThread(NetworkManager nm, Socket socket) {this.nm=nm;this.socket=socket;}
				public void run() {
					String username ="";
					synchronized(this.nm) {
						while (username.equals("")) {
							//Agent.printAndLog(String.format("Could not get User from address : %s -> Waiting for canAccess\n",socket.getInetAddress()));
							try {this.nm.wait(Agent.timeout);} catch (InterruptedException e) {}
							username=this.nm.agent.getUsernameManager().getUsername(socket.getInetAddress(),externId);
						}
					}
					if (this.nm.getSocket(username)!=null) {
						Agent.printAndLog(String.format("Ignored creating UserSocket: "+username+"\n"));
					}else {
						Agent.printAndLog(String.format("New socket connected: "+username+"\n"));
						this.nm.mapSockets.put(username, new UserSocket(username, this.nm.agent, socket));
						if (Agent.debug) nm.printAll();
					}
				}
			}
			(new MyThread(this,socket)).start();
		}
	}
		
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
		
	
	/**
     * This method returns the username associated to a specific UserSocket
     *
     * @param usocket UserSocket that we want to resolve
     * 
     * @return The username associated to the user socket
     */
	protected String socketResolve(UserSocket uSocket) {
		return uSocket.username;
	}
	
	/**
     * This method returns the UserSocket associated to this user
     *
     * @param username
     */
	protected UserSocket getSocket(String username) {
		return this.mapSockets.get(username);
	}
	
	/**
     * This method returns the IP Address associated to this username
     *
     * @param username
     * 
     * @return The IP Address
     */
	protected InetAddress getAddress(String username) {
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectByName);
	        pstmt.setString(1,username);
	        ResultSet rs  = pstmt.executeQuery();
	        return InetAddress.getByName(rs.getString("address"));
		} catch (Exception e) {
			Agent.errorMessage(
					String.format("ERROR when trying to get address of user %s in the database\n",username), e);
		}
        return null; // Pas accessible
	}
	
	/**
     * This method returns the extern id associated to this username
     *
     * @param username
     * 
     * @return The extern id
     */
	protected int getExternId(String username) {
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectByName);
	        pstmt.setString(1,username);
	        ResultSet rs  = pstmt.executeQuery();
	        return Integer.valueOf(rs.getString("externId"));
		} catch (Exception e) {
			Agent.errorMessage(
					String.format("ERROR when trying to get externId of user %s in the database\n",username), e);
		}
        return 0; // Pas accessible
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
     * This method removes the UserSocket associated to a specific username
     *
     * @param username User that we want to remove 
     */
	protected void removeSocket(String username) {
		if (!username.equals(this.agent.getUsername())) {// If the user is not us
			UserSocket us = this.mapSockets.get(username);
			if (us != null) {
				us.interrupt();
			}
			this.mapSockets.remove(username);
		}
	}
	
	/**
     * This method changes the username associated to a UserSocket
     *
     * @param oldUsername
     * @param newUsername
     */
	protected void changeSocketUsername(String oldUsername, String newUsername) {
		UserSocket us = this.getSocket(oldUsername);
		if (us != null) {
			us.username = newUsername;
			this.mapSockets.remove(oldUsername);
			this.mapSockets.put(newUsername,us);
		}
	}
	
	/**
     * This methods stops and deletes the different servers (UDP and TCP)
     */
	protected void stopAll() {
		this.udpServer.interrupt();
		this.connectionServer.interrupt();
		this.connectionServer=null; // supprime le TCPServer et les Sockets associés
		this.udpServer=null; // supprime l'UDPServer et les Sockets associés
		for (String u : this.agent.getUserStatusManager().getActiveUsers())
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
     * This method prints both UserSocket maps and Addresses from the database
     */
	protected void printAll() {
		System.out.println("Network maps");
		System.out.println("	Addresses");
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectAll);
	        ResultSet rs  = pstmt.executeQuery();
	        // loop through the result set
            while (rs.next()) {
    	        System.out.printf("%s -> %s\n", rs.getString("username"), rs.getString("address"));
            }
		} catch (Exception e) {
			Agent.errorMessage(
					String.format("ERROR when trying to get all the address of the table users in the database\n"), e);
		}
		System.out.println("	mapSockets");
		for (String u: this.mapSockets.keySet()) {
			System.out.printf("%s -> %s\n", u, this.mapSockets.get(u));
		}
	}
	
	/**
     * Return true if the database contains this address
     *
     * @param address IP Address to check
     * @return True if the database contains this address
     */
	protected boolean containsAddressAndExternId(InetAddress address, int externId) {
		try {
			PreparedStatement pstmt  = this.agent.getDatabaseManager().getConnection().prepareStatement(
					this.agent.getDatabaseManager().selectByAddressAndExternId);
			pstmt.setString(1, NetworkManager.addressToString(address));
			pstmt.setInt(2, externId);
	        ResultSet rs  = pstmt.executeQuery();
	        return !(rs.isClosed());
		} catch (SQLException e) {
        	Agent.errorMessage(
					String.format("ERROR when trying to check if address %s | externId %d is in the database\n",address,externId), e);
		}
		return true; // Pas accessible
	}
	
	
/*-----------------------Méthodes - Utilitaires-------------------------*/	
	
	
	/**
	 * Use this method to convert from address to String
	 * 
	 * @param address Address to convert
	 * @return The address as a String
	 */
	public static String addressToString(InetAddress address) {
		return address.toString().split("/")[1];
	}

}
