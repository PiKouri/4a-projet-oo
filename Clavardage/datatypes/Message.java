import java.util.Date;

public class Message {
	private int size;
	private Date date;
	
	// Get
	
	public int getSize() {
		return this.size;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	// Set
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	// Check type
	
	public boolean isFile(){
		return (this instanceof File);
	}
	
	public boolean isImage(){
		return (this instanceof Image);
	}
	
	public boolean isText(){
		return (this instanceof Text);
	}
	
	public boolean isNewUsername() {
		return (this instanceof NewUsername);
	}
}
