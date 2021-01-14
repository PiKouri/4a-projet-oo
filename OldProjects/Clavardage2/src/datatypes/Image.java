package datatypes;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import userInterface.User;

public class Image extends Message {

	private static final long serialVersionUID = 1L;
	
	private ImageIcon image;
	
	public Image(User sender, String filename) {
		super(sender);
		try {this.image=new ImageIcon(ImageIO.read(new File(filename)));} catch (IOException e) {}
	}
	
	/**
	 * This method returns the image of the message
	 * 
	 * @return Image of the message
	 */
	public ImageIcon getImage() {
		return this.image;
	}

}
