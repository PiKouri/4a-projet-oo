package userInterface;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import agent.Agent;
import datatypes.MyMap;
import datatypes.Text;

public class Interface {

	/*private void testMyMap() {
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
	
	private void testVerifyUniqAddress() throws UnknownHostException {
		ArrayList<User> test = new ArrayList<User>();
		User us = new User("1",InetAddress.getLocalHost());
		test.add(us);
		if (Agent.verifyUniqAddress(test,us.getAddress()))test.add(us);
		User us2 = new User("2",InetAddress.getLocalHost());
		User us3 = new User("3",InetAddress.getByName("192.168.1.2"));
		if (Agent.verifyUniqAddress(test,us2.getAddress()))test.add(us2);
		if (Agent.verifyUniqAddress(test,us3.getAddress()))test.add(us3);
		for (User u : test) System.out.println(u.getUsername());
	}*/
	
	public static String enterName() {
		System.out.print("Enter name: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String name = scanner.next();
		return name;
	}
	
	public static String enterText() {
		System.out.print("Enter text: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String text = scanner.next();
		return text;
	}
	
	public static void commandsDebugging() throws IOException {
		//testMyMap();
		//testVerifyUniqAddress();
		//System.out.println(InetAddress.getByName("172.17.0.2").toString().split("/")[1]);
		boolean leave = false;

		System.out.print("Enter your name: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String name = scanner.next();
		User me = new User(name); // Initialisation
		Agent agent = new Agent(me);
		
		while (!leave) {
			try {Thread.sleep(100);} catch (Exception e) {} // Attente pour affichage
			String in = "";
			while (!in.equals("disconnect")) {
				System.out.println("\n---------------Enter commands---------------\n");
				System.out.println("\nchangeUsername | printAll | send | disconnect | reconnect | end\n");
				in = scanner.next();
				if (in.equals("changeUsername")) {
					System.out.print("\nChange username : ");
					String name2 = scanner.next();
					agent.chooseUsername(name2);
				}else if (in.equals("printAll")) {
					System.out.printf("\nMy IP is : %s | My Name is : %s\n", InetAddress.getLocalHost().getHostAddress(),me.getUsername());
					System.out.printf("\nActive Users : ");
					for (String activeName : agent.viewActiveUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
					System.out.printf("\nDisconnected Users : ");
					for (String activeName : agent.viewDisconnectedUsernames()) {
						System.out.printf("%s | ",activeName);
					} System.out.println();
				}else if (in.equals("send")) {
					String dest = enterName();
					String text = enterText();
					agent.sendMessage(dest, new Text(text));
				} else if (in.equals("end")) {
					leave = true;
					break;
				} else if (in.equals("reconnect")) {
					System.out.printf("\n\nReconnection\n\n");
					agent.reconnect();
				}
			}
			agent.disconnect();
			try {Thread.sleep(100);} catch (Exception e) {} // Attente pour affichage
			if (leave) break;
		}		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		/*Map<InetAddress,User> test=new HashMap<>();
		User me = new User("Moi");
		InetAddress test1 = InetAddress.getByName("192.168.1.1");
		InetAddress test2 = InetAddress.getByName("192.168.1.1");
		System.out.printf("%s, %s\n", test1,test2);
		test.put(test1,me);
		User res = test.get(test2);
		System.out.printf("%s\n",res.getUsername());*/
		commandsDebugging();
	}

}
