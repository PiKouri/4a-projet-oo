import java.util.ArrayList;

public class Agent extends Subject {
	private MyMap<String,User> mapUsernames;
	private MyMap<User,MessageEmissionObserver> mapObserver;
	
	public boolean chooseUsername(String name) {
		
	}
	
	public ArrayList<String> viewActiveUsernames(){
		
	}
	
	public ArrayList<Message> getMessageHistory(String username){
		
	}
	
	public void sendMessage(Message message) {
		
	}
	
	private User nameResolve(String username) {
		
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
