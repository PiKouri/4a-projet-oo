import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Window3 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window3 frame = new Window3();
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
	public Window3() {
		super("ChatSystem_Liste_users");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
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
        home.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        panel1.add(home);
        contentPane.add(BorderLayout.LINE_END, panel1);
        
        
        
        
        
		
	}

}
