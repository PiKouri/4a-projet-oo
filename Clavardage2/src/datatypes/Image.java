package datatypes;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Image extends Message {

	private static final long serialVersionUID = 1L;
	
	private ImageIcon image;
	
	public Image(String filename) {
		super();
		try {
			this.image=new ImageIcon(ImageIO.read(new File(filename)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ImageIcon getImage() {
		return this.image;
	}

}
