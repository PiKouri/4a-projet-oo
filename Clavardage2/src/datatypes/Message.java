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
	
	
/*---------------------------Attributs---------------------------*/
	
	
	public Message() {
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
	
	
/*---------------------------Check type---------------------------*/
	
	
	public boolean isFile(){
		return (this instanceof File);
	}
	
	public boolean isImage(){
		return (this instanceof Image);
	}
	
	public boolean isText(){
		return (this instanceof Text);
	}
}
