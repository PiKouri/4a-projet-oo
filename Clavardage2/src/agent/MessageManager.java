package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import datatypes.*;
import userInterface.User;

public class MessageManager {

	private Agent agent;
	
	private Map<User,ArrayList<Message>> mapMessages;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	protected MessageManager(Agent agent) {
		this.agent=agent;
		this.mapMessages = new HashMap<>();		
	}
	
	
/*-----------------------Méthodes - Réception de messages-------------------------*/
	
	
	protected void receiveFile(File file) {
		
	}
	
	protected void receiveText(Text text) {
		
	}

	protected void receiveImage(Image image) {
	
	}
	
	
/*-----------------------Méthodes - Lire messages-------------------------*/

	
	protected ArrayList<Message> getMessages(User user){
		return this.mapMessages.get(user);
	}
}
