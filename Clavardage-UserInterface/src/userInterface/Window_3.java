package userInterface;
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
import javax.swing.AbstractAction;
import javax.swing.Action;

public class Window_3 extends JFrame {

	private JPanel contentPane;
	private final Action action_modifier_pseudo = new SwingAction_modifier_pseudo(this);
	private final Action action_voir_utilisateurs = new SwingAction_voir_utilisateurs(this);
	private final Action action_home = new SwingAction_home(this);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window_3 frame = new Window_3();
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
	public Window_3() {
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
		m11.setAction(action_modifier_pseudo);
		JMenuItem m12 = new JMenuItem("Me déconnecter");
		m1.add(m11);
		m1.add(m12);
		        
		JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
		m21.setAction(action_voir_utilisateurs);
		m2.add(m21);
		contentPane.add(BorderLayout.NORTH, mb);
		        
		// Bouton home, pannel a droite
		JPanel panel1 = new JPanel();
        JButton home = new JButton("HOME");
        home.setAction(action_home);
        panel1.add(home);
        contentPane.add(BorderLayout.LINE_END, panel1);
        
        
        
        
        
		
	}

	private class SwingAction_modifier_pseudo extends AbstractAction {
		private Window_3 window;
		public SwingAction_modifier_pseudo(Window_3 w) {
			window=w;
			putValue(NAME, "Modifier mon pseudo...");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			Window_1 w1=new Window_1();
			w1.setVisible(true);
			window.setVisible(false);
		}
	}
	
	
	private class SwingAction_voir_utilisateurs extends AbstractAction {
		private Window_3 window;
		public SwingAction_voir_utilisateurs(Window_3 w) {
			window=w;
			putValue(NAME, "Voir les utilisateurs de MyChat");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			Window_3 w3=new Window_3();
			w3.setVisible(true);
			window.setVisible(false);
		}
	}
	
	
	private class SwingAction_home extends AbstractAction {
		private Window_3 window;
		public SwingAction_home(Window_3 w) {
			window=w;
			putValue(NAME, "HOME");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			Window_2 w2= new Window_2();
			w2.setVisible(true);
			window.setVisible(false);
		}
	}
	
	
}
