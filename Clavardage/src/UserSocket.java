import java.net.Socket;
import java.util.Date;

public class UserSocket implements Runnable {
	
/*-----------------------Attributs-------------------------*/

	private Socket socket;
	private User user;
	private Agent agent; // A modifier sur le diagramme
	private Thread thread; // A modifier sur le diagramme
	
/*-----------------------Constructeurs-------------------------*/
	
	public UserSocket(User user, Agent agent, Socket socket) {
		this.user=user;
		this.agent=agent;
		this.socket=socket;
		this.thread = new Thread(this);
		this.thread.start();
	}
	
/*-----------------------Guetteurs-------------------------*/
	
	
	public User getUser() {
		return this.user;
	}
	
/*-----------------------Méthodes-------------------------*/
	
	@Override
	public void run() { // Reception des messages
		
	}
	
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
