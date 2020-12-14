package datatypes;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import userInterface.User;

public class Image extends Message {

	private static final long serialVersionUID = 1L;
	
	private ImageIcon image;
	
	public Image(User u, String filename) {
		super(u);
		try {this.image=new ImageIcon(ImageIO.read(new File(filename)));} catch (IOException e) {}
	}
	
	public ImageIcon getImage() {
		return this.image;
	}

}
