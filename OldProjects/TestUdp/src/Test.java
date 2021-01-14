import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws IOException {
		String message = "";
		BroadcastClient client = new BroadcastClient();
		Scanner scanner = new Scanner(System.in);
		while (!message.equals("end")) {
			message = scanner.next();
			//client.sendBroadcast(message);
			client.sendUDP(message, InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 168, (byte) 56, (byte) 101}));
		}
		//scanner.close();
	}
}
