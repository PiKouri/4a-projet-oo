package agent;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import userInterface.Interface;

public class DatabaseManager {

	/**The agent that created this DatabaseManager*/
	private Agent agent;
	/**Connection to the database*/
	private Connection conn = null;
	
	/**Sql statement used to get elements by address (only used for checking if localhost is in the database)*/
	protected final String selectByAddress = "SELECT address, username, status, externId "
            + "FROM users WHERE address = ?";
	
	/**Sql statement used to get elements by address and extern id*/
	protected final String selectByAddressAndExternId = "SELECT address, username, status, externId "
            + "FROM users WHERE address = ? AND externId = ?";
	
	/**Sql statement used to get elements by name*/
	protected final String selectByName = "SELECT address, username, status, externId "
            + "FROM users WHERE username = ?";
	
	/**Sql statement used to get all the elements in table users*/
	protected final String selectAll = "SELECT address, username, status, externId FROM users ";
	
	/**Sql statement used to get all the connected users in table users*/
	protected final String selectConnected = "SELECT address, username, status, externId FROM users WHERE status = 1";
	
	/**Sql statement used to get all the disconnected users in table users*/
	protected final String selectDisconnected = "SELECT address, username, status, externId FROM users WHERE status = 0";
/*
 * Database table users :
 * id
 * address
 * username
 * status (0=disconnected or 1=connected)
 * externId (0=intern users or >0 for extern users)
 * 
 * example : 
 * 123
 * 127.0.0.1
 * myName
 * 0
 * 0 
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
		checkOldDatabase();
	}
	
	
/*-----------------------Méthodes - Initialisation des bases de données-------------------------*/
	
	
	 /**
     * Creates a sample database
     *
     * @param fileName the database file name
     */
    protected void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:"+Agent.dir+fileName;
        try {
        	Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
            	this.conn=conn;
                DatabaseMetaData meta = conn.getMetaData();
                Agent.printAndLog(String.format("The driver name is " + meta.getDriverName()+"\n"));
                Agent.printAndLog(String.format("A new database has been created.\n"));
            }
        } catch (SQLException e) {
        	Agent.errorMessage("ERROR when creating database\n", e);
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
			Agent.printAndLog(String.format("New users table in the database\n"));
			// SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + " address text,\n"
	                + "	username text,\n"
	                + " status integer,\n"
	                + " externId integer"
	                + ");";
			stmt.execute(sql);
		} catch (SQLException e) {
        	Agent.errorMessage("ERROR when trying to create users table\n", e);
		}
	}
	
	/**
     * Creates a messages table for the given user in the database
     *
     * @param username Username of the user
     */
	protected void createMessagesTable(String username) {
		try {
			Statement stmt = conn.createStatement();
			Agent.printAndLog(String.format("New messages table for user %s in the database\n",username));
			// SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS messages_"+username+" (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + " message BLOB\n"
	                + ");";
			stmt.execute(sql);
		} catch (SQLException e) {
			Agent.errorMessage(
					String.format("ERROR when trying to create a messages for user %s table\n",username), e);
		}
	}
	
	
/*-----------------------Méthodes - Ajout des utilisateurs-------------------------*/
	
	
	/**
     * Adds a user to the database
     *
     * @param address Address of the user we add
     * @param username Username of the user we add
     * @param status Int representing the status of the user we add (0=disconnected, 1=connected)
     * @param externId Extern id of the user we add (0 if intern user)
     */
	protected void addUser(InetAddress address, String username, int status, int externId){
		try {
			if (this.agent.getNetworkManager().containsAddressAndExternId(address,externId)) {
				Agent.printAndLog(String.format(
						"Address %s for user %s already in the database we just change username and status\n",
						address,username));
				this.agent.getUsernameManager().userChangeUsername(
						this.agent.getUsernameManager().getUsername(address,externId),username);
				this.agent.getUserStatusManager().userChangeStatus(username, status);
			} else {
				Agent.printAndLog(String.format(
						"New user %s | address %s | status %d | externId %d added to the database\n",
						username,this.agent.getNetworkManager().addressToString(address),status,externId));
				
				// Add info to the users table
				String sql = "INSERT INTO users(address,username,status,externId) VALUES(?,?,?,?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
		        pstmt.setString(1, this.agent.getNetworkManager().addressToString(address));
				pstmt.setString(2, username);
				pstmt.setInt(3, status);
				pstmt.setInt(4, externId);
		        pstmt.executeUpdate();
		        if (!username.equals(this.agent.getUsername()))// If the user is not us
			        // Create one messages table for this user
			        createMessagesTable(username);
			}
		} catch (SQLException e) {
			Agent.errorMessage(
					String.format("ERROR when trying to add user %s to the database\n",username), e);
		}
	}
	
	
/*-----------------------Méthodes - Récupération et Modification de données-------------------------*/
	
	
	/**
     * Get the connection to the database
     *
     * @return The connection to the database
     */
	protected Connection getConnection(){
		return this.conn;
	}
	
	/**
	 * Check if agent was already used and notify old Username if so
	 */
	private void checkOldDatabase() {
		try {
			PreparedStatement pstmt  = this.conn.prepareStatement(selectByAddress);
			pstmt.setString(1, this.agent.getNetworkManager().addressToString(this.agent.localhost));
	        ResultSet rs  = pstmt.executeQuery();
	        if (!rs.isClosed()) {
	        	Interface.notifyOldUsername(rs.getString("username"));
	        }
		} catch (SQLException e) {
        	Agent.errorMessage("ERROR when trying to check if it is an old database\n", e);
		}
	}
	
	/**
     * Get the old informations from the database and update the users and messages lists
     *
     */
	/*protected void getOldInformations() {
		try {
			String sql = "SELECT address,username,status,externId FROM users";
			Statement stmt = conn.createStatement();
	        ResultSet rs    = stmt.executeQuery(sql);
	        // loop through the result set
            while (rs.next()) {
            	String stringAddress = rs.getString("address");
            	InetAddress address = InetAddress.getByName(stringAddress);
            	String username = rs.getString("username");
                
            	if (!(this.agent.getNetworkManager().addressToString(this.agent.localhost).equals(stringAddress))) {
	            	User user = new User(username,address);
					ArrayList<Message> messages = this.agent.getMessageManager().getMessages(address);
	            	this.agent.getNetworkManager().addAddress(user, address);
	            	this.agent.getUserStatusManager().userDisconnect(username, address);
	            	this.agent.getUsernameManager().addUsername(user, username);
	            	Agent.printAndLog(String.format("Address: %s | Name: %s | Number of messages: %d\n", address,username,messages.size()));
	            } else {
	            	this.agent.getNetworkManager().addAddress(Interface.me, address);
	            	this.agent.getUsernameManager().addUsername(Interface.me, username);
	            }
            }
		} catch (Exception e) {
			Agent.errorMessage("ERROR when trying to get old informations from the database", e);
		}
	}*/
}
