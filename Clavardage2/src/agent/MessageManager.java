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
	 * This methode is used when receiving a Message (File, Text or Image)
	 * 
	 * @param message
	 * */
	protected void receiveMessage(Message message) {
		// TODO
		
		/*if (message.isFile()) receiveFile(message);
		if (message.isText()) receiveText(message);
		if (message.isImage()) receiveImage(message);
		*/
		
	}
	
	/**
	 * This methode is used when receiving a File message
	 * 
	 * @param file
	 * */
	protected void receiveFile(File file) {
		// TODO
	}
	
	/**
	 * This methode is used when receiving a Text message
	 * 
	 * @param text
	 * */
	protected void receiveText(Text text) {
		// TODO
		
	}

	/**
	 * This methode is used when receiving a Image message
	 * 
	 * @param image
	 * */
	protected void receiveImage(Image image) {
		// TODO
			
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
}
