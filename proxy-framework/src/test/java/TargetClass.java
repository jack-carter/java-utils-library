
class TargetClass implements ITargetClass {
	
	private String text;
	
	public TargetClass(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String toString() {
		return getText(); 
	}

}