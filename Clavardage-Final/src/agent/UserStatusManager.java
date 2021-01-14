package agent;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import userInterface.Interface;

public class UserStatusManager {

	/**The agent that created this UserStatusManager*/
	private Agent agent;


	/*-----------------------Constructeurs-------------------------*/


	/**
	 * Constructor for the class UserStatusManager
	 * 
	 * @param agent The agent that created this UserStatusManager
	 */
	protected UserStatusManager(Agent agent) {
		this.agent=agent;
		this.putAllUsersDisconnected();
	}


	/*-----------------------Méthodes - Actualisation de l'état des autres utilisateurs-------------------------*/


	/**
	 * This methods updates the disconnected users' list when receiving "updateDisconnectedUsers" messages
	 * 
	 * @param username The disconnected user
	 * @param address Address of the disconnected user
	 * @param isExtern True if extern user
	 * */
	protected void updateDisconnectedUsers(String username, InetAddress address, int isExtern) {
		if (!this.agent.getNetworkManager().containsAddress(address)) { // Si l'addresse n'est pas déjà connue, on l'ajoute
			this.agent.getDatabaseManager().addUser(address,username,0,isExtern);
		}
	}

	/**
	 * This method sets the status of a user to disconnected when receiving a "disconnect" message
	 *  
	 * @param username Username of the user who disconnected
	 */
	protected void userDisconnect(String username) throws IOException { 
		if (!this.agent.isFirstConnection) {
			this.agent.getNetworkManager().removeSocket(username);
			this.userChangeStatus(username, 0);
		}
	}

	/**
	 * This method sets the status of a user to active when receiving a "connect" message
	 *  
	 * @param username Username of the user who connected
	 * @param address Address of the user who connected
	 * @param isExtern True if extern user
	 */
	protected void userConnect(String username, InetAddress address, int isExtern) { 
		if (!this.agent.isFirstConnection) {
			if (!this.agent.getNetworkManager().containsAddress(address)) { // New address+extern_id
				Agent.printAndLog(String.format("New user connected : %s -> %s\n", address, username));
				this.agent.getDatabaseManager().addUser(address,username,1,isExtern);
				synchronized(this.agent.getNetworkManager()) {this.agent.getNetworkManager().notifyAll();}
				
				// Create folder for this user in file and image for this User (IP address)
				File dir = new File(Agent.dir+"file/"+NetworkManager.addressToString(address));
                if (!dir.isDirectory()) dir.mkdir();
                dir = new File(Agent.dir+"image/"+NetworkManager.addressToString(address));
                if (!dir.isDirectory()) dir.mkdir();
                
			} else { // Old User reconnected
				String oldUsername = this.agent.getUsernameManager().getUsername(address);
				if (!(oldUsername.equals(username))) {
					Agent.printAndLog(String.format("Old user reconnected : %s -> %s\n",oldUsername, username));
					this.agent.getUsernameManager().userChangeUsername(oldUsername, username);
				} else {
					Agent.printAndLog(String.format("Old user reconnected : %s\n",username));
				}
			} 
			this.userChangeStatus(username, 1);
		}
	}


	/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/


	/**
	 * This method returns the list of disconnected users (String)
	 * 
	 * @return List of all disconnected users (String)
	 */
	protected ArrayList<String> getDisconnectedUsers() {
		ArrayList<String> disconnectedUsers = new ArrayList<String>();
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectDisconnected);
	        ResultSet rs  = pstmt.executeQuery();
	        // loop through the result set
	        while (rs.next()) {
	        	String username = rs.getString("username");
	        	if (!username.equals(this.agent.getUsername()))
	        		disconnectedUsers.add(username);
	        }
		} catch (Exception e) {
        	Agent.errorMessage("ERROR when trying to get all disconnected users in table users in the database\n", e);
		}
		return disconnectedUsers;
	}

	/**
	 * This method returns the list of active users (String)
	 * 
	 * @return List of all active users (String)
	 */
	protected ArrayList<String> getActiveUsers() {
		ArrayList<String> activeUsers = new ArrayList<String>();
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectConnected);
	        ResultSet rs  = pstmt.executeQuery();
	        // loop through the result set
	        while (rs.next()) {
	        	String username = rs.getString("username");
	        	if (!username.equals(this.agent.getUsername()))
	        		activeUsers.add(username);
	        }
		} catch (Exception e) {
			Agent.errorMessage("ERROR when trying to get all active users in table users in the database\n",e);
		}
		return activeUsers;
	}

	/**
	 * This method puts all active users as disconnected
	 * <p>Note that this method is used when disconnecting and also at first connection
	 */
	protected void putAllUsersDisconnected() {
		try {
			Agent.printAndLog(String.format("Putting all users as disconnected\n"));
			String sql = "UPDATE users SET status = 0 WHERE status = 1";
			PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement(sql);
	        pstmt.executeUpdate();
		} catch (Exception e) {
        	Agent.errorMessage("ERROR when trying to put all users as disconnected in table users in the database\n", e);
		}
	}
	
	/**
	 * This method puts all extern active users as disconnected
	 * <p>Note that this method is used the presence server is shutdown
	 */
	protected void putAllExternUsersDisconnected() {
		try {
			Agent.printAndLog(String.format("Putting all extern users as disconnected\n"));
			String sql = "UPDATE users SET status = 0 WHERE status = 1 AND isExtern = 1";
			PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement(sql);
	        pstmt.executeUpdate();
		} catch (Exception e) {
        	Agent.errorMessage("ERROR when trying to put all extern users as disconnected in table users in the database\n", e);
		}
	}
	
	/**
     * This method changers the status of a user in the database
     * 
     * @param username
     * @param status
     */
	protected void userChangeStatus(String username, int status) {
		if (!this.agent.isFirstConnection || username.equals(this.agent.getUsername()) ) { // Not during First Connection
			try {
				Agent.printAndLog(String.format("User status changed in the database : %s -> %d\n",username,status));
				InetAddress address = this.agent.getNetworkManager().getAddress(username);
				// Change username in table users
				String sql = "UPDATE users SET status = ? "
		                + "WHERE address = ?";
				PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement(sql);
				pstmt.setInt(1, status);
				pstmt.setString(2, NetworkManager.addressToString(address));
		        pstmt.executeUpdate();
			} catch (SQLException e) {
				Agent.errorMessage(
						String.format("ERROR when trying to change status for user %s in the database\n",username), e);
			}
			if (status==1) Interface.notifyUserReconnected(username);
			else Interface.notifyUserDisconnected(username);
		}
	}
}
