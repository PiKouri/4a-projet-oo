package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import agent.Agent;
import userInterface.Interface;

public class Panel3 extends MyPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel users;
	private RunUpdate autoUpdate;
	private MyPanel thisPanel;
	private Boolean isUpdating = false;
	
/*-----------------------Classes - Thread pour la mise à jour des utilisateurs-------------------------*/
	
	
	private class RunUpdate extends Thread {
		public RunUpdate(){}
	    public void run(){
	    	if (!isUpdating) {
	    		isUpdating=true;
	    		try {Thread.sleep(200);} catch (Exception e) {} // Attente pour affichage
		    	if (Agent.debug) System.out.println("Update List Users");
		    	Interface.voirUtilisateurs();
		    	isUpdating=false;
	    	}
	    }
	}
	
	
/*-----------------------Méthodes - Utilisées par Interface-------------------------*/
	
	
	public void updateActiveUsers(ArrayList<String> names) {
		users.removeAll();
		users.add(new JLabel("<html><span style=\"color:green\";>Active Users(+)</span></html>"));
		for (String name : names) {
			JButton bouton = new JButton("(+)"+name);
			bouton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
				Interface.ouvrirConversation(thisPanel, name, true);
			}});
			users.add(bouton);
		}
	}
	
	public void updateDisconnectedUsers(ArrayList<String> names) {
		users.add(new JLabel("<html><span style=\"color:red\";>Disconnected Users(-)</span></html>"));
		for (String name : names) {
			JButton bouton = new JButton("(-)"+name);
			bouton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
				Interface.ouvrirConversation(thisPanel, name, false);
			}});
			users.add(bouton);
		}
	}
	
	
/*-----------------------Constructeur-------------------------*/
	
	
	/**
	 * Create the panel.
	 */
	public Panel3() {
		super();
		this.thisPanel = this;
		this.setName("ChatSystem_Liste_users - Connecté");
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));

		// Ajout de la MenuBar
        this.add(BorderLayout.NORTH, getMenuBar());

		// Bouton home, pannel a droite
		this.users = new JPanel();
		JScrollPane scrollPane=new JScrollPane(users);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		users.setLayout(new BoxLayout(users,BoxLayout.Y_AXIS));
        this.add(BorderLayout.LINE_START, scrollPane);
		// Bouton home, pannel a droite
		JPanel panel1 = new JPanel();
		JButton home = new JButton("HOME");
		home.setAction(actionAccueil);
		panel1.add(home);
        this.add(BorderLayout.LINE_END, panel1);
        
        //this.autoUpdate = new RunAutoUpdate();

	}	
	
	public void update() {
		if (Interface.isDisconnected)
			this.setName("ChatSystem_Liste_users - "+Interface.me+" - Déconnecté");
		else 
			this.setName("ChatSystem_Liste_users - "+Interface.me+" - Connecté");
		if (Interface.isDisconnected) {
			deconnecter.setVisible(false);
			changerPseudo.setVisible(false);
			reconnecter.setVisible(true);
		}else {
			reconnecter.setVisible(false);
			changerPseudo.setVisible(true);
			deconnecter.setVisible(true);
		}
		this.autoUpdate=new RunUpdate();
		autoUpdate.start();
	}
}
