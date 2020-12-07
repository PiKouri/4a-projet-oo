import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Agent extends Subject {
	
	// NewUsername enlevé
	
	public static boolean debug = true;
	
	public static int broadcastPortNumber = 4445; // A modifier sur le diagramme
	public static int defaultPortNumber = 1234; // A modifier sur le diagramme
	
	private User me; // A modifier sur le diagramme
	
	private MyMap<User,String> mapUsernames; // A modifier sur le diagramme
	private MyMap<User,MessageEmissionObserver> mapObservers; // A modifier sur le diagramme
	private Map<User,ArrayList<Message>> mapMessages; // A modifier sur le diagramme
	
	private ArrayList<User> activeUsers;  // A modifier sur le diagramme
	private Map<InetAddress, User> disconnectedUsers;  // A modifier sur le diagramme
	
	private Server connectionServer; // A modifier sur le diagramme
	private UDPServer udpServer; // A modifier sur le diagramme
	
	private UDPClient udpClient; // A modifier sur le diagramme
	
	private boolean firstConnection = true;
	
	public Agent(User me) throws IOException { // A modifier sur le diagramme
		this.me = me;
		
		this.udpClient = new UDPClient();
		
		this.udpServer = new UDPServer(this);
		(new Thread(udpServer)).start();
		
		this.connectionServer = new Server(this);
		(new Thread(connectionServer)).start();

		this.mapUsernames = new MyMap<User,String>();
		this.mapObservers = new MyMap<User,MessageEmissionObserver>();
		this.mapMessages = new HashMap<>();
		
		this.activeUsers = new ArrayList<User>();
		this.disconnectedUsers = new HashMap<>();
		
		chooseUsername(me.getUsername());
		this.udpClient.sendBroadcast("connect " + this.me.getUsername()); // A voir
		this.firstConnection=false;
		
		// Creer UDP Server, BroadcastClient + viewActiveUsernames 
	}
	
	public void chooseUsername(String name) { // A modifier sur le diagramme
		boolean ok=false;
		ok = checkUsernameAvailability(name);
		String newName = name;
		Scanner scanner = new Scanner(System.in); // A changer avec l'interface graphique
		while (!ok) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.printf("Thread Interrupted\n");
				System.exit(-1);
			}
			System.out.print("Enter your name: ");
			newName = scanner.next();
			ok = checkUsernameAvailability(newName);
		}
		scanner.close();
		if (!this.firstConnection) {
			this.udpClient.sendBroadcast("changeUsername "+ this.me.getUsername() + " " + newName);
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
	
	public ArrayList<Message> getMessageHistory(String username){
		return this.mapMessages.get(nameResolve(username));
	}
	
	public void sendMessage(Message message) {
		
	}
	
	public void disconnect() throws IOException { // A modifier sur le diagramme
		this.udpClient.sendBroadcast("disconnect " + this.me.getUsername()); // A voir
		// A voir
		this.udpServer.running = false;
		
		this.connectionServer.interrupt();
	}
	
	private User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}
	
	private boolean checkUsernameAvailability(String username) { // A modifier sur le diagramme
		boolean ok = false;
		
		long time = System.currentTimeMillis();//temps de depart
		long fin=time + 5000;//temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailablity)
		
		if (this.firstConnection) { // Première connexion
			this.udpClient.sendBroadcast("checkUsernameAvailablity "+username);
			while (!(this.udpServer.lastUsernameChecked.equals(username)) && (fin > System.currentTimeMillis())) {}
			ok = this.udpServer.lastUsernameAvailablity;
			System.out.printf("%b\n",ok);
			if (fin <= System.currentTimeMillis()) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
				this.mapUsernames.putUser(this.me,username);
			}
		} else { // Autrement on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
		}
		return ok;
	}
	
	public void tellUsernameAvailibility(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			boolean ok = checkUsernameAvailability(username) && (username != this.me.getUsername());
			// Envoie vrai si le nom est inconnu ...
			if (!ok) {
				User user = nameResolve(username);
				ok = user.getAddress().equals(address);
				// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
			}
			this.udpClient.sendBroadcast("tellUsernameAvailablity "+username+" "+ ok);
		}
	}
	
	public void userDisconnect(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User user = nameResolve(username);
			//this.mapUsernames.remove(user);
			this.mapObservers.remove(user);
			this.activeUsers.remove(user);
			this.disconnectedUsers.put(address,user);
		}
	}
	
	public void userConnect(String username, InetAddress address) { // A modifier sur le diagramme
		if (!this.firstConnection) {
			User oldUser = this.disconnectedUsers.get(address);
			if (oldUser==null) {
				User user = new User(username, address);
				//this.mapUsernames.putUser(user, username);
				this.activeUsers.add(user);
			} else {
				oldUser.changeUsername(username);
				this.mapUsernames.replaceValue(oldUser, username);
				this.activeUsers.add(oldUser);
				this.disconnectedUsers.remove(address);		
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
		
	}
	
	public void receiveFile(File file) {
		
	}
	
	public void receiveText(Text text) {
		
	}

	public void receiveImage(Image image) {
	
	}

}
