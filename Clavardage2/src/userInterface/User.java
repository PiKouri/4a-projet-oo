package userInterface;
import java.net.InetAddress;

public class User {
	private String username;
	private InetAddress address = null;
	
	public User(String username) { // A modifier sur le diagramme
		this.username = username;
	}
	
	public User(String username, InetAddress address) { // A modifier sur le diagramme
		this.username = username;
		this.address = address;
	}
	
	public void changeUsername(String name) {
		this.username = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public InetAddress getAddress() { // A modifier sur le diagramme
		return this.address;
	}
}
