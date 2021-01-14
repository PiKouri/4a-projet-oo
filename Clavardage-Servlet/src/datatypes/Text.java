package datatypes;

public class Text extends Message {
	
	private static final long serialVersionUID = 1L;
	
	private String text;
	
	public Text(String text) {
		super();
		this.text=text;
	}
	
	/**
	 * This method returns the text of the message
	 * 
	 * @return Text of the message
	 */
	public String getText() {
		return this.text;
	}

}
