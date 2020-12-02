import java.io.IOException;

public class Interface {

	public static void main(String[] args) throws IOException {
		User me = new User("JR");
		Agent agent = new Agent(me);
	}

}
