package userInterface;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class Window_2 extends JFrame {

	private JPanel contentPane;
	private final Action action_modifier_pseudo = new SwingAction_modifier_pseudo(this);
	private final Action action_voir_utilisateurs = new SwingAction_voir_utilisateurs(this);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window_2 frame = new Window_2();
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
	public Window_2() {
		//Nom de la fenetre
				super("ChatSystem_Accueil_connecte");
				
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
				
				//Pannel du centre
				JPanel panel1 = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panel1.getLayout();
				flowLayout.setVgap(100);
				//texte du centre
				JLabel label = new JLabel("<html> Bonjour 'pseudo' <br> Bienvenue sur ChatSystem </html>");
				panel1.add(label);
				contentPane.add(BorderLayout.CENTER, panel1);
				
				
	}

	private class SwingAction_modifier_pseudo extends AbstractAction {
		private Window_2 window;
		public SwingAction_modifier_pseudo(Window_2 w) {
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
		private Window_2 window;
		public SwingAction_voir_utilisateurs(Window_2 w) {
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
}
