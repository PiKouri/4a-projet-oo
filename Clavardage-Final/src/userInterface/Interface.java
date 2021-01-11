package userInterface;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.IOException;

import javax.swing.JOptionPane;

import agent.Agent;
import datatypes.Message;
import userInterface.GraphicalElements.MyFrame;
import userInterface.GraphicalElements.MyPanel;
import userInterface.GraphicalElements.Panel1;
import userInterface.GraphicalElements.Panel2;
import userInterface.GraphicalElements.Panel3;
import userInterface.GraphicalElements.Panel4;

public class Interface {

	public static int autoUpdateTime = 2000;
	
	public static PropertiesReader propertiesReader = new PropertiesReader();
	
	public static Agent agent;
		
	public static MyFrame mainWindow;
	public static Panel1 panel1;	
	public static Panel2 panel2;
	public static Panel3 panel3;
	public static Panel4 panel4;
	
	public static boolean isDisconnected = true;
	
	
/**
 * Informations sur l'interface
 * 
 * Dans une conversation, les fichiers et images sont cliquables => Enregistrer Sous
 * Les images disposent d'un aperçu en mettant la souris sur le message 
 * 
 * 
 * */
	
	

/*-----------------------Méthodes - Mise à jour de notre MyFrame-------------------------*/
	
	
	private static class RunAutoUpdate extends Thread {
		private boolean running=true;
		public RunAutoUpdate(){}
		public void interrupt() {this.running=false;}
	    public void run(){
	    	while (running) {
		    	try {Thread.sleep(Interface.autoUpdateTime/10);} catch (Exception e) {}
		    	mainWindow.revalidate();
		    	mainWindow.repaint();
		    	mainWindow.setTitle(mainWindow.getContentPane().getName());
		    	mainWindow.getContentPane().revalidate();
		    	mainWindow.getContentPane().repaint();
	    	}
	    }
	}
	
	private static void switchPanel(MyPanel from, MyPanel toOpen) {
		mainWindow.setContentPane(toOpen);
	}
	
	public static void popUp(String message) { // Quand quelqu'un change de pseudo par exemple
		final class MyThread extends Thread {
			public MyThread() {}
			public void run() {
				JOptionPane.showMessageDialog(Interface.mainWindow, message);
				}
		}
		(new MyThread()).start();
	}
	
	
/*-----------------------Méthodes - Notifications depuis l'Agent-------------------------*/
	
	public static void notifyAllOK() {
		mainWindow.setContentPane(panel1);
		mainWindow.setVisible(true);
		(new RunAutoUpdate()).start();
	}
	
	public static void notifyOldUsername(String name) {
		panel1.emptyInfo();
		panel1.displayOldUsername(name);
	}
	
	public static void notifyUsernameContainsSpaces() {
		panel1.emptyInfo();
		Interface.popUp("<html> <b>Votre pseudo contient un/des espace(s)</b> </html>");
	}
	
	public static void notifyUsernameContainsSpecialCharacters() {
		panel1.emptyInfo();
		Interface.popUp("<html> <b>Votre pseudo contient un/des caractere(s) specia(ux)l</b> </html>");
	}
	
	public static void notifyUsernameNotAvailable(String name) {
		panel1.emptyInfo();
		Interface.popUp("<html> Le pseudo <i>"+name+"</i> est déjà  utilisé  <br> Veuillez en choisir un autre SVP </html>");
	}
	
	public static void notifyUsernameAvailable() {
		isDisconnected=false;
		panel2.update();
		switchPanel(panel1, panel2);
	}
	
	public static void notifyUsernameChanged(String oldUsername, String newUsername) {
		popUp("<html>L'utilisateur <i>"+oldUsername+"</i> a changé de pseudo -> <i>"+newUsername+"</i></html>");
		if (mainWindow.getContentPane().equals(panel4) && panel4.destination.equals(oldUsername)) {
			panel4.setDestination(newUsername);
			panel4.update();
		} else if (mainWindow.getContentPane().equals(panel3)) {
			panel3.update();
		}
	}
	
	public static void notifyUserDisconnected(String name) {
		if (mainWindow.getContentPane().equals(panel4) && panel4.destination.equals(name)) {
			panel4.destDisconnected=true;
			panel4.update();
		} 
		else if (mainWindow.getContentPane().equals(panel3)) {
			panel3.update();
		}
	}
	
	public static void notifyUserReconnected(String name) {
		if (mainWindow.getContentPane().equals(panel4) && panel4.destination.equals(name)) {
			panel4.destDisconnected=false;
			panel4.update();
		} else if (mainWindow.getContentPane().equals(panel3)) {
			panel3.update();
		}
	}
	
	public static void notifyNewMessage(String username, Message msg) {
		if (mainWindow.getContentPane().equals(panel4) && panel4.destination.equals(username)) {
			panel4.nouveauMessage(username,msg);
		} else {
			String notif=
					"<html>L'utilisateur <i>"+username+"</i> vous a envoyé : "+
			"<p>"+panel4.construireMessageString(username,msg)+"</p></html>";
			popUp(notif);
		}
	}
	
	
/*-----------------------Méthodes - Boutons des menus bar-------------------------*/
	
	
	
	public static void afficherInformationsInterface() {
		popUp("<html><h>Informations sur l'interface<br>"+
				" Dans une conversation, les <u>fichiers</u> et <u>images</u> sont cliquables => Enregistrer Sous<br>" + 
				" Les images disposent d'un aperçu en mettant la souris sur le message </html>");
	}
	
	public static void modifierPseudo(MyPanel from) {
		panel1.emptyInfo();
		panel1.displayOldUsername(agent.getUsername());
		switchPanel(from, panel1);
	}
	
	public static void ouvrirVoirUtilisateurs(MyPanel from) {
		switchPanel(from, panel3);
		panel3.update();
	}
	
	public static void deconnecter(MyPanel from) {
		isDisconnected=true;
		panel4.destDisconnected=true;
		agent.disconnect();
		from.update();
	}
	
	public static void reconnecter(MyPanel from) {
		panel1.emptyInfo();
		panel1.displayOldUsername(agent.getUsername());
		switchPanel(from, panel1);
		isDisconnected=false;
		try {agent.reconnect();} catch (IOException e) {}
		from.update();
	}	
	
	
/*-----------------------Méthodes - Autres boutons-------------------------*/
	
	
	public static void accueil(MyPanel from) {
		switchPanel(from, panel2);
		panel2.update();
	}
	
	public static void ouvrirConversation(MyPanel from, String username, boolean isActive) {
		panel4.setDestination(username);
		panel4.destDisconnected=!isActive;
		switchPanel(from, panel4);
		panel4.update();
	}
	
	public static void envoyerMessage(String dest, Message message) {
		agent.sendMessage(dest, message);
	}
	
	public static void end() {
		agent.disconnect();
	}
	
	
/*-----------------------Méthodes - Méthodes utilitaires-------------------------*/
	
	
	public static void voirUtilisateurs() {
		panel3.updateActiveUsers(agent.viewActiveUsernames());
		panel3.updateDisconnectedUsers(agent.viewDisconnectedUsernames());
	}
	
	public static void voirMessages(String dest) {
		if (dest != null) panel4.updateMessages(dest, agent.getMessageHistory(dest));
	}		
	
	
/*-----------------------Méthodes - Main = Lancement de l'Interface-------------------------*/
	
	
	public static void main(String[] args) throws IOException, InterruptedException, HeadlessException, AWTException {
		propertiesReader.getProperties();
		panel1=new Panel1();
		panel2=new Panel2();
		panel3=new Panel3();
		panel4=new Panel4();
		mainWindow=new MyFrame();
		agent = new Agent();
	}

}
