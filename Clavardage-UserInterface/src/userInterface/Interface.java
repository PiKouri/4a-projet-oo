package userInterface;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import agent.Agent;
import datatypes.Message;

public class Interface {

	public static int autoUpdateTime = 2000;
	
	public static Agent agent;
	public static User me=new User("");
		
	public static MyFrame mainWindow;
	public static Panel1 panel1;	
	public static Panel2 panel2;
	public static Panel3 panel3;
	public static Panel4 panel4;
	
	public static boolean isDisconnected = true;
	
	private static class RunAutoUpdate extends Thread {
		private boolean running=true;
		public RunAutoUpdate(){}
		public void interrupt() {this.running=false;}
	    public void run(){
	    	while (running) {
		    	try {Thread.sleep(Interface.autoUpdateTime/10);} catch (Exception e) {}
		    	mainWindow.revalidate();
		    	mainWindow.repaint();
	    	}
	    }
	}
	
	public static void switchPanel(MyPanel from, MyPanel toOpen) {
		from.customSetVisible(false);
		toOpen.customSetVisible(true);
		mainWindow.setContentPane(toOpen);
	}
	
	public static void popUp(String message) { // Quand quelqu'un change de pseudo par exemple
		JOptionPane.showMessageDialog(null, message);
	}
	
	
	
	
	
	public static void notifyUsernameNotAvailable(String name) {
		panel1.errorMessage(name);
	}
	
	public static void notifyUsernameAvailable() {
		isDisconnected=false;
		panel2.update();
		switchPanel(panel1, panel2);
	}
	
	public static void notifyUsernameChanged(String oldUsername, String newUsername) {
		popUp("<html>L'utilisateur <i>"+oldUsername+"</i> a changé de pseudo -> <i>"+newUsername+"</i></html>");
		if (panel4.destination.equals(oldUsername))panel4.setDestination(newUsername);
	}
	
	public static void notifyUserDisconnected(String name) {
		if (panel4.destination.equals(name))panel4.destDisconnected=true;
	}
	
	public static void notifyUserReconnected(String name) {
		if (panel4.destination.equals(name))panel4.destDisconnected=false;
	}
	
	public static void displayOldUsername() {
		panel1.displayOldUsername(me.getUsername());
	}
	
	
	
	
	
	public static void modifierPseudo(MyPanel from) {
		panel1.emptyInfo();
		displayOldUsername();
		switchPanel(from, panel1);
	}
	
	public static void ouvrirVoirUtilisateurs(MyPanel from) {
		panel3.update();
		voirUtilisateurs();
		switchPanel(from, panel3);
	}
	
	public static void voirUtilisateurs() {
		//TODO
		ArrayList<String> test = new ArrayList<String>();
		test.add("Test1");
		test.add("Test2");
		panel3.updateActiveUsers(test);
		ArrayList<String> test2 = new ArrayList<String>();
		test2.add("Test3");
		test2.add("Test4");
		panel3.updateDisconnectedUsers(test2);
		//panel3.updateActiveUsers(agent.viewActiveUsernames());
		//panel3.updateDisconnectedUsers(agent.viewDisconnectedUsernames());
		
	}
	
	public static void accueil(MyPanel from) {
		panel2.update();
		switchPanel(from, panel2);
	}
	
	public static void ouvrirConversation(MyPanel from, String username, boolean isActive) {
		panel4.setDestination(username);
		panel4.update();
		panel4.destDisconnected=!isActive;
		switchPanel(from, panel4);
	}
	
	public static void voirMessages(String dest) {
		if (dest != null) panel4.updateMessages(agent.getMessageHistory(dest));
	}
	
	
	
	
	
	public static void envoyerMessage(String dest, Message message) {
		agent.sendMessage(dest, message);
	}
	
	public static void deconnecter(MyPanel from) {
		from.disconnect();
		try {agent.disconnect();} catch (IOException e) {}
		switchPanel(from,from);
		isDisconnected=true;
		panel4.destDisconnected=true;
	}
	
	public static void reconnecter(MyPanel from) {
		panel1.emptyInfo();
		displayOldUsername();
		switchPanel(from, panel1);
		try {agent.reconnect();} catch (IOException e) {}
		panel2.update();
		isDisconnected=false;
	}
	
	public static void end() {
		try {agent.disconnect();} catch (IOException e) {}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, HeadlessException, AWTException {
		me = new User("");
		agent = new Agent(me);
		
		//popUp("<html>L'utilisateur <i>"+"Test1"+"</i> a changé de pseudo : <i>"+"Test2"+"</i></html>");
		
		/*window1=new Window_1();
		window2=new Window_2();
		window3=new Window_3();
		window4=new Window_4();
		window1.setVisible(true);
		window2.setVisible(false);
		window3.setVisible(false);
		window4.setVisible(false);*/
		
		panel1=new Panel1();
		panel2=new Panel2();
		panel3=new Panel3();
		panel4=new Panel4();
		mainWindow=new MyFrame();
		mainWindow.setContentPane(panel1);
		mainWindow.setVisible(true);
		(new RunAutoUpdate()).start();
	}

}
