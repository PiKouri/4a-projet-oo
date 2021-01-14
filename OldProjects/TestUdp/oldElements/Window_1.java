package userInterface;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;

public class Window_1 extends JFrame {

	private JLabel oldUsername=new JLabel("");
	private JLabel label=new JLabel("");
	private JTextField tf;

	private JPanel contentPane;

	public class RunChooseUsername extends Thread {
		private String name;
		public RunChooseUsername(String name){this.name=name;}
	    public void run(){
	    	Interface.agent.chooseUsername(name);
	    }
	}
	
	private void readName() {
		emptyInfo();
		label.setText("Pseudo en cours de vérification");
		String name=tf.getText();
		(new RunChooseUsername(name)).start();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window_1 frame = new Window_1();
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
	public Window_1() {
		super("ChatSystem_Accueil_choix_pseudo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// CrÃ©ation pannel central
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();

		panel2.add(this.label);
		panel2.add(this.oldUsername);


		JLabel label = new JLabel("Entrez votre pseudo");
		panel1.add(label);

		JTextField tf = new JTextField(15);
		this.tf=tf;
		panel1.add(tf);

		JButton valider = new JButton("Valider");
		valider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {readName();}
		});
		tf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {readName();}
		});

		panel1.add(valider);

		contentPane.add(BorderLayout.CENTER, panel1);
		contentPane.add(BorderLayout.SOUTH, panel2);


	}

	public void errorMessage(String name) {
		tf.setText("");
		label.setText("<html> Le pseudo <i>"+name+"</i> est déjà  utilisé  <br> Veuillez en choisir un autre SVP </html>");
	}
	
	public void displayOldUsername(String name) {
		oldUsername.setText("<html>Votre ancien pseudo était : <i>"+name+"</i></html>");
	}

	public void emptyInfo() {
		oldUsername.setText("");
		label.setText("");
	}
	
}
