package swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import userInterface.Interface;
import userInterface.MyPanel;

public class SwingActionVoirUtilisateurs extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private MyPanel panel;
	
	public SwingActionVoirUtilisateurs(MyPanel panel) {
		this.panel=panel;
		putValue(NAME, "Voir les utilisateurs de MyChat");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}
	public void actionPerformed(ActionEvent e) {
		Interface.ouvrirVoirUtilisateurs(panel);
	}
}
