package astrochart.shared;

public enum Planet {
	Sun(     3, '\u2609'), 
	Moon(    4, '\u263e'), 
	Node(   13, '\u260A'), //ascending, 260B = descending
	Mercury( 5, '\u263f'),
	Venus(   6, '\u2640'), 
	Mars(    7, '\u2642'),
	Jupiter( 8, '\u2643'),
	Saturn(  9, '\u2644'),
	Uranus( 10, '\u2645'),
	Neptune(11, '\u2646'), 
	Pluto(  12, '\u2647');

	private int token;
	private char unicode;
	
	private Planet(final int token, final char unicode) {
		this.token = token;
		this.unicode = unicode;
    }
    
    public int getToken() {
    	return token;
    }
    
    public char getUnicode() {
    	return unicode;
    }
    
	@Override
    public String toString() {
        return this.name();
    }
}
