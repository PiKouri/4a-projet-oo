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
	
	
/*-----------------------M�thodes - R�ception de messages-------------------------*/
	
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
	protected void receiveText(UserSocket us, Text text) {
		// TODO
		User user = this.agent.getNetworkManager().socketResolve(us);
		System.out.printf("%s : %s \n", user.getUsername(), text.getText());
	}

	/**
	 * This methode is used when receiving a Image message
	 * 
	 * @param image
	 * */
	protected void receiveImage(Image image) {
		// TODO
			
	}
	
	/**
	 * This methode is used when sending a Message (File, Text or Image)
	 * 
	 * @param dest Destination user
	 * @param message Message
	 * */
	protected void sendMessage(User dest, Message message) {
		// TODO
		UserSocket us = this.agent.getNetworkManager().getSocket(dest);
		if (message.isText()) us.sendText((Text) message);
	}
	
	
/*-----------------------M�thodes - Lire messages-------------------------*/

	
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
