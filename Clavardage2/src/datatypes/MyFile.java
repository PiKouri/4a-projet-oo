package datatypes;

import java.io.File;

import userInterface.User;

public class MyFile extends Message {

	private static final long serialVersionUID = 1L;
	
	private File file;
	private String filename;
	
	public MyFile(User u, String filename) {
		super(u);
		this.file = new File(filename);
		this.filename = this.file.getName();
	}
	
	public File getFile() {
		return this.file;
	}
	
	public String getFilename() {
		return this.filename;
	}

}
