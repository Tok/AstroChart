package astrochart.shared;

public enum Planet {
	Sun(       3, '\u2609', true,  false), 
	Moon(      4, '\u263e', true,  false), 
	Node(     13, '\u260A', false, false), //ascending
	SouthNode(14, '\u260B', false, false), //descending
	Mercury(   5, '\u263f', true,  false),
	Venus(     6, '\u2640', true,  false), 
	Mars(      7, '\u2642', true,  false),
	Jupiter(   8, '\u2643', true,  false),
	Saturn(    9, '\u2644', true,  false),
	Uranus(   10, '\u2645', true,  true),
	Neptune(  11, '\u2646', true,  true), 
	Pluto(    12, '\u2647', true,  true);

	private int token;
	private char unicode;
	private boolean isBody;
	private boolean isOuter;
	
	private Planet(final int token, final char unicode, 
			final boolean isBody, final boolean isOuter) {
		this.token = token;
		this.unicode = unicode;
		this.isBody = isBody;
		this.isOuter = isOuter;
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
    
    public boolean isOuter() {
    	return isOuter;
    }
    
	@Override
    public String toString() {
        return this.name();
    }
}
