package astrochart.shared;

/**
 * compare: http://www.vedicastro.com/astronomy6.asp
 */
public enum RashimanaGroup {
	I(1674),
	II(1795),
	III(1931);
	
	final int equatorialAsus;
	
	private RashimanaGroup(final int equatorialAsus) {
        this.equatorialAsus = equatorialAsus;
    }

	public int getEquatorialAsus() {
    	return equatorialAsus;
    }

}
