package astrochart.shared;

public enum Planet {
	Sun(       3, '\u2609', true), 
	Moon(      4, '\u263e', true), 
	Node(     13, '\u260A', false), //ascending
	SouthNode(14, '\u260B', false), //descending
	Mercury(   5, '\u263f', true),
	Venus(     6, '\u2640', true), 
	Mars(      7, '\u2642', true),
	Jupiter(   8, '\u2643', true),
	Saturn(    9, '\u2644', true),
	Uranus(   10, '\u2645', true),
	Neptune(  11, '\u2646', true), 
	Pluto(    12, '\u2647', true);

	private int token;
	private char unicode;
	private boolean isBody;
	
	private Planet(final int token, final char unicode, final boolean isBody) {
		this.token = token;
		this.unicode = unicode;
		this.isBody = isBody;
    }
    
    public int getToken() {
    	return token;
    }
    
    public char getUnicode() {
    	return unicode;
    }
    
    public boolean isBody() {
    	return isBody;
    }
    
	@Override
    public String toString() {
        return this.name();
    }
}
