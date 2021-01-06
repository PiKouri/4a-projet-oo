package userInterface.GraphicalElements;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import userInterface.swingAction.SwingActionAccueil;
import userInterface.swingAction.SwingActionDeconnecter;
import userInterface.swingAction.SwingActionInfos;
import userInterface.swingAction.SwingActionModifierPseudo;
import userInterface.swingAction.SwingActionReconnecter;
import userInterface.swingAction.SwingActionVoirUtilisateurs;

public abstract class MyPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	// JMenuItems
	protected JMenuItem deconnecter;
	protected JMenuItem reconnecter;
	protected JMenuItem changerPseudo;
	// Actions
	protected Action actionModifierPseudo = new SwingActionModifierPseudo(this);
	protected Action actionVoirUtilisateurs = new SwingActionVoirUtilisateurs(this);
	protected Action actionAccueil = new SwingActionAccueil(this);
	protected Action actionDeconnecter = new SwingActionDeconnecter(this);
	protected Action actionReconnecter = new SwingActionReconnecter(this);
	protected Action actionInfos = new SwingActionInfos();
	
	/**
	 * Method used to create the default menu bar
	 * 
	 * @return The MenuBar
	 * */
	protected final JMenuBar getMenuBar() {
		
		// Menu bar
		JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("MyChat");
        JMenu m2 = new JMenu("Utilisateurs");
        JButton m3 = new JButton("Aide");
        m3.setAction(actionInfos);
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        
		//Contenu des items de la menu bar
        JMenuItem m11 = new JMenuItem("Modifier mon pseudo");
		m11.setAction(actionModifierPseudo);
		changerPseudo = m11;
		JMenuItem m12 = new JMenuItem("Me deconnecter");
		deconnecter = m12;
		m12.setAction(actionDeconnecter);
		JMenuItem m13 = new JMenuItem("Me reconnecter");
		reconnecter = m13;
		m13.setAction(actionReconnecter);
		m13.setVisible(false);
		m1.add(m11);
		m1.add(m12);
		m1.add(m13);
		
		JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
        m21.setAction(actionVoirUtilisateurs);
        m2.add(m21);
        return mb;
	}

	/**
	 * Method used by the Interface to update the Panel's informations
	 * */
	public abstract void update();
}
