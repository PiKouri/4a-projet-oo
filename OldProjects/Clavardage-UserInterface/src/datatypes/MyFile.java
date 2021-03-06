package datatypes;

import java.io.File;

import userInterface.User;

public class MyFile extends Message {

	private static final long serialVersionUID = 1L;
	
	private File file;
	private String filename;
	
	public MyFile(User sender, String filename) {
		super(sender);
		this.file = new File(filename);
		this.filename = this.file.getName();
	}
	
	/**
	 * This method returns the file of the message
	 * 
	 * @return File of the message
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * This method returns the filename of the File
	 *  
	 * @return Filename of the File
	 */
	public String getFilename() {
		return this.filename;
	}

}
