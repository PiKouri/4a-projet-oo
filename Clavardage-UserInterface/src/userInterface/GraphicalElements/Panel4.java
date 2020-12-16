package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import datatypes.Text;
import datatypes.Image;
import datatypes.MyFile;
import datatypes.Message;
import userInterface.Interface;

public class Panel4 extends MyPanel{
private static final long serialVersionUID = 1L;

	public static final Color myTextColor = Color.white;
	public static final Color destTextColor = Color.white;
	public static final Color myTextBackgroundColor = Color.blue;
	public static final Color destTextBackgroundColor = Color.gray;
	public static final float myTextAlignment = LEFT_ALIGNMENT;
	public static final float destTextAlignment = RIGHT_ALIGNMENT;
	
	public String destination;
	private String filename="";
	private JLabel conversationTitle;
	private JPanel messages;
	private JPanel temp;
	private JFrame imageFrame;
	private JTextField tf;
	public Boolean destDisconnected=false;
	
	private RunUpdate autoUpdate;

	
/*-----------------------Classes - Thread pour la mise à jour des messages-------------------------*/
	
	
	private class RunUpdate extends Thread {
		private String dest;
		public RunUpdate(String dest){this.dest=dest;}
		public void run(){
	    	System.out.println("Update Voir Messages");
	    	Interface.voirMessages(dest);
	    }
	}
	
	private class RunAjoutMessage extends Thread {
		private Message msg;
		private Panel4 panel;
		public RunAjoutMessage(Message msg, Panel4 panel){
			this.msg=msg;this.panel=panel;
		}
		public void run(){
	    	System.out.println("Update Ajout Message");
	    	panel.ajoutMessage(msg);
	    	panel.revalidate();
	    	panel.repaint();
	    }
	}

	
/*-----------------------Méthodes - Utilisées par Interface-------------------------*/
	
	
	public String construireMessageString(Message msg) {
		String date = msg.getDate();
		String thisMsgString="<html>"+date+" - <i>"+msg.getSender().getUsername()+"</i> : ";
		if (msg.isText()) thisMsgString += ((Text)msg).getText() + "</html>";
		else if (msg.isFile()) 
			thisMsgString += "<u>(Fichier)"+((MyFile)msg).getFilename() + "</u></html>";
		else if (msg.isImage())
			thisMsgString += "<u>(Image)"+((Image)msg).getFilename() + "</u></html>";
		return thisMsgString;
	}

	
	public void updateMessages(ArrayList<Message> listMsg) {
		messages.removeAll();
		if (!destDisconnected) {
			this.conversationTitle.setText("<html>Conversation avec <i>"+destination+
					"</i> (<span style=\"color:green\";>Connecté</span>)</html>");
		} else {
			this.conversationTitle.setText("<html>Conversation avec <i>"+destination+
					"</i> (<span style=\"color:red\";>Déconnecté</span>)</html>");
		}
		if (listMsg!=null)
			for (Message msg : listMsg) {
				ajoutMessage(msg);
			}
	}
	
	public void setDestination(String dest) {
		destination=dest;
		destDisconnected=false;
		this.autoUpdate=new RunUpdate(destination);
	}
	
	public void nouveauMessage(Message msg) {
		(new RunAjoutMessage(msg, this)).start();
	}
		
	public void update() {
		tf.setText("");
		if (Interface.isDisconnected) {
			this.setName("ChatSystem_Clavardage - "+Interface.me+" - Déconnecté");
			deconnecter.setVisible(false);
			changerPseudo.setVisible(false);
			reconnecter.setVisible(true);
		}else {
			this.setName("ChatSystem_Clavardage - "+Interface.me+" - Connecté");
			reconnecter.setVisible(false);
			changerPseudo.setVisible(true);
			deconnecter.setVisible(true);
		}
		this.autoUpdate=new RunUpdate(destination);
		autoUpdate.start();
	}
	

/*-----------------------Méthodes - Utilitaires-------------------------*/
	
	
	private void ajoutMessage(Message msg) {
		String thisMsgString=construireMessageString(msg);
		JLabel thisMsg;
		if (msg.isText()) {
			thisMsg=new JLabel(thisMsgString);
			thisMsg.setOpaque(true);
			if (msg.getSender().equals(Interface.me)) {
				thisMsg.setForeground(myTextColor);
				thisMsg.setBackground(myTextBackgroundColor);
				thisMsg.setAlignmentX(myTextAlignment);
			} else {
				thisMsg.setForeground(destTextColor);
				thisMsg.setBackground(destTextBackgroundColor);
				thisMsg.setAlignmentX(destTextAlignment);
			}
			messages.add(thisMsg);
		}else if (msg.isImage()) {
			thisMsg=new JLabel(thisMsgString);
			thisMsg.setOpaque(true);
			JLabel img = new JLabel(((Image)msg).getImage());
			/*img.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {enregisterImage(msg);}
			});*/
			thisMsg.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {enregisterImage(msg);}
	            public void mouseEntered(MouseEvent e) {
	            	int x =  MouseInfo.getPointerInfo().getLocation().x; //e.getX();
	            	int y =  MouseInfo.getPointerInfo().getLocation().y; //e.getY();
	            	imageFrame.getContentPane().removeAll();
	            	imageFrame.getContentPane().add(img);
	            	imageFrame.setLocation(x+10,y+10);
	            	imageFrame.pack();
	            	imageFrame.setVisible(true);
	            }
	            public void mouseExited(MouseEvent e) {
	            	imageFrame.setVisible(false);
	            }
			});
			if (msg.getSender().equals(Interface.me)) {
				thisMsg.setForeground(myTextColor);
				thisMsg.setBackground(myTextBackgroundColor);
				thisMsg.setAlignmentX(myTextAlignment);
				img.setAlignmentX(CENTER_ALIGNMENT);
			} else {
				thisMsg.setForeground(destTextColor);
				thisMsg.setBackground(destTextBackgroundColor);
				thisMsg.setAlignmentX(destTextAlignment);
				img.setAlignmentX(CENTER_ALIGNMENT);
			}
			messages.add(thisMsg);
			//messages.add(img);
		}else if (msg.isFile()) {
			thisMsg=new JLabel(thisMsgString);
			thisMsg.setOpaque(true);
			thisMsg.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {enregisterFichier(msg);}
			});
			if (msg.getSender().equals(Interface.me)) {
				thisMsg.setForeground(myTextColor);
				thisMsg.setBackground(myTextBackgroundColor);
				thisMsg.setAlignmentX(myTextAlignment);
			} else {
				thisMsg.setForeground(destTextColor);
				thisMsg.setBackground(destTextBackgroundColor);
				thisMsg.setAlignmentX(destTextAlignment);
			}
			messages.add(thisMsg);
		}
		messages.revalidate();
    	messages.repaint();
	}
	
	private void ouvrir() {
		JFileChooser jc = new JFileChooser();
    	jc.setDialogTitle("Ouvrir"); 
		int i = jc.showOpenDialog(this);
		if (i!=1) {
			String filename = jc.getSelectedFile().getAbsolutePath() ;
			System.out.println(filename);
			this.filename=filename;
			tf.setText(filename);
		}
	}
	
	private void enregisterFichier(Message msg) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Enregistrer sous");   
		fileChooser.setSelectedFile(new File(((MyFile)msg).getFilename()));
		int userSelection = fileChooser.showSaveDialog(Interface.mainWindow);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    try {
				Files.copy(((MyFile)msg).getFile().toPath(),fileToSave.toPath());
			} catch (IOException e1) {}
		}
	}
	
	private void enregisterImage(Message msg) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Enregistrer sous");   
		fileChooser.setSelectedFile(new File(((Image)msg).getFilename()));
		int userSelection = fileChooser.showSaveDialog(Interface.mainWindow);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    try {
		    	BufferedImage bi = (BufferedImage) (((Image)msg).getImage().getImage());
		    	String filename = ((Image)msg).getFilename();
		        File thisImage = new File(filename);
		        ImageIO.write(bi, "png", thisImage);
				Files.copy(thisImage.toPath(),fileToSave.toPath());
			} catch (IOException e1) {}
		}
	}
	
	private void send() throws IOException {
		Message msg=null;
		if (!(Interface.isDisconnected || destDisconnected)) {
			String text = tf.getText();
			if (filename!="") { // Send file
				// Check if it is a image
				if( ImageIO.read(new File(filename)) == null) {
					// Send File
					msg = (Message) new MyFile(Interface.me,filename);
					Interface.envoyerMessage(destination, msg);
				} else {
					// Send Image
					msg = (Message) new Image(Interface.me,filename);
					Interface.envoyerMessage(destination,msg); 
				}
			} else { // Send Text
				msg = (Message) new Text(Interface.me,text);
				Interface.envoyerMessage(destination,msg);
			}
			tf.setText("");
			filename="";
		} else {
			Interface.popUp("Utilisateur déconnecté, envoi impossible");
		}
		nouveauMessage(msg);
	}
	
	
/*-----------------------Constructeur-------------------------*/
	
	
	/**
	 * Create the panel.
	 */
	public Panel4() {
		super();
		// Nom de la fenetre
		this.setName("ChatSystem_Clavardage - Connecté");		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		
		// Ajout de la MenuBar
        this.add(BorderLayout.NORTH, getMenuBar());
        
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
        JLabel label = new JLabel("Entrer texte : ");
        this.tf = new JTextField(15);
        JButton send = new JButton("Envoyer");
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
        this.temp = new JPanel();
        this.conversationTitle=new JLabel();
        this.conversationTitle.setAlignmentX(CENTER_ALIGNMENT);
        this.messages=new JPanel();
        JScrollPane scrollPane=new JScrollPane(messages);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messages.setLayout(new BoxLayout(messages,BoxLayout.Y_AXIS));
        temp.setLayout(new BoxLayout(temp,BoxLayout.Y_AXIS));
        temp.add(BorderLayout.NORTH, conversationTitle);
        temp.add(BorderLayout.SOUTH, scrollPane);
        this.add(BorderLayout.CENTER, temp);
        
        
        // Scroll Bar
        /*JScrollBar scrollBar = new JScrollBar();
        this.add(scrollBar, BorderLayout.WEST);*/
        
        this.imageFrame = new JFrame("Aperçu de l'image");
        imageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        imageFrame.setContentPane(new JPanel());
        
        this.autoUpdate=null;
        
	}

}
