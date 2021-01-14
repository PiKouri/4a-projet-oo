package datatypes;

import java.io.File;

public class MyFile extends Message {

	private static final long serialVersionUID = 1L;
	
	private String filename;
	private String filepath;
	
	public MyFile(String filepath) {
		super();
		this.filepath=filepath;
		this.filename = (new File(filepath)).getName();
	}
	
	/**
	 * This method returns the file of the message
	 * 
	 * @return File of the message
	 */
	public File getFile() {
		return new File(filepath);
	}
	
	/**
	 * This method returns the filename of the MyFile
	 *  
	 * @return Filename of the File
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
