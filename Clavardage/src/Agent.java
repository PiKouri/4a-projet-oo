import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

public class Agent extends Subject {
	
	// NewUsername enlevé
	
	public static int broadcastPortNumber = 4445; // A modifier sur le diagramme
	public static int defaultPortNumber = 1234; // A modifier sur le diagramme
	
	private User me; // A modifier sur le diagramme
	
	private MyMap<User,String> mapUsernames; // A modifier sur le diagramme
	private MyMap<User,MessageEmissionObserver> mapObservers; // A modifier sur le diagramme
	private Map<User,ArrayList<Message>> mapMessages; // A modifier sur le diagramme
	private ArrayList<User> otherUsers;  // A modifier sur le diagramme
	private Server connectionServer; // A modifier sur le diagramme
	private UDPServer broadcastServer; // A modifier sur le diagramme
	private BroadcastClient broadcastClient; // A modifier sur le diagramme
	
	public Agent(User me) throws IOException { // A modifier sur le diagramme
		this.me = me;
		this.broadcastClient = new BroadcastClient();
		this.broadcastServer = new UDPServer(this);
		new Thread(broadcastServer);
		this.mapUsernames = new MyMap<User,String>();
		this.connectionServer = new Server(this);
		new Thread(connectionServer);
		
		chooseUsername(me.getUsername());
		this.broadcastClient.sendBroadcast("connect " + this.me.getUsername()); // A voir
		// Creer UDP Server, BroadcastClient + viewActiveUsernames 
	}
	
	public void chooseUsername(String name) { // A modifier sur le diagramme
		boolean ok = checkUsernameAvailability(name);
		String newName = name;
		while (!ok) {
			
			ok = checkUsernameAvailability(name);
		}
		this.me.changeUsername(newName);
		this.broadcastClient.sendBroadcast("changeUsername "+ this.me.getUsername() + " " + this.me.getUsername());
	}
	
	public ArrayList<String> viewActiveUsernames(){
		ArrayList<String> list = new ArrayList<String>();
		for (User user : otherUsers) {
			list.add(this.mapUsernames.getValue(user));
		}
		return list;
	}
	
	public ArrayList<Message> getMessageHistory(String username){
		return this.mapMessages.get(this.nameResolve(username));
	}
	
	public void sendMessage(Message message) {
		
	}
	
	public void disconnect() throws IOException { // A modifier sur le diagramme
		this.broadcastClient.sendBroadcast("disconnect " + this.me.getUsername()); // A voir
		// A voir
		broadcastServer.running = false;
		connectionServer.running = false;
	}
	
	private User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}
	
	private boolean checkUsernameAvailability(String username) { // A modifier sur le diagramme
		boolean ok = false;
		
		long time = System.currentTimeMillis();//temps de depart
		long fin=time + 5000;//temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailablity)
		
		if (this.mapUsernames.isEmpty()) { // Première connexion
			this.broadcastClient.sendBroadcast("checkUsernameAvailablity "+username);
			while (this.broadcastServer.lastUsernameChecked != username && (fin > System.currentTimeMillis())) {}
			ok = this.broadcastServer.lastUsernameAvailablity;
			if (fin <= System.currentTimeMillis()) {
				System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}
		} else { // Autrement on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
		}
		return ok;
	}
	
	public void tellUsernameAvailibility(String username) { // A modifier sur le diagramme
		boolean ok = checkUsernameAvailability(username) && (username != this.me.getUsername());
		this.broadcastClient.sendBroadcast("tellUsernameAvailablity "+username+" "+ ok);
	}
	
	public void userDisconnect(String username) { // A modifier sur le diagramme
		User user = nameResolve(username);
		this.mapUsernames.remove(user);
		this.mapObservers.remove(user);
		this.otherUsers.remove(user);
	}
	
	public void userConnect(String username) { // A modifier sur le diagramme
		if (!this.mapUsernames.containsValue(username)) {
			User user = new User(username);
			this.mapUsernames.putUser(user, username);
		}
	}
	
	public void userChangeUsername(String oldUsername, String newUsername) { // A modifier sur le diagramme
		if (oldUsername != newUsername) {
			User user = nameResolve(oldUsername);
			user.changeUsername(newUsername);
			this.mapUsernames.replaceValue(user, newUsername); 
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
