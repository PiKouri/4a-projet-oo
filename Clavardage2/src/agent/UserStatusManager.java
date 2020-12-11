package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import userInterface.User;

public class UserStatusManager {

	private Agent agent;
	
	private ArrayList<User> activeUsers;
	private ArrayList<User> disconnectedUsers;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	protected UserStatusManager(Agent agent) {
		this.agent=agent;
		this.activeUsers = new ArrayList<User>();
		this.disconnectedUsers = new ArrayList<User>();
	}
	
	
/*-----------------------Méthodes - Actualisation de l'état des autres utilisateurs-------------------------*/
	
	
	protected void updateDisconnectedUsers(String username, InetAddress address) {
		if (verifyUniqAddress(this.disconnectedUsers,address) && verifyUniqAddress(this.activeUsers,address)) { // Si l'addresse n'est pas déjà connue, on l'ajoute
			User user = new User(username,address);
			this.disconnectedUsers.add(user);
			this.agent.getUsernameManager().addUsername(user, username);
			this.agent.getNetworkManager().addAddress(user, address);
		}
	}
	
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
	
	private boolean verifyUniqAddress(ArrayList<User> list, InetAddress address) {
		boolean ok = true;
		for (User temp : list) {
			ok = !temp.getAddress().equals(address);
			if (!ok) break;
		}
		return ok;
	}
	
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
	
	
	protected ArrayList<User> getDisconnectedUsers() {
		return disconnectedUsers;
	}
	
	protected ArrayList<User> getActiveUsers() {
		return activeUsers;
	}
	
	protected void putAllUsersDisconnected() {
		for (User u : this.activeUsers) this.disconnectedUsers.add(u);
		this.activeUsers=new ArrayList<User>();
	}
}
