package astrochart.shared;

public enum AspectType {
	Conjunction('\u260C',   0D, 8D),
	Sextile(    '\u26B9',  60D, 4D),
	Square(     '\u25A1',  90D, 5D),
	Trine(      '\u25B3', 120D, 4D),
	Opposition( '\u260D', 180D, 7D);
		
	private final char unicode;
	private final double angle;
	private final double orb;
	
	private AspectType(final char unicode, final double angle, final double orb) {
        this.unicode = unicode;
        this.angle = angle;
        this.orb = orb;
    }

	public final char getUnicode() {
    	return unicode;
    }	
	
	public final double getAngle() {
    	return angle;
    }
    
	public final double getOrb() {
    	return orb;
    }
	
	public final String toString() {
    	return name();
    }
}
