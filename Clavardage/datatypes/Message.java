import java.util.Date;

public class Message {
	
/*---------------------------Attributs---------------------------*/
	
	private int size;
	private Date date;
	private User sender;
	private User receiver;
	
	
/*---------------------------Getteurs---------------------------*/
	
	public int getSize() {
		return this.size;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public User getSender() {
		return this.sender;
	}
	
	public User getReceiver() {
		return this.receiver;
	}
	
	
	
/*---------------------------Setters---------------------------*/
	
	public void setDate(Date date) {
		this.date = date;
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
