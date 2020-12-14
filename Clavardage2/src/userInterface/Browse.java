package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import agent.Agent;
import datatypes.Image;

public class Browse extends JFrame implements ActionListener {
	private Agent agent;
	private String dest;
	public Browse(Agent agent,String dest){
		this.agent=agent;
		this.dest=dest;
		JButton bouton = new JButton("Browse");
		bouton.addActionListener(this);
		this.getContentPane().add(bouton);
		this.setVisible(true);
		}
	public void ouvrir() {
		JFileChooser jc = new JFileChooser();
		int i = jc.showOpenDialog(this);
		if (i!=1) {
			String filename = jc.getSelectedFile().getAbsolutePath() ;
			System.out.println(filename);
			agent.sendMessage(dest,new Image(filename));
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().contentEquals("Browse")) ouvrir();
	}
}
