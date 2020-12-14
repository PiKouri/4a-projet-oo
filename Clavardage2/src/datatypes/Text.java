package datatypes;

import userInterface.User;

public class Text extends Message {
	
	private static final long serialVersionUID = 1L;
	
	private String text;
	
	public Text(User u, String s) {
		super(u);
		this.text=s;
	}
	
	public String getText() {
		return this.text;
	}

}
