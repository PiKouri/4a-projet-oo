import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{ // a modifier sur le diagramme
	
	public boolean running;
	
	private ServerSocket socket;
	
	private Agent agent;
	
	public Server(Agent agent) throws IOException {
		this.agent = agent;
		System.out.println("UDP Server créé");
		this.socket = new ServerSocket(Agent.defaultPortNumber);
	}
	
	public void run() {
    	System.out.println("UDP Server créé");
		this.running=true;
		try {
			waitForConnection();
		} catch (IOException e) {
			System.out.printf("Error in waitForConnection\n");
			System.exit(-1);
		}
	}
	
	public void waitForConnection() throws IOException {
		while (this.running) {
			Socket link = this.socket.accept();
			this.agent.newActiveUserSocket(link);
		}
		this.socket.close();
	}

}
