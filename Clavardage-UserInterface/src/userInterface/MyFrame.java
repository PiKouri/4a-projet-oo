package userInterface;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MyFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public void setContentPanel1(JPanel panel) {
		setContentPane(panel);
	}
	
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
	            JOptionPane.showMessageDialog(null, "Bienvenue dans le ChatSystem");
	        }
	    });
		JPanel contentPane = new JPanel();
		setBounds(100, 100, 450, 300);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(contentPane);
	}
	
	public void setContentPane(Container pane) {
		this.setTitle(pane.getName());
		super.setContentPane(pane);
	}
}
