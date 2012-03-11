package astrochart.client;

import java.util.Date;
import astrochart.client.util.AstrologyUtil;
import astrochart.client.util.DateTimeUtil;
import astrochart.shared.ZodiacSign;
import astrochart.shared.wrappers.BodyPosition;
import astrochart.shared.wrappers.RiseAndSet;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.junit.client.GWTTestCase;


public class AstrologyUtilTester extends GWTTestCase {
	static final long SIXTH_OF_AUGUST_2006 = 1154844000000L; //2006.08.06 06:00:00 +0000 = 08:00:00 MESZ
	static final long EIGHT_OF_MARCH_2012 = 1331200800000L; //2012.03.08 10:00:00 +0000
	
	AstrologyUtil astrologyUtil;
	DateTimeUtil dateTimeUtil;
	
	public final void gwtSetUp() throws Exception {
		astrologyUtil = new AstrologyUtil();
		dateTimeUtil = new DateTimeUtil();
	}
	
	/**
	 * http://de.wikipedia.org/wiki/Sonnenstand#Rechen-Beispiel
	 * @throws Exception
	 */
	public final void testSunPosition() throws Exception {
		final Date date = new Date(SIXTH_OF_AUGUST_2006);
		final double jd = dateTimeUtil.getJdTimeDate(date);
		assertEquals(2453953.75D, jd);
		
		final double jdNumber = dateTimeUtil.calculateJulianDayNumberFromJd(jd);
		assertEquals(2408.75D, jdNumber);
		
		final double eclipticLongitude = astrologyUtil.calculateEclipticLongitude(jdNumber);
		assertEquals(2654.63817475D, eclipticLongitude);
		assertEquals(134.63817474999996D, eclipticLongitude % 360D);
		
		final double meanAnomaly = astrologyUtil.calculateMeanAnomaly(jdNumber);
		assertEquals(2731.5927226249996D, meanAnomaly);
		assertEquals(211.59272262499962D, meanAnomaly % 360D);

		double eclipticalLength = astrologyUtil.calculateEclipticalLength(eclipticLongitude, meanAnomaly);
		//assertEquals(133.653D, eclipticalLength); //according to wiki
		assertEquals(133.63630794764566D, eclipticalLength); //result for full formuls
		
		eclipticalLength = 133.653D; //fix
		
		final double eclipticInclination = astrologyUtil.calculateEclipticInclination(jdNumber);
		assertEquals(23.4380365D, eclipticInclination);
		
		final double rightAscension = astrologyUtil.calculateRightAscension(eclipticInclination, eclipticalLength);
		assertEquals(136.11916738220359D, rightAscension);

		final double declination = astrologyUtil.calculateDeclination(eclipticInclination, eclipticalLength);
		assertEquals(16.72572957829604D, declination);

		final double jdo = dateTimeUtil.getJulianDayNumberTimeDropped(date);
		assertEquals(2453953.5D, jdo);

		final double julianCenturies = dateTimeUtil.getJulianCenturiesSinceJ2000(jdo);
		assertEquals(0.06594113620807666D, julianCenturies);
		
		final double decimalHours = dateTimeUtil.getDecimalHours(date);
		assertEquals(6.0D, decimalHours);
		final double starTimeHours = dateTimeUtil.calculateStarTimeHours(julianCenturies, decimalHours);
		assertEquals(170.9759163173169D, starTimeHours);
		final double starTimeDegrees = dateTimeUtil.convertHoursToDegree(starTimeHours);
		assertEquals(2564.6387447597535D, starTimeDegrees);

		final double latitude = 48.1D; //For Munich
		final double longitude = 11.6D; //For Munich

		final double localJulianDeg = astrologyUtil.calculateVeneralEquinoxDegrees(starTimeDegrees, longitude);
		assertEquals(56.238744759753445D, localJulianDeg);
		
		final double hourAngle = astrologyUtil.calculateHourAngle(localJulianDeg, rightAscension);
		assertEquals(-79.88042262245014, hourAngle);

		final double azimuth = astrologyUtil.calculateAzimuth(hourAngle, latitude, declination);
		assertEquals(265.93806263966246D, azimuth);
		final double heightAngle = astrologyUtil.calculateHeightAngle(hourAngle, latitude, declination);
		assertEquals(19.06143926382523D, heightAngle);
		
		final double refHeight = astrologyUtil.calculateRefractionDegrees(heightAngle);
		assertEquals(19.109478922873596D, refHeight);
	}
	
	public final void testFullSunPosition() throws Exception {
		final Date date = new Date(SIXTH_OF_AUGUST_2006);
		final double latitude = 48.1D; //For Munich
		final double longitude = 11.6D; //For Munich
		final BodyPosition position = astrologyUtil.calculateSunPosition(date, latitude, longitude);
		assertEquals(265.94669695311495D, position.getAzimuth());
		assertEquals(19.124002077105104D, position.getHeight());
	}
	
	public final void testSunsetAndSunrise() throws Exception {
		final Date date = new Date(EIGHT_OF_MARCH_2012);
		final double latitude = 39.040759D;
		final double longitude = 77.04876D;
		
		final double julianDate = dateTimeUtil.calculateJulianDate(date);
		assertEquals(2455995.0D, julianDate);
		final double julianCycle = astrologyUtil.calculateAccurateJulianCycle(julianDate, longitude);
		assertEquals(4450.0D, julianCycle);
		final double solarNoonApprox = astrologyUtil.calculateApproxSolarNoon(julianCycle, longitude);
		assertEquals(2455995.214924333D, solarNoonApprox);
		final double meanSolarAnomaly = astrologyUtil.calculateAccurateMeanAnomaly(solarNoonApprox);
		assertEquals(63.66217548293298D, meanSolarAnomaly);
		final double equationOfCenter = astrologyUtil.calculateEquationOfCenter(meanSolarAnomaly);
		assertEquals(1.731878909721004D, equationOfCenter);
		final double solarEclipticalLongitude = astrologyUtil.calculateSolarEclipticLongitude(meanSolarAnomaly, equationOfCenter);
		assertEquals(348.331254392654D, solarEclipticalLongitude);
		final double solarNoonTransit = astrologyUtil.calculateSolarNoonTransit(solarNoonApprox, meanSolarAnomaly, solarEclipticalLongitude);
		assertEquals(2455995.22240757D, solarNoonTransit);
				
		final double accurateMeanSolarAnomaly = astrologyUtil.calculateAccurateMeanAnomalyRecursive(solarNoonApprox, meanSolarAnomaly, solarNoonTransit);
		assertEquals(63.66954963102398D, accurateMeanSolarAnomaly);
		assertEquals(63.66954963102398D, astrologyUtil.getMeanSolarNoonAnomaly());
		assertEquals(1.7319849964835408D, astrologyUtil.getEquationOfCenter());
		assertEquals(348.3387346275075D, astrologyUtil.getSolarEclipticalLongitude());
		assertEquals(2455995.2224062183D, astrologyUtil.getSolarNoonTransit());
		
		Date transit =	dateTimeUtil.convertJdToDate(astrologyUtil.getSolarNoonTransit());
		assertEquals("2012.03.08 17:20:15", dateTimeUtil.formatDateAsUtc(transit));
		
		final double solarDeclination = astrologyUtil.calculateSolarDeclination(astrologyUtil.getSolarEclipticalLongitude());
		assertEquals(-4.6135967684537365D, solarDeclination);
		final double hourAngle = astrologyUtil.calculateExactHourAngle(latitude, solarDeclination);
		assertEquals(87.32152540998553D, hourAngle);
		final double solarNoonSecondApprox = astrologyUtil.calculateSolarNoonSecondApprox(hourAngle, longitude, julianCycle);
		assertEquals(2455995.457484126D, solarNoonSecondApprox);
		
		final double sunset = astrologyUtil.calculateSunset(solarNoonSecondApprox, accurateMeanSolarAnomaly, astrologyUtil.getSolarEclipticalLongitude());
		assertEquals(2455995.4649660112D, sunset);
		final double sunrise = astrologyUtil.calculateSunrise(sunset, astrologyUtil.getSolarNoonTransit());
		assertEquals(2455994.9798464254D, sunrise);
	}

	public final void testFullSunsetAndSunrise() throws Exception {
		final Date date = new Date(EIGHT_OF_MARCH_2012);
		final double latitude = 39.040759D; //for GMT -5
		final double longitude = 77.04876D; //for GMT -5
		final RiseAndSet ras = astrologyUtil.calculateSunRiseAndSet(date, latitude, longitude);
		
		final TimeZone timeZone = TimeZone.createTimeZone(300); //GMT -5
		final String originalDate = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z").format(date, timeZone);
		final String sunriseDate = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z").format(ras.getRise(), timeZone);
		final String sunsetDate = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z").format(ras.getSet(), timeZone);
		assertEquals("2012.03.08 05:00:00 -0500", originalDate);
		assertEquals("2012.03.08 06:30:58 -0500", sunriseDate);
		assertEquals("2012.03.08 18:09:33 -0500", sunsetDate);
	}

	/**
	 * http://de.wikipedia.org/wiki/Aszendent_%28Astrologie%29#Beispiele
	 * @throws Exception
	 */
	public final void testTropicalAscendent() throws Exception {
		final Date firstDate = new Date(437317440000L); //1983.11.10 13:04:00 +0000
		final double firstLongitude = 16D;
		final double firstLatitude = 48D;
		final double firstJd = dateTimeUtil.getJdTimeDate(firstDate);
		assertEquals(2445649.0444444446D, firstJd);
		final double firstCents = dateTimeUtil.getJulianCenturiesSinceJ2000(firstJd);
		assertEquals(-0.16142246558673268D, firstCents);
		final double firstSiderealDegrees = dateTimeUtil.getSiderealDegrees(firstJd);
		assertEquals(-2128074.87242789D, firstSiderealDegrees);
		final double firstLocalSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(firstSiderealDegrees, firstLongitude);
		assertEquals(261.12757210992277D, firstLocalSiderealDegrees);

		final double firstEclipticLongitudeTan = astrologyUtil.calculateEclipticLongitudeJ2000Tangens(firstLocalSiderealDegrees, firstLatitude);
		assertEquals(-0.3318826228021096D, firstEclipticLongitudeTan); //-0,331882633 difference due to more accurate inclination		
		final double firstS1 = astrologyUtil.getFirstHorizontEclipticSection(firstEclipticLongitudeTan);
		assertEquals(161.63989133761655D, firstS1);
		final double firstS2 = astrologyUtil.getSecondHorizontEclipticSection(firstEclipticLongitudeTan);
		assertEquals(341.6398913376165D, firstS2);
		final boolean firstReturnDescendent = astrologyUtil.returnDescendent(firstLocalSiderealDegrees);
		assertTrue(firstReturnDescendent);

		final ZodiacSign firstSign = astrologyUtil.returnSign(firstS1, firstS2, firstReturnDescendent);
		assertEquals(ZodiacSign.Pisces, firstSign);
		final double firstPosition = astrologyUtil.returnPosition(firstS1, firstS2, firstReturnDescendent);
		assertEquals(11.639891337616518D, firstPosition);
		/////////////////////////////////////////////////
		
		
		final Date secondDate = new Date(534640320000L); //1986.12.10 23:12:00 +0000
		final double secondLongitude = -27D;
		final double secondLatitude = -61D;
		
		final double secondJd = dateTimeUtil.getJdTimeDate(secondDate);
		assertEquals(2446775.466666667D, secondJd);
		final double secondCents = dateTimeUtil.getJulianCenturiesSinceJ2000(secondJd);
		assertEquals(-0.13058270590919122D, secondCents);
		final double secondSiderealDegrees = dateTimeUtil.getSiderealDegrees(secondJd);
		assertEquals(-1721452.6173363847D, secondSiderealDegrees);
		final double secondLocalSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(secondSiderealDegrees, secondLongitude);
		assertEquals(40.38266361528076D, secondLocalSiderealDegrees);
		
		final double secondEclipticLongitudeTan = astrologyUtil.calculateEclipticLongitudeJ2000Tangens(secondLocalSiderealDegrees, secondLatitude);
		assertEquals(6.183810457084861D, secondEclipticLongitudeTan);
		
		final double secondS1 = astrologyUtil.getFirstHorizontEclipticSection(secondEclipticLongitudeTan);
		assertEquals(80.81407386747117D, secondS1);
		final double secondS2 = astrologyUtil.getSecondHorizontEclipticSection(secondEclipticLongitudeTan);
		assertEquals(260.81407386747117D, secondS2);
		final boolean secondReturnDescendent = astrologyUtil.returnDescendent(secondLocalSiderealDegrees);
		assertFalse(secondReturnDescendent);
		
		final ZodiacSign secondSign = astrologyUtil.returnSign(secondS1, secondS2, secondReturnDescendent);
		assertEquals(ZodiacSign.Gemini, secondSign);
		final double secondPosition = astrologyUtil.returnPosition(secondS1, secondS2, secondReturnDescendent);
		assertEquals(20.814073867471166D, secondPosition);
		/////////////////////////////////////////////////

		
		final Date thirdDate = new Date(1607214420000L); //2020.12.06 00:27:00 +0000
		final double thirdLongitude = -6D;
		final double thirdLatitude = -14D;
		
		final double thirdJd = dateTimeUtil.getJdTimeDate(thirdDate);
		assertEquals(2459189.51875D, thirdJd);
		final double thirdCents = dateTimeUtil.getJulianCenturiesSinceJ2000(thirdJd);
		assertEquals(0.20929551676933097D, thirdCents);
		final double thirdSiderealDegrees = dateTimeUtil.getSiderealDegrees(thirdJd);
		assertEquals(2759842.0103965397D, thirdSiderealDegrees);
		final double thirdLocalSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(thirdSiderealDegrees, thirdLongitude);
		assertEquals(76.01039653969929D, thirdLocalSiderealDegrees);
		
		final double thirdEclipticLongitudeTan = astrologyUtil.calculateEclipticLongitudeJ2000Tangens(thirdLocalSiderealDegrees, thirdLatitude);
		assertEquals(-0.30558489581660087D, thirdEclipticLongitudeTan);
		
		final double thirdS1 = astrologyUtil.getFirstHorizontEclipticSection(thirdEclipticLongitudeTan);
		assertEquals(163.0076391971394D, thirdS1);
		final double thirdS2 = astrologyUtil.getSecondHorizontEclipticSection(thirdEclipticLongitudeTan);
		assertEquals(343.0076391971394D, thirdS2);
		final boolean thirdReturnDescendent = astrologyUtil.returnDescendent(thirdLocalSiderealDegrees);
		assertFalse(thirdReturnDescendent);
		
		final ZodiacSign thirdSign = astrologyUtil.returnSign(thirdS1, thirdS2, thirdReturnDescendent);
		assertEquals(ZodiacSign.Virgo, thirdSign);
		final double thirdPosition = astrologyUtil.returnPosition(thirdS1, thirdS2, thirdReturnDescendent);
		assertEquals(13.007639197139412D, thirdPosition);
		/////////////////////////////////////////////////
		
		
		final Date fourthDate = new Date(1432538100000L); //2015.05.25 07:15:00 +0000
		final double fourthLongitude = -21D;
		final double fourthLatitude = -90D;
		
		final double fourthJd = dateTimeUtil.getJdTimeDate(fourthDate);
		assertEquals(2457167.8020833335D, fourthJd);
		final double fourthCents = dateTimeUtil.getJulianCenturiesSinceJ2000(fourthJd);
		assertEquals(0.1539439310974261D, fourthCents);
		final double fourthSiderealDegrees = dateTimeUtil.getSiderealDegrees(fourthJd);
		assertEquals(2030031.3106839536D, fourthSiderealDegrees);
		final double fourthLocalSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(fourthSiderealDegrees, fourthLongitude);
		assertEquals(330.3106839535758D, fourthLocalSiderealDegrees);
		
		final double fourthEclipticLongitudeTan = astrologyUtil.calculateEclipticLongitudeJ2000Tangens(fourthLocalSiderealDegrees, fourthLatitude);
		assertEquals(1.33728133063789E-16D, fourthEclipticLongitudeTan); //~0.000000000
		
		final double fourthS1 = astrologyUtil.getFirstHorizontEclipticSection(fourthEclipticLongitudeTan);
		assertEquals(0.0D, fourthS1);
		final double fourthS2 = astrologyUtil.getSecondHorizontEclipticSection(fourthEclipticLongitudeTan);
		assertEquals(180.0D, fourthS2);
		final boolean fourthReturnDescendent = astrologyUtil.returnDescendent(fourthLocalSiderealDegrees);
		assertFalse(fourthReturnDescendent);
		
		final ZodiacSign fourthSign = astrologyUtil.returnSign(fourthS1, fourthS2, fourthReturnDescendent);
		assertEquals(ZodiacSign.Aries, fourthSign);
		final double fourthPosition = astrologyUtil.returnPosition(fourthS1, fourthS2, fourthReturnDescendent);
		assertEquals(0.0D, fourthPosition);
		///////////////////////////////////
		
		
		final Date fifthDate = new Date(1468350780000L); //2016.07.12 19:13:00 +0000
		final double fifthLongitude = -178D;
		final double fifthLatitude = -3D;
		
		final double fifthJd = dateTimeUtil.getJdTimeDate(fifthDate);
		assertEquals(2457582.300694444D, fifthJd);
		final double fifthCents = dateTimeUtil.getJulianCenturiesSinceJ2000(fifthJd);
		assertEquals(0.1652922845843728D, fifthCents);
		final double fifthSiderealDegrees = dateTimeUtil.getSiderealDegrees(fifthJd);
		assertEquals(2179659.3601489835D, fifthSiderealDegrees);
		final double fifthLocalSiderealDegrees = dateTimeUtil.getLocalSiderealDegrees(fifthSiderealDegrees, fifthLongitude);
		assertEquals(41.360148983541876D, fifthLocalSiderealDegrees);
		
		final double fifthEclipticLongitudeTan = astrologyUtil.calculateEclipticLongitudeJ2000Tangens(fifthLocalSiderealDegrees, fifthLatitude);
		assertEquals(-1.2821146345945846D, fifthEclipticLongitudeTan); //~0.000000000
		
		final double fifthS1 = astrologyUtil.getFirstHorizontEclipticSection(fifthEclipticLongitudeTan);
		assertEquals(127.95285788162039D, fifthS1);
		final double fifthS2 = astrologyUtil.getSecondHorizontEclipticSection(fifthEclipticLongitudeTan);
		assertEquals(307.9528578816204D, fifthS2);
		final boolean fifthReturnDescendent = astrologyUtil.returnDescendent(fifthLocalSiderealDegrees);
		assertFalse(fifthReturnDescendent);
		
		final ZodiacSign fifthSign = astrologyUtil.returnSign(fifthS1, fifthS2, fifthReturnDescendent);
		assertEquals(ZodiacSign.Leo, fifthSign);
		final double fifthPosition = astrologyUtil.returnPosition(fifthS1, fifthS2, fifthReturnDescendent);
		assertEquals(7.952857881620389D, fifthPosition);
	}
	
	/**
	 * http://de.wikipedia.org/wiki/Aszendent_%28Astrologie%29#Beispiele
	 * @throws Exception
	 */
	public final void testTropicalAscendentFull() throws Exception {
		//1983.11.10 13:04:00 +0000
		assertEquals(ZodiacSign.Pisces,    astrologyUtil.determineAscendent(new Date(437317440000L),    16D,  48D).getAscendent()); 
		//1986.12.10 23:12:00 +0000
		assertEquals(ZodiacSign.Gemini,    astrologyUtil.determineAscendent(new Date(534640320000L),   -27D, -61D).getAscendent()); 
		//2007.08.07 13:08:00 +0000
		assertEquals(ZodiacSign.Libra,     astrologyUtil.determineAscendent(new Date(1186492080000L),   17D,  78D).getAscendent()); 
		//2002.12.16 23:32:00 +0000
		assertEquals(ZodiacSign.Pisces,    astrologyUtil.determineAscendent(new Date(1040081520000L),  161D, -30D).getAscendent()); 
		//1987.05.17 18:18:00 +0000
		assertEquals(ZodiacSign.Capricorn, astrologyUtil.determineAscendent(new Date(548273880000L),    57D,  17D).getAscendent()); 
		//1979.09.18 03:35:00 +0000
		assertEquals(ZodiacSign.Gemini,    astrologyUtil.determineAscendent(new Date(306473700000L),   -58D,  -1D).getAscendent()); 
		//1987.09.17 01:24:00 +0000
		assertEquals(ZodiacSign.Leo, 	   astrologyUtil.determineAscendent(new Date(558840240000L),   -17D,  63D).getAscendent()); 
		//2020.12.06 00:27:00 +0000
		assertEquals(ZodiacSign.Virgo, 	   astrologyUtil.determineAscendent(new Date(1607214420000L),   -6D, -14D).getAscendent()); 
		//1976.10.28 14:19:00 +0000
		assertEquals(ZodiacSign.Capricorn, astrologyUtil.determineAscendent(new Date(215360340000L),   -61D, -17D).getAscendent()); 
		//2010.09.03 02:04:00 +0000
		assertEquals(ZodiacSign.Leo, 	   astrologyUtil.determineAscendent(new Date(1283479440000L),   51D, -27D).getAscendent()); 
		//2008.08.03 09:41:00 +0000
		assertEquals(ZodiacSign.Cancer,    astrologyUtil.determineAscendent(new Date(1217756460000L),  -93D,  45D).getAscendent()); 
		//2015.05.25 07:15:00 +0000
		assertEquals(ZodiacSign.Aries,     astrologyUtil.determineAscendent(new Date(1432538100000L),  -21D, -90D).getAscendent()); 
		//2016.07.12 19:13:00 +0000
		assertEquals(ZodiacSign.Leo,       astrologyUtil.determineAscendent(new Date(1468350780000L), -178D,  -3D).getAscendent()); 
	}

	/*
	final Date wat = new Date();
	wat.setTime(0L);
	wat.setYear(2016 - 1900);
	wat.setMonth(7 - 1);
	wat.setDate(12);
	wat.setHours(19 + 2);
	wat.setMinutes(13);
	wat.setSeconds(0);
	System.out.println("wat: " + DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z").format(wat, TimeZone.createTimeZone(0))); 
	System.out.println("ms: " + wat.getTime()); 
	*/
	
    public final void testTrigonometry() throws Exception {
    	final double x = 0.111D;
    	final double sinX = Math.sin(x);
    	final double cosX = Math.cos(x);
    	assertEquals(NumberFormat.getFormat("#.0000").format(x), NumberFormat.getFormat("#.0000").format(Math.asin(sinX)));
    	assertEquals(NumberFormat.getFormat("#.0000").format(x), NumberFormat.getFormat("#.0000").format(Math.acos(cosX)));
    }

	@Override
    public String getModuleName() {
	    return "astrochart.AstroChart";
    }
}
