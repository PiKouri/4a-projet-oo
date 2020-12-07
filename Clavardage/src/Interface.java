import java.io.IOException;
import java.util.Scanner;

public class Interface {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.print("Enter your name: ");
		Scanner scanner = new Scanner(System.in);
		String name = scanner.next();
		User me = new User(name);
		Agent agent = new Agent(me);
		//agent.disconnect();
		//scanner.close();
	}

}
