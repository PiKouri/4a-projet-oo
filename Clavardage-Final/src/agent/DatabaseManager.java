package agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import datatypes.Message;
import userInterface.Interface;
import userInterface.User;

public class DatabaseManager {

	/**The agent that created this DatabaseManager*/
	private Agent agent;
	/**Connection to the database*/
	Connection conn = null;
	
	/**Sql statement used to get elements by address*/
	private final String selectAddress = "SELECT address, name "
            + "FROM users WHERE address = ?";
	
/*
 * Database table users :
 * id
 * address
 * name
 * 
 * example : 
 * 123
 * 127.0.0.1
 * myName
 * 
 * Database table username_messages :
 * id
 * message
 * 
 * example : 
 * 123
 * Text("Bonjour")
*/
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class DatabaseManager
     * 
     * @param agent The agent that created this DatabaseManager
     */
	protected DatabaseManager(Agent agent) {
		this.agent=agent;
		createNewDatabase(Agent.databaseFileName);
		createUsersTable();
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			if (containsAddress(localhost)) {
				Interface.notifyOldUsername(getUsername(localhost));
			} else {
				addUser(Interface.me);
			}
		} catch (UnknownHostException e) {}
	}
	
	
/*-----------------------Méthodes - Initialisation des bases de données-------------------------*/
	
	
	 /**
     * Creates a sample database
     *
     * @param fileName the database file name
     */
    protected void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:"+Agent.dir+"\\"+fileName;
        try {
        	Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
            	this.conn=conn;
                DatabaseMetaData meta = conn.getMetaData();
                if (Agent.debug) System.out.println("The driver name is " + meta.getDriverName());
                if (Agent.debug) System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    
	
/*-----------------------Méthodes - Création de tables-------------------------*/
	
    /**
     * Creates a users table in the database
     *
     */
	protected void createUsersTable() {
		try {
			Statement stmt = conn.createStatement();
			if (Agent.debug) System.out.printf("New users table in the database\n");
			// SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + " address text,\n"
	                + "	name text\n"
	                + ");";
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("ERROR when trying to create users table");
			System.out.println(e.getMessage());
            System.exit(-1);
		}
	}
	
	/**
     * Creates a messages table for the given user in the database
     *
     */
	protected void createMessagesTable(User user) {
		try {
			Statement stmt = conn.createStatement();
			if (Agent.debug) System.out.printf("New messages table for user %s in the database\n",user.getUsername());
			// SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS messages_"+user.getUsername()+" (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + " message BLOB\n"
	                + ");";
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("ERROR when trying to create a messages table");
			System.out.println(e.getMessage());
            System.exit(-1);
		}
	}
	
	
/*-----------------------Méthodes - Ajout des utilisateurs-------------------------*/
	
	
	/**
     * Adds a user to the database
     *
     * @param newUser User to add
     */
	protected void addUser(User newUser){
		try {
			if (containsAddress(newUser.getAddress())) {
				if (Agent.debug) System.out.printf("Address %s for user %s already in the database we just change username\n",newUser.getAddress(),newUser.getUsername());
				changeUsername(newUser.getAddress(),newUser.getUsername());
			} else {
				if (Agent.debug) System.out.printf("New user added to the database\n");
				
				// Add info to the users table
				String sql = "INSERT INTO users(address,name) VALUES(?,?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
		        pstmt.setString(1, newUser.getAddress().toString().split("/")[1]);
				pstmt.setString(2, newUser.getUsername());
		        pstmt.executeUpdate();
		        
		        // Create one messages table for this user
		        createMessagesTable(newUser);
			}
		} catch (SQLException e) {
			System.out.printf("ERROR when trying to add user %s to the database\n",newUser.getUsername());
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
	}
	
	
/*-----------------------Méthodes - Récupération et Modification de données-------------------------*/
	
	
	/**
     * Get username by address from the database
     *
     * @param address IP Address of the user
     * @return User object containing the username and the address
     */
	protected String getUsername(InetAddress address) {
		try {
			PreparedStatement pstmt  = conn.prepareStatement(selectAddress);
	        pstmt.setString(1,address.toString().split("/")[1]);
	        ResultSet rs  = pstmt.executeQuery();
	        return rs.getString("name");
		} catch (SQLException e) {
        	System.out.printf("ERROR when trying to get user from address %s in the database\n",address);
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
        return null; // Pas accessible
	}
	
	/**
     * Get the messages from a user from the database
     *
     * @param address IP Address of the user
     * @return The messages history with this user
     */
	protected ArrayList<Message> getMessages(InetAddress address){
		try {
			// Get name associated to this address
	        String username = getUsername(address);
	        
	        // Get messages in the messages table associated to the user
	        PreparedStatement pstmt  = conn.prepareStatement(
	        		"SELECT message "+ "FROM messages_"+username);
	        ResultSet rs  = pstmt.executeQuery();
	        ArrayList<Message> messages = new ArrayList<Message>();
	        // loop through the result set
            while (rs.next()) {
		        // fetch the serialized object to a byte array
	            byte[] st = (byte[])rs.getObject(1);
	            ByteArrayInputStream baip = 
	                new ByteArrayInputStream(st);
	            ObjectInputStream ois = new ObjectInputStream(baip);
	            // re-create the object
	            Message message = (Message)ois.readObject();
	            messages.add(message);
            }
	        return messages;
		} catch (Exception e) {
        	System.out.printf("ERROR when trying to get messages from address %s in the database\n",address);
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
		return null; // Pas accessible
	}
	
	/**
     * Get the old informations from the database and update the users and messages lists
     *
     */
	protected void getOldInformations() {
		try {
			String sql = "SELECT address,name FROM users";
			Statement stmt = conn.createStatement();
	        ResultSet rs    = stmt.executeQuery(sql);
	        // loop through the result set
            while (rs.next()) {
            	String stringAddress = rs.getString("address");
            	InetAddress address = InetAddress.getByName(stringAddress);
            	String name = rs.getString("name");
                
            	if (!(Interface.me.getAddress().toString().split("/")[1].equals(stringAddress))) {
	            	User user = new User(name,address);
					ArrayList<Message> messages = getMessages(address);
	            	this.agent.getNetworkManager().addAddress(user, address);
	            	this.agent.getUserStatusManager().userDisconnect(name, address);
	            	this.agent.getUsernameManager().addUsername(user, name);
	            	if (Agent.debug) System.out.printf("Address: %s | Name: %s | Number of messages: %d\n", address,name,messages.size());
	            } else {
	            	this.agent.getNetworkManager().addAddress(Interface.me, address);
	            	this.agent.getUsernameManager().addUsername(Interface.me, name);
	            }
            }
		} catch (Exception e) {
			System.out.println("ERROR when trying to get old informations from the database");
			System.out.println(e.getMessage());
			e.printStackTrace();
	        System.exit(-1);
		}
	}
	
	/**
     * Return true if the database contains this address
     *
     * @param address IP Address to check
     * @return True if the database contains this address
     */
	protected boolean containsAddress(InetAddress address) {
		try {
			PreparedStatement pstmt  = conn.prepareStatement(selectAddress);
			pstmt.setString(1, address.toString().split("/")[1]);
	        ResultSet rs  = pstmt.executeQuery();
	        return !(rs.isClosed());
		} catch (SQLException e) {
        	System.out.printf("ERROR when trying to check if address %s is in the database\n",address);
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
		return true; // Pas accessible
	}
	
	/**
     * Change the username of selected user by address
     *
     * @param address Address of the user
     * @param newUsername New Username of the user
     */
	protected void changeUsername(InetAddress address, String newUsername){
		try {
			String oldUsername = getUsername(address);
			if (!(oldUsername.equals(newUsername))) {
				if (Agent.debug) System.out.printf("Username changed in the database : %s -> %s\n",oldUsername,newUsername);
				// Change username in table users
				String sql = "UPDATE users SET name = ? "
		                + "WHERE address = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, newUsername);
				pstmt.setString(2, address.toString().split("/")[1]);
		        pstmt.executeUpdate();
		        // Change messages table's name
		        sql = "ALTER TABLE messages_"+oldUsername+" RENAME TO messages_"+newUsername;
		        Statement stmt = conn.createStatement();
		        stmt.execute(sql);
			}
		} catch (SQLException e) {
			System.out.printf("ERROR when trying to change username for address %s in the database\n",address);
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
	}
	
	/**
     * Add a message to the messages list from a user in the database
     *
     * @param message Message we want to add
     * @param address IP Address of the user
     */
	protected void addMessage(InetAddress address, Message message){
		try {
			String username = getUsername(address);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(message);
		    // serialize the employee object into a byte array
		    byte[] messageAsBytes = baos.toByteArray();
		    PreparedStatement pstmt = conn.prepareStatement
		            ("INSERT INTO messages_"+username+" (message) VALUES(?)");
		    ByteArrayInputStream bais = new ByteArrayInputStream(messageAsBytes);
		    // bind our byte array  to the message column
		    pstmt.setBinaryStream(1,bais, messageAsBytes.length);
		    pstmt.executeUpdate();
		} catch (Exception e) {
        	System.out.printf("ERROR when trying to get messages from address %s in the database\n",address);
			System.out.println(e.getMessage());
	        System.exit(-1);
		}
	}
}
