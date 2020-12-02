import java.util.Scanner;

public class Test {
	public static void main(String[] args) {
		String message = "";
		BroadcastClient client = new BroadcastClient();
		Scanner scanner = new Scanner(System.in);
		
		while (!message.equals("end")) {
			message = scanner.next();
			client.sendBroadcast(message);
		}
		scanner.close();
	}
}
