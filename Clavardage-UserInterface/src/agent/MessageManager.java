package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import datatypes.*;
import userInterface.User;

public class MessageManager {

	/**The agent that created this MessageManager*/
	private Agent agent;
	
	/**MyMap that associate a User to a ArrayList of Message*/
	private Map<User,ArrayList<Message>> mapMessages;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class MessageManager
     * 
     * @param agent The agent that created this MessageManager
     */
	protected MessageManager(Agent agent) {
		this.agent=agent;
		this.mapMessages = new HashMap<>();		
	}
	
	
/*-----------------------Méthodes - Réception de messages-------------------------*/
	
	/**
	 * This method is used when receiving a Message (File, Text or Image)
	 * 
	 * @param us UserSocket
	 * @param message Message
	 * */
	protected void receiveMessage(UserSocket us, Message message) {
		User user = this.agent.getNetworkManager().socketResolve(us);
		message.setSender(user);
		this.getMessages(user).add(message);
		
	}
	
	/**
	 * This method is used when sending a Message (File, Text or Image)
	 * 
	 * @param dest Destination user
	 * @param message Message
	 * */
	protected void sendMessage(User dest, Message message) {
		this.getMessages(dest).add(message);
		UserSocket us = this.agent.getNetworkManager().getSocket(dest);
		us.send(message);
	}
	
	
/*-----------------------Méthodes - Lire messages-------------------------*/

	
	/**
	 * This methods returns the list of messages associated to a specific user
	 * 
	 * @param user The user associated to the conversation
	 * 
	 * @return List of messages with the user
	 * */
	protected ArrayList<Message> getMessages(User user){
		return this.mapMessages.get(user);
	}
	
	/**
	 * This methods create a empty list of messages associated to a specific user
	 * 
	 * @param user The user associated to the conversation
	 * 
	 * */
	protected void initMessages(User user){
		this.mapMessages.put(user,new ArrayList<Message>());
	}
}
