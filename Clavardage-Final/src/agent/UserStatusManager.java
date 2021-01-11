package agent;

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
	 * @param extern_id Extern_id of the disconnected user (0 if intern user)
	 * */
	protected void updateDisconnectedUsers(String username, InetAddress address, int extern_id) {
		if (!this.agent.getNetworkManager().containsAddressAndExternId(address,extern_id)) { // Si l'addresse n'est pas déjà connue, on l'ajoute
			this.agent.getDatabaseManager().addUser(address,username,0,extern_id);
		}
	}

	/**
	 * This method sets the status of a user to disconnected when receiving a "disconnect" message
	 *  
	 * @param username Username of the user who disconnected
	 * @param address Address of the user who disconnected
	 */
	protected void userDisconnect(String username, InetAddress address) throws IOException { 
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
	 * @param extern_id Extern_id of the user who connected (0 if intern user)
	 */
	protected void userConnect(String username, InetAddress address, int extern_id) { 
		if (!this.agent.isFirstConnection) {
			if (!this.agent.getNetworkManager().containsAddressAndExternId(address, extern_id)) { // New address+extern_id
				Agent.printAndLog(String.format("New user connected : %s -> %s\n", address, username));
				this.agent.getDatabaseManager().addUser(address,username,1,extern_id);
				synchronized(this.agent.getNetworkManager()) {this.agent.getNetworkManager().notifyAll();}
			} else { // Old User reconnected
				String oldUsername = this.agent.getUsernameManager().getUsername(address, extern_id);
				if (!(oldUsername.equals(username))) {
					Agent.printAndLog(String.format("Old user reconnected : %s -> %s\n",oldUsername, username));
					this.agent.getUsernameManager().userChangeUsername(oldUsername, username);
				} else {
					Agent.printAndLog(String.format("Old user reconnected : %s\n",username));
				}
				this.userChangeStatus(username, 1);
			} 
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
				int externId = this.agent.getNetworkManager().getExternId(username);
				// Change username in table users
				String sql = "UPDATE users SET status = ? "
		                + "WHERE address = ? AND externId= ?";
				PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement(sql);
				pstmt.setInt(1, status);
				pstmt.setString(2, this.agent.getNetworkManager().addressToString(address));
				pstmt.setInt(3, externId);
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
