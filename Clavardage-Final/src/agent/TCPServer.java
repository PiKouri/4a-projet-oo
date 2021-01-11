package agent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread{

/*-----------------------Attributs privés-------------------------*/
	
	/**ServerSocket associated to this TCPServer*/
	private ServerSocket socket;
	
	/**The agent that created this TCPServer*/
	private Agent agent;

	
/*-----------------------Méthodes - Gestion connexions TCP entrantes-------------------------*/
	
	
	/**
     * Constructor for the class TCPServer
     * <p>This class manages the incoming TCP connections
     * 
     * @param agent The agent that created this TCPServer
     */
	protected TCPServer(Agent agent) throws IOException {
		this.agent = agent;
		Agent.printAndLog(String.format("Connection Server created\n"));
		this.socket = new ServerSocket(Agent.defaultPortNumber);
		this.start();
	}
	
	@Override
	public void interrupt() {
		Agent.printAndLog(String.format("Connection Server interrupted\n"));
		try {
			this.socket.close();
		} catch (IOException e) {
        	Agent.errorMessage("ERROR in socket.close()\n", e);
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
			this.agent.getNetworkManager().newActiveUserSocket(link,0);
		}
	}

}
