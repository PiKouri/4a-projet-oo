package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import agent.Agent;
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
	public static final int myTextAlignment = JLabel.RIGHT;
	public static final int destTextAlignment = JLabel.LEFT;
	
	private Boolean isUpdating = false;
	public String destination;
	private String filename="";
	private JLabel conversationTitle;
	private JScrollPane scrollPane;
	private JPanel messages;
	private JPanel temp;
	private JFrame imageFrame;
	private JTextField tf;
	public Boolean destDisconnected=false;
	private int widthForMessages;
	
	private RunUpdate autoUpdate;

	
/*-----------------------Classes - Thread pour la mise à jour des messages-------------------------*/
	
	
	private class RunUpdate extends Thread {
		private String dest;
		public RunUpdate(String dest){this.dest=dest;}
		public void run(){
			if (!isUpdating) {
				isUpdating=true;
				try {Thread.sleep(200);} catch (Exception e) {} // Attente pour affichage
				widthForMessages = scrollPane.getWidth()-50;
				Agent.printAndLog(String.format("Update Voir Messages\n"));
		    	Interface.voirMessages(dest);
		    	isUpdating=false;
			}
	    }
	}
	
	private class RunAjoutMessage extends Thread {
		private String username;
		private Message msg;
		private Panel4 panel;
		public RunAjoutMessage(String username, Message msg, Panel4 panel){
			this.msg=msg;this.panel=panel;this.username=username;
		}
		public void run(){
			try {Thread.sleep(200);} catch (Exception e) {} // Attente pour affichage
	        widthForMessages = scrollPane.getWidth()-50;
	        Agent.printAndLog(String.format("Update Ajout Message\n"));
	    	panel.ajoutMessage(username, msg);
	    	panel.revalidate();
	    	panel.repaint();
	    }
	}

	
/*-----------------------Méthodes - Utilisées par Interface-------------------------*/
	
	public String construireSimpleMessageString(String username, Message msg) {
		String date = msg.getDate();
		// Line wrap
		String thisMsgString="<html>"+date+" - <i>"+username+"</i> : ";
		if (msg.isText()) thisMsgString += ((Text)msg).getText() + "</html>";
		else if (msg.isFile()) 
			thisMsgString += "<u>(Fichier)"+((MyFile)msg).getFilename() + "</u></html>";
		else if (msg.isImage())
			thisMsgString += "<u>(Image)"+((Image)msg).getFilename() + "</u></html>";
		return thisMsgString;
	}
	
	public String construireMessageString(String username, Message msg) {
		String date = msg.getDate();
		// Line wrap
		String thisMsgString="<html><p style='width: "+widthForMessages+";'>"+date+" - <i>"+username+"</i> : ";
		if (msg.isText()) thisMsgString += ((Text)msg).getText() + "</p></html>";
		else if (msg.isFile()) 
			thisMsgString += "<u>(Fichier)"+((MyFile)msg).getFilename() + "</u></html>";
		else if (msg.isImage())
			thisMsgString += "<u>(Image)"+((Image)msg).getFilename() + "</u></html>";
		return thisMsgString;
	}

	
	public void updateMessages(String username, ArrayList<Message> listMsg) {
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
				ajoutMessage(username,msg);
			}
	}
	
	public void setDestination(String dest) {
		destination=dest;
		destDisconnected=false;
		this.autoUpdate=new RunUpdate(destination);
	}
	
	public void nouveauMessage(String username, Message msg) {
		(new RunAjoutMessage(username, msg, this)).start();
	}
		
	public void update() {
		tf.setText("");
		if (Interface.isDisconnected) {
			this.setName("ChatSystem_Clavardage - "+Interface.agent.getUsername()+" - Déconnecté");
			deconnecter.setVisible(false);
			changerPseudo.setVisible(false);
			reconnecter.setVisible(true);
		}else {
			this.setName("ChatSystem_Clavardage - "+Interface.agent.getUsername()+" - Connecté");
			reconnecter.setVisible(false);
			changerPseudo.setVisible(true);
			deconnecter.setVisible(true);
		}
		if (!(scrollPane.getWidth()<50))
			widthForMessages = scrollPane.getWidth()-50;
		this.autoUpdate=new RunUpdate(destination);
		autoUpdate.start();
	}
	

/*-----------------------Méthodes - Utilitaires-------------------------*/
	
	
	private void ajoutMessage(String username, Message msg) {
		String thisMsgString=construireMessageString(username, msg);
		JLabel thisMsg=new JLabel(thisMsgString);
		if (msg.isSending()) {
	    	//thisMsg.setHorizontalAlignment(myTextAlignment);
			thisMsg.setForeground(myTextColor);
			thisMsg.setBackground(myTextBackgroundColor);
		} else {
	    	//thisMsg.setHorizontalAlignment(destTextAlignment);
			thisMsg.setForeground(destTextColor);
			thisMsg.setBackground(destTextBackgroundColor);
		}
		thisMsg.setHorizontalAlignment(JLabel.LEFT);
		thisMsg.setAlignmentX(LEFT_ALIGNMENT);
    	//thisMsg.setPreferredSize(new java.awt.Dimension(10, 1000)); // Pour line wrap mais ne marche pas avec le scroll pane
		thisMsg.setOpaque(true);
		thisMsg.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		messages.add(thisMsg);
		
		if (msg.isText()); // Text
		else if (msg.isImage()) { // Image
			ImageIcon myImg = ((Image)msg).getImage();
			if (myImg != null) {
				JLabel img = new JLabel(myImg);
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
				img.setAlignmentX(CENTER_ALIGNMENT);
			}
		}else if (msg.isFile()) { // File
			thisMsg.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {enregisterFichier(msg);}
			});
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
			Agent.printAndLog("File opened: "+String.format(filename)+"\n");
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
				//Files.copy(((MyFile)msg).getFile().toPath(),fileToSave.toPath());
				InputStream in = new BufferedInputStream(new FileInputStream(((MyFile)msg).getFile()));
	    		OutputStream out = new BufferedOutputStream(new FileOutputStream(fileToSave));
		    	byte[] buffer = new byte[1024];
		    	int lengthRead;
		    	while ((lengthRead = in.read(buffer)) > 0) {
		    		out.write(buffer, 0, lengthRead);
		    		out.flush();
		    	}
		    	in.close();
		    	out.close();
				Agent.printAndLog("File saved: "+String.format(fileToSave.getAbsolutePath())+"\n");
			} catch (IOException e1) {
				StringWriter sw = new StringWriter();
				e1.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				Agent.printAndLog(
						String.format("File could not be saved to %s\n%s\n",
								fileToSave.toPath(),
								exceptionAsString));
				Interface.popUp("Le fichier n'a pas pu être enregistré");
			}
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
		    	//BufferedImage bi = (BufferedImage) (((Image)msg).getImage().getImage());
		    	// Create a buffered image with transparency
		    	java.awt.Image img = (((Image)msg).getImage().getImage());
		        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		        // Draw the image on to the buffered image
		        Graphics2D bGr = bi.createGraphics();
		        bGr.drawImage(img, 0, 0, null);
		        bGr.dispose();
		        
		    	String filename = ((Image)msg).getFilename();
		        File thisImage = new File(filename);
		        ImageIO.write(bi, "png", thisImage);
				Files.copy(thisImage.toPath(),fileToSave.toPath());
				Agent.printAndLog("Image saved: "+String.format(fileToSave.getAbsolutePath())+"\n");
			} catch (IOException e1) {
				StringWriter sw = new StringWriter();
				e1.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				Agent.printAndLog(
						String.format("Image could not be saved : error - %s\n",exceptionAsString));
				Interface.popUp("L'image n'a pas pu être enregistrée");
			}
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
					msg = (Message) new MyFile(filename);
					Agent.printAndLog(String.format("Sending file %s to %s\n",filename,destination));
					Interface.envoyerMessage(destination, msg);
				} else {
					// Send Image
					msg = (Message) new Image(filename);
					Agent.printAndLog(String.format("Sending image %s to %s\n",filename,destination));
					Interface.envoyerMessage(destination,msg); 
				}
			} else { // Send Text
				msg = (Message) new Text(text);
				Agent.printAndLog(String.format("Sending text %s to %s\n",text,destination));
				Interface.envoyerMessage(destination,msg);
			}
			tf.setText("");
			filename="";
		} else {
			Interface.popUp("Utilisateur déconnecté, envoi impossible");
		}
		nouveauMessage(Interface.agent.getUsername(), msg);
	}
	
	// Auto Update when window resized
	private class PanelListen implements ComponentListener{
        public void componentHidden(ComponentEvent arg0) {}
        public void componentMoved(ComponentEvent arg0) {}
        public void componentResized(ComponentEvent arg0) {
        	update();
        }
        public void componentShown(ComponentEvent arg0) {}
    }
	
	
/*-----------------------Constructeur-------------------------*/
	
	
	/**
	 * Create the panel.
	 */
	public Panel4() {
		super();
		// Nom de la fenetre
		this.addComponentListener(new PanelListen());
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
				revalidate();
				repaint();
			} catch (IOException e1) {}}
		});
        
        tf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {try {
				send();
				revalidate();
				repaint();
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
        this.scrollPane=new JScrollPane(messages);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(messages);
        scrollPane.setAutoscrolls(true);
        messages.setLayout(new BoxLayout(messages,BoxLayout.Y_AXIS));
		
        temp.setLayout(new BoxLayout(temp,BoxLayout.Y_AXIS));
        temp.add(BorderLayout.NORTH, conversationTitle);
        temp.add(BorderLayout.SOUTH, scrollPane);
        this.add(BorderLayout.CENTER, temp);
        
        
        // Scroll Bar
        /*JScrollBar scrollBar = new JScrollBar();
        this.add(scrollBar, BorderLayout.WEST);*/
        
        this.imageFrame = new JFrame("Aperçu de l'image");
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                imageFrame.setVisible(false);
            }
        };
        imageFrame.addWindowListener(exitListener);
        imageFrame.setContentPane(new JPanel());
        widthForMessages = 0;
        this.autoUpdate=null;        
        
	}

}
