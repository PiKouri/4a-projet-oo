package agent;

import datatypes.MyMap;
import userInterface.User;

public class UsernameManager {
	
	/**The agent that created this UsernameManager*/
	private Agent agent;
	
	/**MyMap that associate a User to a Username*/
	private MyMap<User,String> mapUsernames;

	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class UsernameManager
     * 
     * @param agent The agent that created this UsernameManager
     */
	protected UsernameManager(Agent agent) {
		this.agent=agent;
		this.mapUsernames = new MyMap<User,String>();
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
				if (Agent.debug) System.out.println("Connection timeout, we consider ourselves as first user");
				ok = true;
			}
		} else { // Hors première connexion, on regarde dans notre table locale
			ok = !(this.mapUsernames.containsValue(username));
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
		if (!this.agent.isFirstConnection) {
			if (oldUsername != newUsername) {
				User user = nameResolve(oldUsername);
				user.changeUsername(newUsername);
				this.mapUsernames.replaceValue(user, newUsername); 
			}
		}
	}
	
	
/*-----------------------Méthodes - Getteurs et setteurs-------------------------*/
	
	/**
     * This method returns the user associated to a specific username
     *
     * @param username Username that we want to resolve
     * 
     * @return The user associated to the username
     */
	protected User nameResolve(String username) {
		return this.mapUsernames.getUser(username);
	}
	
	/**
     * This method returns the username associated to a specific user from the MyMap
     *
     * @param user 
     * 
     * @return The username of the user
     */
	protected String getUsername(User user) {
		return this.mapUsernames.getValue(user);
	}
	
	/**
     * This method returns the MyMap of users and their associated usernames
     * 
     * @return MyMap of users and their associated usernames
     */
	protected MyMap<User,String> getMapUsernames() {
		return this.mapUsernames;
	}
	
	/**
     * This method adds an association of a user and a username
     * 
     * @param user
     * @param username
     */
	protected void addUsername(User user, String username) {
		this.mapUsernames.putUser(user,username);
	}
	
	/**
     * This method replaces the username of a user in the MyMap
     * 
     * @param user
     * @param newUsername
     */
	protected void changeUsername(User user, String newUsername) {
		this.mapUsernames.replaceValue(user, newUsername);
	}
}
