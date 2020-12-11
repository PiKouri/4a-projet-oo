package agent;

import datatypes.MyMap;
import userInterface.User;

public class UsernameManager {
	
	private Agent agent;
	
	private MyMap<User,String> mapUsernames;

	
/*-----------------------Constructeurs-------------------------*/
	
	
	protected UsernameManager(Agent agent) {
		this.agent=agent;
		this.mapUsernames = new MyMap<User,String>();
	}
	
	
/*-----------------------Méthodes - Noms des utilisateurs-------------------------*/
	
	protected boolean checkUsernameAvailability(String username) {
		boolean ok = false;
		
		if (username.equals("")) return false;
		
		long time = System.currentTimeMillis();//temps de depart
		long fin=time + Agent.timeout;//temps de fin
		
		// Timeout pour le cas où on est le premier utilisateur (personne ne répond à la requête checkUsernameAvailability)
		
		if (this.agent.isFirstConnection) { // Première connexion
			this.agent.getNetworkManager().sendBroadcast("checkUsernameAvailability "+username);
			/*try {
				while (!ok) {
					if (fin <= System.currentTimeMillis()) throw new TimeoutException();
					ok = this.udpServer.wasUsernameChecked();
					try {Thread.sleep(100);} catch (Exception e) {}
				}
				ok = this.udpServer.getLastUsernameAvailability();
			} catch (TimeoutException e) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}*/
			
			try {
				synchronized(this.agent) {this.agent.wait(Agent.timeout);}
			} catch (InterruptedException e) {} // wait for response of other users
			ok = this.agent.getNetworkManager().getLastUsernameAvailability();
			if (fin <= System.currentTimeMillis()) {
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}
		} else { // Autrement on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
		}
		return ok;
	}
	
	protected void userChangeUsername(String oldUsername, String newUsername) {
		if (!this.agent.isFirstConnection) {
			if (oldUsername != newUsername) {
				User user = nameResolve(oldUsername);
				user.changeUsername(newUsername);
				this.mapUsernames.replaceValue(user, newUsername); 
			}
		}
	}
	
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
	
	protected User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}
	
	protected String getUsername(User user) {
		return this.mapUsernames.getValue(user);
	}
	
	protected MyMap<User,String> getMapUsernames() {
		return this.mapUsernames;
	}
	
	protected void addUsername(User user, String username) {
		this.mapUsernames.putUser(user,username);
	}
	
	protected void changeUsername(User user, String newUsername) {
		this.mapUsernames.replaceValue(user, newUsername);
	}
}
