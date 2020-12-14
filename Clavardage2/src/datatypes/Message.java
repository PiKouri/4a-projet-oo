package datatypes;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import userInterface.User;

public class Message implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	

/*---------------------------Attributs---------------------------*/
	
	/**DateTimeFormatter*/
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		
	/**Date of the message*/
	private String date;
	/**Sender*/
	private User sender;
	
	
/*---------------------------Attributs---------------------------*/
	
	/**
     * Constructor for the class Message
     * @param sender User who sends the message
     * 
     */
	public Message(User sender) {
		this.sender=sender;
		this.date = Message.dtf.format(LocalDateTime.now());
	}
	
	
/*---------------------------Getteurs---------------------------*/
	
	/**
	 * This method returns the Date of creation of the message
	 * 
	 * @return Date of creation of the message
	 */
	public String getDate() {
		return this.date;
	}
	
	/**
	 * This method returns the Sender of the message
	 * 
	 * @return Sender of the message
	 */
	public User getSender() {
		return this.sender;
	}
	
	
/*---------------------------Setteurs---------------------------*/
	
	
	/**
	 * This method sets the Sender of the message
	 * 
	 * @param sender Sender of the message
	 */
	public void setSender(User sender) {
		this.sender=sender;
	}
	
	
/*---------------------------Check type---------------------------*/
	
	
	public boolean isFile(){
		return (this instanceof MyFile);
	}
	
	public boolean isImage(){
		return (this instanceof Image);
	}
	
	public boolean isText(){
		return (this instanceof Text);
	}
}
