package astrochart.shared;

public enum ZodiacSign {
	Aries(          0,   0,  30, "ED1C24", '\u2648', "AR", "LI", RashimanaGroup.I,   false),
	Taurus(       -30,  30,  60, "22B14C", '\u2649', "TA", "SC", RashimanaGroup.II,  false),
	Gemini(       -60,  60,  90, "FFF200", '\u264A', "GE", "SG", RashimanaGroup.III, false),
	Cancer(       -90,  90, 120, "3F48CC", '\u264B', "CN", "CP", RashimanaGroup.III, true),
	Leo(         -120, 120, 150, "ED1C24", '\u264C', "LE", "AQ", RashimanaGroup.II,  true),
	Virgo(       -150, 150, 180, "22B14C", '\u264D', "VI", "PI", RashimanaGroup.I,   true),
	Libra(       -180, 180, 210, "FFF200", '\u264E', "LI", "AR", RashimanaGroup.I,   true),
	Scorpio(     -210, 210, 240, "3F48CC", '\u264F', "SC", "TA", RashimanaGroup.II,  true),
	Sagittaurius(-240, 240, 270, "ED1C24", '\u2650', "SG", "GE", RashimanaGroup.III, true),
	Capricorn(   -270, 270, 300, "22B14C", '\u2651', "CP", "CN", RashimanaGroup.III, false),
	Aquarius(    -300, 300, 330, "FFF200", '\u2652', "AQ", "LE", RashimanaGroup.II,  false),
	Pisces(      -330, 330, 360, "3F48CC", '\u2653', "PI", "VI", RashimanaGroup.I,   false);
		
	final int offsetAngle;
	final int eclipticLongitudeStart;
	final int eclipticLongitudeEnd;
	final String color;
	final char unicode;
	final String abbreviation;
	final String descendent;
	final RashimanaGroup rashimanaGroup;
	final boolean hasLongAscension;
	
	private ZodiacSign(final int offsetAngle, final int eclipticLongitudeStart, final int eclipticLongitudeEnd,
			final String color, final char unicode, final String abbreviation, final String descendent,
			final RashimanaGroup rashimanaGroup, final boolean hasLongAscension) {
        this.offsetAngle = offsetAngle;
        this.eclipticLongitudeStart = eclipticLongitudeStart;
        this.eclipticLongitudeEnd = eclipticLongitudeEnd;
        this.color = color;
        this.unicode = unicode;
        this.abbreviation = abbreviation;
        this.descendent = descendent;
        this.rashimanaGroup = rashimanaGroup;
        this.hasLongAscension = hasLongAscension;
    }
	
	public static ZodiacSign getSignAtDegree(final double degree) {
		for (ZodiacSign sign : ZodiacSign.values()) {
			if (degree >= sign.getEclipticLongitudeStart() &&
				degree < sign.getEclipticLongitudeEnd()) {
				return sign;
			}
		}
		throw new IllegalArgumentException("Value out of range: " + degree);
	}
	
    public int getOffsetAngle() {
        return offsetAngle;
    }
    
	public int getEclipticLongitudeStart() {
    	return eclipticLongitudeStart;
    }

	public int getEclipticLongitudeEnd() {
    	return eclipticLongitudeEnd;
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
