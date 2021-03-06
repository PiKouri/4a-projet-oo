package userInterface.swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import userInterface.Interface;
import userInterface.GraphicalElements.MyPanel;

public class SwingActionAccueil extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private MyPanel panel;
	public SwingActionAccueil(MyPanel panel) {
		this.panel=panel;
		putValue(NAME, "HOME");
		putValue(SHORT_DESCRIPTION, "Vers l'Accueil");
	}
	public void actionPerformed(ActionEvent e) {
		Interface.accueil(panel);
	}
}
