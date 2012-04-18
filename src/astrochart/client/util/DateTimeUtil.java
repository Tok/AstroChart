package astrochart.client.util;

import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;

public class DateTimeUtil {
    private static final long MS_2000 = 946684800000L; //2000-01-01 00:00:00 +0000 in milliseconds
    private static final double JD_2000 = Constants.JULIAN_DAY_AT_01_01_2000 - Constants.HALF_JULIAN_DAY; //2000-01-01 00:00:00 as JD
    private static final long MS_PER_DAY = 86400000L;
    public static final double J_CONSTANT = 2451545.0009D;
    private static final double DAYS_PER_YEAR = 365D;
    private static final long MONTHS_PER_YEAR = 12L;
    private static final double DEGREES_PER_HOUR = 15D;
    private static final double DAYS_PER_JULIAN_CENTURY = 36525D;
    private static final double MINUTES_PER_DAY = 1440D;
    private static final double SECONDS_PER_HOUR = 3600D;
    private static final double SECONDS_PER_DAY = 86400D;
    private static final double SIDEREAL_CONSTANT_1 = 13185000.77D;
    private static final double SIDEREAL_CONSTANT_2 = 2577.765D;
    private static final double SIDEREAL_CONSTANT_3 = 38710000D;
    private static final double STARTIME_CONSTANT_1 = 6.697376D;
    private static final double STARTIME_CONSTANT_2 = 2400.05134D;
    private static final double STARTIME_CONSTANT_3 = 1.002738D;
    private static final long LEAP_YEAR_CONSTANT_4 = 4L;
    private static final long LEAP_YEAR_CONSTANT_100 = 100L;
    private static final long LEAP_YEAR_CONSTANT_400 = 400L;
    private static final long JULIAN_DAY_0_BC = 4800L;
    private static final long MONTH_FACTOR = 153L;
    private static final long JDN_OFFSET = 32045L;

    private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
    private final NumberFormat nf = NumberFormat.getFormat("00"); //number format for hours, minutes or seconds
    private final NumberFormat jdNf = NumberFormat.getFormat("0.0000"); //number format for JD

    private enum DateTimeRange {
        Year(0, 4),
        Month(5, 7),
        Days(8, 10),
        Hours(11, 13),
        Minutes(14, 16),
        Seconds(17, 19);

        private int from;
        private int to;

        DateTimeRange(final int from, final int to) {
            this.from = from;
            this.to = to;
        }

        public final int getFrom() {
            return from;
        }

        public final int getTo() {
            return to;
        }
    }

    public final String formatDateAsUtc(final Date localDate) {
        return dateTimeFormat.format(localDate, TimeZone.createTimeZone(0));
    }

    public final String formatLocalDate(final Date localDate) {
        return dateTimeFormat.format(localDate);
    }

    public final Date getUtcDate(final Date localDate) {
        return dateTimeFormat.parse(formatDateAsUtc(localDate));
    }

    public final Date getUtcDateFromUtcString(final String utcDateString) {
        return dateTimeFormat.parse(utcDateString);
    }

    public final Date getLocalDateFromUtc(final Date providedUtcDate) {
        return dateTimeFormat.parse(dateTimeFormat.format(providedUtcDate));
    }

    /**
     * http://en.wikipedia.org/wiki/Julian_day#Julian_Date
     * @param date
     * @return Julian Date
     */
    public final double getJdTimeDate(final Date date) {
        final long differenceMs = date.getTime() - new Date(MS_2000).getTime(); //milliseconds since 2000-01-01 00:00:00
        final double daysSince2000 = differenceMs / Double.valueOf(MS_PER_DAY);
        final double jd = JD_2000 + daysSince2000; //Julian Date
        return jd;
    }

    public final Date convertJdToDate(final double jd) {
        final double daysSince2000 = jd - JD_2000;
        final double differenceMs = daysSince2000 * Double.valueOf(MS_PER_DAY);
        final long dateMs = Double.valueOf(differenceMs).longValue() + new Date(MS_2000).getTime();
        final Date result = new Date(dateMs);
        return result;
    }

    public final String getFormattedJdTimeDate(final Date date) {
        return jdNf.format(getJdTimeDate(date));
    }

//  /**
//   * Also known as GMST
//   * @param localDate
//   * @return
//   */
//  public final Date getGmtSidTimeDate(final Date localDate) {
//      return getLocalSidTimeDate(getUtcDate(localDate));
//  }

    //TODO cleanup magic numbers
    /**
     * Also known as LST
     * http://en.wikipedia.org/wiki/Sidereal_time
     * @param date
     * @return
     */
    public final Date getLocalSidTimeDate(final Date date) {
        final double jd = getJdTimeDate(date);

        /*
        final double a = 18.697374558D + (24.06570982441908D * (jd - 2451545.0D));
         */

        final double a = getSiderealDegrees(jd) / DEGREES_PER_HOUR;

        final double gmstHours = a % Constants.HOURS_PER_DAY;
        final double gmstMinutes = ((a % Constants.HOURS_PER_DAY) * Constants.MINUTES_PER_HOUR)
                % Constants.MINUTES_PER_HOUR;
        final double gmstSeconds = ((a % Constants.HOURS_PER_DAY) * Constants.MINUTES_PER_HOUR * Constants.SECONDS_PER_MINUTE)
                % Constants.SECONDS_PER_MINUTE;

        final String dateString = dateTimeFormat.format(date);
        final int dateYear = Integer.valueOf(dateString.substring(0, 4));
        final int dateMonth = Integer.valueOf(dateString.substring(5, 7));
        final int dateDay = Integer.valueOf(dateString.substring(8, 10));

        final String gmstHoursString = nf.format(Math.floor(gmstHours));
        final String gmstMinutesString = nf.format(Math.floor(gmstMinutes));
        final String gmstSecondsString = nf.format(Math.floor(gmstSeconds));

        final String gmstDateString =
                nf.format(dateYear) + "." + nf.format(dateMonth) + "." + nf.format(dateDay) + " "
                + gmstHoursString + ":" + gmstMinutesString + ":" + gmstSecondsString;

        return dateTimeFormat.parse(gmstDateString);
    }

    /**
     * http://de.wikipedia.org/wiki/Sternzeit#Berechnung_der_Sternzeit
     * @param jd
     * @return sidereal time in degrees.
     */
    public final double getSiderealDegrees(final double jd) {
        final double t = getJulianCenturiesSinceJ2000(jd);
        final double deg =
                Constants.MEAN_LONGITUDE_OF_THE_SUN_PRECISE
                + (SIDEREAL_CONSTANT_1 * t)
                + ((Math.pow(t, 2)) / SIDEREAL_CONSTANT_2)
                - ((Math.pow(t, 3)) / SIDEREAL_CONSTANT_3);
        return deg;
    }

    /**
     * Converts asus into seconds compare:
     * http://www.aryabhatt.com/vediclessons/vediclesson5.htm
     * @param asus
     * @return
     */
    public final long convertAsusToSiderealSeconds(final long asus) {
        return asus * 4L;
    }

    /**
     * http://en.wikipedia.org/wiki/Julian_day#Converting_Gregorian_calendar_date_to_Julian_Day_Number
     * @param date
     * @return
     */
    public final double getJulianDayNumber(final Date date) {
        final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
        final int dateYear = Integer.valueOf(dateString.substring(0, 4));
        final int dateMonth = Integer.valueOf(dateString.substring(5, 7));
        final int dateDay = Integer.valueOf(dateString.substring(8, 10));
        final long a = ((MONTHS_PER_YEAR + 2L) - dateMonth) / MONTHS_PER_YEAR;
        final long y = dateYear + JULIAN_DAY_0_BC - a;
        final long m = dateMonth + (MONTHS_PER_YEAR * a) - 3;
        final double jdn = dateDay
                + (((MONTH_FACTOR * m) + 2L) / 5L)
                + (DAYS_PER_YEAR * y)
                + (y / LEAP_YEAR_CONSTANT_4)
                - (y / LEAP_YEAR_CONSTANT_100)
                + (y / LEAP_YEAR_CONSTANT_400)
                - JDN_OFFSET;
//      return Double.valueOf(jdn).longValue();
        return jdn;
    }

    public final double getJulianDayNumberWithTime(final Date date) {
        final double jdn = getJulianDayNumber(date);
        final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
        final double hours = Double.valueOf(dateString.substring(11, 13));
        final double minutes = Double.valueOf(dateString.substring(14, 16));
        final double seconds = Double.valueOf(dateString.substring(17, 19));
        final double result = jdn
                + ((hours - MONTHS_PER_YEAR) / Constants.HOURS_PER_DAY)
                + minutes / MINUTES_PER_DAY
                + seconds / SECONDS_PER_DAY;
        return result;
    }

    public final double getJulianDayNumberTimeDropped(final Date date) {
        final double jdn = getJulianDayNumber(date);
        final double hours = 0D;
        final double minutes = 0D;
        final double seconds = 0D;
        final double result = jdn
                + ((hours - MONTHS_PER_YEAR) / Constants.HOURS_PER_DAY)
                + minutes / MINUTES_PER_DAY
                + seconds / SECONDS_PER_DAY;
        return result;
    }

    /**
     * calculates julian centuries since J2000.0 for the provided julian day number
     * @param julianDayNumber
     * @return
     */
    public final double getJulianCenturiesSinceJ2000(final double jd) {
        return ((jd - Constants.JULIAN_DAY_AT_01_01_2000) / DAYS_PER_JULIAN_CENTURY); //julian centuries since J2000
    }

    public final double calculateJulianDayNumberFromJd(final double julianDay) {
        return julianDay - Constants.JULIAN_DAY_AT_01_01_2000; // days since equinox J2000.0
    }

    /**
     * days since Jan 1, 2000 + 2451545
     * @param date
     * @return
     */
    public final double calculateJulianDate(final Date date) {
        final long differenceMs = date.getTime() - new Date(MS_2000).getTime(); //milliseconds since 2000-01-01 00:00:00
        final double daysSince2000 = differenceMs / Double.valueOf(MS_PER_DAY);
        return Math.floor(daysSince2000 + Constants.JULIAN_DAY_AT_01_01_2000);
    }

    public final double calculateStarTimeHours(final double julianCenturies, final double decimalHours) {
        return STARTIME_CONSTANT_1
                + (STARTIME_CONSTANT_2 * julianCenturies)
                + (STARTIME_CONSTANT_3 * decimalHours);
    }

    public final double convertHoursToDegree(final double starTimeHours) {
        return starTimeHours * DEGREES_PER_HOUR;
    }

    public final double getDecimalHours(final Date date) {
        final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
        final double hours = Double.valueOf(dateString.substring(11, 13));
        final double minutes = Double.valueOf(dateString.substring(14, 16));
        final double seconds = Double.valueOf(dateString.substring(17, 19));
        final double decimalHours = hours + (minutes / Constants.SECONDS_PER_MINUTE) + (seconds / SECONDS_PER_HOUR);
        return decimalHours;
    }

    public final int getYear(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Year.getFrom(), DateTimeRange.Year.getTo()));
    }

    public final int getMonth(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Month.getFrom(), DateTimeRange.Month.getTo()));
    }

    public final int getDay(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Days.getFrom(), DateTimeRange.Days.getTo()));
    }

    public final int getHours(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Hours.getFrom(), DateTimeRange.Hours.getTo()));
    }

    public final int getMinutes(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Minutes.getFrom(), DateTimeRange.Minutes.getTo()));
    }

    public final int getSeconds(final Date date) {
        return Integer.valueOf(dateTimeFormat.format(date).substring(DateTimeRange.Seconds.getFrom(), DateTimeRange.Seconds.getTo()));
    }

    public final double getLocalSiderealDegrees(final double siderealDegrees, final double longitude) {
        double result = (siderealDegrees % Constants.DEGREES_IN_CIRCLE) + longitude;
        if (result < 0) {
            result = result + Constants.DEGREES_IN_CIRCLE;
        }
        return result;
    }

    /**
     * http://en.wikipedia.org/wiki/Leap_year#Algorithm
     * @param year
     * @return
     */
    public static final boolean isLeapYear(final int year) {
        if (year % LEAP_YEAR_CONSTANT_4 == 0) {
            if (year % LEAP_YEAR_CONSTANT_100 == 0) {
                return (year % LEAP_YEAR_CONSTANT_400 == 0);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

}
