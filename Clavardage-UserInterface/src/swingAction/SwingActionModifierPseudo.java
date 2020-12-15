package swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import userInterface.Interface;
import userInterface.MyPanel;

public class SwingActionModifierPseudo extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private MyPanel panel;
	public SwingActionModifierPseudo(MyPanel panel) {
		this.panel=panel;
		putValue(NAME, "Modifier mon pseudo...");
		putValue(SHORT_DESCRIPTION, "Some short description");
	}
	public void actionPerformed(ActionEvent e) {
		Interface.modifierPseudo(panel);
	}
}
