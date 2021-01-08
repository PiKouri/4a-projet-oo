package agent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import datatypes.*;

public class UserSocket extends Thread {
	
/*-----------------------Attributs-------------------------*/

	/**Socket associated to this UserSocket*/
	private Socket socket;
	/**User associated to this UserSocket*/
	protected String username;
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
	public UserSocket(String username, Agent agent, Socket socket){
		this.username=username;
		if (Agent.debug) System.out.println("UserSocket "+ this.username +" created");
		this.agent=agent;
		this.socket=socket;
		try {
			this.os = new ObjectOutputStream(socket.getOutputStream());
			this.is = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
        	Agent.errorMessage("ERROR when creating Streams (input or output)\n", e);
		}
		this.running = true;
		this.start();
	}
	
	
/*-----------------------Méthodes - Gestion Thread-------------------------*/
	
	
	@Override
	public void run() { // Reception des messages
		waitForMessage();
	}
	
	@Override
	public void interrupt(){
		if (Agent.debug) System.out.println("UserSocket "+ this.username +" interrupted");
		this.running=false;
		try {
			this.socket.close();
		} catch (IOException e) {
        	Agent.errorMessage("ERROR when calling socket.close()\n", e);
		}
	}
	
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	
	/**
	 * This method is used to wait for message reception
	 * <p> Note that the message is then managed by the MessageManager
	 */
	public void waitForMessage() {
		Message message;
		while (this.running) {
			try {
				message=(Message) this.is.readObject();
				this.agent.getMessageManager().receiveMessage(this,message);
			} catch (Exception e) {}
		}
	}
	
	
/*-----------------------Méthodes - Envoi des messages TCP-------------------------*/
	
	
	/**
	 * This method is used to send Messages through this specific socket
	 * 
	 * @param message The Message that we want to send
	 */
	public void send(Message message) {
		try {
			this.os.writeObject(message);
		} catch (IOException e) {
        	Agent.errorMessage("ERROR when trying to send TCP message\n", e);
		}
	}
	
}
