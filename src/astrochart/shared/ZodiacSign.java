package astrochart.shared;

public enum ZodiacSign {
	Aries(         0, "ED1C24", '\u2648', "AR", "LI", RashimanaGroup.I,   false),
	Taurus(       30, "22B14C", '\u2649', "TA", "SC", RashimanaGroup.II,  false),
	Gemini(       60, "FFF200", '\u264A', "GE", "SG", RashimanaGroup.III, false),
	Cancer(       90, "3F48CC", '\u264B', "CN", "CP", RashimanaGroup.III, true),
	Leo(         120, "ED1C24", '\u264C', "LE", "AQ", RashimanaGroup.II,  true),
	Virgo(       150, "22B14C", '\u264D', "VI", "PI", RashimanaGroup.I,   true),
	Libra(       180, "FFF200", '\u264E', "LI", "AR", RashimanaGroup.I,   true),
	Scorpio(     210, "3F48CC", '\u264F', "SC", "TA", RashimanaGroup.II,  true),
	Sagittaurius(240, "ED1C24", '\u2650', "SG", "GE", RashimanaGroup.III, true),
	Capricorn(   270, "22B14C", '\u2651', "CP", "CN", RashimanaGroup.III, false),
	Aquarius(    300, "FFF200", '\u2652', "AQ", "LE", RashimanaGroup.II,  false),
	Pisces(      330, "3F48CC", '\u2653', "PI", "VI", RashimanaGroup.I,   false);

	final int eclipticLongitude;
	final String color;
	final char unicode;
	final String abbreviation;
	final String descendent;
	final RashimanaGroup rashimanaGroup;
	final boolean hasLongAscension;
	
	private ZodiacSign(final int eclipticLongitude, final String color, final char unicode, 
			final String abbreviation, final String descendent,
			final RashimanaGroup rashimanaGroup, final boolean hasLongAscension) {
        this.eclipticLongitude = eclipticLongitude;
        this.color = color;
        this.unicode = unicode;
        this.abbreviation = abbreviation;
        this.descendent = descendent;
        this.rashimanaGroup = rashimanaGroup;
        this.hasLongAscension = hasLongAscension;
    }
	
	public static ZodiacSign getSignAtDegree(final double degree) {
		for (ZodiacSign sign : ZodiacSign.values()) {
			if (degree >= sign.getEclipticLongitude() &&
				degree < sign.getEclipticLongitude() + 30) {
				return sign;
			}
		}
		throw new IllegalArgumentException("Value out of range: " + degree);
	}
	
	public int getEclipticLongitude() {
    	return eclipticLongitude;
    }
	
    public String getColor() {
        return color;
    }
    
    public char getUnicode() {
        return unicode;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    public String getDescendent() {
        return descendent;
    }
    
    public RashimanaGroup getRashimanaGroup() {
        return rashimanaGroup;
    }
    
    public boolean hasLongAscension() {
        return hasLongAscension;
    }
    
    public static final ZodiacSign valueOfAbbrevistion(final String abbreviation) {
    	for (ZodiacSign sign : ZodiacSign.values()) {
    		if (sign.getAbbreviation().equals(abbreviation)) {
    			return sign;
    		}
    	}
    	throw new IllegalArgumentException("Fail: Abbreviation unknown: " + abbreviation);
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
