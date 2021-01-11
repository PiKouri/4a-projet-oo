package agent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
		Agent.printAndLog(String.format("UserSocket "+ this.username +" created\n"));
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
		Agent.printAndLog(String.format("UserSocket "+ this.username +" interrupted\n"));
		this.running=false;
		try {
			this.socket.close();
		} catch (IOException e) {
        	Agent.errorMessage("ERROR when calling socket.close()\n", e);
		}
	}
	
	public String getAddressAsString() {
		return NetworkManager.addressToString(socket.getInetAddress());
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
				if (message.isFile() || message.isImage()) {
					OutputStream out = null;
					if (message.isFile()) out = new FileOutputStream(Agent.dir+"file/"+getAddressAsString()+"/"+((MyFile)message).getFilename());
					else if (message.isImage()) out = new FileOutputStream(Agent.dir+"image/"+getAddressAsString()+"/"+((Image)message).getFilename());
					byte[] bytes = new byte[16*1024];
			        int count;
			        while ((count = this.is.read(bytes)) > 0) {
			            out.write(bytes, 0, count);
			        }
			        out.close();
				}
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
			if (message.isFile() || message.isImage()) {
				File file = null;
				if (message.isFile()) file = new File(((MyFile)message).getFilepath());
				else if (message.isImage()) file = new File(((Image)message).getFilepath());
		        byte[] bytes = new byte[16 * 1024];
		        InputStream in = new FileInputStream(file);
		        int count;
		        while ((count = in.read(bytes)) > 0) {
		            this.os.write(bytes, 0, count);
		        }
		        in.close();
			}
		} catch (IOException e) {
        	Agent.errorMessage("ERROR when trying to send TCP message\n", e);
		}
	}
	
}
