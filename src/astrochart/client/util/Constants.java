package astrochart.client.util;

public final class Constants {
    public static final double JULIAN_DAY_AT_01_01_2000 = 2451545D;
    public static final double HALF_JULIAN_DAY = 0.5D;
    public static final double MEAN_LONGITUDE_OF_THE_SUN = 280.460D; // XXX sometimes given as 280.461
    public static final double MEAN_LONGITUDE_OF_THE_SUN_PRECISE = 280.46061837D;
    public static final double DEGREES_IN_CIRCLE = 360.0D;
    public static final double MINUTES_PER_DEGREE = 60.0D;
    public static final double HOURS_PER_DAY = 24D;
    public static final double MINUTES_PER_HOUR = 60D;
    public static final double SECONDS_PER_MINUTE = 60D;
    public static final long MILLISECONDS_PER_MINUTE = 60000L;
    public static final long MILLISECONDS_PER_DAY = 86400000L;
    public static final char DEGREE_SIGN = '\u00B0';
    public static final char MINUTE_SIGN = '\u2032';
    public static final char SECOND_SIGN = '\u2033';

    private Constants() { }
}
