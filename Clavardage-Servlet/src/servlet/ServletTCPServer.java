package servlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Deprecated
public class ServletTCPServer extends Thread{

/*-----------------------Attributs privés-------------------------*/
	
	/**ServerSocket associated to this TCPServer*/
	private ServerSocket socket;
	
	/**The servlet that created this TCPServer*/
	private MyServlet servlet;

	
/*-----------------------Méthodes - Gestion connexions TCP entrantes-------------------------*/
	
	
	/**
     * Constructor for the class TCPServer
     * <p>This class manages the incoming TCP connections
     * 
     * @param Servlet that created this TCPServer
     */
	protected ServletTCPServer(MyServlet servlet) throws IOException {
		this.servlet = servlet;
		MyServlet.printAndLog(String.format("Connection Server created\n"));
		this.socket = new ServerSocket(MyServlet.defaultPortNumber);
		this.start();
	}
	
	@Override
	public void interrupt() {
		MyServlet.printAndLog(String.format("Connection Server interrupted\n"));
		try {
			this.socket.close();
		} catch (IOException e) {
        	MyServlet.errorMessage("ERROR in socket.close()\n", e);
		}
	}
	
	@Override
	public void run() {
		try {
			waitForConnection();
		} catch (IOException e) {}
	}
	
	/**
     * The TCPServer waits for incoming TCP connections
     * <p>When a new connection is established, it will
     * send the socket to the Agent
     */
	protected void waitForConnection() throws IOException {
		while (true) {
			Socket link;
			link = this.socket.accept();
			this.servlet.newActiveUserSocket(MyServlet.addressToString(link.getInetAddress()),link);
		}
	}

}
