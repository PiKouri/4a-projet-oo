
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
	
	public bool isFile(){
		return (this.getClass()==File);
	}
	
	public bool isImage(){
		return (this.getClass()==Image);
	}
	
	public bool isText(){
		return (this.getClass()==Text);
	}
	
	public bool isNewUsername() {
		return (this.getClass()==NewUsername)
	}
}
