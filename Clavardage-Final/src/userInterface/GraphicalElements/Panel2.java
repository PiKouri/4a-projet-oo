package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import userInterface.Interface;

public class Panel2 extends MyPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**Label to display "Welcome user..."*/
	public JLabel label;
	
	
/*-----------------------Méthodes - Utilisées par Interface-------------------------*/
	
	
	@Override
	public void update() {
		if (Interface.isDisconnected)
			this.setName("ChatSystem_Accueil - "+Interface.agent.getUsername()+" - Déconnecté");
		else 
			this.setName("ChatSystem_Accueil - "+Interface.agent.getUsername()+" - Connecté");
		if (Interface.isDisconnected) {
			deconnecter.setVisible(false);
			changerPseudo.setVisible(false);
			reconnecter.setVisible(true);
		}else {
			reconnecter.setVisible(false);
			changerPseudo.setVisible(true);
			deconnecter.setVisible(true);
		}
		label.setText("<html> Bonjour <i>"+Interface.agent.getUsername()+"</i> <br> Bienvenue sur ChatSystem </html>");
	}

	
/*-----------------------Constructeur-------------------------*/
	
	
	/**
	 * Create the panel.
	 */
	public Panel2() {
		super();
		this.setName("ChatSystem_Accueil_connecte");
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));

		// Ajout de la MenuBar
        this.add(BorderLayout.NORTH, getMenuBar());

		//Pannel du centre
		JPanel panel1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel1.getLayout();
		flowLayout.setVgap(100);
		//texte du centre
		JLabel label = new JLabel("<html> Bonjour "+""+" <br> Bienvenue sur ChatSystem </html>");
		this.label=label;
		panel1.add(label);
		this.add(BorderLayout.CENTER, panel1);

	}

}
