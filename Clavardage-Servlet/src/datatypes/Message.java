package datatypes;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	

/*---------------------------Attributs---------------------------*/
	
	/**DateTimeFormatter*/
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		
	/**Date of the message*/
	private String date;
	/**Destination Address*/
	private String destination;
	/**Sending boolean : true if me is sending, false if receiving*/
	private boolean sending;
	
	
/*---------------------------Attributs---------------------------*/
	
	/**
     * Constructor for the class Message
     * 
     */
	public Message() {
		this.sending=true; // Sending is true by default, put false when receiving
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
	 * This method returns true if me sent this message
	 * 
	 */
	public boolean isSending() {
		return this.sending;
	}
	
	/**
	 * This method returns the Destination IP Address
	 * 
	 * @return Destination IP Address
	 */
	public String getDestination() {
		return this.destination;
	}
	
	
/*---------------------------Setteurs---------------------------*/
	
	
	/**
	 * This method sets the sending boolean to false
	 * 
	 */
	public void receiving() {
		this.sending=false;
	}
	
	/**
	 * This method sets the Destination IP Address
	 * 
	 * @param destination Destination IP Address
	 */
	public void setDestination(String destination) {
		this.destination=destination;
	}
	
	
/*---------------------------Check type---------------------------*/
	
	/**
	 * This method returns true if the message is a MyFile message
	 * 
	 * @return True if the message is a MyFile message
	 */
	public boolean isFile(){
		return (this instanceof MyFile);
	}
	
	/**
	 * This method returns true if the message is a Image message
	 * 
	 * @return True if the message is a Image message
	 */
	public boolean isImage(){
		return (this instanceof Image);
	}
	
	/**
	 * This method returns true if the message is a Text message
	 * 
	 * @return True if the message is a Text message
	 */
	public boolean isText(){
		return (this instanceof Text);
	}
}
