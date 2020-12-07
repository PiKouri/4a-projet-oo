import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{ // a modifier sur le diagramme
	
	private ServerSocket socket;
	
	private Agent agent;
	
	public Server(Agent agent) throws IOException {
		this.agent = agent;
		if (Agent.debug) System.out.println("Connection Server created");
		this.socket = new ServerSocket(Agent.defaultPortNumber);
	}
	
	public void interrupt() {
		if (Agent.debug) System.out.println("Connection Server interrupted");
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Error in socket.close()");
		}
	}
	
	public void run() {
		try {
			waitForConnection();
		} catch (IOException e) {
			System.out.printf("Connection Server - Exited waitForConnection\n");
		}
	}
	
	public void waitForConnection() throws IOException {
		while (true) {
			Socket link;
			link = this.socket.accept();
			this.agent.newActiveUserSocket(link);
		}
	}

}
