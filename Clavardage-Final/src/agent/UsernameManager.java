package agent;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import userInterface.Interface;

public class UsernameManager {
	
	/**The agent that created this UsernameManager*/
	private Agent agent;

	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class UsernameManager
     * 
     * @param agent The agent that created this UsernameManager
     */
	protected UsernameManager(Agent agent) {
		this.agent=agent;
	}
	
	
/*-----------------------Méthodes - Noms des utilisateurs-------------------------*/
	
	/**
     * This method returns true if the username is available
     * <p>If it is the first time we use this method, 
     * it will send a broadcast message to check if the username
     * is available
     * <br>Else we check in our local table
     * <br>UDP Message template : "checkUsernameAvailability username"
     *
     * @param username Username that we want to check
     * 
     * @return True if the username is available
     */
	protected boolean checkUsernameAvailability(String username) {
		boolean ok = false;
		
		if (username.equals("")) return false;
		
		long time = System.currentTimeMillis(); //Temps de depart
		long fin=time + Agent.timeout; //Temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailability)
		
		if (this.agent.isFirstConnection) { // Première connexion
			// On vérifie la disponibilité du nom auprès des autres utilisateurs
			this.agent.getNetworkManager().sendBroadcast("checkUsernameAvailability "+username);			
			try {
				synchronized(this) {this.wait(Agent.timeout);}
			} catch (InterruptedException e) {} // On attend la réponse des autres utilisateurs
			ok = this.agent.getNetworkManager().getLastUsernameAvailability();
			if (fin <= System.currentTimeMillis()) { // Timeout
				Agent.printAndLog(String.format("Connection timeout, we consider ourselves as first user\n"));
				ok = true;
			}
		} else { // Hors première connexion, on regarde dans notre table locale
			ok = !(this.containsUsername(username));
		}
		return ok;
	}

	/**
     * This method replaces the username of a user with a new one after receiving a "changeUsername" message
     * 
     * @param oldUsername The old username
     * @param newUsername The new username
     */
	protected void userChangeUsername(String oldUsername, String newUsername) {
		if (!this.agent.isFirstConnection || newUsername.equals(this.agent.getUsername())) { // Not during First Connection
			try {
				if (!(oldUsername.equals(newUsername))) {
					Agent.printAndLog(String.format("Username changed in the database : %s -> %s\n",oldUsername,newUsername));
					// Change username in table users
					String sql = "UPDATE users SET username = ? "
			                + "WHERE username = ?";
					PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement(sql);
					pstmt.setString(1, newUsername);
					pstmt.setString(2, oldUsername);
			        pstmt.executeUpdate();
			        if (!newUsername.equals(this.agent.getUsername())) {// If the user is not us
						this.agent.getNetworkManager().changeSocketUsername(oldUsername,newUsername);
				        // Change messages table's name
				        sql = "ALTER TABLE messages_"+oldUsername+" RENAME TO messages_"+newUsername;
				        Statement stmt = this.agent.getDatabaseManager().getConnection().createStatement();
				        stmt.execute(sql);
						Interface.notifyUsernameChanged(oldUsername, newUsername);
			        }
				}
			} catch (SQLException e) {
				Agent.errorMessage(
						String.format("ERROR when trying to change username for user %s in the database\n",oldUsername), e);
			}
		} 
	}
	
	/**
     * Get username by address from the database
     *
     * @param address IP Address of the user
     * @param externId Extern id of the user we add (0 if intern user)
     * 
     * @return User object containing the username and the address
     */
	protected String getUsername(InetAddress address, int externId) {
		try {
			PreparedStatement pstmt  = 
					this.agent.getDatabaseManager().getConnection().prepareStatement(
							this.agent.getDatabaseManager().selectByAddressAndExternId);
	        pstmt.setString(1,NetworkManager.addressToString(address));
	        pstmt.setInt(2, externId);
	        ResultSet rs  = pstmt.executeQuery();
	        if (rs.isClosed()) return "";
	        else return rs.getString("username");
		} catch (SQLException e) {
        	Agent.errorMessage(
					String.format("ERROR when trying to get user from address %s | externId %d in the database\n",address,externId), e);
		}
        return null; // Pas accessible
	}
	
	/**
     * Return true if the database contains this username
     *
     * @param username Username to check
     * @return True if the database contains this username
     */
	protected boolean containsUsername(String username) {
		try {
			PreparedStatement pstmt  = this.agent.getDatabaseManager().getConnection().prepareStatement(
					this.agent.getDatabaseManager().selectByName);
			pstmt.setString(1,username);
	        ResultSet rs  = pstmt.executeQuery();
	        return !(rs.isClosed());
		} catch (SQLException e) {
        	Agent.errorMessage(
					String.format("ERROR when trying to check if username %s is in the database\n",username), e);
		}
		return true; // Pas accessible
	}
}