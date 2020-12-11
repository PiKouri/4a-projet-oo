package agent;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import datatypes.*;
import userInterface.User;

public class UserSocket extends Thread {
	
/*-----------------------Attributs-------------------------*/

	/**Socket associated to this UserSocket*/
	private Socket socket;
	/**User associated to this UserSocket*/
	private User user;
	/**Agent that created this UserSocket*/
	private Agent agent;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class UserSocket
     * 
     * @param agent Agent that created this UserSocket
     * @param socket Socket associated to this UserSocket 
     */
	public UserSocket(User user, Agent agent, Socket socket) {
		if (Agent.debug) System.out.println("UserSocket "+ this.user.getUsername() +" created");
		this.user=user;
		this.agent=agent;
		this.socket=socket;
	}
	
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	
	@Override
	public void run() { // Reception des messages
		
	}
	
	@Override
	public void interrupt(){
		if (Agent.debug) System.out.println("UserSocket "+ this.user.getUsername() +" interrupted");
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Error in socket.close()");
		}
	}
	
	public void receiveMessage() {
		
	}
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	public void sendImage(Image image) {
		
	}
	
	public void sendText(Text text) {
		
	}
	
	public void sendFile(File file){
		
	}
	
	public void sendDate(Date date) {
		
	}
	
	
	
}
