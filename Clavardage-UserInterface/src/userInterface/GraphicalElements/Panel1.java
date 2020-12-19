package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import userInterface.Interface;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.Component;

public class Panel1 extends MyPanel{
	private static final long serialVersionUID = 1L;
	private JLabel oldUsername;
	private JLabel label;
	private JTextField tf;
	private boolean isChecking=false;

	
/*-----------------------Classes - Thread pour la vérification du pseudo-------------------------*/
	
	
	public class RunChooseUsername extends Thread {
		private String name;
		public RunChooseUsername(String name){this.name=name;}
	    public void run(){
	    	if (!isChecking) {
	    		isChecking=true;
	    		//Interface.me.changeUsername(name);
	    		Interface.agent.chooseUsername(name);
	    		isChecking=false;
	    	}
	    }
	}
	

/*-----------------------Méthodes - Utilisées par Interface-------------------------*/
	
	
	@Override
	public void update() {}
	
	/**
	 * This method displays the old username of the user
	 * 
	 * @param name old username of the user
	 * */
	public void displayOldUsername(String name) {
		oldUsername.setText("<html>Votre ancien pseudo était : <i>"+name+"</i></html>");
	}

	/**
	 * This method clear the informations
	 * */
	public void emptyInfo() {
		oldUsername.setText("");
		label.setText("Note: Pas d'espace dans le nom");
	}

	
/*-----------------------Méthodes - Utilitaires-------------------------*/
	
	/**
	 * In another Thread : read the name from the TextField and uses agent.chooseUsername()
	 * */
	private void readName() {
		emptyInfo();
		String name=tf.getText();
		if (name.contains(" ")) {
			tf.setText("");
			Interface.notifyUsernameContainsSpaces();
		} else if (!name.equals("")) {
			label.setText("Pseudo en cours de vérification");
			(new RunChooseUsername(name)).start();
		}
	}
	
	
/*-----------------------Constructeur-------------------------*/
	
	
	/**
	 * Create the panel.
	 */
	public Panel1() {
		super();
		setBounds(100, 100, 450, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		
		this.setName("ChatSystem_Accueil_choix_pseudo");

		// Création pannel central
		JPanel panel1 = new JPanel();
		panel1.setAlignmentY(CENTER_ALIGNMENT);
		
		JTextField tf = new JTextField(15);
		this.tf=tf;
		tf.setHorizontalAlignment(SwingConstants.CENTER);

		JButton valider = new JButton("Valider");
		valider.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {readName();}});
		tf.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {readName();}});
		
		JLabel label_3 = new JLabel("Entrez votre pseudo");
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setVerticalAlignment(SwingConstants.CENTER);
		FlowLayout fl_panel1 = new FlowLayout(FlowLayout.CENTER, 0, 50);
		fl_panel1.setAlignOnBaseline(true);
		panel1.setLayout(fl_panel1);
		panel1.add(label_3);
		panel1.add(tf);
		panel1.add(valider);

		this.add(BorderLayout.CENTER, panel1);
		
		// Création pannel sud : informations
		JPanel panel2 = new JPanel();
		panel2.setBorder(new EmptyBorder(20, 20, 20, 20));
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
		panel2.setAlignmentX(CENTER_ALIGNMENT);
		this.oldUsername=new JLabel("");
		oldUsername.setHorizontalAlignment(SwingConstants.CENTER);
		this.label=new JLabel("Note: Pas d'espace dans le nom");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		oldUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel2.add(this.label);
		panel2.add(this.oldUsername);
		
		this.add(BorderLayout.SOUTH, panel2);


	}

	

}
