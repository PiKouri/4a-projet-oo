package userInterface;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import swingAction.SwingActionDeconnecter;
import swingAction.SwingActionModifierPseudo;
import swingAction.SwingActionReconnecter;
import swingAction.SwingActionVoirUtilisateurs;

public class Panel2 extends MyPanel {
	
	private static final long serialVersionUID = 1L;
	private final Action actionModifierPseudo = new SwingActionModifierPseudo(this);
	private final Action actionVoirUtilisateurs = new SwingActionVoirUtilisateurs(this);
	private final Action actionDeconnecter = new SwingActionDeconnecter(this);
	private final Action actionReconnecter = new SwingActionReconnecter(this);
	public JLabel label;
	public String name = "ChatSystem_Accueil_choix_pseudo";
	public JMenuItem deconnecter;
	public JMenuItem reconnecter;
	public JMenuItem changerPseudo;
	
	/**
	 * Create the panel.
	 */
	public Panel2() {
		super();
		this.setName("ChatSystem_Accueil_connecte");
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("MyChat");
		JMenu m2 = new JMenu("Utilisateurs");
		mb.add(m1);
		mb.add(m2);

		//Contenu des items de la menu bar
		JMenuItem m11 = new JMenuItem("Modifier mon pseudo");
		m11.setAction(actionModifierPseudo);
		this.changerPseudo = m11;
		JMenuItem m12 = new JMenuItem("Me deconnecter");
		this.deconnecter = m12;
		m12.setAction(actionDeconnecter);
		JMenuItem m13 = new JMenuItem("Me reconnecter");
		this.reconnecter = m13;
		m13.setAction(actionReconnecter);
		m13.setVisible(false);
		
		m1.add(m11);
		m1.add(m12);
		m1.add(m13);

		JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
		m21.setAction(actionVoirUtilisateurs);
		m2.add(m21);
		this.add(BorderLayout.NORTH, mb);

		//Pannel du centre
		JPanel panel1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel1.getLayout();
		flowLayout.setVgap(100);
		//texte du centre
		JLabel label = new JLabel("<html> Bonjour "+Interface.me.getUsername()+" <br> Bienvenue sur ChatSystem </html>");
		this.label=label;
		panel1.add(label);
		this.add(BorderLayout.CENTER, panel1);

	}
	
	@Override
	public void disconnect() {
		this.setName("ChatSystem_Accueil - Déconnecté");
		deconnecter.setVisible(false);
		changerPseudo.setVisible(false);
		reconnecter.setVisible(true);
	}
	
	public void update() {
		this.setName("ChatSystem_Accueil - Connecté");
		reconnecter.setVisible(false);
		changerPseudo.setVisible(true);
		deconnecter.setVisible(true);
		label.setText("<html> Bonjour <i>"+Interface.me.getUsername()+"</i> <br> Bienvenue sur ChatSystem </html>");
	}

	@Override
	public void customSetVisible(Boolean bool) {}
}
