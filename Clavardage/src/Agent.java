import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Agent extends Subject {
	
/*-----------------------Attributs-------------------------*/
	
	// NewUsername enlevé
	
	public static boolean debug = true;
	
	public static int timeout = 1000; // Timeout
	
	public static int broadcastPortNumber = 4445; // A modifier sur le diagramme
	public static int defaultPortNumber = 1234; // A modifier sur le diagramme
	
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
	
	private boolean firstConnection = true;
	
/*-----------------------Constructeurs-------------------------*/
	
	public Agent(User me) throws IOException { // A modifier sur le diagramme
		this.me = me;
		
		this.udpClient = new UDPClient();
		
		this.udpServer = new UDPServer(this);
		(new Thread(udpServer)).start();
		
		this.connectionServer = new Server(this);
		(new Thread(connectionServer)).start();

		this.mapUsernames = new MyMap<User,String>();
		this.mapSockets = new MyMap<User,UserSocket>();
		this.mapAddresses = new MyMap<User,InetAddress>();
		this.mapMessages = new HashMap<>();
		
		this.activeUsers = new ArrayList<User>();//new HashMap<>();
		this.disconnectedUsers = new ArrayList<User>();//new HashMap<>();
		
		chooseUsername(me.getUsername());
		this.firstConnection=false;
		this.udpClient.sendBroadcast("connect " + this.me.getUsername()); // A voir
		
		// Creer UDP Server, BroadcastClient + viewActiveUsernames 
	}
	
/*-----------------------Méthodes utilisateur-------------------------*/
	public void chooseUsername(String name) { // A modifier sur le diagramme
		boolean ok=false;
		ok = checkUsernameAvailability(name);
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
		/*for (Map.Entry<InetAddress,User> me : activeUsers.entrySet()) {
			User user = me.getValue();*/
		for (User user : activeUsers) {
			list.add(this.mapUsernames.getValue(user));
		}
		return list;
	}
	
	public ArrayList<String> viewDisconnectedUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		/*for (Map.Entry<InetAddress,User> me : disconnectedUsers.entrySet()) {
		User user = me.getValue();*/
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
		this.udpClient.sendBroadcast("disconnect " + this.me.getUsername()); // A voir
		// A voir
		this.udpServer.interrupt();
		
		this.connectionServer.interrupt();
	}

/*-----------------------Méthodes utilitaires-------------------------*/
	
	private User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}

	private boolean checkUsernameAvailability(String username) { // A modifier sur le diagramme
		boolean ok = false;
		
		long time = System.currentTimeMillis();//temps de depart
		long fin=time + timeout;//temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailability)
		
		if (this.firstConnection) { // Première connexion
			this.udpClient.sendBroadcast("checkUsernameAvailability "+username);
			while (!(this.udpServer.wasUsernameChecked() && this.udpServer.getLastUsernameChecked().equals(username)) && (fin > System.currentTimeMillis())) {System.out.printf("\n%b\n",this.udpServer.wasUsernameChecked());}
			System.out.printf("\n\n %b | %b | %s | %s \n\n",this.udpServer.wasUsernameChecked(),this.udpServer.getLastUsernameChecked().equals(username), this.udpServer.getLastUsernameChecked(), username);
			ok = this.udpServer.getLastUsernameAvailability();
			if (fin <= System.currentTimeMillis()) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}
			this.udpServer.setUsernameChecked(false);
		} else { // Autrement on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
		}
		return ok;
	}

/*-----------------------Méthodes Réseau-------------------------*/
	
	public void tellUsernameAvailibility(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			boolean ok = checkUsernameAvailability(username);
			// Envoie vrai si le nom est inconnu ...
			if (!ok && !(username.equals(this.me.getUsername()))) {
				User user = nameResolve(username);
				ok = user.getAddress().equals(address);
				/*System.out.printf("User demandé : %s | Adresse : %s | Adresse de la demande : %s | ok = %b\n", 
						user.getUsername(),user.getAddress().toString(),address.toString(),ok);*/
				// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
			}
			this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ ok, address);
		} else { // A la première connexion : premier arrivé / premier servi
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
		User user = new User(username,address);
		this.disconnectedUsers.add(user);
		this.mapUsernames.putUser(user, username);
		this.mapAddresses.putUser(user, address);
	}
	
	public void userDisconnect(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User user = nameResolve(username);
			//this.mapUsernames.remove(user);
			this.mapSockets.remove(user);
			this.activeUsers.remove(user);
			this.disconnectedUsers.add(user);//this.disconnectedUsers.put(address,user);
		}
	}
	
	public void userConnect(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User oldUser = mapAddresses.getUser(address);//this.disconnectedUsers.get(address);
			if (oldUser==null) { // New address
				if (Agent.debug) System.out.printf("New user connected : %s\n",username);
				User user = new User(username, address);
				this.mapUsernames.putUser(user, username);
				this.activeUsers.add(user);//this.activeUsers.put(address, user);
				this.mapAddresses.putUser(user, address);
			} else {
				if (Agent.debug) {
					if (!(oldUser.getUsername().equals(username))) System.out.printf("Old user reconnected : %s -> %s\n",oldUser.getUsername(), username);
					else System.out.printf("Old user reconnected : %s\n",username);
				}
				oldUser.changeUsername(username);
				this.mapUsernames.replaceValue(oldUser, username);
				this.activeUsers.add(oldUser);//this.activeUsers.put(address, oldUser);
				this.disconnectedUsers.remove(oldUser);//this.disconnectedUsers.remove(address);		
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
