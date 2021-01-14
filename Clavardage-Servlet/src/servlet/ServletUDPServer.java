package servlet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Deprecated
public class ServletUDPServer extends Thread {

	/*-----------------------Attributs - UDP broadcast-------------------------*/

	/**DatagramSocket associated to this UDPServer*/
	private DatagramSocket socket;
	/**True if the UDPServer is running*/
	protected boolean running;
	/**Reception buffer*/
	private byte[] buf = new byte[256];
	private MyServlet servlet;

	/*-----------------------Attributs - Agent-------------------------*/
	

	/**List of all own local addresses*/
	private List<String> listAllOwnLocalAddresses = null;


	/*-----------------------Méthodes - Gestion des connexions UDP entrantes-------------------------*/


	/**
	 * Constructor for the class UDPServer
	 * <p> This class manages the incoming UDP messages
	 * 
	 * @param agent The agent that created this UDPServer
	 */
	protected ServletUDPServer(MyServlet servlet) throws SocketException {
		MyServlet.printAndLog(String.format("UDP Server created\n"));
		this.servlet=servlet;
		this.socket = new DatagramSocket(MyServlet.broadcastPortNumber);
		try {
			listAllOwnLocalAddresses = listAllOwnLocalAddresses();
		} catch (SocketException e) {
			MyServlet.errorMessage("ERROR when trying to list own local addresses\n", e);
		}
		this.start();
	}

	/**
	 * This method lists all own local addresses
	 * 
	 * @return List of all own local addresses
	 * */
	private List<String> listAllOwnLocalAddresses() throws SocketException {
		List<String> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces 
		= NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
		    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
		    while (addresses.hasMoreElements())
		    {
		        addressList.add(MyServlet.addressToString((InetAddress) addresses.nextElement()));
		    }
		}
		return addressList;
	}

	@Override
	public void interrupt() {
		MyServlet.printAndLog(String.format("UDP Server interrupted\n"));
		this.running = false;
		socket.close();
	}

	@Override
	public void run(){
		this.running = true;

		try {while (this.running) {
			buf = new byte[256];
			DatagramPacket packet 
			= new DatagramPacket(buf, buf.length);

			socket.receive(packet);


			InetAddress address = packet.getAddress();
			// On ignore nos propres messages broadcast
			if (listAllOwnLocalAddresses.contains(MyServlet.addressToString(address))) continue;  

			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			String received 
			= new String(packet.getData(), 0, packet.getLength());
			received = received.replaceAll("\0\0", "");
			received = received.replaceAll("(\0)$", "");
			MyServlet.printAndLog(String.format("UDP Server - %s:%d said : %s.\n", address.toString(), port , received)); 

			String[] strip = received.split(" ");

			String action = strip[0];
			String username ="";
			if (strip.length>1) username = strip[1];

			switch (action) {

			case "connect" :
				this.servlet.connect(MyServlet.addressToString(address),username);
				this.servlet.internTellDisconnectedUsers(address);
				break;
			case "disconnect" :
				this.servlet.disconnect(MyServlet.addressToString(address));
				break;
			case "changeUsername" :
				String newUsername = strip[2];
				this.servlet.changeUsername(MyServlet.addressToString(address), newUsername);
				break;            
			case "checkUsernameAvailability" : 
				this.servlet.tellUsernameAvailability(username, address);
				break;
			default :
				System.out.printf("ERROR reading packet \n %s \n", received);
			}
			//System.out.printf("Local IP of this packet was: %s.\n",getOutboundAddress(packet.getSocketAddress()).getHostAddress());
		}} catch (IOException e) {}
	}
	
	public List<String> getAllOwnLocalAddresses(){
    	return listAllOwnLocalAddresses;
    }
}