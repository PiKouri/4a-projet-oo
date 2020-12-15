package userInterface;

import javax.swing.JPanel;

public abstract class MyPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	public abstract void customSetVisible(Boolean bool);
	public abstract void disconnect();
}
