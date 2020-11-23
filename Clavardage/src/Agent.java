import java.net.Socket;
import java.util.ArrayList;

public class Agent extends Subject {
	private MyMap<String,User> mapUsernames;
	private MyMap<User,MessageEmissionObserver> mapObserver;
	
	public boolean chooseUsername(String name) {
		return true;
	}
	
	public ArrayList<String> viewActiveUsernames(){
		return new ArrayList<String>(1);
	}
	
	public ArrayList<Message> getMessageHistory(String username){
		return new ArrayList<Message>(1);
	}
	
	public void sendMessage(Message message) {
		
	}
	
	private User nameResolve(String username) {
		return new User();
	}
	
	private void checkUsernameAvailability(String username) {
		
	}
	
	public void userDisconnect(User user) {
		
	}
	
	public void newActiveUserSocket(Socket socket) {
		
	}
	
	public void receiveFile(File file) {
		
	}
	
	public void receiveText(Text text) {
		
	}

	public void receiveImage(Image image) {
	
	}

}
