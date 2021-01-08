package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;

import datatypes.*;
import userInterface.Interface;

public class Agent{
	
/*-----------------------Attributs static qui d�finissent des param�tres de l'agent distribu�-------------------------*/
	
	/**Enable/disable debug informations*/
	public static boolean debug = true;
	/**Timeout used when checking username availability*/
	public static int timeout = 1000;
	/**Port number used for broadcast connections*/
	public static int broadcastPortNumber = 4445;
	/**Default port number used for outgoing TCP connections*/
	public static int defaultPortNumber = 1234;
	/**Current directory*/
	public static final String dir = System.getProperty("user.dir");
	/**Presence Server IP address*/
	public static InetAddress presenceServerIPAddress;
	/**Database Name*/
	public static String databaseFileName="database.db";
	
/*-----------------------Attributs priv�s-------------------------*/
	
	/**Username of the user of this Agent*/
	protected String username;
	/**For the first connection*/
	protected String tempName; 
	/**IP Address localhost*/
	protected InetAddress localhost;	
	/**Our Extern_id (0 if intern, 1 if extern*/
	protected int extern_id = 0;
	/**True when first connection (or after reconnection)*/
	protected boolean isFirstConnection;
	/**True when reconnection (or after reconnection)*/
	protected boolean isReconnection;
	/**True when disconnected*/
	protected boolean isDisconnected;
	
	/**UsernameManager associated to this agent*/
	private UsernameManager usernameManager;
	/**NetworkManager associated to this agent*/
	private NetworkManager networkManager;
	/**MessageManager associated to this agent*/
	private MessageManager messageManager;
	/**UserStatusManager associated to this agent*/
	private UserStatusManager userStatusManager;
	/**DatabaseManager associated to this agent*/
	private DatabaseManager databaseManager;
	
/*-----------------------Constructeurs-------------------------*/
	/**
     * Constructor for the class Agent
     * <p>This method will send a "connect"
     * broadcast message to notify other users
     * <br>UDP Message template : "connect username"
     * 
     * @param me Object User representing the actual user
     */
	public Agent() throws IOException {
		this.username="";
		this.localhost=InetAddress.getLocalHost();
		this.isFirstConnection=true;
		this.isReconnection=false;
		this.isDisconnected = false;

		this.networkManager = new NetworkManager(this);
		this.databaseManager = new DatabaseManager(this);
		this.usernameManager = new UsernameManager(this);
		this.messageManager = new MessageManager(this);
		this.userStatusManager = new UserStatusManager(this);
		Interface.notifyAllOK();
	}
	
	
/*-----------------------M�thodes - Utilisateur-------------------------*/
	
	/**
     * The user can change his username with this method
     * <p>This method uses checkUsernameAvailability() from usernameManager 
     * to check if the username is available. <br>It will send a "changeUsername"
     * broadcast message to notify other users
     * <br>UDP Message template : "changeUsername oldUsername newUsername"
     * 
     * @param name Username that the user wants to use
     */
	public void chooseUsername(String name) {
		this.tempName=name;
		boolean ok=false;
		if ((!this.isFirstConnection ||this.isReconnection) && name.equals(this.username)) ok = true;
		else ok = this.usernameManager.checkUsernameAvailability(name);
		if (ok) {
			if (!this.isFirstConnection) { // Not first connection
				if (!this.username.equals(name)) {
					this.networkManager.sendBroadcast("changeUsername "+ this.username + " " + name);
					String oldUsername = this.username;
					this.username = name;
					this.usernameManager.userChangeUsername(oldUsername, name);
				}
			} else { // First connection
				this.username = name;
				databaseManager.addUser(localhost,name,1,this.extern_id);
				isFirstConnection=false;
				networkManager.sendBroadcast("connect " + username);
			}
			Interface.notifyUsernameAvailable();
			if (this.isReconnection)this.isReconnection=false;
		} else {
			Interface.notifyUsernameNotAvailable(name);
		}
		
	}
	
	/**
     * The user can get the active usernames with this method
     * 
     * @return List of all active usernames
     */
	public ArrayList<String> viewActiveUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		this.userStatusManager.getActiveUsers().sort(Comparator.comparing(String::toString));
		for (String username : this.userStatusManager.getActiveUsers()) {
			list.add(username);
		}
		//list.sort;
		return list;
	}
	
	/**
     * The user can get the disconnected usernames with this method
     * 
     * @return List of all disconnected usernames
     */
	public ArrayList<String> viewDisconnectedUsernames(){
		this.userStatusManager.getDisconnectedUsers().sort(
						Comparator.comparing(String::toString));
		return this.userStatusManager.getDisconnectedUsers();
	}
	
	/**
     * The user can get the conversation history that he/she had 
     * with a certain user with this method
     * 
     * @param username Username of the other user
     * 
     * @return List of all messages
     */
	public ArrayList<Message> getMessageHistory(String username){
		return this.messageManager.getMessages(username);
	}
	
	/**
     * The user can send messages to a certain user with this method
     * <p>This method will use the receiver attribute of the class Message
     * to know the destination
     * 
     * @param dest Destination user (as String)
     * @param message Message that the user wants to send
     */
	public void sendMessage(String dest, Message message) {
		if (Agent.debug) this.getNetworkManager().printAll();
		this.messageManager.sendMessage(dest,message);
	}
	
	/**
     * The user can disconnect with this method
     * <p>Note that this method put all the other users as disconnected
     * <br>It will send a "disconnect" broadcast message to notify other users
     * <br>UDP Message template : "disconnect username"
     */
	public void disconnect() {
		if (!this.isFirstConnection && !this.isDisconnected) {
			this.networkManager.sendBroadcast("disconnect " + this.username);
			this.networkManager.stopAll();
			//on met tous les utilisateurs comme d�connect�s lorsque l'on se d�connecte
			this.userStatusManager.putAllUsersDisconnected();
			this.isDisconnected=true;
		}
	}
	
	/**
     * The user can reconnect with this method
     * <p>The user will have to re-enter a new username
     * (he/she can re-use his/her old username)
     * <br>It will send a "connect" broadcast message to notify other users
     * <br>UDP Message template : "connect username"
     */
	public void reconnect() throws IOException {
		if (this.isDisconnected) {
			this.isFirstConnection=true;
			this.isReconnection=true;
			this.isDisconnected=false;
			this.networkManager.startAll();
		} else {
			if (Agent.debug) System.out.printf("\nNot disconnected, cannot reconnect\n");
		}
	}
	
	/**
     * Get the Username associated to the Agent
     */
	public String getUsername() {
		return this.username;
	}

	
/*-----------------------M�thodes - Getteurs des Managers-------------------------*/
	// Protected pour rendre accessible seulement aux autres classes du m�me paquetage (agent)
	
	
	/**
     * Get the UsernameManager associated to the Agent
     */
	protected UsernameManager getUsernameManager() {
		return this.usernameManager;
	}
	
	/**
     * Get the NetworkManager associated to the Agent
     */
	protected NetworkManager getNetworkManager() {
		return this.networkManager;
	}
	
	/**
     * Get the MessageManager associated to the Agent
     */
	protected MessageManager getMessageManager() {
		return this.messageManager;
	}
	
	/**
     * Get the UserStatusManager associated to the Agent
     */
	protected UserStatusManager getUserStatusManager() {
		return this.userStatusManager;
	}
	
	/**
     * Get the DatabaseManager associated to the Agent
     */
	protected DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}
	
	
/*-----------------------M�thodes - Erreur-------------------------*/
	
	
	public static final void errorMessage(String s, Exception e) {
		System.out.printf(s);
		System.out.println(e.getMessage());
		e.printStackTrace();
        System.exit(-1);
	}
	
}
