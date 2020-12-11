package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import userInterface.User;

public class UserStatusManager {

	/**The agent that created this UserStatusManager*/
	private Agent agent;
	
	/**List of the active users*/
	private ArrayList<User> activeUsers;
	/**List of the disconnected users*/
	private ArrayList<User> disconnectedUsers;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class UserStatusManager
     * 
     * @param agent The agent that created this UserStatusManager
     */
	protected UserStatusManager(Agent agent) {
		this.agent=agent;
		this.activeUsers = new ArrayList<User>();
		this.disconnectedUsers = new ArrayList<User>();
	}
	
	
/*-----------------------Méthodes - Actualisation de l'état des autres utilisateurs-------------------------*/
	
	
	/**
	 * This methods updates the disconnected users' list when receiving "updateDisconnectedUsers" messages
	 * 
	 * @param user The user associated to the conversation
	 * 
	 * @return List of messages with the user
	 * */
	protected void updateDisconnectedUsers(String username, InetAddress address) {
		if (verifyUniqAddress(this.disconnectedUsers,address) && verifyUniqAddress(this.activeUsers,address)) { // Si l'addresse n'est pas déjà connue, on l'ajoute
			User user = new User(username,address);
			this.disconnectedUsers.add(user);
			this.agent.getUsernameManager().addUsername(user, username);
			this.agent.getNetworkManager().addAddress(user, address);
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
			User user = this.agent.getUsernameManager().nameResolve(username);
			//this.mapUsernames.remove(user);
			///////////////////////////////////Pas encore fait/////////////////////////////////////////this.mapSockets.getValue(user).interrupt();
			this.agent.getNetworkManager().removeSocket(user);
			this.activeUsers.remove(user);
			this.disconnectedUsers.add(user);
		}
	}
	
	/**
     * This method sets the status of a user to active when receiving a "connect" message
     *  
     * @param username Username of the user who connected
     * @param address Address of the user who connected
     */
	protected void userConnect(String username, InetAddress address) { 
		if (!this.agent.isFirstConnection) {
			User oldUser = this.agent.getNetworkManager().addressResolve(address);
			if (oldUser==null) { // New address
				if (Agent.debug) System.out.printf("New user connected : %s\n",username);
				User user = new User(username, address);
				this.agent.getUsernameManager().addUsername(user, username);
				this.activeUsers.add(user);
				this.agent.getNetworkManager().addAddress(user, address);
			} else {
				 {
					if (!(oldUser.getUsername().equals(username))) {
						if (Agent.debug) System.out.printf("Old user reconnected : %s -> %s\n",oldUser.getUsername(), username);
						oldUser.changeUsername(username);
						this.agent.getUsernameManager().changeUsername(oldUser, username);
					} else {
						if (Agent.debug) System.out.printf("Old user reconnected : %s\n",username);
					}
				}
				this.activeUsers.add(oldUser);
				this.disconnectedUsers.remove(oldUser);		
			} 
		}
	}
	
	/**
     * This method verifies that no user from the list has the requested address
     *  
     * @param list List of the users
     * @param address Address that we want to check
     * 
     * @return True if no user from the list has the requested address
     */
	private boolean verifyUniqAddress(ArrayList<User> list, InetAddress address) {
		boolean ok = true;
		for (User temp : list) {
			ok = !temp.getAddress().equals(address);
			if (!ok) break;
		}
		return ok;
	}
	
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
	
	
	/**
     * This method returns the list of disconnected users
     * 
     * @return List of all disconnected users
     */
	protected ArrayList<User> getDisconnectedUsers() {
		return disconnectedUsers;
	}
	
	/**
     * This method returns the list of active users
     * 
     * @return List of all active users
     */
	protected ArrayList<User> getActiveUsers() {
		return activeUsers;
	}
	
	/**
     * This method puts all active users as disconnected
     * <p>Note that this method is used when disconnecting
     */
	protected void putAllUsersDisconnected() {
		for (User u : this.activeUsers) this.disconnectedUsers.add(u);
		this.activeUsers=new ArrayList<User>();
	}
}
