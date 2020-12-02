
public class User {
	private String username;
	
	public User(String username) { // A modifier sur le diagramme
		this.username = username;
	}
	
	public void changeUsername(String name) {
		this.username = name;
	}
	
	public String getUsername() {
		return username;
	}
}
