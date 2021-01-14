package agent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import datatypes.*;
import userInterface.Interface;

public class Agent{
	
/*-----------------------Attributs static qui définissent des paramètres de l'agent distribué-------------------------*/
	
	/**Enable/disable debug informations*/
	public static boolean debug = true;
	/**Timeout used when checking username availability*/
	public static int timeout = 1000;
	/**Port number used for broadcast connections*/
	public static int broadcastPortNumber = 4445;
	/**Default port number used for outgoing TCP connections*/
	public static int defaultPortNumber = 1234;
	/**Current directory*/
	public static final String dir = System.getProperty("user.dir")+"/";
	/**Presence Server IP address*/
	public static String presenceServerIPAddress;
	/**Database Name*/
	public static String databaseFileName="database.db";
	/**Logs*/
	public static Logger logger = Logger.getLogger("MyLog");
	/**Logs File Handler*/
	public static FileHandler logfh;
	/**Log file name*/
	public static String logFileName="log.log";
	
/*-----------------------Attributs privés-------------------------*/
	
	/**Username of the user of this Agent*/
	protected String username;
	/**For the first connection*/
	protected String tempName; 	
	/** 1 if extern (not in the subnet of the Presence Server) & 0 if intern */
	protected int isExtern = 0;
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
	public Agent() {
		// Logs
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");
        /*LocalDateTime now = LocalDateTime.now();  
        String logFilename = "log-"+DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss").format(now)+".log";*/
        try {
			Agent.logfh = new FileHandler(Agent.dir+Agent.logFileName,true);
		} catch (Exception e) {
			Agent.errorMessage(String.format(
					"Could not create log file at %s",Agent.dir+logFileName),e);
		} 
        Agent.logger.addHandler(logfh);
        logger.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();  
        logfh.setFormatter(formatter); 
        Agent.printAndLog("\n\n\n\n----------------Agent was started----------------\n\n\n\n");
        
        // Files and images directories
        try {
			Files.createDirectories(Paths.get(Agent.dir+"file/"));
	        Files.createDirectories(Paths.get(Agent.dir+"image/"));
		} catch (IOException e) {
			Agent.errorMessage(String.format(
					"Could not create folders file and image at %s",Agent.dir),e);
		}
        
		this.username="";
		this.isFirstConnection=true;
		this.isReconnection=false;
		this.isDisconnected = false;

		this.databaseManager = new DatabaseManager(this);
		try {
			this.networkManager = new NetworkManager(this);
		} catch (Throwable t) {
			Interface.notifyAnotherInstanceIsRunning();
			synchronized(Interface.sync) {
				try {Interface.sync.wait();} catch (InterruptedException e1) {}
			}
			/*Remove duplicate logs*/
			Agent.closeLogs();
			(new File(Agent.dir+Agent.logFileName+".1")).delete();
			Agent.logfh = null;
			Agent.logger = null;
			Agent.errorMessage("Une autre instance de l'application est en cours"
					+ " d'éxecution, réessayer après avoir fermer l'autre instance",
			new Exception(t));
		}
		this.usernameManager = new UsernameManager(this);
		this.messageManager = new MessageManager(this);
		this.userStatusManager = new UserStatusManager(this);
		Interface.notifyAllOK();
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
		this.tempName=name;
		boolean ok=false;
		if ((!this.isFirstConnection ||this.isReconnection) && name.equals(this.username)) ok = true;
		else ok = this.usernameManager.checkUsernameAvailability(name);
		if (ok) {
			if (!this.isFirstConnection) { // Not first connection
				if (!this.username.equals(name)) {
					// Intern Users => Broadcast + POST Request
					// Extern Users => POST Request to the Presence Server
					if (this.isExtern==0)
						this.networkManager.sendBroadcast("changeUsername "+ this.username + " " + name);
					this.networkManager.sendPost("changeUsername",name);
					String oldUsername = this.username;
					this.username = name;
					this.usernameManager.userChangeUsername(oldUsername, name);
				}
			} else { // First connection
				this.username = name;
				databaseManager.addUser(null,name,1,this.isExtern);
				isFirstConnection=false;
				// Intern Users => Broadcast + POST Request
				// Extern Users => POST Request to the Presence Server
				if (this.isExtern==0) 
					networkManager.sendBroadcast("connect " + username + " "+ isExtern);
				String response = networkManager.sendPost("connect",username);
				if (!(response.trim().isEmpty()))
					this.networkManager.fromExternList(response);
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
     * 
     * @return True if the message could be sent
     */
	public boolean sendMessage(String dest, Message message) {
		return this.messageManager.sendMessage(dest,message);
	}
	
	/**
     * The user can disconnect with this method
     * <p>Note that this method put all the other users as disconnected
     * <br>It will send a "disconnect" broadcast message to notify other users
     * <br>UDP Message template : "disconnect username"
     */
	public void disconnect() {
		if (!this.isFirstConnection && !this.isDisconnected) {
			// Intern Users => Broadcast + POST Request
			// Extern Users => POST Request to the Presence Server
			if (this.isExtern==0)
				this.networkManager.sendBroadcast("disconnect " + this.username);
			this.networkManager.sendPost("disconnect",this.username);				
			this.networkManager.stopAll();
			//on met tous les utilisateurs comme déconnectés lorsque l'on se déconnecte
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
	 * @throws Throwable 
     */
	public void reconnect() throws Throwable {
		if (this.isDisconnected) {
			this.isFirstConnection=true;
			this.isReconnection=true;
			this.isDisconnected=false;
			this.networkManager.startAll();
		} else {
			Agent.printAndLog(String.format("\nNot disconnected, cannot reconnect\n"));
		}
	}
	
	/**
     * Get the Username associated to the Agent
     */
	public String getUsername() {
		return this.username;
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
	
	/**
     * Get the DatabaseManager associated to the Agent
     */
	protected DatabaseManager getDatabaseManager() {
		return this.databaseManager;
	}
	
	
/*-----------------------Méthodes - Erreur et débug-------------------------*/

	
	/**
	 * Method used to print ERROR message + exception infos
	 * 
	 * @param s
	 * @param e
	 * */
	public static final void errorMessage(String s, Exception e) {
		System.out.printf(s);
		System.out.println(e.getMessage());
		e.printStackTrace();
		if (logger!=null) {
			logger.info(s);
			logger.log(Level.INFO,e.getMessage(),e);
			logfh.close();
		}
        System.exit(-1);
	}
	
	/**
	 * Method used to print infos in debug mode and add logs
	 * 
	 * @param message
	 * */
	public static final void printAndLog(String message) {
		if (Agent.debug) System.out.printf(message);
		logger.info(message);
	}
	
	/**
	 * This methods is used to close the file handler for logs
	 * */
	public static final void closeLogs() {
		logfh.close();
	}
}
