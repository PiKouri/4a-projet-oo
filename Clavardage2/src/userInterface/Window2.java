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

public class Window2 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window2 frame = new Window2();
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
	public Window2() {
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
				JMenuItem m12 = new JMenuItem("Me déconnecter");
				m1.add(m11);
				m1.add(m12);
				        
				JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
				m2.add(m21);
				contentPane.add(BorderLayout.NORTH, mb);
				
				//Pannel du centre
				JPanel panel1 = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panel1.getLayout();
				flowLayout.setVgap(100);
				//texte du centre
				JLabel label = new JLabel("Bonjour 'pseudo' \n Bienvenue sur ChatSystem");
				panel1.add(label);
				contentPane.add(BorderLayout.CENTER, panel1);
				
				
	}

}
