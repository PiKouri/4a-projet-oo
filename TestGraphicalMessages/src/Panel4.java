import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.ComponentOrientation;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.CardLayout;

public class Panel4 extends JPanel {

	public static final Color myTextColor = Color.white;
	public static final Color destTextColor = Color.white;
	public static final Color myTextBackgroundColor = Color.blue;
	public static final Color destTextBackgroundColor = Color.gray;
	public static final float myTextAlignment = LEFT_ALIGNMENT;
	public static final float destTextAlignment = RIGHT_ALIGNMENT;
	private static final long serialVersionUID = 1L;

	protected JMenuItem deconnecter;
	protected JMenuItem reconnecter;
	protected JMenuItem changerPseudo;

	public String destination;
	private JLabel conversationTitle;
	private JPanel messages;
	private JPanel temp;
	private JFrame imageFrame;
	private JTextField tf;
	public Boolean destDisconnected=false;
	// Actions

	/**
	 * Method used to create the default menu bar
	 * 
	 * @return The MenuBar
	 * */
	protected final JMenuBar getMenuBar() {

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("MyChat");
		JMenu m2 = new JMenu("Utilisateurs");
		JMenuItem m3 = new JMenuItem("Aide");
		m3.setMaximumSize( m3.getPreferredSize() );
		mb.add(m1);
		mb.add(m2);
		mb.add(m3);

		//Contenu des items de la menu bar
		JMenuItem m11 = new JMenuItem("Modifier mon pseudo");
		changerPseudo = m11;
		JMenuItem m12 = new JMenuItem("Me deconnecter");
		deconnecter = m12;
		JMenuItem m13 = new JMenuItem("Me reconnecter");
		reconnecter = m13;
		m13.setVisible(false);
		m1.add(m11);
		m1.add(m12);
		m1.add(m13);

		JMenuItem m21 = new JMenuItem("Voir les utilisateurs de MyChat");
		m2.add(m21);
		return mb;
	}

	/**
	 * Create the panel.
	 */
	public Panel4() {
		super();
		// Nom de la fenetre
		this.setName("ChatSystem_Clavardage - Connecté");		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));

		// Ajout de la MenuBar
		this.add(BorderLayout.NORTH, getMenuBar());

		// Bouton home, pannel a droite
		JPanel panel1 = new JPanel();
		JButton home = new JButton("HOME");
		panel1.add(home);
		this.add(BorderLayout.LINE_END, panel1);

		// Pannel du bas, envoyer texte etc

		JPanel panel2 = new JPanel();
		JButton fileopen = new JButton("Parcourir");
		fileopen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {}
		});
		JLabel label = new JLabel("Entrer texte : ");
		this.tf = new JTextField(15);
		JButton send = new JButton("Envoyer");

		panel2.add(fileopen);
		panel2.add(label);
		panel2.add(tf);
		panel2.add(send);
		this.add(BorderLayout.SOUTH, panel2);

		//panel du centre avec historique des messages
		this.temp = new JPanel();
		this.conversationTitle=new JLabel();
		conversationTitle.setText("Coucou");
		this.conversationTitle.setAlignmentX(CENTER_ALIGNMENT);
		this.messages=new JPanel();
		messages.setMaximumSize(new Dimension(20, 20));
		messages.setPreferredSize(new Dimension(123,123));
		JScrollPane scrollPane=new JScrollPane(messages);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JLabel label_13 = new JLabel("");
		
		JLabel label_1 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_1.setOpaque(true);
		label_1.setForeground(Color.WHITE);
		label_1.setBackground(Color.BLUE);
		
		JLabel label_2 = new JLabel("123");
		label_2.setOpaque(true);
		label_2.setForeground(Color.WHITE);
		label_2.setBackground(Color.GRAY);
		label_2.setAlignmentX(1.0f);
		
		JLabel label_14 = new JLabel("");
		
		JLabel label_15 = new JLabel("");
		
		JLabel label_3 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_3.setOpaque(true);
		label_3.setForeground(Color.WHITE);
		label_3.setBackground(Color.BLUE);
		
		JLabel label_16 = new JLabel("");
		
		JLabel label_4 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_4.setOpaque(true);
		label_4.setForeground(Color.WHITE);
		label_4.setBackground(Color.BLUE);
		
		JLabel label_5 = new JLabel("123");
		label_5.setOpaque(true);
		label_5.setForeground(Color.WHITE);
		label_5.setBackground(Color.GRAY);
		label_5.setAlignmentX(1.0f);
		
		JLabel label_17 = new JLabel("");
		
		JLabel label_18 = new JLabel("");
		
		JLabel label_6 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk "
				+ "Mon message super long dfgoi dfkgo dkfgop kdfogkMon message super long "
				+ "dfgoi dfkgo dkfgop kdfogkMon message super long dfgoi dfkgo dkfgop kdfogk"
				+ "Mon message super long dfgoi dfkgo dkfgop kdfogk");
		label_6.setOpaque(true);
		label_6.setForeground(Color.WHITE);
		label_6.setBackground(Color.BLUE);
		
		JLabel label_19 = new JLabel("");
		
		JLabel label_7 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_7.setOpaque(true);
		label_7.setForeground(Color.WHITE);
		label_7.setBackground(Color.BLUE);
		
		JLabel label_8 = new JLabel("123");
		label_8.setOpaque(true);
		label_8.setForeground(Color.WHITE);
		label_8.setBackground(Color.GRAY);
		label_8.setAlignmentX(1.0f);
		
		JLabel label_20 = new JLabel("");
		
		JLabel label_21 = new JLabel("");
		
		JLabel label_9 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_9.setOpaque(true);
		label_9.setForeground(Color.WHITE);
		label_9.setBackground(Color.BLUE);
		
		JLabel label_22 = new JLabel("");
		
		JLabel label_10 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_10.setOpaque(true);
		label_10.setForeground(Color.WHITE);
		label_10.setBackground(Color.BLUE);
		
		JLabel label_11 = new JLabel("123");
		label_11.setOpaque(true);
		label_11.setForeground(Color.WHITE);
		label_11.setBackground(Color.GRAY);
		label_11.setAlignmentX(1.0f);
		
		JLabel label_23 = new JLabel("");
		
		JLabel label_24 = new JLabel("");
		
		JLabel label_12 = new JLabel("Mon message super long dfgoi dfkgo dkfgop kdfogk ");
		label_12.setOpaque(true);
		label_12.setForeground(Color.WHITE);
		label_12.setBackground(Color.BLUE);
		messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
		messages.add(label_13);
		messages.add(label_1);
		messages.add(label_2);
		messages.add(label_14);
		messages.add(label_15);
		messages.add(label_3);
		messages.add(label_16);
		messages.add(label_4);
		messages.add(label_5);
		messages.add(label_17);
		messages.add(label_18);
		messages.add(label_6);
		messages.add(label_19);
		messages.add(label_7);
		messages.add(label_8);
		messages.add(label_20);
		messages.add(label_21);
		messages.add(label_9);
		messages.add(label_22);
		messages.add(label_10);
		messages.add(label_11);
		messages.add(label_23);
		messages.add(label_24);
		messages.add(label_12);
		temp.setLayout(new BoxLayout(temp,BoxLayout.Y_AXIS));
		temp.add(BorderLayout.NORTH, conversationTitle);
		temp.add(BorderLayout.SOUTH, scrollPane);
		this.add(BorderLayout.CENTER, temp);

		// Scroll Bar
		/*JScrollBar scrollBar = new JScrollBar();
        this.add(scrollBar, BorderLayout.WEST);*/

		this.imageFrame = new JFrame("Aperçu de l'image");
		imageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		imageFrame.setContentPane(new JPanel());
	}
}
