package astrochart.client;


import java.util.Date;
import astrochart.client.util.DateTimeUtil;
import com.google.gwt.junit.client.GWTTestCase;


public class DateTimeUtilTester extends GWTTestCase {
    private DateTimeUtil dateTimeUtil;

    static final long SIXTH_OF_AUGUST_2006 = 1154844000000L; //2006.08.06 06:00:00 +0000 = 08:00:00 MESZ
    static final double JD_SIXTH_OF_AUGUST_2006 = 2453953.75D; //JD for 2006.08.06 06:00:00 +0000

    static final long FIRST_OF_JANUARY_2004 = 1072915200000L; //2004.01.01 00:00:00 +0000
    static final String FIRST_OF_JANUARY_2004_SD = "2004.01.01 06:39:58";
    static final String FIRST_OF_JANUARY_2004_SD_WAT = "2004.01.01 06:39:59";

    static final long FIRST_OF_JANUARY_2001_MIDDAY = 946728000000L;

    public final void gwtSetUp() throws Exception {
        dateTimeUtil = new DateTimeUtil();
    }

    public final void testJdDate() throws Exception {
        final Date date = new Date();
        date.setTime(SIXTH_OF_AUGUST_2006);
        assertEquals(JD_SIXTH_OF_AUGUST_2006, dateTimeUtil.getJdTimeDate(date));
    }

    public final void testSidDate() throws Exception {
        final Date date = new Date();
        date.setTime(FIRST_OF_JANUARY_2004);
        final Date lst = dateTimeUtil.getLocalSidTimeDate(date);
        assertEquals(FIRST_OF_JANUARY_2004_SD_WAT, dateTimeUtil.formatLocalDate(lst));
        // assertEquals(FIRST_OF_JANUARY_2004_SD,
        // dateTimeUtil.formatLocalDate(lst));
    }

    public final void testJdn() throws Exception {
        final Date date = new Date();
        date.setTime(SIXTH_OF_AUGUST_2006);
        final double jdn = dateTimeUtil.getJulianDayNumber(date);
        assertEquals(2453954.0D, jdn);
        final double jdo = dateTimeUtil.getJulianDayNumberTimeDropped(date);
        assertEquals(2453953.5D, jdo);
    }

    public final void testJulianCenturies() throws Exception {
        // final long jdn = 2453954L;
        // final double jCent = dateTimeUtil.calculateJulianCenturies(jdn);
        // assertEquals(2451545L, jCent);
    }

    /*
    final Date wat = new Date();
    wat.setTime(0L);
    wat.setYear(2006 - 1900);
    wat.setMonth(7);
    wat.setDate(6);
    wat.setHours(8);
    wat.setMinutes(0);
    wat.setSeconds(0);
    System.out.println("wat: " + dateTimeUtil.formatDateAsUtc(wat));
    System.out.println("wat: " + DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z").format(wat, TimeZone.createTimeZone(0)));
    System.out.println("ms: " + wat.getTime());
     */

    @Override
    public final String getModuleName() {
        return "astrochart.AstroChart";
    }
}
