import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Window4 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window4 frame = new Window4();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the frame.
	 */
	public Window4() {
		
		// Nom de la fenetre
		super("ChatSystem_Clavardage");
	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 315);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// Menu bar
		JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("MyChat");
        JMenu m2 = new JMenu("Utilisateurs");
        mb.add(m1);
        mb.add(m2);
        
        //Contenu des items de la menu bar
        JMenuItem m11 = new JMenuItem("Modifier mon pseudo");
        JMenuItem m12 = new JMenuItem("Me déconnecter");
        m1.add(m11);
        m1.add(m12);
        
        JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
        m2.add(m21);
        contentPane.add(BorderLayout.NORTH, mb);
        
        // Bouton home, pannel a droite
        JPanel panel1 = new JPanel();
        JButton home = new JButton("HOME");
        panel1.add(home);
        contentPane.add(BorderLayout.LINE_END, panel1);
        
        // Pannel du bas, envoyer texte etc
        
        JPanel panel2 = new JPanel();
        JButton filesend = new JButton("File...");
        JLabel label = new JLabel("Text : ");
        JTextField tf = new JTextField(15);
        JButton send = new JButton("Send");
        panel2.add(filesend);
        panel2.add(label);
        panel2.add(tf);
        panel2.add(send);
        contentPane.add(BorderLayout.SOUTH, panel2);
        
        
        
        //panel du centre avec historique des messages
        JPanel panel3= new JPanel();
        contentPane.add(BorderLayout.CENTER, panel3);
        
        // Scroll Bar
        JScrollBar scrollBar = new JScrollBar();
        contentPane.add(scrollBar, BorderLayout.WEST);
  
        
        
	}

}
