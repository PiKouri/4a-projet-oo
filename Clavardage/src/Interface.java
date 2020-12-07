import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class Interface {

	private void testMyMap() {
		MyMap<User,String> test = new MyMap<User,String>();
		User u1 = new User("u11");
		User u2 = new User("u22");
		test.putUser(u1, "u11");
		test.putUser(u2, "u22");
		System.out.printf("%s\n",test.getValue(u1));
		System.out.println(test.containsUser(u1));
		System.out.println(test.containsValue("u11"));
		System.out.println(test.containsValue("u1"));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		/*testMyMap();*/
		//System.out.println(InetAddress.getByName("172.17.0.2").toString().split("/")[1]);
		boolean leave = false;
		while (!leave) {
			System.out.print("\nEnter your name: ");
			Scanner scanner = new Scanner(System.in);
			String name = scanner.next();
			User me = new User(name);
			Agent agent = new Agent(me);
			String in = "";
			while (!in.equals("disconnect")) {
				System.out.println("\n---------------Enter commands---------------\n");
				System.out.println("\nchangeUsername | printActiveUsers | printDisconnectedUsers | getOwnIP | disconnect | end\n");
				in = scanner.next();
				if (in.equals("changeUsername")) {
					System.out.print("\nChange username : ");
					String name2 = scanner.next();
					agent.chooseUsername(name2);
				/*} else if (in.equals("printActiveUsers")) {
					System.out.printf("\nActive Users : ");
					for (String activeName : agent.viewActiveUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
				} else if (in.equals("printDisconnectedUsers")) {
					System.out.printf("\nDisconnected Users : ");
					for (String activeName : agent.viewDisconnectedUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
				} else if (in.equals("getOwnIP")) {
					System.out.printf("\nMy IP is : %s", InetAddress.getLocalHost().getHostAddress());*/
				}else if (in.equals("print")) {
					System.out.printf("\nMy IP is : %s\n", InetAddress.getLocalHost().getHostAddress());
					System.out.printf("\nActive Users : ");
					for (String activeName : agent.viewActiveUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
					System.out.printf("\nDisconnected Users : ");
					for (String activeName : agent.viewDisconnectedUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
				} else if (in.equals("end")) {
					leave = true;
					break;
				}
			}
			agent.disconnect();
			try {Thread.sleep(100);} catch (Exception e) {} // Attente pour affichage
		}		
	}

}
