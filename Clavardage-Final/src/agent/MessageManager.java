package agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import datatypes.*;
import userInterface.Interface;

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
		String header = String.format("Message received from %s : ",us.username);
		if (message.isFile()) {
			Agent.printAndLog(header+"file:"+((MyFile)message).getFilename()+"\n");
			((MyFile) message).setFilepath(Agent.dir+"file/"+us.getAddressAsString()+"/"+((MyFile) message).getFilename());
		} else if (message.isImage()) { 
			Agent.printAndLog(header+"image:"+((Image)message).getFilename()+"\n"); 
			((Image) message).setFilepath(Agent.dir+"image/"+us.getAddressAsString()+"/"+((Image) message).getFilename());			
		} else if (message.isText()) Agent.printAndLog(header+"text:"+((Text)message).getText()+"\n"); 
		String username = this.agent.getNetworkManager().socketResolve(us);
		message.receiving();
		this.addMessage(this.agent.getNetworkManager().getAddress(username),
				this.agent.getNetworkManager().getExternId(username), message);
		Interface.notifyNewMessage(username,message);
	}
	
	/**
	 * This method is used when sending a Message (File, Text or Image)
	 * 
	 * @param dest Destination user
	 * @param message Message
	 * */
	protected void sendMessage(String username, Message message) {
		this.addMessage(this.agent.getNetworkManager().getAddress(username),
				this.agent.getNetworkManager().getExternId(username), message);
		UserSocket us = this.agent.getNetworkManager().getSocket(username);
		us.send(message);
	}
	
	/**
     * Add a message to the messages list from a user in the database
     *
     * @param address IP Address of the user
     * @param externId Extern id of the user
     * @param message Message we want to add
     */
	protected void addMessage(InetAddress address, int externId, Message message){
		try {
			String username = this.agent.getUsernameManager().getUsername(address, externId);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(message);
		    // serialize the employee object into a byte array
		    byte[] messageAsBytes = baos.toByteArray();
		    PreparedStatement pstmt = this.agent.getDatabaseManager().getConnection().prepareStatement
		            ("INSERT INTO messages_"+username+" (message) VALUES(?)");
		    ByteArrayInputStream bais = new ByteArrayInputStream(messageAsBytes);
		    // bind our byte array  to the message column
		    pstmt.setBinaryStream(1,bais, messageAsBytes.length);
		    pstmt.executeUpdate();
		} catch (Exception e) {
	        Agent.errorMessage(String.format("ERROR when trying to get messages from address %s in the database\n",address), e);
		}
	}
	
	
/*-----------------------Méthodes - Lire messages-------------------------*/

	
	/**
	 * This methods returns the list of messages associated to a specific user
	 * 
	 * @param username The username associated to the conversation
	 * 
	 * @return List of messages with the user
	 * */
	protected ArrayList<Message> getMessages(String username){
		try {
	        // Get messages in the messages table associated to the user
	        PreparedStatement pstmt  = this.agent.getDatabaseManager().getConnection().prepareStatement(
	        		"SELECT message "+ "FROM messages_"+username);
	        ResultSet rs  = pstmt.executeQuery();
	        ArrayList<Message> messages = new ArrayList<Message>();
	        // loop through the result set
            while (rs.next()) {
		        // fetch the serialized object to a byte array
	            byte[] st = (byte[])rs.getObject(1);
	            ByteArrayInputStream baip = 
	                new ByteArrayInputStream(st);
	            ObjectInputStream ois = new ObjectInputStream(baip);
	            // re-create the object
	            Message message = (Message)ois.readObject();
	            messages.add(message);
            }
	        return messages;
		} catch (Exception e) {
        	Agent.errorMessage(String.format("ERROR when trying to get messages from user %s in the database\n", username), e);
		}
		return null; // Pas accessible
	}
}
