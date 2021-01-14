package userInterface.GraphicalElements;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import userInterface.Interface;

public class MyFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor
	 * */
	public MyFrame() {
		super("ChatSystem");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            super.windowClosing(e); 
	            Interface.end();
	        }
	        @Override
	        public void windowOpened(WindowEvent e) {
	            super.windowOpened(e); 
	            //JOptionPane.showMessageDialog(null, "Bienvenue dans le ChatSystem");
	        }
	    });
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(contentPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setBounds(0,0,screenSize.width/2, screenSize.height/2);
	}
	
	/**
	 * Same method as setContentPane from JFrame but changes the title
	 * 
	 * @param pane The Panel that we want to set
	 * */
	public void setContentPane(Container pane) {
		this.setTitle(pane.getName());
		super.setContentPane(pane);
	}
}
