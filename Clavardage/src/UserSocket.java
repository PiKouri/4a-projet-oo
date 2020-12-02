import java.net.Socket;
import java.util.Date;

public class UserSocket extends Subject {
	
/*-----------------------Attributs-------------------------*/

	private Socket socket;
	private User user;
	private MessageReceptionObserver messageReceptionObserver;
	
/*-----------------------Guetteurs-------------------------*/
	
	
	public User getUser() {
		return new User("");
	}
	
/*-----------------------Méthodes-------------------------*/
	
	public void informAllUsersNewUsername(String name) {
		
	}
	
	public void sendImage(Image image) {
		
	}
	
	public void sendText(Text text) {
		
	}
	
	public void sendFile(File file){
		
	}
	
	public void sendDate(Date date) {
		
	}
	
	
	public void receiveMessage() {
		
	}
}
