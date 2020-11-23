import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Agent extends Subject {
	private MyMap<User,String> mapUsernames; // A modifier sur le diagramme
	private MyMap<User,MessageEmissionObserver> mapObservers; // A modifier sur le diagramme
	private Map<User,ArrayList<Message>> mapMessages; // A modifier sur le diagramme
	private ArrayList<User> otherUsers;  // A modifier sur le diagramme
	
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
		return mapUsernames.getUser(username);
	}
	
	private void checkUsernameAvailability(String username) {
		
	}
	
	public void userDisconnect(User user) {
		mapUsernames.remove(user);
		mapObservers.remove(user);
		otherUsers.remove(user);
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
