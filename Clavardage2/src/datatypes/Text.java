package datatypes;

public class Text extends Message {
	
	private static final long serialVersionUID = 1L;
	
	private String text;
	
	public Text(String s) {
		super();
		this.text=s;
	}
	
	public String getText() {
		return this.text;
	}

}
