package agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import datatypes.*;
import userInterface.Interface;
import userInterface.User;

public class Agent{
	
/*-----------------------Attributs static qui d�finissent des param�tres de l'agent distribu�-------------------------*/
	
	/**Enable/disable debug informations*/
	public static final boolean debug = true;
	/**Timeout used when checking username availability*/
	public static final long timeout = 1000; // Timeout
	/**Port number used for broadcast connections*/
	public static final int broadcastPortNumber = 4445;
	/**Default port number used for outgoing TCP connections*/
	public static final int defaultPortNumber = 1234;
	
/*-----------------------Attributs priv�s-------------------------*/
	
	/**User of this Agent*/
	protected User me;
	/**For the first connection*/
	protected String tempName; 
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
		this.isReconnection=false;
		this.isDisconnected = false;
		
		this.usernameManager = new UsernameManager(this);
		this.networkManager = new NetworkManager(this);
		this.messageManager = new MessageManager(this);
		this.userStatusManager = new UserStatusManager(this);
		
	}
	

/*-----------------------Classes - Comparateur pour trier les listes dans l'ordre alphab�tique-------------------------*/


	private class NameSorter implements Comparator<User> 
	{
		@Override
		public int compare(User u1, User u2) {
			return u1.getUsername().compareToIgnoreCase(u2.getUsername());
		}
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
		if ((!this.isFirstConnection ||this.isReconnection) && name.equals(this.me.getUsername())) ok = true;
		else ok = this.usernameManager.checkUsernameAvailability(name);
		if (ok) {
			if (!this.isFirstConnection) {
				if (!this.me.getUsername().equals(name))
					this.networkManager.sendBroadcast("changeUsername "+ this.me.getUsername() + " " + name);
				this.usernameManager.changeUsername(this.me,name);
				this.me.changeUsername(name);
			} else {
				this.usernameManager.addUsername(this.me,name);
				this.isFirstConnection=false;
				this.me.changeUsername(name);
				this.networkManager.sendBroadcast("connect " + this.me.getUsername());
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
		this.userStatusManager.getActiveUsers().sort(new NameSorter());
		for (User user : this.userStatusManager.getActiveUsers()) {
			list.add(this.usernameManager.getUsername((user)));
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
		ArrayList<String> list = new ArrayList<String>();
		this.userStatusManager.getDisconnectedUsers().sort(new NameSorter());
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
		//this.messageManager.sendMessage(dest,message);
	}
	
	/**
     * The user can disconnect with this method
     * <p>Note that this method put all the other users as disconnected
     * <br>It will send a "disconnect" broadcast message to notify other users
     * <br>UDP Message template : "disconnect username"
     */
	public void disconnect() throws IOException {
		if (!this.isFirstConnection && !this.isDisconnected) {
			this.networkManager.sendBroadcast("disconnect " + this.me.getUsername());
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
     * Get the User associated to the Agent
     */
	public User getUser() {
		return this.me;
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
	
}
