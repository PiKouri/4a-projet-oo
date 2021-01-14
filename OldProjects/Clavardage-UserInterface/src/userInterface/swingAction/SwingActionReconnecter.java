package userInterface.swingAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import userInterface.Interface;
import userInterface.GraphicalElements.MyPanel;

public class SwingActionReconnecter extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private MyPanel panel;
	public class RunReconnecter extends Thread {
		private MyPanel panel;
		public RunReconnecter(MyPanel panel){this.panel=panel;}
	    public void run(){
	    	Interface.reconnecter(panel);
	    }
	}
	
	public SwingActionReconnecter(MyPanel panel) {
		this.panel=panel;
		putValue(NAME, "Reconnecter");
		putValue(SHORT_DESCRIPTION, "Me reconnecter");
	}
	public void actionPerformed(ActionEvent e) {
		(new RunReconnecter(panel)).start();
	}
}
