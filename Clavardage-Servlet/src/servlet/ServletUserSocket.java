package servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import datatypes.*;

@Deprecated
public class ServletUserSocket extends Thread {
	
/*-----------------------Attributs-------------------------*/

	/**Socket associated to this UserSocket*/
	private Socket socket;
	/**Address associated to this UserSocket*/
	protected String address;
	/**Servlet that created this UserSocket*/
	private MyServlet servlet;
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
     * @param servlet Servlet that created this UserSocket
     * @param socket Socket associated to this UserSocket 
	 * @throws IOException 
     */
	public ServletUserSocket(String address, MyServlet servlet, Socket socket){
		this.address=address;
		MyServlet.printAndLog(String.format("UserSocket "+ this.address +" created\n"));
		this.servlet=servlet;
		this.socket=socket;
		try {
			this.os = new ObjectOutputStream(socket.getOutputStream());
			this.is = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			MyServlet.printAndLog("ERROR when creating Streams (input or output)\n");
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
		MyServlet.printAndLog(String.format("UserSocket "+ this.address +" interrupted\n"));
		this.running=false;
		try {
			this.socket.close();
		} catch (IOException e) {
			MyServlet.printAndLog("ERROR when calling socket.close()\n");
		}
	}
	
	public String getAddressAsString() {
		return MyServlet.addressToString(socket.getInetAddress());
	}
	
	
/*-----------------------Méthodes - Réception des messages TCP-------------------------*/
	
	
	/**
	 * This method is used to wait for message reception
	 * <p> Note that the message is then managed by the MessageManager
	 */
	public void waitForMessage() {
		while (this.running) {
			try {
				Message message = (Message)this.is.readObject();
				this.servlet.receiveMessage(this, message, this.is);
			} catch (Exception e) {
				MyServlet.printAndLog(e.getMessage());
			}
		}
	}
	
	
/*-----------------------Méthodes - Envoi des messages TCP-------------------------*/
	
	
	/**
	 * This method is used to send Messages through this specific socket
	 * 
	 * @param message The Message that we want to send
	 * @param type
	 * @param is (only use for File or Image)
	 */
	public void send(Message message,ObjectInputStream is) {
		try {
			this.os.writeObject(message);
			if (message.isFile() || message.isImage()) {
		        byte[] bytes = new byte[16 * 1024];
		        int count;
		        while ((count = is.read(bytes)) > 0) {
		            this.os.write(bytes, 0, count);
		        }
			}
		} catch (IOException e) {
			MyServlet.errorMessage("ERROR when trying to send TCP message\n", e);
		}
	}
	
}
