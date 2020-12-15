package userInterface;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Panel1 extends MyPanel{
	private static final long serialVersionUID = 1L;
	private JLabel oldUsername=new JLabel("");
	private JLabel label=new JLabel("");
	private JTextField tf;

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
	 * Create the panel.
	 */
	public Panel1() {
		super();
		setBounds(100, 100, 450, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		
		this.setName("ChatSystem_Accueil_choix_pseudo");

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

		this.add(BorderLayout.CENTER, panel1);
		this.add(BorderLayout.SOUTH, panel2);


	}

	public void errorMessage(String name) {
		tf.setText("");
		Interface.popUp("<html> Le pseudo <i>"+name+"</i> est déjà  utilisé  <br> Veuillez en choisir un autre SVP </html>");
	}
	
	public void displayOldUsername(String name) {
		oldUsername.setText("<html>Votre ancien pseudo était : <i>"+name+"</i></html>");
	}

	public void emptyInfo() {
		oldUsername.setText("");
		label.setText("");
	}
	
	public void setVisible(Boolean bool) {
		if (bool) System.out.println("Coucou");
		else System.out.println("Coucou2");
		super.setVisible(bool);
	}

	@Override
	public void disconnect() {}

	@Override
	public void customSetVisible(Boolean bool) {}
}
