package userInterface;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import swingAction.SwingActionAccueil;
import swingAction.SwingActionDeconnecter;
import swingAction.SwingActionModifierPseudo;
import swingAction.SwingActionReconnecter;
import swingAction.SwingActionVoirUtilisateurs;

public class Panel3 extends MyPanel{
	private static final long serialVersionUID = 1L;
	
	private final Action actionModifierPseudo;
	private final Action actionVoirUtilisateurs;
	private final Action actionAccueil;
	private final Action actionDeconnecter;
	private final Action actionReconnecter;
	private JMenuItem deconnecter;
	private JMenuItem reconnecter;
	private JMenuItem changerPseudo;
	private JPanel users;
	private RunAutoUpdate autoUpdate;
	private MyPanel thisPanel;

	public class RunAutoUpdate extends Thread {
		private boolean running=true;
		public RunAutoUpdate(){}
		public void interrupt() {this.running=false;}
	    public void run(){
	    	while (running) {
		    	System.out.println("AutoUpdate List Users");
		    	Interface.voirUtilisateurs();
		    	try {Thread.sleep(Interface.autoUpdateTime);} catch (Exception e) {}
	    	}
	    }
	}
	
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
	
	/**
	 * Create the panel.
	 */
	public Panel3() {
		actionModifierPseudo = new SwingActionModifierPseudo(this);
		actionVoirUtilisateurs = new SwingActionVoirUtilisateurs(this);
		actionAccueil = new SwingActionAccueil(this);
		actionDeconnecter = new SwingActionDeconnecter(this);
		actionReconnecter = new SwingActionReconnecter(this);
		this.thisPanel = this;
		this.setName("ChatSystem_Liste_users - Connecté");
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("MyChat");
		JMenu m2 = new JMenu("Utilisateurs");
		mb.add(m1);
		mb.add(m2);

		//Contenu des items de la menu bar
		JMenuItem m11 = new JMenuItem("Modifier mon pseudo");
		m11.setAction(actionModifierPseudo);
		this.changerPseudo = m11;
		JMenuItem m12 = new JMenuItem("Me deconnecter");
		this.deconnecter = m12;
		m12.setAction(actionDeconnecter);
		JMenuItem m13 = new JMenuItem("Me reconnecter");
		this.reconnecter = m13;
		m13.setAction(actionReconnecter);
		m13.setVisible(false);
		m1.add(m11);
		m1.add(m12);
		m1.add(m13);

		JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
		m21.setAction(actionVoirUtilisateurs);
		m2.add(m21);

		this.add(BorderLayout.NORTH, mb);

		// Bouton home, pannel a droite
		this.users = new JPanel();
		users.setLayout(new BoxLayout(users,BoxLayout.Y_AXIS));
        this.add(BorderLayout.LINE_START, users);
		
		// Bouton home, pannel a droite
		JPanel panel1 = new JPanel();
		JButton home = new JButton("HOME");
		home.setAction(actionAccueil);
		panel1.add(home);
        this.add(BorderLayout.LINE_END, panel1);
        
        this.autoUpdate = new RunAutoUpdate();

	}	
	
	@Override
	public void disconnect() {
		this.setName("ChatSystem_Liste_users - Déconnecté");
		deconnecter.setVisible(false);
		changerPseudo.setVisible(false);
		reconnecter.setVisible(true);
	}
	
	public void autoUpdateLists() {
		this.autoUpdate=new RunAutoUpdate();
		autoUpdate.start();
	}
	
	public void stopAutoUpdateLists() {
		System.out.println("StopAutoUpdate List Users");
		autoUpdate.interrupt();
	}
	
	public void update() {
		this.setName("ChatSystem_Liste_users - Connecté");
		reconnecter.setVisible(false);
		changerPseudo.setVisible(true);
		deconnecter.setVisible(true);
	}
	
	public void customSetVisible(Boolean bool) {
		if (!bool) stopAutoUpdateLists();
		else autoUpdateLists();
	}
}
