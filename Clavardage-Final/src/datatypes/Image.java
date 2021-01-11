package datatypes;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Image extends Message {

	private static final long serialVersionUID = 1L;
	
	private String filename;
	private String filepath;
	
	public Image(String filepath) {
		super();
		this.filename = (new File(filepath)).getName();
		this.filepath=filepath;
	}
	
	/**
	 * This method returns the image of the message
	 * 
	 * @return Image of the message
	 */
	public ImageIcon getImage() {
		try {return new ImageIcon(ImageIO.read(new File(filepath)));} catch (IOException e) {}
		return null;
	}
	
	/**
	 * This method returns the filename of the Image
	 *  
	 * @return Filename of the Image
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * This method returns the filepath of the MyFile
	 *  
	 * @return Filepath of the File
	 */
	public String getFilepath() {
		return this.filepath;
	}
	
	/**
	 * This method modifies the filepath of the MyFile
	 *  
	 * @param filepath New filepath of the File
	 */
	public void setFilepath(String filepath) {
		this.filepath=filepath;
	}

}
