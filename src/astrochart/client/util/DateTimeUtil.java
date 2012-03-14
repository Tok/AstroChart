package astrochart.client.util;

import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;

public class DateTimeUtil {
	private static final long MS_2000 = 946684800000L; //2000-01-01 00:00:00 +0000 in milliseconds
	private static final double JD_2000 = 2451544.5D; //2000-01-01 00:00:00 as JD
	private static final long MS_PER_DAY = 86400000L;
	public static final double J_CONSTANT = 2451545.0009D;
	
	private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");

	private final NumberFormat nf = NumberFormat.getFormat("00"); //number format for hours, minutes or seconds
	private final NumberFormat jdNf = NumberFormat.getFormat("0.0000"); //number format for JD
    
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
	
	public Date getLocalDateFromUtc(Date providedUtcDate) {
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

//	/**
//	 * Also known as GMST
//	 * @param localDate
//	 * @return
//	 */
//	public final Date getGmtSidTimeDate(final Date localDate) {
//		return getLocalSidTimeDate(getUtcDate(localDate));
//	}

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

		final double a = getSiderealDegrees(jd) / 15D;
		
		final double gmstHours = a % 24D;
		final double gmstMinutes = ((a % 24D) * 60D) % 60D;
		final double gmstSeconds = ((a % 24D) * 60D * 60D) % 60D;

		final String dateString = dateTimeFormat.format(date);
		final int dateYear = Integer.valueOf(dateString.substring(0,4));
		final int dateMonth = Integer.valueOf(dateString.substring(5,7));
		final int dateDay = Integer.valueOf(dateString.substring(8,10));

		final String gmstHoursString = nf.format(Math.floor(gmstHours));
		final String gmstMinutesString = nf.format(Math.floor(gmstMinutes)); 
		final String gmstSecondsString = nf.format(Math.floor(gmstSeconds));

		final String gmstDateString = "" +
			nf.format(dateYear) + "." + nf.format(dateMonth) + "." + nf.format(dateDay) + " " + 
			gmstHoursString + ":" + gmstMinutesString + ":" + gmstSecondsString;

		return dateTimeFormat.parse(gmstDateString);
	}
	

//    public final double getLocalSiderealDegrees(final double jd) {
//	}
    
	/**
	 * @param jd
	 * @return sidereal time in degrees.
	 */
    public final double getSiderealDegrees(final double jd) {
		final double t = getJulianCenturiesSinceJ2000(jd);
		final double deg = 
			280.46061837D + 
			(13185000.77D * t) + 
			((Math.pow(t, 2)) / 2577.765D) - 
			((Math.pow(t, 3)) / 38710000D);
		return deg;
	}

	/**
	 * Converts asus into seconds
	 * compare: http://www.aryabhatt.com/vediclessons/vediclesson5.htm
	 * @param asus
	 * @return
	 */
	public final long convertAsusToSiderealSeconds(long asus) {
		return asus * 4L;
	}

	/**
	 * http://en.wikipedia.org/wiki/Julian_day#Converting_Gregorian_calendar_date_to_Julian_Day_Number
	 * @param date
	 * @return
	 */
	public final double getJulianDayNumber(final Date date) {
		final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
		final int dateYear = Integer.valueOf(dateString.substring(0,4));
		final int dateMonth = Integer.valueOf(dateString.substring(5,7));
		final int dateDay = Integer.valueOf(dateString.substring(8,10));
		
		final long a = (14L - dateMonth) / 12L;
		final long y = dateYear + 4800L - a;
		final long m = dateMonth + (12L * a) - 3;
		
		final double jdn = dateDay + 
			(((153L * m) + 2L) / 5L) +
			(365L * y) +
			(y / 4L) -
			(y / 100L) +
			(y / 400L) -
			32045L;
		
//		return Double.valueOf(jdn).longValue();
		return jdn;		
	}
	
	public final double getJulianDayNumberWithTime(final Date date) {
		final double jdn = getJulianDayNumber(date);
		final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
		
		final double hours = Double.valueOf(dateString.substring(11,13));
		final double minutes = Double.valueOf(dateString.substring(14,16));
		final double seconds = Double.valueOf(dateString.substring(17,19));
		
		final double result = jdn + 
			((hours - 12D) / 24D) +
			minutes / 1440D +
			seconds / 86400D;
		
		return result; 
	}

	public final double getJulianDayNumberTimeDropped(final Date date) {
		final double jdn = getJulianDayNumber(date);
		
		final double hours = 0D;
		final double minutes = 0D;
		final double seconds = 0D;
		
		final double result = jdn + 
			((hours - 12D) / 24D) +
			minutes / 1440D +
			seconds / 86400D;
		
		return result; 
	}

	
	/**
	 * calculates julian centuries since J2000.0 for the provided julian day number
	 * @param julianDayNumber
	 * @return
	 */
	public final double getJulianCenturiesSinceJ2000(final double jd) {
		return ((jd - 2451545.0D) / 36525D); //julian centuries since J2000
    }
	
	
	public final double calculateJulianDayNumberFromJd(final double julianDay) {
	    return julianDay - 2451545.0D; //days since equinox J2000.0
    }

	/**
	 * days since Jan 1, 2000 + 2451545
	 * @param date
	 * @return
	 */
	public final double calculateJulianDate(final Date date) {
  		final long differenceMs = date.getTime() - new Date(MS_2000).getTime(); //milliseconds since 2000-01-01 00:00:00
		final double daysSince2000 = differenceMs / Double.valueOf(MS_PER_DAY);
		return Math.floor(daysSince2000 + 2451545.0D);
    }

	public final double calculateStarTimeHours(final double julianCenturies, final double decimalHours) {
	    return 6.697376D + 
	    	(2400.05134D * julianCenturies) + 
	    	(1.002738D * decimalHours);
    }

	public final double convertHoursToDegree(final double starTimeHours) {
	    return starTimeHours * 15D;
    }

	public final double getDecimalHours(final Date date) {
		final String dateString = dateTimeFormat.format(date, TimeZone.createTimeZone(0));
		
		final double hours = Double.valueOf(dateString.substring(11,13));
		final double minutes = Double.valueOf(dateString.substring(14,16));
		final double seconds = Double.valueOf(dateString.substring(17,19));
		
		final double decimalHours = 
			hours +
			minutes / 60D +
			seconds / 3600D;
			
	    return decimalHours;
    }

	public final double getLocalSiderealDegrees(double siderealDegrees, double longitude) {
		double result = (siderealDegrees % 360D) + longitude;
		if (result < 0) {
			result = result + 360D;
		}
	    return result;
    }

	/**
	 * http://en.wikipedia.org/wiki/Leap_year#Algorithm
	 * @param year
	 * @return
	 */
	public final static boolean isLeapYear(final int year) {
		if (year % 4 == 0) {			
			if (year % 100 == 0) {
				if (year % 400 == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

}
