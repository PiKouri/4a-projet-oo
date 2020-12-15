package userInterface;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import datatypes.Text;
import datatypes.Image;
import datatypes.MyFile;
import datatypes.Message;
import swingAction.SwingActionAccueil;
import swingAction.SwingActionDeconnecter;
import swingAction.SwingActionModifierPseudo;
import swingAction.SwingActionReconnecter;
import swingAction.SwingActionVoirUtilisateurs;

public class Panel4 extends MyPanel{
private static final long serialVersionUID = 1L;
	
	private final Action actionModifierPseudo = new SwingActionModifierPseudo(this);
	private final Action actionVoirUtilisateurs = new SwingActionVoirUtilisateurs(this);
	private final Action actionAccueil = new SwingActionAccueil(this);
	private final Action actionDeconnecter = new SwingActionDeconnecter(this);
	private final Action actionReconnecter = new SwingActionReconnecter(this);
	public JMenuItem deconnecter;
	public JMenuItem reconnecter;
	public JMenuItem changerPseudo;
	public String destination;
	private String filename="";
	private JPanel messages;
	private JTextField tf;
	public Boolean destDisconnected=false;
	
	private RunAutoUpdate autoUpdate;

	public class RunAutoUpdate extends Thread {
		private boolean running=true;
		private String dest;
		public RunAutoUpdate(String dest){this.dest=dest;}
		public void interrupt() {this.running=false;}
		public void run(){
	    	while (running) {
		    	System.out.println("AutoUpdate Voir Messages");
		    	Interface.voirMessages(dest);
		    	try {Thread.sleep(Interface.autoUpdateTime);} catch (Exception e) {}
	    	}
	    }
	}
	
	public void updateMessages(ArrayList<Message> listMsg) {
		messages.removeAll();
		if (!destDisconnected)
			messages.add(new JLabel("<html>Conversation avec <i>"+destination+
				"</i> (<span style=\"color:green\";>Connecté</span>)</html>"));
		else 
			messages.add(new JLabel("<html>Conversation avec <i>"+destination+
				"</i> (<span style=\"color:red\";>Déconnecté</span>)</html>"));
		if (listMsg!=null)
			for (Message msg : listMsg) {
				String date = msg.getDate();
				String thisMsgString="<html>"+date+" - <i>"+destination+"</i> : ";
				JLabel thisMsg;
				if (msg.isText()) {
					thisMsgString += ((Text)msg).getText() + "</html>";
					thisMsg=new JLabel(thisMsgString);
					messages.add(thisMsg);
				}else if (msg.isImage()) {
					thisMsgString += "</html>";
					thisMsg=new JLabel(thisMsgString);
					messages.add(thisMsg);
					JLabel img = new JLabel(((Image)msg).getImage());
					messages.add(img);
				}else if (msg.isFile()) {
					thisMsgString += ((MyFile)msg).getFilename() + "</html>";
					thisMsg=new JLabel(thisMsgString);
					messages.add(thisMsg);
				}
			}
	}

	private void ouvrir() {
		JFileChooser jc = new JFileChooser();
		int i = jc.showOpenDialog(this);
		if (i!=1) {
			String filename = jc.getSelectedFile().getAbsolutePath() ;
			System.out.println(filename);
			this.filename=filename;
			tf.setText(filename);
		}
	}
	//TODO Can't send message if disconnected (print disconnected)
	private void send() throws IOException {
		if (!(Interface.isDisconnected || destDisconnected)) {
			String text = tf.getText();
			if (text.equals(filename)) { // Send file
				// Check if it is a image
				if( ImageIO.read(new File(filename)) == null) 
					// Send File
					Interface.envoyerMessage(destination, (Message) new MyFile(Interface.me,filename));
				else 
					// Send Image
					Interface.envoyerMessage(destination, (Message) new Image(Interface.me,filename)); 
			} else { // Send Text
				Interface.envoyerMessage(destination,(Message) new Text(Interface.me,text));
			}
			tf.setText("");
		} else {
			Interface.popUp("Utilisateur déconnecté, envoi impossible");
		}
	}
	
	/**
	 * Create the panel.
	 */
	public Panel4() {
		super();
		// Nom de la fenetre
		this.setName("ChatSystem_Clavardage - Connecté");		
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
        JPanel panel1 = new JPanel();
        JButton home = new JButton("HOME");
        home.setAction(actionAccueil);
        panel1.add(home);
        this.add(BorderLayout.LINE_END, panel1);
        
        // Pannel du bas, envoyer texte etc
        
        JPanel panel2 = new JPanel();
        JButton fileopen = new JButton("Parcourir");
        fileopen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {ouvrir();}
		});
        JLabel label = new JLabel("Text : ");
        this.tf = new JTextField(15);
        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {try {
				send();
			} catch (IOException e1) {}}
		});
        
        tf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {try {
				send();
			} catch (IOException e1) {}}
		});
        
        panel2.add(fileopen);
        panel2.add(label);
        panel2.add(tf);
        panel2.add(send);
        this.add(BorderLayout.SOUTH, panel2);
        
        //panel du centre avec historique des messages
        JPanel panel3= new JPanel();
        this.messages=panel3;
        this.add(BorderLayout.CENTER, panel3);
        
        // Scroll Bar
        JScrollBar scrollBar = new JScrollBar();
        this.add(scrollBar, BorderLayout.WEST);
        
        this.autoUpdate=null;
        
	}
	
	public void setDestination(String dest) {
		destination=dest;
		destDisconnected=false;
		autoUpdate=new RunAutoUpdate(dest);
	}
	
	public void autoUpdateLists() {
		this.autoUpdate=new RunAutoUpdate(destination);
		autoUpdate.start();
	}
	
	public void stopAutoUpdateLists() {
		System.out.println("StopAutoUpdate Voir Messages");
		autoUpdate.interrupt();
	}
	
	@Override
	public void disconnect() {
		this.setName("ChatSystem_Clavardage - Déconnecté");
		deconnecter.setVisible(false);
		changerPseudo.setVisible(false);
		reconnecter.setVisible(true);
		tf.setText("");
	}
	
	public void update() {
		this.setName("ChatSystem_Clavardage - Connecté");
		tf.setText("");
		reconnecter.setVisible(false);
		changerPseudo.setVisible(true);
		deconnecter.setVisible(true);
	}
	
	public void customSetVisible(Boolean bool) {
		if (!bool) stopAutoUpdateLists();
		else autoUpdateLists();
	}
}
