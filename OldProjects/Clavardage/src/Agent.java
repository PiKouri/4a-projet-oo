import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Agent extends Subject {
	
	public static final boolean debug = true;
	
	public static final long timeout = 1000; // Timeout
	
	public static final int broadcastPortNumber = 4445; // A modifier sur le diagramme
	public static final int defaultPortNumber = 1234; // A modifier sur le diagramme
	
	private User me; // A modifier sur le diagramme
	
	private MyMap<User,String> mapUsernames; // A modifier sur le diagramme
	private MyMap<User,UserSocket> mapSockets; // A modifier sur le diagramme
	private MyMap<User, InetAddress> mapAddresses; // A modifier sur le diagramme
	private Map<User,ArrayList<Message>> mapMessages; // A modifier sur le diagramme
	
	private ArrayList<User> activeUsers; //private Map<User, InetAddress> activeUsers;  // A modifier sur le diagramme
	private ArrayList<User> disconnectedUsers;//private Map<InetAddress, User> disconnectedUsers;  // A modifier sur le diagramme
	
	private Server connectionServer; // A modifier sur le diagramme
	private UDPServer udpServer; // A modifier sur le diagramme
	private UDPClient udpClient; // A modifier sur le diagramme
	
	private Object usernameChecked; // Objet qui permet de synchronisé l'Agent et l'UDP Server (lors de checkUsernameAvailability // A modifier sur le diagramme
	
	private boolean disconnected; // A modifier sur le diagramme
	
	private boolean firstConnection; // A modifier sur le diagramme
	
/*-----------------------Constructeurs-------------------------*/
	
	public Agent(User me) throws IOException { // A modifier sur le diagramme
		this.firstConnection=true;
		this.disconnected = false;
		this.me = me;
		
		this.usernameChecked = new Object();
		
		this.udpClient = new UDPClient();
		
		this.udpServer = new UDPServer(this,this.usernameChecked);
		
		this.connectionServer = new Server(this);

		this.mapUsernames = new MyMap<User,String>();
		this.mapSockets = new MyMap<User,UserSocket>();
		this.mapAddresses = new MyMap<User,InetAddress>();
		this.mapMessages = new HashMap<>();
		
		this.activeUsers = new ArrayList<User>();
		this.disconnectedUsers = new ArrayList<User>();
		
		chooseUsername(me.getUsername());
		this.firstConnection=false;
		this.udpClient.sendBroadcast("connect " + this.me.getUsername());
	}
	
/*-----------------------Méthodes utilisateur-------------------------*/
	public void chooseUsername(String name) { // A modifier sur le diagramme
		boolean ok=false;
		ok = checkUsernameAvailability(name);
		if (!this.firstConnection && name.equals(this.me.getUsername())) ok = true;
		String newName = name;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in); // A changer avec l'interface graphique
		while (!ok) {
			try {Thread.sleep(100);} catch (Exception e) {} // Attente pour affichage
			System.out.print("Enter your name: ");
			newName = scanner.next();
			ok = checkUsernameAvailability(newName);
		}
		if (!this.firstConnection) {
			this.udpClient.sendBroadcast("changeUsername "+ this.me.getUsername() + " " + newName);
		} else {
			this.mapUsernames.putUser(this.me,newName);
		}
		this.me.changeUsername(newName);
		this.mapUsernames.replaceValue(this.me,newName);
	}
	
	public ArrayList<String> viewActiveUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : activeUsers) {
			list.add(this.mapUsernames.getValue(user));
		}
		return list;
	}
	
	public ArrayList<String> viewDisconnectedUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : disconnectedUsers) {
			list.add(this.mapUsernames.getValue(user));
		}
		return list;
	}
	
	public ArrayList<Message> getMessageHistory(String username){
		return this.mapMessages.get(nameResolve(username));
	}
	
	public void sendMessage(Message message) {
		
	}
	
	public void disconnect() throws IOException { // A modifier sur le diagramme
		this.udpClient.sendBroadcast("disconnect " + this.me.getUsername());
		this.udpServer.interrupt();
		this.connectionServer.interrupt();
		this.connectionServer=null; // supprime le Server et les Sockets
		this.udpServer=null; // supprime le Server et les Sockets
		
		//on met tous les utilisateurs comme déconnectés lorsque l'on se déconnecte
		for (User u : this.activeUsers) this.disconnectedUsers.add(u);
		this.activeUsers=new ArrayList<User>();
		
		this.disconnected=true;
	}
	
	public void reconnect() throws IOException { // A modifier sur le diagramme
		if (this.disconnected) {
			this.firstConnection=true;
			this.udpServer = new UDPServer(this,this.usernameChecked);
			this.connectionServer = new Server(this);
			if (Agent.debug) System.out.printf("\nLast time, your name was : %s\n",this.me.getUsername()); // A changer avec l'interface graphique
			this.chooseUsername("");
			this.firstConnection=false;
			this.udpClient.sendBroadcast("connect " + this.me.getUsername()); // A voir
		} else {
			if (Agent.debug) System.out.printf("\nNot disconnected, cannot reconnect\n");
		}
	}

/*-----------------------Méthodes utilitaires-------------------------*/
	
	private User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}

	private boolean checkUsernameAvailability(String username) { // A modifier sur le diagramme
		boolean ok = false;
		
		if (username.equals("")) return false;
		
		long time = System.currentTimeMillis();//temps de depart
		long fin=time + timeout;//temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailability)
		
		if (this.firstConnection) { // Première connexion
			this.udpClient.sendBroadcast("checkUsernameAvailability "+username);
			/*try {
				while (!ok) {
					if (fin <= System.currentTimeMillis()) throw new TimeoutException();
					ok = this.udpServer.wasUsernameChecked();
					try {Thread.sleep(100);} catch (Exception e) {}
				}
				ok = this.udpServer.getLastUsernameAvailability();
			} catch (TimeoutException e) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}*/
			
			try {
				synchronized(this.usernameChecked) {this.usernameChecked.wait(timeout);}
			} catch (InterruptedException e) {} // wait for response of other users
			ok = this.udpServer.getLastUsernameAvailability();
			if (fin <= System.currentTimeMillis()) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}
		} else { // Autrement on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
		}
		return ok;
	}
	
	public static boolean verifyUniqAddress(ArrayList<User> list, InetAddress address) { // A modifier sur le diagramme
		boolean ok = true;
		for (User temp : list) {
			ok = !temp.getAddress().equals(address);
			if (!ok) break;
		}
		return ok;
	}

/*-----------------------Méthodes Réseau-------------------------*/
	
	public void tellUsernameAvailibility(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			boolean ok = checkUsernameAvailability(username);
			// Envoie vrai si le nom est inconnu ...
			if (!(username.equals(this.me.getUsername())) && !ok) {
				User user = nameResolve(username);
				ok = user.getAddress().equals(address);
				// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
			}
			this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ ok, address);
		} else { 
			// A la première connexion : premier arrivé / premier servi
			// Si quelqu'un veut le même nom, on lui dit qu'il n'est pas disponible
			if (username.equals(this.me.getUsername())) this.udpClient.sendBroadcast("tellUsernameAvailability "+username+" "+ false);
		}
			
	}
	
	public void tellCanAccess(InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			this.udpClient.sendUDP("canAccess "+this.me.getUsername(), address);
			// Une nouvelle personne veut qu'on se connecte à son serveur : on lui donne notre IP d'abord (pour faire le lien IP <-> Username)
		}
	}
	
	public void tellDisconnectedUsers(InetAddress address) {// A modifier sur le diagramme
		if (!this.firstConnection) {
			for (User disconnectedUser : this.disconnectedUsers) {
				String disconnectedUsername = disconnectedUser.getUsername();
				String disconnectedAddress = disconnectedUser.getAddress().toString();
				this.udpClient.sendUDP("updateDisconnectedUsers "+ disconnectedUsername+" "+disconnectedAddress, address);
			}
		}
	}

/*-----------------------Méthodes actualisation de nos informations-------------------------*/

	public void updateDisconnectedUsers(String username, InetAddress address) {// A modifier sur le diagramme
		if (verifyUniqAddress(this.disconnectedUsers,address) && verifyUniqAddress(this.activeUsers,address)) { // Si l'addresse n'est pas déjà connue, on l'ajoute
			User user = new User(username,address);
			this.disconnectedUsers.add(user);
			this.mapUsernames.putUser(user, username);
			this.mapAddresses.putUser(user, address);
		}
	}
	
	public void userDisconnect(String username, InetAddress address) throws IOException { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User user = nameResolve(username);
			//this.mapUsernames.remove(user);
			///////////////////////////////////Pas encore fait/////////////////////////////////////////this.mapSockets.getValue(user).interrupt();
			this.mapSockets.remove(user);
			this.activeUsers.remove(user);
			this.disconnectedUsers.add(user);
		}
	}
	
	public void userConnect(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User oldUser = mapAddresses.getUser(address);
			if (oldUser==null) { // New address
				if (Agent.debug) System.out.printf("New user connected : %s\n",username);
				User user = new User(username, address);
				this.mapUsernames.putUser(user, username);
				this.activeUsers.add(user);
				this.mapAddresses.putUser(user, address);
			} else {
				 {
					if (!(oldUser.getUsername().equals(username))) {
						if (Agent.debug) System.out.printf("Old user reconnected : %s -> %s\n",oldUser.getUsername(), username);
						oldUser.changeUsername(username);
						this.mapUsernames.replaceValue(oldUser, username);
					}
					if (Agent.debug) System.out.printf("Old user reconnected : %s\n",username);
				}
				this.activeUsers.add(oldUser);
				this.disconnectedUsers.remove(oldUser);		
			} 
		}
	}
	
	public void userChangeUsername(String oldUsername, String newUsername) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			if (oldUsername != newUsername) {
				User user = nameResolve(oldUsername);
				user.changeUsername(newUsername);
				this.mapUsernames.replaceValue(user, newUsername); 
			}
		}
	}

	public void newActiveUserSocket(Socket socket) {
		if (Agent.debug) System.out.printf("New socket connected\n");
	}

/*-----------------------Méthodes réception de messages-------------------------*/
	
	public void receiveFile(File file) {
		
	}
	
	public void receiveText(Text text) {
		
	}

	public void receiveImage(Image image) {
	
	}
	
}
