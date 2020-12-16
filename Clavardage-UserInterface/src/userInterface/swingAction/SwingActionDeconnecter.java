package userInterface.swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import userInterface.Interface;
import userInterface.GraphicalElements.MyPanel;

public class SwingActionDeconnecter extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private MyPanel panel;
	public class RunDeconnecter extends Thread {
		private MyPanel panel;
		public RunDeconnecter(MyPanel panel){this.panel=panel;}
	    public void run(){
	    	Interface.deconnecter(panel);
	    }
	}
	public SwingActionDeconnecter(MyPanel panel) {
		this.panel=panel;
		putValue(NAME, "Deconnecter");
		putValue(SHORT_DESCRIPTION, "Me d�connecter");
	}
	public void actionPerformed(ActionEvent e) {
		(new RunDeconnecter(panel)).start();
	}
}
