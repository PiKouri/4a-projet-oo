package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import datatypes.*;
import userInterface.Interface;
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
	 * @param us UserSocket
	 * @param message Message
	 * */
	protected void receiveMessage(UserSocket us, Message message) {
		User user = this.agent.getNetworkManager().socketResolve(us);
		message.setSender(user);
		this.getMessages(user).add(message);
		if (message.isFile()) receiveFile(us,(MyFile)message);
		if (message.isText()) receiveText(us,(Text)message);
		if (message.isImage()) receiveImage(us,(Image)message);	
		
	}
	
	/**
	 * This methode is used when receiving a File message
	 * 
	 * @param us UserSocket
	 * @param file
	 * */
	protected void receiveFile(UserSocket us, MyFile file) {
		// TODO
		User user = this.agent.getNetworkManager().socketResolve(us);
		System.out.printf("%s - %s : %s \n", file.getDate(), user.getUsername(), file.getFilename());
	}
	
	/**
	 * This methode is used when receiving a Text message
	 * 
	 * @param us UserSocket
	 * @param text
	 * */
	protected void receiveText(UserSocket us, Text text) {
		// A changer avec l'interface graphique
		User user = this.agent.getNetworkManager().socketResolve(us);
		System.out.printf("%s - %s : %s \n", text.getDate(), user.getUsername(), text.getText());
	}

	/**
	 * This methode is used when receiving a Image message
	 * 
	 * @param us UserSocket
	 * @param image
	 * */
	protected void receiveImage(UserSocket us, Image image) {
		// A changer avec l'interface graphique
		User user = this.agent.getNetworkManager().socketResolve(us);
		System.out.printf("%s - %s : \n", image.getDate(), user.getUsername());
		Interface.display(this.agent.me.getUsername()+ " got this image from " + user.getUsername(), image.getImage());
	}
	
	/**
	 * This methode is used when sending a Message (File, Text or Image)
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
	
	protected void initMessages(User user){
		this.mapMessages.put(user,new ArrayList<Message>());
	}
}
