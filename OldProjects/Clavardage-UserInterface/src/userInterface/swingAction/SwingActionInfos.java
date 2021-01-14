package userInterface.swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import userInterface.Interface;

public class SwingActionInfos extends AbstractAction {
	private static final long serialVersionUID = 1L;
	public SwingActionInfos() {
		putValue(NAME, "Aide");
		putValue(SHORT_DESCRIPTION, "Afficher des informations sur l'interface");
	}
	public void actionPerformed(ActionEvent e) {
		Interface.afficherInformationsInterface();
	}
}
