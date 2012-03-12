package astrochart.shared.wrappers;


public class TextPosition {
	private final String text;
	private final double x;
	private final double y;
	
	public TextPosition(final String text, final double x, final double y) {
		this.text = text;
		this.x = x;
		this.y = y;
	}
	
	public final String getText() {
	    return text;
    }

	public final double getX() {
	    return x;
    }

	public final double getY() {
	    return y;
    }	
}
