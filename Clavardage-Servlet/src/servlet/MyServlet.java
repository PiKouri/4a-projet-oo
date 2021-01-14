package servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.io.File;
//import java.util.logging.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import datatypes.Message;

@WebServlet("/")
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
//    /**Logs*/
//    public static Logger logger = Logger.getLogger("MyLog");
//    /**Logs File Handler*/
//    public static FileHandler logfh;
//    public static String realPath;
    public static List<String>logs=new ArrayList<String>();
    
	/**Timeout used when checking username availability*/
	public static int timeout=1000;
	/**Port number used for broadcast connections*/
	public static int broadcastPortNumber = 4445;
	/**Default port number used for outgoing TCP connections*/
	public static int defaultPortNumber = 1234;
	/**Presence Server Subnet, example 192.168.255.255*/
	public static String presenceServerSubnet="172.17.255.255";//"192.168.1.255";//"172.17.0.255";
	/**Map that indicates if a IP is intern or extern*/
	private Map<String,Boolean> externMap= new HashMap<>();
	/**Map associate a IP to a username*/
	private Map<String,String> usernamesMap= new HashMap<>();
	/**Map associate a Username to a IP*/
	private Map<String,String> reverseUsernamesMap= new HashMap<>();
	/**Map associate a IP to a status*/
	private Map<String,Boolean> statusMap= new HashMap<>();
	/**Map associate a IP to a UserSocket*/
	@Deprecated
	private Map<String,ServletUserSocket> userSocketsMap= new HashMap<>();
	
	/**List of all own local addresses*/
	private List<String> listAllOwnLocalAddresses = null;
	
	//protected ServletUDPServer udpServer;
	protected ServletUDPClient udpClient;
	//protected ServletTCPServer tcpServer;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
    public MyServlet() throws SecurityException, IOException {
        super();
//        MyServlet.realPath=getServletContext().getRealPath("/");
//        // Logs
//        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");
//        /*LocalDateTime now = LocalDateTime.now();  
//        String logFilename = "log-"+DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss").format(now)+".log";*/
//        String logFilename=(realPath+"/log.log");
//		MyServlet.logfh = new FileHandler(logFilename,true);  
//		MyServlet.logger.addHandler(logfh);
//        logger.setUseParentHandlers(false);
//        SimpleFormatter formatter = new SimpleFormatter();  
//        logfh.setFormatter(formatter); 
//        (new PropertiesReader()).getProperties();
        MyServlet.printAndLog("\n\n\n\n----------------Servlet was started----------------\n\n\n\n");
        try {
			listAllOwnLocalAddresses = listAllOwnLocalAddresses();
		} catch (SocketException e) {
			MyServlet.errorMessage("ERROR when trying to list own local addresses\n", e);
		}
        //this.udpServer = new ServletUDPServer(this);
        this.udpClient = new ServletUDPClient(this);
        //this.tcpServer = new ServletTCPServer(this);
    }

    
/*-----------------------Méthodes - Servlet-------------------------*/
    
    @Override
    public void destroy() {
    	//this.udpServer.interrupt();
		//this.tcpServer.interrupt();
		String message = "presenceServerShutdown .";
		try {
			this.sendAllExtern(message,"localhost");
			this.sendAllIntern(message,"localhost");
		} catch (UnknownHostException e) {
        	MyServlet.errorMessage(
					String.format("ERROR UnknowHost : %s.\n", "localhost"), e);
		}
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MyServlet.printAndLog("GET Request received\n");
         
        String login = request.getParameter( "username" );
        if ( login == null ) login = "";

        response.setContentType( "text/html" );
        try ( PrintWriter out = response.getWriter() ) {
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "    <head>" );
            out.println( "        <title>Web test actions</title>" );
            out.println( "        <link rel='stylesheet' type='text/css' href='styles.css' />" );
            out.println( "    </head>" );
            out.println( "    <body>" );
            out.println( "        <h1>Web test actions</h1>" ); 
            out.println( "        <h1>Subnet of Presence Server :"+MyServlet.presenceServerSubnet+"</h1>" ); 
            out.println( "        <h2>Liste des actions possibles</h2>" );
            out.println( "        <h3>Users</h3>" );
            out.println( "        <ul>" );
            out.println( "        <li>checkIfExtern </li>" );
            out.println( "        <li>checkUsernameAvailability username</li>" );
            out.println( "        <li>presenceServerNotifyConnection username</li>" );
            out.println( "        <li>disconnect username</li>" );
            out.println( "        <li>changeUsername newUsername</li>" );
            out.println( "        </ul>" );
            
            out.println( "        <form method='POST' action=''>" );
            out.println( "            <label for='action'>Action :</label>" ); 
            //out.println( "            <input id='action' name='action' type='text' value='' autofocus /><br/>" );

            out.println( "        	  <select id='action' name='action'>"
    					+"			  	<option value='checkIfExtern'>checkIfExtern</option>"
    					+"			  	<option value='checkUsernameAvailability'>checkUsernameAvailability</option>"
    					+"			  	<option value='presenceServerNotifyConnection'>presenceServerNotifyConnection</option>"
        				+"			  	<option value='disconnect'>disconnect</option>"
            			+"			  	<option value='changeUsername'>changeUsername</option>"
            			+"			  	<option value='printInfos'>printInfos</option>" );
            out.println( "            	<option value='tellDisconnectedUsers'>tellDisconnectedUsers</option>"
            			+"		      </select>" );
            
            out.println( "            <label for='username'>Username :</label>" ); 
            out.println( "            <input id='username' name='username' type='text' value='" + login + "' autofocus /><br/>" );
            
            out.println( "            <br/>" );
            out.println( "            <input name='btnSend' type='submit' value='Envoyer' /><br/>" );
            out.println( "        </form>" );
            
            out.println( "        <h3>Server debugging</h3>" );
            out.println( "        <ul>" );
            out.println( "        <li>printInfos</li>" );
            out.println( this.printAll().replaceAll("\n","<br>")+"<br>");
            out.println( "        <li>Broadcast Addresses</li>" );
            out.println( this.getBroadcastAddresses()+"<br>");
            out.println( "        <li>Local Addresses</li>" );
            out.println( this.getLocalAddresses()+"<br>");
            out.println( "        <li>Logs</li>" );
            out.println( MyServlet.getLogs().replaceAll("\n","<br>")+"<br>");
            out.println( "        </ul>" );
            out.println( "    </body>" );
            out.println( "</html>" );

        }
    }

    // POST Request are used by extern users
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = request.getRemoteAddr();
    	String action = request.getParameter("action");
    	String username = request.getParameter("username");
    	MyServlet.printAndLog(
    			String.format("%s: POST Request: %s %s\n",address,action,username));
         
        MyServlet.printAndLog(String.format(""
        		+ "Action: %s | "
        		+ "Username: %s | "
        		+ "IP Address: %s"
        		+ "\n",action, username, address));

        switch (action){
        // Users
	    case "checkIfExtern" :
	    	response.setContentType( "text/html" );
	    	String responseString = "";
	    	Boolean isExtern = false;
	    	if (isKnown(address)) {
	    		isExtern = this.externMap.get(address);
	    	} else {
	        	// Send true if extern = not in subnet
		    	isExtern = !this.isInSubnet(address);	    	
		    	this.externMap.put(address, isExtern);  
        	}
	    	// Write 1 (extern user) or 0 (intern user)
	    	if (isExtern) responseString="1\n";
	    	else responseString="0\n";	
	    	// Write all local broadcast addresses
	    	responseString+=this.getLocalAddresses();
	    	MyServlet.printAndLog("Response : "+responseString+"\n");
    		response.getWriter().println(responseString);
	    	break;
	    case "checkUsernameAvailability" :
	    	response.setContentType( "text/html" );
	    	boolean responseBoolean=this.tellUsernameAvailability(username, InetAddress.getByName(address));
	    	String responseString2=String.valueOf(responseBoolean);
	    	MyServlet.printAndLog("Response : "+responseString2+"\n");
	    	response.getWriter().println(responseString2);
	    	break;
	    case "connect" :
	    	response.setContentType( "text/html" );
	    	this.connect(address,username);
	    	String responseString3 = this.tellExternUsers(address);
	    	MyServlet.printAndLog("Response : "+responseString3+"\n");
			response.getWriter().println(responseString3);
	    	break;
	    case "disconnect":
	    	this.disconnect(address);
	    	break;
	    case "changeUsername" :
			this.changeUsername(address, username);
			break;  
	    default :
	    	MyServlet.errorMessage("ERROR Unknown action\n", new Exception());
        }
    }

    
/*-----------------------Méthodes - Gestion utilisateurs-------------------------*/
    
    
	protected void connect(String address,String username) throws UnknownHostException {
    	this.statusMap.remove(address);
    	this.statusMap.put(address, true);
		String oldUsername = this.usernamesMap.get(address);
		this.usernamesMap.remove(address);
		this.usernamesMap.put(address, username);
		if (oldUsername!=null) this.reverseUsernamesMap.remove(oldUsername);
		this.reverseUsernamesMap.put(username,address);
    	if (isKnown(address)) {
    		if (isExtern(address)==1) {
        		// Send broadcast in local network + Send to each extern user
    			// Be careful connect username address / not only connect username
    			sendAllIntern("presenceServerNotifyConnection "+username+" "+isExtern(address),address);
    			sendAllExtern("presenceServerNotifyConnection "+username+" "+1,address);
    		} else {
    			// Send to each extern user
    			sendAllExtern("presenceServerNotifyConnection "+username+" "+1,address);
    		}
    	} else {
    		MyServlet.errorMessage("ERROR Unknown address\n", new Exception());
    	}
    }
    
    protected void disconnect(String address) throws UnknownHostException {
    	this.statusMap.remove(address);
    	this.statusMap.put(address, false);
		/*ServletUserSocket us = this.userSocketsMap.get(address);
		if (us!=null) {
			this.userSocketsMap.get(address).interrupt();
			this.userSocketsMap.remove(address);
		}*/
    	if (isKnown(address)) {
    		String username = getUsername(address);
    		if (isExtern(address)==1) {
    			// Send broadcast in local network + Send to each extern user
    			sendAllIntern("disconnect "+username,address);
    			sendAllExtern("disconnect "+username,address);
    		} else {
    			// Send to each extern user
    			sendAllExtern("disconnect "+username,address);
    		}
    	} else {
    		MyServlet.errorMessage("ERROR Unknown address\n", new Exception());
    	}
    }
    
    protected void changeUsername(String address, String newUsername) throws UnknownHostException {
    	if (isKnown(address)) {
    		String oldUsername = getUsername(address);
    		if (isExtern(address)==1) {
    			// Send broadcast in local network + Send to each extern user
    			sendAllIntern("changeUsername "+oldUsername+" "+newUsername,address);
    			sendAllExtern("changeUsername "+oldUsername+" "+newUsername,address);
    		} else {
    			// Send to each extern user
    			sendAllExtern("changeUsername "+oldUsername+" "+newUsername,address);
    		}
    		this.usernamesMap.remove(address);
    		this.usernamesMap.put(address, newUsername);
    		this.reverseUsernamesMap.remove(oldUsername);
    		this.reverseUsernamesMap.put(newUsername, address);
    	} else {
    		MyServlet.errorMessage("ERROR Unknown address\n", new Exception());
    	}
    }
    
    /**
     * This method adds a new UserSocket to the local list after a new connection was established
     *  
     * @param socket Socket from which we create the UserSocket
     */
    @Deprecated
	protected void newActiveUserSocket(String address,Socket socket) {
		MyServlet.printAndLog(String.format("New socket connected: "+address+"\n"));
		this.userSocketsMap.put(address, new ServletUserSocket(address, this, socket));
	}
    
	/**
     * This method sends a UDP message after a request of username availability
     * <p>UDP Message template : "tellUsernameAvailability username true/false"
     *  
     * @param username Username of which we want to verify the availability
     * @param address Address that requested the verification
     * 
     * @return The availability (used for HTTP response)
     */
	protected boolean tellUsernameAvailability(String username, InetAddress address) {
		// Vérifie la disponibilité du nom (dans notre table local)
		boolean ok = !this.reverseUsernamesMap.containsKey(username);
		// Envoie vrai si le nom est inconnu ...
		if (!ok) {
			ok = (MyServlet.addressToString(address).equals(
					this.getAddress(username)));
		// ... ou si le nom est connu mais que l'utilisateur demandant est le même (adressse IP)
		}
		if (isExtern(MyServlet.addressToString(address))==0) // Only if intern user was asking
			this.udpClient.sendUDP("tellUsernameAvailability "+username+" "+ ok, address);
		return ok;
	}

	/**
	 * This methods return list of users
	 * 
	 * @param sourceAddress
	 * */
	public String tellExternUsers(String sourceAddress) {
		String result = "";
		boolean sourceIsExtern=this.externMap.get(sourceAddress);
		for(String address : this.statusMap.keySet()) {
			if (address.equals(sourceAddress))
				continue;
			int isExtern = this.isExtern(address);
			boolean status = this.statusMap.get(address);
			//No need to send infos of intern user to intern user
			//Unless user is disconnected
			if (!sourceIsExtern && isExtern==0 && status==true)
				continue; 
			if (sourceIsExtern) isExtern=1; // Extern user sees everyone as extern
			String username = this.getUsername(address);
			result+=username+" "+address+" "+isExtern+" "+status+"\n";
		}
		return result;
	}
	
	@Deprecated
	public void internTellDisconnectedUsers(InetAddress address) {
		for(String disconnectedAddress : this.statusMap.keySet()) {
			if (this.statusMap.get(disconnectedAddress)==false) {// Disconnected Users
				String username = this.getUsername(disconnectedAddress);
				int isExtern = this.isExtern(disconnectedAddress);
				this.udpClient.sendUDP("updateDisconnectedUsers "+ username+" "+disconnectedAddress+" "+isExtern, address);
			}
		}
	}
	
    protected void sendAllExtern(String action, String sourceAddress) throws UnknownHostException {
    	for(String addr : this.statusMap.keySet()) {
    		if (isExtern(addr)==1 && isConnected(addr) && !addr.equals(sourceAddress)) { // Extern Users except sourceAddress
    	    	this.udpClient.sendUDP(action+" "+sourceAddress,InetAddress.getByName(addr));
    		}
    	}
    }
    
    protected void sendAllIntern(String action, String sourceAddress) {
    	this.udpClient.sendBroadcast(action+" "+sourceAddress);
    }
    
    
/*-----------------------Méthodes - Messages des utilisateurs-------------------------*/
    
    
    /**
	 * This method is used when receiving a Message (File, Text or Image)
	 * 
	 * @param us UserSocket
	 * @param destination
	 * @param message Message
	 * @param type
	 * @param is (only use for File or Image)
	 * */
    @Deprecated
	protected void receiveMessage(ServletUserSocket us, Message message, ObjectInputStream is) {
		MyServlet.printAndLog(String.format("Message received from %s to %s : ",
				us.getAddressAsString(),message.getDestination()));
		sendMessage(getUsername(message.getDestination()),message,is);
	}
	
	/**
	 * This method is used when sending a Message (File, Text or Image)
	 * 
	 * @param dest Destination user
	 * @param message Message
	 * @param type
	 * @param is (only use for File or Image)
	 * */
    @Deprecated
	protected void sendMessage(String username, Message message, ObjectInputStream is) {
		String address = this.getAddress(username);
		ServletUserSocket us = this.getUserSocket(address);
		us.send(message,is);
		MyServlet.printAndLog("Debug4\n");
	}
    

/*-----------------------Méthodes - Setteurs et Getteurs-------------------------*/
	
    
    protected boolean isKnown(String address) {
    	if (this.externMap==null) {
    		return false;
    	} else return this.externMap.containsKey(address);
    }
    
    protected int isExtern(String address) {
    	int isExtern = 0;
    	if (this.externMap.get(address)) isExtern = 1;
    	return isExtern;
    }

    @Deprecated
    protected ServletUserSocket getUserSocket(String address) {
    	return this.userSocketsMap.get(address);
    }
    
    protected String getUsername(String address) {
    	return this.usernamesMap.get(address);
    }
    
    protected String getAddress(String username) {
    	return this.reverseUsernamesMap.get(username);
    }
    
    protected boolean isConnected(String address) {
    	return this.statusMap.get(address);
    }
    
    protected boolean isInSubnet(String address) {
    	return address.contains(presenceServerSubnet.split(".255")[0]);
    }
    
    private String printAll() {
    	String infos="";
    	for (String address : this.externMap.keySet()) {
    		String line = String.format("IP: %s | Username: %s | Connected: %b | Extern: %b\n",
					address, usernamesMap.get(address),
					statusMap.get(address),externMap.get(address));
    		infos+=line;
    	}
    	return infos;
    }
    
    private String getBroadcastAddresses() {
    	String result="";
    	for(InetAddress s : this.udpClient.getBroadcastAddresses()) {
    		result+=MyServlet.addressToString(s)+" ";
    	}
		return result;
	}
    
    private String getLocalAddresses() {
    	String result="";
    	for(String s : this.getAllOwnLocalAddresses()) {
    		if (!s.contains("%"))
    			result+=s+" ";
    	}
		return result;
	}
    
    /**
	 * This method lists all own local addresses
	 * 
	 * @return List of all own local addresses
	 * */
	private List<String> listAllOwnLocalAddresses() throws SocketException {
		List<String> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces 
		= NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
		    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
		    while (addresses.hasMoreElements())
		    {
		        addressList.add(MyServlet.addressToString((InetAddress) addresses.nextElement()));
		    }
		}
		return addressList;
	}
	
	public List<String> getAllOwnLocalAddresses(){
    	return listAllOwnLocalAddresses;
    }
    
    /**
	 * Use this method to convert from address to String
	 * 
	 * @param address Address to convert
	 * @return The address as a String
	 */
	public static String addressToString(InetAddress address) {
		return address.toString().split("/")[1];
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
//		logger.info(s);
//		logger.log(Level.INFO,e.getMessage(),e);
		MyServlet.logs.add(s);
		MyServlet.logs.add(e.getMessage());
        System.exit(-1);
	}
	
	/**
	 * Method used to print infos in debug mode and add logs
	 * 
	 * @param message
	 * */
	public static final void printAndLog(String message) {
		System.out.printf(message);
		MyServlet.logs.add(message);
//		logger.info(message);
	}
	
	/**
	 * Method used to get logs
	 * 
	 * */
	public static final String getLogs() {
		String result = "";
		for (String s : MyServlet.logs) {
			result+=s+"\n";
		}
		return result;
	}
	
//	/**
//	 * This methods is used to close the file handler for logs
//	 * */
//	public static final void closeLogs() {
//		logfh.close();
//	}

}