package agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import datatypes.*;
import userInterface.User;

public class Agent{
	
/*-----------------------Attributs static qui définissent des paramètres de l'agent distribué-------------------------*/
	
	public static final boolean debug = true;
	public static final long timeout = 1000; // Timeout
	public static final int broadcastPortNumber = 4445;
	public static final int defaultPortNumber = 1234;
	
/*-----------------------Attributs privés-------------------------*/
	
	protected User me;
	protected boolean isFirstConnection;
	protected boolean isDisconnected;
	
	private UsernameManager usernameManager;
	private NetworkManager networkManager;
	private MessageManager messageManager;
	private UserStatusManager userStatusManager;
	
/*-----------------------Constructeurs-------------------------*/
	
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
	
	public ArrayList<String> viewActiveUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : this.userStatusManager.getActiveUsers()) {
			list.add(this.usernameManager.getUsername((user)));
		}
		return list;
	}
	
	public ArrayList<String> viewDisconnectedUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : this.userStatusManager.getDisconnectedUsers()) {
			list.add(this.usernameManager.getUsername((user)));
		}
		return list;
	}
	
	public ArrayList<Message> getMessageHistory(String username){
		return this.messageManager.getMessages(this.usernameManager.nameResolve(username));
	}
	
	public void sendMessage(Message message) {
		
	}
	
	public void disconnect() throws IOException {
		this.networkManager.sendBroadcast("disconnect " + this.me.getUsername());

		this.networkManager.stopAll();
		
		//on met tous les utilisateurs comme déconnectés lorsque l'on se déconnecte
		this.userStatusManager.putAllUsersDisconnected();
		
		this.isDisconnected=true;
	}
	
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

	
/*-----------------------Méthodes - Getteurs des Managers-------------------------*/
	// Protected pour rendre accessible seulement aux autres classes du même paquetage (agent)
	
	protected UsernameManager getUsernameManager() {
		return this.usernameManager;
	}
	
	protected NetworkManager getNetworkManager() {
		return this.networkManager;
	}
	
	protected MessageManager getMessageManager() {
		return this.messageManager;
	}
	
	protected UserStatusManager getUserStatusManager() {
		return this.userStatusManager;
	}
	
}
