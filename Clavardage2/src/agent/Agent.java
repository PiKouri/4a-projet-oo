package agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import datatypes.*;
import userInterface.Interface;
import userInterface.User;

public class Agent{
	
/*-----------------------Attributs static qui définissent des paramètres de l'agent distribué-------------------------*/
	
	/**Enable/disable debug informations*/
	public static final boolean debug = true;
	/**Timeout used when checking username availability*/
	public static final long timeout = 1000; // Timeout
	/**Port number used for broadcast connections*/
	public static final int broadcastPortNumber = 4445;
	/**Default port number used for outgoing TCP connections*/
	public static final int defaultPortNumber = 1234;
	
/*-----------------------Attributs privés-------------------------*/
	
	/**User of this Agent*/
	protected User me;
	/**True when first connection (or after reconnection)*/
	protected boolean isFirstConnection;
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
	
/*-----------------------Constructeurs-------------------------*/
	/**
     * Constructor for the class Agent
     * <p>This method will send a "connect"
     * broadcast message to notify other users
     * <br>UDP Message template : "connect username"
     * 
     * @param me Object User representing the actual user
     */
	public Agent(User me) throws IOException {
		this.me = me;
		this.isFirstConnection=true;
		this.isDisconnected = false;
		
		this.usernameManager = new UsernameManager(this);
		this.networkManager = new NetworkManager(this);
		this.messageManager = new MessageManager(this);
		this.userStatusManager = new UserStatusManager(this);
		
		chooseUsername(me.getUsername());
		this.isFirstConnection=false;
		
		this.networkManager.sendBroadcast("connect " + this.me.getUsername());
	}
	
	
/*-----------------------Méthodes - Utilisateur-------------------------*/
	
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
		boolean ok=false;
		ok = this.usernameManager.checkUsernameAvailability(name);
		if (!this.isFirstConnection && name.equals(this.me.getUsername())) ok = true;
		String newName = name;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in); // A changer avec l'interface graphique
		while (!ok) {
			try {Thread.sleep(100);} catch (Exception e) {} // Attente pour affichage
			System.out.print("Enter your name: ");
			newName = scanner.next();
			ok = this.usernameManager.checkUsernameAvailability(newName);
		}
		if (!this.isFirstConnection) {
			this.networkManager.sendBroadcast("changeUsername "+ this.me.getUsername() + " " + newName);
			this.usernameManager.changeUsername(this.me,newName);
		} else {
			this.usernameManager.addUsername(this.me,newName);
		}
		this.me.changeUsername(newName);
	}
	
	/**
     * The user can get the active usernames with this method
     * 
     * @return List of all active usernames
     */
	public ArrayList<String> viewActiveUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : this.userStatusManager.getActiveUsers()) {
			list.add(this.usernameManager.getUsername((user)));
		}
		return list;
	}
	
	/**
     * The user can get the disconnected usernames with this method
     * 
     * @return List of all disconnected usernames
     */
	public ArrayList<String> viewDisconnectedUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : this.userStatusManager.getDisconnectedUsers()) {
			list.add(this.usernameManager.getUsername((user)));
		}
		return list;
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
		User u = this.usernameManager.nameResolve(username);
		if (u!=null)return this.messageManager.getMessages(u);
		else return null; // Erreur sur le nom, Impossible avec l'interface graphique normalement
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
		// TODO
		if (Agent.debug) this.getNetworkManager().printAll();
		User user = this.usernameManager.nameResolve(dest);
		if (user == null) System.out.printf("Could not resolve username : %s\n", dest);
		else this.messageManager.sendMessage(user,message);
	}
	
	/**
     * The user can disconnect with this method
     * <p>Note that this method put all the other users as disconnected
     * <br>It will send a "disconnect" broadcast message to notify other users
     * <br>UDP Message template : "disconnect username"
     */
	public void disconnect() throws IOException {
		this.networkManager.sendBroadcast("disconnect " + this.me.getUsername());
		this.networkManager.stopAll();
		//on met tous les utilisateurs comme déconnectés lorsque l'on se déconnecte
		this.userStatusManager.putAllUsersDisconnected();
		this.isDisconnected=true;
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
			
			this.networkManager.startAll();
			if (Agent.debug) System.out.printf("\nLast time, your name was : %s\n",this.me.getUsername()); // A changer avec l'interface graphique
			this.chooseUsername("");
			this.isFirstConnection=false;
			this.networkManager.sendBroadcast("connect " + this.me.getUsername()); // A voir
		} else {
			if (Agent.debug) System.out.printf("\nNot disconnected, cannot reconnect\n");
		}
	}
	
	/**
     * Get the User associated to the Agent
     */
	public User getUser() {
		return this.me;
	}

	
/*-----------------------Méthodes - Getteurs des Managers-------------------------*/
	// Protected pour rendre accessible seulement aux autres classes du même paquetage (agent)
	
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
	
}
