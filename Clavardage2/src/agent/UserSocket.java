package agent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	/**True if the UserSocket is running*/
	protected boolean running;
	
	/**ObjectOutputStream which is used to send Messages*/
	private ObjectOutputStream os;
	/**ObjectInputStream which is used to receive Messages*/
	private ObjectInputStream is;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class UserSocket
     * 
     * @param agent Agent that created this UserSocket
     * @param socket Socket associated to this UserSocket 
	 * @throws IOException 
     */
	public UserSocket(User user, Agent agent, Socket socket){
		this.user=user;
		if (Agent.debug) System.out.println("UserSocket "+ this.user.getUsername() +" created");
		this.agent=agent;
		this.socket=socket;
		try {
			this.os = new ObjectOutputStream(socket.getOutputStream());
			this.is = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error when creating Streams (input or output)");
			e.printStackTrace();
		}
		this.running = true;
		this.start();
	}
	
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	
	@Override
	public void run() { // Reception des messages
		waitForMessage();
	}
	
	@Override
	public void interrupt(){
		if (Agent.debug) System.out.println("UserSocket "+ this.user.getUsername() +" interrupted");
		this.running=false;
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Error when calling socket.close()");
		}
	}
	
	public void waitForMessage() {
		Text message;
		while (this.running) {
			try {
				message=(Text) this.is.readObject();
				this.agent.getMessageManager().receiveText(this,message);
			} catch (Exception e) {}
		}
	}
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	public void send(Message message) {
		
	}
	
	public void sendImage(Image image) {
		
	}
	
	public void sendText(Text text) {
		try {
			this.os.writeObject(text);
		} catch (IOException e1) {
			System.out.println("Error when trying to send Text message");
			e1.printStackTrace();
		}
	}
	
	public void sendFile(File file){
		
	}
	
	public void sendDate(Date date) {
		
	}
	
	
	
}
