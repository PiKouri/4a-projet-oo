package agent;

import java.util.ArrayList;

import datatypes.*;
import userInterface.Interface;
import userInterface.User;

public class MessageManager {

	/**The agent that created this MessageManager*/
	private Agent agent;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class MessageManager
     * 
     * @param agent The agent that created this MessageManager
     */
	protected MessageManager(Agent agent) {
		this.agent=agent;
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
		message.receiving();
		this.agent.getDatabaseManager().addMessage(user.getAddress(), message);
		Interface.notifyNewMessage(user.getUsername(),message);
	}
	
	/**
	 * This method is used when sending a Message (File, Text or Image)
	 * 
	 * @param dest Destination user
	 * @param message Message
	 * */
	protected void sendMessage(User dest, Message message) {
		this.agent.getDatabaseManager().addMessage(dest.getAddress(), message);
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
		return this.agent.getDatabaseManager().getMessages(user.getAddress());
	}
}
