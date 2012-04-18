package astrochart.shared.enums;


public enum ZodiacSign {
    Aries(0,          ChartColor.Red,    '\u2648', "AR", "LI", RashimanaGroup.I,   false),
    Taurus(30,        ChartColor.Green,  '\u2649', "TA", "SC", RashimanaGroup.II,  false),
    Gemini(60,        ChartColor.Yellow, '\u264A', "GE", "SG", RashimanaGroup.III, false),
    Cancer(90,        ChartColor.Blue,   '\u264B', "CN", "CP", RashimanaGroup.III, true),
    Leo(120,          ChartColor.Red,    '\u264C', "LE", "AQ", RashimanaGroup.II,  true),
    Virgo(150,        ChartColor.Green,  '\u264D', "VI", "PI", RashimanaGroup.I,   true),
    Libra(180,        ChartColor.Yellow, '\u264E', "LI", "AR", RashimanaGroup.I,   true),
    Scorpio(210,      ChartColor.Blue,   '\u264F', "SC", "TA", RashimanaGroup.II,  true),
    Sagittaurius(240, ChartColor.Red,    '\u2650', "SG", "GE", RashimanaGroup.III, true),
    Capricorn(270,    ChartColor.Green,  '\u2651', "CP", "CN", RashimanaGroup.III, false),
    Aquarius(300,     ChartColor.Yellow, '\u2652', "AQ", "LE", RashimanaGroup.II,  false),
    Pisces(330,       ChartColor.Blue,   '\u2653', "PI", "VI", RashimanaGroup.I,   false);

    private static final int DEGREES_PER_SIGN = 30;
    private final int eclipticLongitude;
    private final ChartColor color;
    private final char unicode;
    private final String abbreviation;
    private final String descendent;
    private final RashimanaGroup rashimanaGroup;
    private final boolean hasLongAscension;

    private ZodiacSign(final int eclipticLongitude, final ChartColor color, final char unicode, final String abbreviation, final String descendent,
            final RashimanaGroup rashimanaGroup, final boolean hasLongAscension) {
        this.eclipticLongitude = eclipticLongitude;
        this.color = color;
        this.unicode = unicode;
        this.abbreviation = abbreviation;
        this.descendent = descendent;
        this.rashimanaGroup = rashimanaGroup;
        this.hasLongAscension = hasLongAscension;
    }

    public static final ZodiacSign getSignAtDegree(final double degree) {
        for (ZodiacSign sign : ZodiacSign.values()) {
            if (degree >= sign.getEclipticLongitude() && degree < sign.getEclipticLongitude() + DEGREES_PER_SIGN) {
                return sign;
            }
        }
        throw new IllegalArgumentException("Value out of range: " + degree);
    }

    public final int getEclipticLongitude() {
        return eclipticLongitude;
    }

    public final ChartColor getColor() {
        return color;
    }

    public final char getUnicode() {
        return unicode;
    }

    public final String getAbbreviation() {
        return abbreviation;
    }

    public final String getDescendent() {
        return descendent;
    }

    public final RashimanaGroup getRashimanaGroup() {
        return rashimanaGroup;
    }

    public final boolean hasLongAscension() {
        return hasLongAscension;
    }

    public static final ZodiacSign valueOfAbbrevistion(final String abbreviation) {
        for (final ZodiacSign sign : ZodiacSign.values()) {
            if (sign.getAbbreviation().equals(abbreviation)) {
                return sign;
            }
        }
        throw new IllegalArgumentException("Fail: Abbreviation unknown: " + abbreviation);
    }

    @Override
    public final String toString() {
        return this.name();
    }
}
