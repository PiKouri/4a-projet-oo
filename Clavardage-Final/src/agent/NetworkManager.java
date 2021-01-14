package agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
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
	/**Socket connected to the Presence Server*/
	@SuppressWarnings("unused")
	@Deprecated
	protected Socket presenceServer=null;
	/**ObjectOutputStream of the socket connected to Presence Server*/
	@SuppressWarnings("unused")
	@Deprecated
	private ObjectOutputStream presenceServerOS;
	/**ObjectInputStream of the socket connected to Presence Server*/
	@SuppressWarnings("unused")
	@Deprecated
	private ObjectInputStream presenceServerIS;
		
	/**TCPServer that will manage incoming TCP connections*/
	private TCPServer connectionServer;
	/**UDPServer that will manage incoming UDP connections*/
	private UDPServer udpServer;
	/**UDPClient used to send UDP messages (also broadcast)*/
	private UDPClient udpClient;
	
	/**Base url for servlet of the Presence Server*/
	private String baseURL;
	
	/**List of all broadcast addresses of the Presence Server*/
	private String listAllPresenceServerBroadcastAddresses = "";
	
/*
 * Message type :
 * "connect username isExtern" -> Pour notifier notre arrivée et que les autres utilisateurs se connectent
 * "disconnect username" -> Pour notifier notre départ
 * "changeUsername oldUsername newUsername" -> Pour notifier le changement de notre username
 * "checkUsernameAvailability username" -> Requête demandant la disponibilité de l'username
 * "tellUsernameAvailability username true/false" -> Réponse notifiant la disponibilité(ou non) de l'username
 * "canAccess username" -> Après avoir reçu un "connect", renvoie "canAccess" pour notifier notre adresse IP au nouvel arrivant
 * "updateDisconnectedUsers username address isExtern" -> Après avoir reçu un "connect", met à jour les utilisateurs déconnectés du nouvel arrivant
 * 
 * Interaction avec le Serveur de présence
 * requete POST :
 * "action=checkIfExtern"
 * "action=connect&username=username"
 * "action=disconnect&username=username"
 * "action=changeUsername&username=oldUsername&3rdArgument=newUsername"
 * 
*/

/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class NetworkManager
     * <p>This constructor create the UDPClient, the UDPServer 
     * and the Server (incoming connections) and it will start
     * both servers
     * 
     * @param agent The agent that created this NetworkManager
	 * @throws Exception 
     */
	protected NetworkManager(Agent agent) throws Throwable {
		this.agent=agent;
		this.baseURL = "http://"+Agent.presenceServerIPAddress+":8080/Clavardage-Servlet/";
		this.mapSockets = new HashMap<>();
		this.udpClient = new UDPClient();
		this.udpServer = new UDPServer(this.agent);
		/*
		try {
			this.presenceServer = new Socket();
			this.presenceServer.setSoTimeout(Agent.timeout);
			this.presenceServer.connect(
					new InetSocketAddress(Agent.presenceServerIPAddress,Agent.defaultPortNumber),
					Agent.timeout);
			this.presenceServerOS=new ObjectOutputStream(presenceServer.getOutputStream());;
			this.presenceServerIS=new ObjectInputStream(presenceServer.getInputStream());;
		} catch (IOException e) {
        	Agent.printAndLog(
        			"ERROR Could not create socket when trying to connect to Presence Server"
        			+ " => We consider ourselves as intern user\n");
        	this.presenceServer=null;
		}
		*/
		this.checkIfExtern();
		Agent.printAndLog(String.format("Extern : %d\n",this.agent.isExtern));
		this.connectionServer = new TCPServer(this.agent);
	}
	
	
/*-----------------------Méthodes - Réseau-------------------------*/
	
	
	/**
     * This method checks if the PresenceServer is on the same broadcast network 
     * or if we are a extern user
     */
	private void checkIfExtern() {
		if (!this.agent.getDatabaseManager().isOld) {
			String response = sendPost("checkIfExtern","").trim();
			if (response!="") {
				String[] lines = response.split("\n");
				String line1 = lines[0];
				String line2 = lines[1];
				this.agent.isExtern=Integer.parseInt(line1.trim());
				this.listAllPresenceServerBroadcastAddresses =  line2;
			} else 
				this.agent.isExtern=0;
			// Par défaut on se considère comme utilisateur interne (sans Serveur de présence)
		} else {
			this.agent.isExtern=this.isExternByAddress("localhost");
		}
	}

	/**
     * This method sends a broadcast message on all available broadcast addresses
     * <p>Note that broadcast will be used only if we are Intern Users
     * Else we will send a POST Request to the Presence Server</p>
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
		if (this.agent.isExtern==0) { // Seulement si nous sommes des utilisateurs internes
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
				if (username.equals(this.agent.tempName)) this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ false,address);
			}
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
			this.udpClient.sendUDP("canAccess "+this.agent.getUsername(), address);
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
		if (this.agent.isExtern==0) { // Seulement si nous sommes des utilisateurs internes
			if (!this.agent.isFirstConnection) {
				for (String username : this.agent.getUserStatusManager().getDisconnectedUsers()) {
					String disconnectedAddress = NetworkManager.addressToString(
							this.agent.getNetworkManager().getAddress(username));
					int isExtern = this.agent.getNetworkManager().isExtern(username);
					this.udpClient.sendUDP("updateDisconnectedUsers "+ username+" "+disconnectedAddress+" "+isExtern, address);
				}
			}
		}
	}

	/**
     * This method adds a new UserSocket to the local list after a new connection was established
     *  
     * @param socket Socket from which we create the UserSocket
     */
	protected void newActiveUserSocket(Socket socket) {
		// Le thread attend que l'on reçoive l'adresse de l'utilisateur avant de pouvoir créer le UserSocket
		if (!this.agent.isFirstConnection) {
			if (this.agent.isExtern==0) { // Seulement si nous sommes des utilisateurs internes
				final class MyThread extends Thread {
					private NetworkManager nm; private Socket socket;
					public MyThread(NetworkManager nm, Socket socket) {this.nm=nm;this.socket=socket;}
					public void run() {
						String username ="";
						synchronized(this.nm) {
							while (username.equals("")) {
								//Agent.printAndLog(String.format("Could not get User from address : %s -> Waiting for canAccess\n",socket.getInetAddress()));
								try {this.nm.wait(Agent.timeout);} catch (InterruptedException e) {}
								username=this.nm.agent.getUsernameManager().getUsername(socket.getInetAddress());
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
	}
	
	
/*-----------------------Méthodes - Intéraction avec le Serveur de présence-------------------------*/
	
	/**
	 * This method is used to send POST request through HTML
	 * 
	 * @param action Action to send
	 * @param username Username to send
	 * 
	 * @return Response from server
	 * */
	protected String sendPost(String action, String username){
		//if (presenceServer!=null) {
		Agent.printAndLog(String.format("Sending POST request : %s %s\n",action,username));
		String url = this.baseURL;
        String urlParameters = "action="+action+"&username="+username;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection con = null;
        try {
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setConnectTimeout(Agent.timeout);
            con.setReadTimeout(Agent.timeout);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(postData);
            StringBuilder content;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String line;
            content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        	Agent.printAndLog(String.format("Response from server : %s\n",content.toString()));
            return (content.toString());
        } catch (Exception e) {
        	//Agent.errorMessage("ERROR when sending POST request : "+action+" "+username, e);
        	Agent.printAndLog(
        			String.format("We could not contact Presence server at %s\n",
        			url));
        }
        if (con != null) con.disconnect();
		/*} else {
			Agent.printAndLog(String.format("Did not send POST request : %s %s because"
					+ " could not connect to Presence Server at the beginning\n",action,username));
		}*/
        return "";
    }
	
	/**
     * This method when a extern user connects
     * It associates UserSocket of Presence Server to the extern user
     *  
     * @param address
     * @param username
     */
	protected void newExternUserConnected(InetAddress address, String username) {
		Agent.printAndLog(String.format("Extern user connected: "+username+"\n"));
		this.agent.getUserStatusManager().userConnect(username, address, 1);
		//this.mapSockets.put(username, new UserSocket(username,this.agent,presenceServer,presenceServerOS,presenceServerIS));
	}
	
	/**
     * This method uses a response receive after a POST Request
     * of type "connect" to add all disconnected users
     *  
     * @param response
     */
	public void fromExternList(String response) {
		// Example : 
		// test1 192.168.1.1 0 true\n
		// test2 170.10.1.2 1 false\n
		String[] lines = response.split("\n");
		for (String s : lines) {
			if (!(s.trim().isEmpty())) {
				String[]line=s.split(" ");
				String username = line[0];
				try {
					InetAddress address = InetAddress.getByName(line[1]);
					int isExtern = Integer.parseInt(line[2].trim());
					boolean isConnected = Boolean.parseBoolean(line[3].trim()) ;
					if (isConnected)
						this.agent.getNetworkManager().newExternUserConnected(address, username);
					else
						this.agent.getUserStatusManager().updateDisconnectedUsers(username,address,isExtern);
				} catch (UnknownHostException e) {
		        	Agent.errorMessage(
							String.format("ERROR UnknowHost : %s.\n", line[1]), e);				
				}
			}
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
     * This method returns true if the user is extern
     *
     * @param username
     * 
     * @return True if extern user
     */
	protected int isExtern(String username) {
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectByName);
	        pstmt.setString(1,username);
	        ResultSet rs  = pstmt.executeQuery();
	        return Integer.parseInt(rs.getString("isExtern"));
		} catch (Exception e) {
			Agent.errorMessage(
					String.format("ERROR when trying to get isExtern of user %s in the database\n",username), e);
		}
        return 0; // Pas accessible
	}
	
	/**
     * This method returns true if the user is extern (by address
     *
     * @param address
     * 
     * @return True if extern user
     */
	protected int isExternByAddress(String address) {
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectByAddress);
	        pstmt.setString(1,address);
	        ResultSet rs  = pstmt.executeQuery();
	        return rs.getInt("isExtern");
		} catch (Exception e) {
			Agent.errorMessage(
					String.format("ERROR when trying to get isExtern of address %s in the database\n",address), e);
		}
        return 0; // Pas accessible
	}
	
	protected String getPresenceServerBroadcastAddresses() {
		return this.listAllPresenceServerBroadcastAddresses;
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
		if (this.udpServer!=null)
			this.udpServer.interrupt();
		if (this.connectionServer!=null)
			this.connectionServer.interrupt();
		this.connectionServer=null; // supprime le TCPServer et les Sockets associés
		this.udpServer=null; // supprime l'UDPServer et les Sockets associés
		if (this.agent.getUserStatusManager()!=null)
			for (String u : this.agent.getUserStatusManager().getActiveUsers())
				this.removeSocket(u);; // interrompt tous les UserSockets
		this.mapSockets.clear();
	}
	
	/**
     * This method initializes and starts the differents servers (UDP and TCP)
	 * @throws Throwable 
     */
	protected void startAll() throws Throwable {
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
     */
	protected boolean containsAddress(InetAddress address) {
		try {
			PreparedStatement pstmt  = this.agent.getDatabaseManager().getConnection().prepareStatement(
					this.agent.getDatabaseManager().selectByAddress);
			if (address==null) pstmt.setString(1, "localhost");
			else pstmt.setString(1, NetworkManager.addressToString(address));
	        ResultSet rs  = pstmt.executeQuery();
	        return !(rs.isClosed());
		} catch (SQLException e) {
        	Agent.errorMessage(
					String.format("ERROR when trying to check if address %s is in the database\n",address), e);
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
		if (address==null) return "localhost";
		else return address.toString().split("/")[1];
	}
}
